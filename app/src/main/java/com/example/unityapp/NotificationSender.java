package com.example.unityapp;

public class NotificationSender {
    private String fcmToken;
    private String title;
    private String message;

    public NotificationSender(String fcmToken, String title, String message) {
        this.fcmToken = fcmToken;
        this.title = title;
        this.message = message;
    }

    // Getters and setters
    // You may also need to define these depending on your requirements
}
