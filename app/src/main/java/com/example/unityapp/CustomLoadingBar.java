package com.example.unityapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.util.Log;

import androidx.annotation.NonNull;

public class CustomLoadingBar extends Dialog {

    public CustomLoadingBar(@NonNull Context context) {
        super(context);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        getWindow().setAttributes(params);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        View view = LayoutInflater.from(context).inflate(R.layout.loading_layout,null);
        setContentView(view);
    }

    /**
     * Placeholder method to prevent compilation errors.
     * Note: This method does nothing as the layout does not contain a TextView
     * to display the message.
     */
    public void setMessage(String message) {
        // Log the message for debugging purposes
        Log.d("CustomLoadingBar", "Message set: " + message);
        // No visual changes will be made as the layout does not support a message.
    }
}