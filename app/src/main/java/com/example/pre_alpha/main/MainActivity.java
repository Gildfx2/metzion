package com.example.pre_alpha.main;

import static com.example.pre_alpha.models.FBref.refUsers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.pre_alpha.R;
import com.example.pre_alpha.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser fbUser;
    BottomNavigationView bottomNavigationView;
    public HomeFragment homeFragment=new HomeFragment();
    SearchFragment searchFragment=new SearchFragment();
    CreatePostFragment createPostFragment;
    FavouriteFragment favouriteFragment=new FavouriteFragment();
    UserFragment userFragment=new UserFragment();
    Dialog dialog;
    DetailedPostFragment detailedPostFragment = new DetailedPostFragment();
    User user;

    Button lost, found;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth=FirebaseAuth.getInstance();
        fbUser=auth.getCurrentUser();
        bottomNavigationView=findViewById(R.id.bottomNavigationBar);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, homeFragment).commit();
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setItemIconTintList(null);
        String check=getIntent().getStringExtra("get_data");
        if(check!=null && check.equals("true")){
            Bundle bundle1=new Bundle();
            String postId=getIntent().getStringExtra("post_id");
            bundle1.putString("post_id",postId);
            detailedPostFragment.setArguments(bundle1);
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, detailedPostFragment).commit();
        }
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, homeFragment).commit();
                        return true;
                    case R.id.search:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, searchFragment).commit();
                        return true;
                    case R.id.post:
                        dialog=new Dialog(MainActivity.this);
                        dialog.setContentView(R.layout.post_dialog_layout);
                        lost=dialog.findViewById(R.id.lost);
                        found=dialog.findViewById(R.id.found);
                        lost.setOnClickListener(new View.OnClickListener(){
                            public void onClick(View view){
                                SharedPreferences state = getSharedPreferences("state", MODE_PRIVATE);
                                SharedPreferences.Editor editor = state.edit();
                                editor.putString("state", "lost");
                                editor.apply();
                                editor.commit();
                                dialog.cancel();
                                if(createPostFragment !=null) createPostFragment.tvState.setText("מה איבדת?");
                                else createPostFragment =new CreatePostFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, createPostFragment).commit();
                            }
                        });
                        found.setOnClickListener(new View.OnClickListener(){
                            public void onClick(View view){
                                SharedPreferences state = getSharedPreferences("state", MODE_PRIVATE);
                                SharedPreferences.Editor editor = state.edit();
                                editor.putString("state", "found");
                                editor.apply();
                                editor.commit();
                                dialog.cancel();
                                if(createPostFragment !=null) createPostFragment.tvState.setText("מה מצאת?");
                                else createPostFragment =new CreatePostFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, createPostFragment).commit();
                            }
                        });
                        dialog.show();
                        return true;
                    case R.id.favourite:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, favouriteFragment).commit();
                        return true;
                    case R.id.profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, userFragment).commit();
                        return true;
                }
                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        refUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dS) {
                for (DataSnapshot data : dS.getChildren()) {
                    User userTmp = data.getValue(User.class);
                    if(userTmp.getUid().equals(fbUser.getUid())) {
                        user = new User(userTmp);
                        user.setStatus("online");
                        refUsers.child(fbUser.getUid()).setValue(user);
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        user.setStatus(String.valueOf(System.currentTimeMillis()));
        refUsers.child(fbUser.getUid()).setValue(user);
    }
}