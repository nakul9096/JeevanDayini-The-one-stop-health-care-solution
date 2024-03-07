package com.example.unityapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginDevices extends AppCompatActivity {

    private LinearLayout deviceContainer;
    private TextView tvemail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_devices);

        deviceContainer = findViewById(R.id.deviceContainer);
        tvemail = findViewById(R.id.tvemail);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            String sanitizedEmail = sanitizeEmail(email);
            Log.e("Test","Sanitized Email: "+sanitizedEmail);
            tvemail.setText(email);
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
            DatabaseReference loginInfoRef = usersRef.child(sanitizedEmail).child("login_info");
            loginInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot deviceSnapshot : dataSnapshot.getChildren()) {
                            String deviceKey = deviceSnapshot.getKey();
                            String deviceModel = deviceSnapshot.child("deviceModel").getValue(String.class);
                            String lastSeenTime = deviceSnapshot.child("lastSeenTime").getValue(String.class);
                            String signInTime = deviceSnapshot.child("signInTime").getValue(String.class);
                            Log.e("Test","1: "+deviceKey);
                            Log.e("Test","2: "+deviceModel);
                            Log.e("Test","3: "+lastSeenTime);
                            Log.e("Test","4: "+signInTime);
                            addDeviceCard(deviceKey, deviceModel, lastSeenTime, signInTime);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                }
            });
        }
    }
    private String sanitizeEmail(String email) {
        return email.replace("@", "").replace(".", "");
    }

    private void addDeviceCard(String deviceKey, String deviceModel, String lastSeenTime, String signInTime) {
        // Inflate device card layout
        LinearLayout deviceCard = (LinearLayout) getLayoutInflater().inflate(R.layout.device_card, null);
        TextView deviceModelTextView = deviceCard.findViewById(R.id.deviceModelTextView);
        TextView lastSeenTimeTextView = deviceCard.findViewById(R.id.lastSeenTimeTextView);
        TextView signInTimeTextView = deviceCard.findViewById(R.id.signInTimeTextView);
        deviceModelTextView.setText("Device Model: " + deviceModel);
        lastSeenTimeTextView.setText("Last Seen Time: " + lastSeenTime);
        signInTimeTextView.setText("Sign In Time: " + signInTime);
        deviceContainer.addView(deviceCard);
        Button logoutButton = findViewById(R.id.logoutButton8);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String email = currentUser.getEmail();
                    String sanitizedEmail = sanitizeEmail(email);
                    DatabaseReference loginInfoRef = FirebaseDatabase.getInstance().getReference("users")
                            .child(sanitizedEmail)
                            .child("login_info");
                    loginInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        DatabaseReference logoutRequestRef = snapshot.child("logout_request").getRef();
                                        logoutRequestRef.setValue("yes")
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(LoginDevices.this, "Logout successful!", Toast.LENGTH_SHORT).show();
                                                        recreate();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(LoginDevices.this, "Logout unsuccessful! Please try again.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
        });
    }
}