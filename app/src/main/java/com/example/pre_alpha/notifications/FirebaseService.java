package com.example.pre_alpha.notifications;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

public class FirebaseService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        String tokenRefresh = String.valueOf(FirebaseMessaging.getInstance().getToken());
        if(fbUser!=null){
            updateToken(tokenRefresh);
        }
    }

    private void updateToken(String tokenRefresh){
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(tokenRefresh);
        ref.child(fbUser.getUid()).setValue(token);
    }
}
