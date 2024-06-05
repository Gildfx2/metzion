package com.example.pre_alpha.main;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pre_alpha.models.FBref;
import com.example.pre_alpha.R;
import com.example.pre_alpha.notifications.Token;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;


public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser fbUser;
    BottomNavigationView bottomNavigationView;
    CreatePostFragment createPostFragment;
    FilterFragment filterFragment;
    MapSearchFragment mapSearchFragment;
    public HomeFragment homeFragment = new HomeFragment();
    UserFragment userFragment=new UserFragment();
    Dialog dialog;
    DetailedPostFragment detailedPostFragment = new DetailedPostFragment();
    Button lost, found, searchDetails, searchMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth=FirebaseAuth.getInstance();
        fbUser=auth.getCurrentUser();
        //initializing the bottom navigation view
        bottomNavigationView=findViewById(R.id.bottomNavigationBar);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, homeFragment).commit();
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setItemIconTintList(null);

        //checking if the user wants to see the post details from the chat activity, and if so, it moves to the detailed post screen
        String check=getIntent().getStringExtra("get_data");
        if(check!=null && check.equals("true")){
            Bundle bundle1=new Bundle();
            String postId=getIntent().getStringExtra("post_id");
            bundle1.putString("post_id",postId);
            bundle1.putString("from_map_search_myPosts_chat", "chat");
            detailedPostFragment.setArguments(bundle1);
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, detailedPostFragment).commit();
        }
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home: //moving to the home screen
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, homeFragment).commit();
                        return true;
                    case R.id.search: //ask which search option the user wants to pick and operates accordingly to his choice
                        dialog=new Dialog(MainActivity.this);
                        dialog.setContentView(R.layout.search_dialog_layout);
                        searchMap=dialog.findViewById(R.id.search_with_map);
                        searchDetails=dialog.findViewById(R.id.search_with_details);
                        searchMap.setOnClickListener(new View.OnClickListener(){
                            public void onClick(View view){
                                dialog.cancel();
                                mapSearchFragment = new MapSearchFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, mapSearchFragment).commit();
                            }
                        });
                        searchDetails.setOnClickListener(new View.OnClickListener(){
                            public void onClick(View view){
                                dialog.cancel();
                                filterFragment = new FilterFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, filterFragment).commit();
                            }
                        });
                        dialog.show();
                        return true;
                    case R.id.post: //moving to the create post screen (firstly it asks the user to pick between lost or found)
                        dialog=new Dialog(MainActivity.this);
                        dialog.setContentView(R.layout.post_dialog_layout);
                        lost=dialog.findViewById(R.id.lost);
                        found=dialog.findViewById(R.id.found);
                        createPostFragment =new CreatePostFragment();
                        SharedPreferences state = getSharedPreferences("state", MODE_PRIVATE);
                        SharedPreferences.Editor editor = state.edit();
                        editor.putString("state", "create");
                        lost.setOnClickListener(new View.OnClickListener(){
                            public void onClick(View view){
                                editor.putString("what_state", "lost");
                                editor.apply();
                                editor.commit();
                                dialog.cancel();
                                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, createPostFragment).commit();
                            }
                        });
                        found.setOnClickListener(new View.OnClickListener(){
                            public void onClick(View view){
                                editor.putString("what_state", "found");
                                editor.apply();
                                editor.commit();
                                dialog.cancel();
                                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, createPostFragment).commit();
                            }
                        });
                        dialog.show();
                        return true;
                    case R.id.profile: //moving to the profile screen
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, userFragment).commit();
                        return true;
                }
                return false;
            }
        });
    }

    private void updateToken(String token) { //update the current token of the phone-necessary to the system so that it would know to which device the other user should receive to notification
        Token mToken = new Token(token);
        if(fbUser!=null)
            FBref.refTokens.child(fbUser.getUid()).setValue(mToken);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String token = task.getResult();
                Log.i("my token", token);
                updateToken(token);
            }
        });
        FBref.refUsers.child(fbUser.getUid()).child("status").setValue("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        FBref.refUsers.child(fbUser.getUid()).child("status").setValue(String.valueOf(System.currentTimeMillis()));
    }
}