package com.example.unityapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMessagingServ";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getNotification() != null) {
            handleNotificationMessage(remoteMessage.getNotification());
            return;
        }
        if (remoteMessage.getData().size() > 0) {
            handleDataMessage(remoteMessage.getData());
        }
    }

    private void handleDataMessage(Map<String, String> data) {
        String title = data.get("title");
        String body = data.get("body");
    }

    private void handleNotificationMessage(RemoteMessage.Notification notification) {
        String title = notification.getTitle();
        String body = notification.getBody();
        sendNotification(title, body);
    }

    private void sendNotification(String title, String body) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_UPDATE_CURRENT);
        Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.defaulttone);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "12345")
                .setSmallIcon(R.drawable.jeevandayini)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(0, notificationBuilder.build());
        }
    }
}