package com.example.pre_alpha.entry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.view.View;

import com.example.pre_alpha.main.MainActivity;
import com.example.pre_alpha.R;
import com.example.pre_alpha.models.User;
import com.example.pre_alpha.models.FBref;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {

    EditText etEmail, etPassword;
    String email, password, str;
    TextInputLayout layoutEmail, layoutPassword;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //initializing
        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.pass);
        layoutEmail = findViewById(R.id.layoutEmail1);
        layoutPassword = findViewById(R.id.layoutPassword1);
        auth = FirebaseAuth.getInstance();

        //checking remembering the account
        SharedPreferences remember = getSharedPreferences("check", MODE_PRIVATE);
        String checkRemember = remember.getString("remember", "");
        if(checkRemember.equals("true")){
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }

    }
    @Override
    protected void onDestroy() {
        etEmail.setText("");
        etPassword.setText("");
        super.onDestroy();
    }
    public void forgotPassword(View view) {
        //move to forgot password screen
        Intent intent = new Intent(this, ForgotPassword.class);
        startActivity(intent);
    }

    public void createAccount(View view) {
        //move to create account screen
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    private boolean validateEmail(String email) {
        if(email==null || email.isEmpty()) return false;
        String emailRegax = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegax);
        Matcher m = pattern.matcher(email);
        return m.matches();
    }

    public void login(View view) {
        layoutEmail.setHelperText("");
        layoutPassword.setHelperText("");
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        if (validateEmail(email)) { //checking if the email's structure is valid
            if (!password.isEmpty()) {
                //sign in with email and password with firebase authentication
                auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                SharedPreferences remember = getSharedPreferences("check", MODE_PRIVATE);
                                SharedPreferences.Editor editor = remember.edit();
                                editor.putString("remember", "true");
                                editor.apply();
                                editor.commit();
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                str="האימייל או הסיסמה שגויים";
                                layoutEmail.setHelperText(str);
                                layoutPassword.setHelperText(str);
                            }
                        });
            } else {
                str = "לא לשכוח להזין סיסמה";
                layoutPassword.setHelperText(str);
            }
        }
        else if(email.isEmpty()) {
            str = "לא לשכוח להזין מייל";
            layoutEmail.setHelperText(str);
            if (password.isEmpty()) {
                str = "לא לשכוח להזין סיסמה";
                layoutPassword.setHelperText(str);
            }
        }
        else {
            str = "אימייל לא תקין";
            layoutEmail.setHelperText(str);
            if (password.isEmpty()) {
                str = "לא לשכוח להזין סיסמה";
                layoutPassword.setHelperText(str);
            }
        }
    }


}
