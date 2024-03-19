package com.example.unityapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unityapp.SendNotificationPack.Client;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirebaseMessagingSender extends AppCompatActivity {
    EditText tokenInput, titleInput, messageInput;
    Button sendButton;
    APIService apiService;
    String serverBaseUrl = "http://192.168.139.40:5000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebasemessagingsender);
        tokenInput = findViewById(R.id.UserID);
        titleInput = findViewById(R.id.Title);
        messageInput = findViewById(R.id.Message);
        sendButton = findViewById(R.id.button);
        apiService = Client.getClient(serverBaseUrl).create(APIService.class);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationData notificationData = new NotificationData(
                        tokenInput.getText().toString(),
                        titleInput.getText().toString(),
                        messageInput.getText().toString()
                );
                sendDataToServer(notificationData);
            }
        });
    }

    private void sendDataToServer(NotificationData notificationData) {
        apiService.sendDataToServer(notificationData).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(FirebaseMessagingSender.this, "Data sent to server successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FirebaseMessagingSender.this, "Failed to send data to server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(FirebaseMessagingSender.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}