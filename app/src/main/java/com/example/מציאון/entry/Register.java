package com.example.מציאון.entry;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.מציאון.main.MainActivity;
import com.example.מציאון.R;
import com.example.מציאון.models.User;
import com.example.מציאון.models.FBref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {
    EditText etEmail, etPassword, etUsername, etConPassword;
    String email,username,password,confirmPassword;
    TextInputLayout layoutEmail, layoutUsername, layoutPassword, layoutConfirmPassword;
    Button btnOkay;
    User user;
    ArrayList<User> userValues = new ArrayList<User>();
    ValueEventListener userListener;
    Dialog dialog;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etEmail=findViewById(R.id.email);
        etUsername=findViewById(R.id.username);
        etPassword=findViewById(R.id.pass);
        etConPassword=findViewById(R.id.conpass);
        layoutEmail=findViewById(R.id.layoutEmail2);
        layoutUsername=findViewById(R.id.layoutUsername);
        layoutPassword=findViewById(R.id.layoutPassword2);
        layoutConfirmPassword=findViewById(R.id.layoutConfirmPassword);
        auth=FirebaseAuth.getInstance();
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
        etUsername.setText("");
        etEmail.setText("");
        etPassword.setText("");
        etConPassword.setText("");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (userListener!=null) {
            FBref.refUsers.removeEventListener(userListener);
        }

    }

    public void register(View view){
        layoutEmail.setHelperText("");
        layoutUsername.setHelperText("");
        layoutPassword.setHelperText("");
        layoutConfirmPassword.setHelperText("");

        email=etEmail.getText().toString();
        username=etUsername.getText().toString();
        password=etPassword.getText().toString();
        confirmPassword=etConPassword.getText().toString();
        if(!validateEmail(email)) layoutEmail.setHelperText("אימייל לא תקין");
        if(!validateUsername(username)) layoutUsername.setHelperText("השם משתמש צריך לכלול לפחות 3 תווים, להתחיל באות מסוימת, ללא רווחים");
        if(!validatePassword(password)) layoutPassword.setHelperText("הסיסמה צריכה לכלול לפחות 6 תווים, אות קטנה אחת, מספר אחד");
        else if(!password.equals(confirmPassword)) {
            layoutConfirmPassword.setHelperText("הסיסמאות לא תואמות אחת לשנייה");
        }
        else if((userExist(email)) && (!username.isEmpty() || username!=null) &&
                (!password.isEmpty() || password!=null) && (!confirmPassword.isEmpty() || confirmPassword!=null)){
            dialog=new Dialog(this);
            dialog.setContentView(R.layout.exist_dialog_layout);
            btnOkay=dialog.findViewById(R.id.emailTaken);
            btnOkay.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                    dialog.cancel();
                }
            });
            dialog.show();
        }
        else{
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        FirebaseUser fbUser= auth.getCurrentUser();
                        user=new User(email,username,fbUser.getUid(), "online");
                        DatabaseReference userRef = FBref.refUsers.child(fbUser.getUid());
                        ValueEventListener userValueEventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    user=new User(email,username,fbUser.getUid(), "online");
                                    FBref.refUsers.child(fbUser.getUid()).setValue(user);
                                }
                                FBref.refUsers.removeEventListener(this);
                            }


                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(Register.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        };
                        userRef.addValueEventListener(userValueEventListener);
                        SharedPreferences remember = getSharedPreferences("check", MODE_PRIVATE);
                        SharedPreferences.Editor editor = remember.edit();
                        editor.putString("remember", "true");
                        editor.apply();
                        editor.commit();
                        Intent intent = new Intent(Register.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(Register.this, "Signup Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }
    public void loginAccount(View view){
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    public boolean validateEmail(String email){
        if(email==null || email.isEmpty()) return false;
        String emailRegax="^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@"+
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern= Pattern.compile(emailRegax);
        Matcher m = pattern.matcher(email);
        return m.matches();
    }
    public static boolean validatePassword(String password) {
        if (password == null && password.isEmpty()) return false;
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z]).{6,20}$";
        // הסיסמה חייבת להכיל בין 6-20 תווים שחייבת להכיל לפחות אות קטנה אחת ומספר אחד
        Pattern pattern = Pattern.compile(passwordRegex);
        Matcher m = pattern.matcher(password);
        return m.matches();
    }
    public static boolean validateUsername(String username) {
        if (username == null && username.isEmpty()) return false;
        String usernameRegex = "^[A-Za-zא-ת]\\w{2,24}$";
        //השם משתמש חייב להכיל בין 3-25 תווים שחייבים להתחיל באות קטנה או אות גדולה
        //נ.ב אסור שיהיו רווחים
        Pattern pattern = Pattern.compile(usernameRegex);
        Matcher m = pattern.matcher(username);
        return m.matches();
    }
    private boolean userExist(String email){
        for(User user : userValues){
            if(user.getEmail().equals(email)) return true;
        }
        return false;
    }




}