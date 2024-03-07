package com.example.unityapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // Make sure the layout resource name matches

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.registerFragmentContainer, new RegisterFragment())
                    .commit();
        }
    }
}
