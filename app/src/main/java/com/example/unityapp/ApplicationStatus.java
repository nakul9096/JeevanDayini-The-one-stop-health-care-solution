package com.example.unityapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ApplicationStatus extends AppCompatActivity {
    private Button registerAsDonorButton;
    LottieAnimationView fireworkAnimationView;
    TextView textEarnedSomething;
    private Button viewEarnedBadgesButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_status);
        registerAsDonorButton = findViewById(R.id.registerAsDonorButton);
        fireworkAnimationView = findViewById(R.id.fireworkAnimationView);
        textEarnedSomething = findViewById(R.id.textEarnedSomething);
        LinearLayout cardContainer = findViewById(R.id.namesLayout25);
        viewEarnedBadgesButton = findViewById(R.id.viewEarnedBadgesButton);
        registerAsDonorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DonarType.class);
                startActivity(intent);
            }

        });
        // Replace "email" with the actual field in your data structure
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in, get their email
            String userEmail = user.getEmail();
            generateCardFromDatabase(userEmail);
        } else {
            Toast.makeText(this, "User not signed in!", Toast.LENGTH_SHORT).show();

        }
        Button goToHomeButton = findViewById(R.id.homebtn);
        goToHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ApplicationStatus.this, Session.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void generateCardFromDatabase(String email) {
        String PREFS_NAME2 = "ProfileData";
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME2, MODE_PRIVATE);
        String userId = preferences.getString("userid", "");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("blooddonation").child(userId);
        reference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String fullName = childSnapshot.child("fullName").getValue(String.class);
                        String timestamp = childSnapshot.child("timestamp").getValue(String.class);
                        String status = childSnapshot.child("status").getValue(String.class);
                        String relationType = childSnapshot.child("relationType").getValue(String.class);
                        String userId = childSnapshot.child("userId").getValue(String.class);
                        String aadhaarNumber = childSnapshot.child("aadhaarNumber").getValue(String.class);
                        generateCard(fullName, timestamp, status, relationType, userId, aadhaarNumber);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private void generateCard(String fullName, String timestamp, String status, String relationType, String userId, String aadhaarNumber) {
        LinearLayout cardContainer = findViewById(R.id.namesLayout25);
        CardView cardView = (CardView) getLayoutInflater().inflate(R.layout.card_status, cardContainer, false);
        TextView donationlocTextView = cardView.findViewById(R.id.statusTextView1);
        TextView fullNameTextView = cardView.findViewById(R.id.fullNameTextView);
        TextView timestampTextView = cardView.findViewById(R.id.timestampTextView);
        TextView statusTextView = cardView.findViewById(R.id.statusTextView);
        TextView relationTypeTextView = cardView.findViewById(R.id.relationTypeTextView);
        Button findBloodBanksButton = cardView.findViewById(R.id.findBloodBanksButton);
        TextView statusTextView2 = cardView.findViewById(R.id.statusTextView2);
        fullNameTextView.setText("Full Name: " + fullName);
        timestampTextView.setText("Timestamp: " + timestamp);
        statusTextView.setText("Form status: " + status.toUpperCase());
        relationTypeTextView.setText("Relation Type: " + relationType);
        if ("approved".equals(status)) {
            statusTextView.setTextColor(getResources().getColor(R.color.green));
            donationlocTextView.setVisibility(View.VISIBLE);
            statusTextView2.setVisibility(View.VISIBLE);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            Log.e("ApplyNow","User ID: "+userId);
            databaseReference.child("hospitals").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot hospitalSnapshot : dataSnapshot.getChildren()) {
                        DataSnapshot donationRequestsSnapshot = hospitalSnapshot.child("donation_requests");
                        for (DataSnapshot donationLocationSnapshot : donationRequestsSnapshot.getChildren()) {
                            for (DataSnapshot donationLocationSnapshot2 : donationLocationSnapshot.getChildren()) {
                                String donationLocation = donationLocationSnapshot.getKey();
                                String donationLocation2 = donationLocationSnapshot2.getKey();
                                if (donationLocation.equals(userId) && donationLocation2.equals(aadhaarNumber)) {
                                    donationlocTextView.setText("Donation location: ELECTED");
                                    String donationStatus = donationLocationSnapshot.child("donation_status").getValue(String.class);
                                    if ("donated".equals(donationStatus)) {
                                        statusTextView2.setText("Donation Status: Donated!");
                                        fireworkAnimationView.setVisibility(View.VISIBLE);
                                        textEarnedSomething.setVisibility(View.VISIBLE);
                                        fireworkAnimationView.playAnimation();
                                        viewEarnedBadgesButton.setVisibility(View.VISIBLE);
                                    } else {
                                        statusTextView2.setText("Donation Status: Awaiting approval!");
                                    }
                                    findBloodBanksButton.setVisibility(View.GONE);
                                    return;
                                } else {
                                    donationlocTextView.setText("Donation location: NOT ELECTED!");
                                    statusTextView2.setText("Donation Status: SELECT BD FIRST!");
                                }
                            }
                        }
                    }
                    findBloodBanksButton.setVisibility(View.VISIBLE);
                   Log.e("Test","no data found");
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("Test","error: "+databaseError);
                }
            });
            findBloodBanksButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ApplicationStatus.this, FindClinics.class);
                    SharedPreferences sharedPreferences = getSharedPreferences("DonationData", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userid", userId);
                    editor.putString("aadhaarNumber",aadhaarNumber);
                    editor.putString("relationType",relationType);
                    editor.putString("userName",fullName);
                    editor.apply();
                    intent.putExtra("requestType","blooddonation");
                    startActivity(intent);
                }
            });
        } else {
            findBloodBanksButton.setVisibility(View.GONE);
        }
        cardContainer.addView(cardView);
    }
}