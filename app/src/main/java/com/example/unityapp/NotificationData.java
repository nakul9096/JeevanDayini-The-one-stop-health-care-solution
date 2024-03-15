package com.example.unityapp;

public class NotificationData {
    private String fcmToken;
    private String title;
    private String message;

    public NotificationData(String fcmToken, String title, String message) {
        this.fcmToken = fcmToken;
        this.title = title;
        this.message = message;
    }

}
