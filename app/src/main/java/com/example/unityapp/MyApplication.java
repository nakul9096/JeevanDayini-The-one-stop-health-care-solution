package com.example.unityapp;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.onesignal.Continue;
import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;
//import com.phonepe.intent.sdk.api.PhonePe;

public class MyApplication extends Application {
    private static final String ONESIGNAL_APP_ID = "627f2e4b-e687-40b1-9946-03a9ff56f0ac";

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        //PhonePe.init(this);
        FirebaseInAppMessaging.getInstance().setAutomaticDataCollectionEnabled(true);
        FirebaseInAppMessaging.getInstance().setMessagesSuppressed(false);
        // Verbose Logging set to help debug issues, remove before releasing your app.
        OneSignal.getDebug().setLogLevel(LogLevel.VERBOSE);

        // OneSignal Initialization
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID);
        // requestPermission will show the native Android notification permission prompt.
        // NOTE: It's recommended to use a OneSignal In-App Message to prompt instead.
        OneSignal.getNotifications().requestPermission(true, Continue.with(r -> {
            if (r.isSuccess()) {
                if (r.getData()) {
                    // `requestPermission` completed successfully and the user has accepted permission
                } else {
                    // `requestPermission` completed successfully but the user has rejected permission
                }
            } else {
                // `requestPermission` completed unsuccessfully, check `r.getThrowable()` for more info on the failure reason
            }
        }));
    }
}