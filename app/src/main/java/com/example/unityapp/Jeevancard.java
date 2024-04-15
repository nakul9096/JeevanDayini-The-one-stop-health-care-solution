package com.example.unityapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Jeevancard extends AppCompatActivity {
    private Button applyNowButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jeevancard);
        applyNowButton = findViewById(R.id.applyNowButton);
        applyNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Jeevancard.this, "We have scheduled a call for you! feel free to ask your queries",Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}