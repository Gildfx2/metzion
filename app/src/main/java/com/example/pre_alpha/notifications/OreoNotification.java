// This class is responsible for handling notifications on Android Oreo (API level 26) and above.
package com.example.pre_alpha.notifications;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

public class OreoNotification extends ContextWrapper {
    private static final String CHANNEL_ID = "com.example.pre_alpha.app";
    private static final String CHANNEL_NAME = "FirebaseAPP";
    private NotificationManager notificationManager;

    // Constructor for the OreoNotification class.
    public OreoNotification(Context base) {
        super(base);
        // Create the notification channel if the Android version is Oreo or higher.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    // Creates a notification channel for Oreo and above.
    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableLights(false);
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(notificationChannel);
    }

    // Retrieves the NotificationManager system service.
    public NotificationManager getManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    // Builds and returns a Notification.Builder for Oreo and above.
    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getOreoNotification(String title,
                                                    String body,
                                                    PendingIntent pIntent,
                                                    Uri soundUri,
                                                    String icon) {
        return new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentIntent(pIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(soundUri)
                .setAutoCancel(true)
                .setSmallIcon(Integer.parseInt(icon));
    }
}
