package com.example.pre_alpha.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.pre_alpha.chat.ChatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    String newToken;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            newToken = task.getResult();
                            Log.d("FCM Token", newToken);
                        } else {
                            Log.e("FCM Token", "Failed to get token: " + task.getException());
                        }
                    }
                });

        if (firebaseUser != null) {
            updateToken(newToken);
        }
    }

    private void updateToken(String newToken) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
            Token token = new Token(newToken);
            reference.child(firebaseUser.getUid()).setValue(token);
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> data_notify = remoteMessage.getData();
        Log.d("FCM", "Message data payload: " + data_notify);

        if (data_notify.size() > 0) {
            Log.d("FCM", "Data payload received: " + data_notify);
            sendOreoNotification(remoteMessage);
            Log.d("FCM", "Sent Oreo notification");
        } else {
            Log.e("FCM", "No data payload in the message");
        }
    }

    // Send a notification using Oreo notification system
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendOreoNotification(RemoteMessage remoteMessage) {
        // Retrieve notification data from the message
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String post = remoteMessage.getData().get("post");
        String username = remoteMessage.getData().get("username");

        // Extract notification information
        RemoteMessage.Notification notification  = remoteMessage.getNotification();

        // Generate a unique integer for notification ID
        int notificationId = user.hashCode(); // Using hashCode as a unique identifier

        // Prepare intent for notification click
        Intent intent = new Intent(this, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("other_user_uid", user);
        bundle.putString("post_id", post);
        bundle.putString("username", username);
        bundle.putString("from_post_or_chatlist", "chatlist");
        intent.putExtras(bundle);
        SharedPreferences chat = getSharedPreferences("chat_pick", MODE_PRIVATE);
        SharedPreferences.Editor editor = chat.edit();
        editor.putString("chat_pick", "send message");
        editor.apply();

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoNotification oreoNotification = new OreoNotification(this);
        Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent, defaultSound, icon);

        oreoNotification.getManager().notify(notificationId, builder.build());
    }


}
