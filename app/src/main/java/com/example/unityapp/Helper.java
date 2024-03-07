package com.example.unityapp;
import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

public class Helper {

    public static void showToast(Activity activity, String message) {
        Context context = activity.getApplicationContext();
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
