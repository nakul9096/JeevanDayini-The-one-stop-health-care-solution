package com.example.unityapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HomeDashboard extends AppCompatActivity {
    private boolean backPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_dashboard);
        TextView privacytv = findViewById(R.id.privacytv1);
        TextView tandctv = findViewById(R.id.tandctv1);
        Button btnUserLogin = findViewById(R.id.btnUserLogin);
        Button btnAdminLogin = findViewById(R.id.btnAdminLogin);
        Button btn_login = findViewById(R.id.btn_login);
        privacytv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeDashboard.this, Privacy.class);
                startActivity(intent);
            }
        });
        tandctv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeDashboard.this, Terms.class);
                startActivity(intent);
            }
        });
        btnUserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the User activity (User.xml)
                Intent intent = new Intent(HomeDashboard.this, Register.class);
                startActivity(intent);
            }
        });

        btnAdminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the Admin activity (Register.xml)
                Intent intent = new Intent(HomeDashboard.this, RegisterAdmin.class);
                startActivity(intent);
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the Admin activity (Register.xml)
                Intent intent = new Intent(HomeDashboard.this, Login.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (backPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.backPressedOnce = true;
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        new android.os.Handler().postDelayed(() -> backPressedOnce = false, 2000);
    }
}