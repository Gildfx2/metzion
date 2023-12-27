package com.example.pre_alpha.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

public class MessageNotification extends ContextWrapper {
    private static final String ID = "some_id";
    private static final String NAME = "FirebaseAPP";
    private NotificationManager notificationManager;

    public MessageNotification(Context base){
        super(base);
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O){
            createChannel();
        }
    }

    private void createChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(ID, NAME, NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
    }

}
