package com.example.unityapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;

import java.util.Map;

public class CustomMessagingService extends FirebaseMessagingService {

    int notificationId = generateUniqueId();
    private NotificationManager notificationManager;
    private Uri defaultSoundUri;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (remoteMessage != null && remoteMessage.getData() != null) {
            Map<String, String> dataMap = remoteMessage.getData();
            String noteType = dataMap.get("type");

            defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            switch (noteType) {
                case "BIGTEXT":
                    bigTextNotification(dataMap);
                    break;
                case "BIGPIC":
                    bigPicNotification(dataMap);
                    break;
                case "ACTIONS":
                    notificationActions(dataMap);
                    break;
                case "DIRECTREPLY":
                    directReply(dataMap);
                    break;
                case "INBOX":
                    inboxTypeNotification(dataMap);
                    break;
                case "MESSAGE":
                    messageTypeNotification(dataMap);
                    break;
                default:
                    Log.e("CustomMessagingService", "Unknown notification type: " + noteType);
            }
        }
    }

    private void notifyUser(String title, String messageBody, PendingIntent pendingIntent) {
        String channelId = getString(R.string.default_notification_channelid);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.notification)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setColor(Color.BLUE)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Fcm notifications",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    public void bigTextNotification(Map<String, String> dataMap) {
        String title = dataMap.get("title");
        String message = dataMap.get("message");

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.bigText(message);
        style.setSummaryText(title);

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        notifyUser(title, message, pendingIntent);
    }

    public void bigPicNotification(Map<String, String> dataMap) {
        String title = dataMap.get("title");
        String message = dataMap.get("message");
        String imageUrl = dataMap.get("imageUrl");

        NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
        style.setBigContentTitle(title);
        style.setSummaryText(message);

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        notifyUser(title, message, pendingIntent);
    }

    public void notificationActions(Map<String, String> dataMap) {
        String title = dataMap.get("title");
        String message = dataMap.get("message");

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent cancelIntent = new Intent(this, NotificationReceiver.class);
        cancelIntent.putExtra("ID", 1);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(this, 1, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder3 = new NotificationCompat.Builder(this, getString(R.string.default_notification_channelid))
                .setSmallIcon(R.drawable.notification)
                .setContentTitle(title)
                .setContentText(message)
                .setColor(Color.BLUE)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(android.R.drawable.ic_menu_view, "VIEW", pendingIntent)
                .addAction(android.R.drawable.ic_delete, "DISMISS", cancelPendingIntent);

        notifyUser(title, message, pendingIntent);
    }

    public void directReply(Map<String, String> dataMap) {
        String title = dataMap.get("title");
        String message = dataMap.get("message");

        Intent cancelIntent = new Intent(this, NotificationReceiver.class);
        cancelIntent.putExtra("ID", 1);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(this, 1, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent feedbackIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent feedbackPendingIntent = PendingIntent.getBroadcast(this,
                100, feedbackIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteInput remoteInput = new RemoteInput.Builder("DirectReplyNotification")
                .setLabel(message)
                .build();

        NotificationCompat.Action action = new NotificationCompat.Action.Builder(android.R.drawable.ic_delete,
                "Write here...", feedbackPendingIntent)
                .addRemoteInput(remoteInput)
                .build();

        NotificationCompat.Builder builder4 = new NotificationCompat.Builder(this, getString(R.string.default_notification_channelid))
                .setSmallIcon(R.drawable.notification)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(feedbackPendingIntent)
                .addAction(action)
                .setColor(Color.RED)
                .addAction(android.R.drawable.ic_menu_compass, "Cancel", cancelPendingIntent);

        notifyUser(title, message, feedbackPendingIntent);
    }

    public void inboxTypeNotification(Map<String, String> dataMap) {
        try {
            String title = dataMap.get("title");
            String message = dataMap.get("message");
            JSONArray jsonArray = new JSONArray(dataMap.get("contentList"));

            NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
            style.setSummaryText(message);
            style.setBigContentTitle(title);
            for (int i = 0; i < jsonArray.length(); i++) {
                String emailName = jsonArray.getString(i);
                style.addLine(emailName);
            }

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder5 = new NotificationCompat.Builder(this, getString(R.string.default_notification_channelid))
                    .setContentTitle(title)
                    .setContentText(message)
                    .setColor(Color.BLUE)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setSmallIcon(R.drawable.notification)
                    .setStyle(style);

            notifyUser(title, message, pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void messageTypeNotification(Map<String, String> dataMap) {
        String title = dataMap.get("title");

        NotificationCompat.MessagingStyle style = new NotificationCompat.MessagingStyle("Janhavi");
        style.addMessage("Is there any online tutorial for FCM?", 0, "member1");
        style.addMessage("Yes", 1, "");
        style.addMessage("How to use constraint layout?", 0, "member2");

        NotificationCompat.Builder builder6 = new NotificationCompat.Builder(this, getString(R.string.default_notification_channelid))
                .setSmallIcon(R.drawable.notification)
                .setColor(Color.RED)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.notification))
                .setSound(defaultSoundUri)
                .setStyle(style)
                .setAutoCancel(true);

        notifyUser(title, "", null);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    private int generateUniqueId() {
        return (int) System.currentTimeMillis();
    }
}
