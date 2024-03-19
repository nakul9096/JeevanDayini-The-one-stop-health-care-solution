package com.example.unityapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Booking extends AppCompatActivity {
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        EditText fullNameEditText = findViewById(R.id.fullName);
        EditText phoneEditText = findViewById(R.id.phone);
        EditText emailEditText = findViewById(R.id.email);
        EditText tokenNumberEditText = findViewById(R.id.tokennumber);
        EditText dateEditText = findViewById(R.id.dateet);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int tokenNumber2 = sharedPreferences.getInt("tokenNumber", -1);
        date = sharedPreferences.getString("selectedDate", "");
        tokenNumberEditText.setText(String.valueOf(tokenNumber2));
        dateEditText.setText(date);
        Button proceedBtn = findViewById(R.id.proceedbtn);
        String signedInEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        if (signedInEmail != null) {
            emailEditText.setText(signedInEmail);
            emailEditText.setEnabled(false);
        }
        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateAndSaveData()) {
                    Intent intent = new Intent(Booking.this, PaymentPage.class);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean validateAndSaveData() {
        EditText fullNameEditText = findViewById(R.id.fullName);
        EditText phoneEditText = findViewById(R.id.phone);
        EditText emailEditText = findViewById(R.id.email);
        String fullName = fullNameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(phone)) {
            Helper.showToast(Booking.this,"Name and phone number are required");
            return false;
        }
        if (phone.length() != 10) {
            Helper.showToast(Booking.this,"Phone number must be 10 digits");
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            email = "notprovided";
        }
        saveDataToSharedPreferences(fullName, phone, email);
        return true;
    }

    private void saveDataToSharedPreferences(String fullName, String phone, String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        EditText tokenNumberEditText = findViewById(R.id.tokennumber);
        EditText dateEditText = findViewById(R.id.dateet);
        String tokenNumber = tokenNumberEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();
        editor.putString("fullName", fullName);
        editor.putString("phone", phone);
        editor.putString("email", email);
        editor.putString("tokenNumber", tokenNumber);
        editor.putString("date", date);
        editor.apply();
    }
}