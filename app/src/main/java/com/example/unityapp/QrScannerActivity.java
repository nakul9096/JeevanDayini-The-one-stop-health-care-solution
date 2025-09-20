package com.example.unityapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QrScannerActivity extends AppCompatActivity {
    private static final String TAG = "QrScannerActivity";
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner); // A simple layout for the scanner view

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // Start the QR code scanner
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan a QR code to view profile");
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan cancelled.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                String scannedData = result.getContents();
                Log.d(TAG, "Scanned data: " + scannedData);
                // The scanned data is the sanitized email
                fetchProfileData(scannedData);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void fetchProfileData(String sanitizedEmail) {
        DatabaseReference userRef = mDatabaseReference.child("users").child(sanitizedEmail);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Extract data from the database
                    String name = dataSnapshot.child("fullName").getValue(String.class);
                    String userId = dataSnapshot.child("userid").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String gender = dataSnapshot.child("gender").getValue(String.class);
                    String state = dataSnapshot.child("selectedState").getValue(String.class);
                    String city = dataSnapshot.child("selectedCity").getValue(String.class);
                    String phone = dataSnapshot.child("phoneNumber").getValue(String.class);
                    String bloodGroup = dataSnapshot.child("selectedBloodGroup").getValue(String.class);
                    String emergencyContactName = dataSnapshot.child("emergencyContactName").getValue(String.class);
                    String emergencyContactPhone = dataSnapshot.child("emergencyContactPhone").getValue(String.class);

                    // Create an Intent to display the profile info
                    Intent intent = new Intent(QrScannerActivity.this, DisplayProfileActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("userId", userId);
                    intent.putExtra("email", email);
                    intent.putExtra("gender", gender);
                    intent.putExtra("state", state);
                    intent.putExtra("city", city);
                    intent.putExtra("phone", phone);
                    intent.putExtra("bloodGroup", bloodGroup);
                    intent.putExtra("emergencyContactName", emergencyContactName);
                    intent.putExtra("emergencyContactPhone", emergencyContactPhone);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(QrScannerActivity.this, "Profile not found.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage());
                Toast.makeText(QrScannerActivity.this, "Error fetching data.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}