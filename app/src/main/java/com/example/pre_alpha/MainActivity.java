package com.example.pre_alpha;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    TextView tv;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth=FirebaseAuth.getInstance();
        tv=findViewById(R.id.idTry);
        FirebaseUser user=auth.getCurrentUser();
        tv.setText(user.getEmail());

    }

    public void Logout(View view){
        SharedPreferences remember = getSharedPreferences("check", MODE_PRIVATE);
        SharedPreferences.Editor editor = remember.edit();
        editor.putString("remember", "false");
        editor.commit();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
}