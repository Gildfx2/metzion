package com.example.pre_alpha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    TextView tv;
    FirebaseAuth auth;
    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment=new HomeFragment();
    SearchFragment searchFragment=new SearchFragment();
    PostFragment postFragment=new PostFragment();
    FavouriteFragment favouriteFragment=new FavouriteFragment();
    UserFragment userFragment=new UserFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth=FirebaseAuth.getInstance();
        bottomNavigationView=findViewById(R.id.bottomNavigationBar);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, homeFragment).commit();
        bottomNavigationView.setSelectedItemId(R.id.home);
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
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, postFragment).commit();
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

    public void Logout(View view){
        SharedPreferences remember = getSharedPreferences("check", MODE_PRIVATE);
        SharedPreferences.Editor editor = remember.edit();
        editor.putString("remember", "false");
        editor.commit();
        auth.signOut();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }
}