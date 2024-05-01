package com.example.pre_alpha.entry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pre_alpha.R;
import com.example.pre_alpha.models.User;
import com.example.pre_alpha.models.FBref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotPassword extends AppCompatActivity {
    EditText etEmail;
    TextInputLayout layoutEmail;
    String email;
    FirebaseAuth auth;
    ArrayList<User> userValues=new ArrayList<User>();
    ValueEventListener userListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        //initializing
        etEmail=findViewById(R.id.emailField);
        layoutEmail=findViewById(R.id.layoutEmail3);
        auth= FirebaseAuth.getInstance();

        //getting users
        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dS) {
                userValues.clear();
                for (DataSnapshot data : dS.getChildren()) {
                    User userTmp = data.getValue(User.class);
                    userValues.add(userTmp);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        };
        FBref.refUsers.addValueEventListener(userListener);
    }
    @Override
    protected void onDestroy() {
        etEmail.setText("");
        super.onDestroy();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (userListener!=null) {
            FBref.refUsers.removeEventListener(userListener);
        }
    }
    public void loginScreen(View view){
        //move to sign in screen
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
    private boolean userExist(String email){
        //check if the account already exist
        for(User user : userValues){
            if(user.getEmail().equals(email)) return true;
        }
        return false;
    }
    private boolean validateEmail(String email){
        //checking if the email's structure is valid
        if(email==null || email.isEmpty()) return false;
        String emailRegax="^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@"+
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern= Pattern.compile(emailRegax);
        Matcher m = pattern.matcher(email);
        return m.matches();
    }
    public void resetPassword(View view){
        layoutEmail.setHelperText("");
        email=etEmail.getText().toString();
        if(!userExist(email) || !validateEmail(email)){
            layoutEmail.setHelperText("הזן את אימייל ההרשמה שלך");
        }
        else{
            //resetting the password
            auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) Toast.makeText(ForgotPassword.this, "בדוק את האימייל שלך", Toast.LENGTH_SHORT).show();
                    else{
                        Toast.makeText(ForgotPassword.this, "לא ניתן לשלוח אימייל", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}