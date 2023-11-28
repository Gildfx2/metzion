package com.example.pre_alpha.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.pre_alpha.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;



public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    BottomNavigationView bottomNavigationView;
    public HomeFragment homeFragment=new HomeFragment();
    SearchFragment searchFragment=new SearchFragment();
    CreatePostFragment createPostFragment;
    FavouriteFragment favouriteFragment=new FavouriteFragment();
    UserFragment userFragment=new UserFragment();
    Dialog dialog;
    DetailedPostFragment detailedPostFragment = new DetailedPostFragment();
    Button lost, found;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth=FirebaseAuth.getInstance();
        bottomNavigationView=findViewById(R.id.bottomNavigationBar);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, homeFragment).commit();
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setItemIconTintList(null);
        String check=getIntent().getStringExtra("get_data");
        if(check!=null && check.equals("true")){
            Bundle bundle1=new Bundle();
            String postUid=getIntent().getStringExtra("post_uid");
            bundle1.putString("post_uid",postUid);
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





}