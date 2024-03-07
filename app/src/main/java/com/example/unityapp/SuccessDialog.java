package com.example.unityapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;

public class SuccessDialog extends Dialog {
    private static final int TIMEOUT_DELAY = 6000;
    private Button okButton;
    private CountDownTimer countDownTimer;

    public SuccessDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.success_dialog);
        okButton = findViewById(R.id.okButton);
        countDownTimer = new CountDownTimer(TIMEOUT_DELAY, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                okButton.setText("OK (" + secondsRemaining + ")");
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(getContext(), HomeDashboard.class);
                getContext().startActivity(intent);
                dismiss();
            }
        }.start();
        okButton.setOnClickListener(v -> {
            countDownTimer.cancel();
            dismiss();
            Intent intent = new Intent(getContext(), HomeDashboard.class);
            getContext().startActivity(intent);
        });
    }
}
