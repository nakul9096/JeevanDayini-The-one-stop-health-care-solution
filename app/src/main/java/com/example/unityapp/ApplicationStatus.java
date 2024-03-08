package com.example.unityapp;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    int badges=0;
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
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
                        generateCard(fullName, timestamp, status, relationType, userId, aadhaarNumber, childSnapshot);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private void generateCard(String fullName, String timestamp, String status, String relationType, String userId, String aadhaarNumber, DataSnapshot donationSnapshot) {
        LinearLayout cardContainer = findViewById(R.id.namesLayout25);
        CardView cardView = (CardView) getLayoutInflater().inflate(R.layout.card_status, cardContainer, false);
        TextView donationlocTextView = cardView.findViewById(R.id.statusTextView1);
        TextView fullNameTextView = cardView.findViewById(R.id.fullNameTextView);
        TextView timestampTextView = cardView.findViewById(R.id.timestampTextView);
        TextView statusTextView = cardView.findViewById(R.id.statusTextView);
        TextView relationTypeTextView = cardView.findViewById(R.id.relationTypeTextView);
        Button findBloodBanksButton = cardView.findViewById(R.id.findBloodBanksButton);
        TextView statusTextView2 = cardView.findViewById(R.id.statusTextView2);
        Button rescheduleButton = cardView.findViewById(R.id.rescheduleButton);
        fullNameTextView.setText("Full Name: " + fullName);
        timestampTextView.setText("Timestamp: " + timestamp);
        statusTextView.setText("Form status: " + status.toUpperCase());
        relationTypeTextView.setText("Relation Type: " + relationType);

        if ("approved".equals(status)) {
            statusTextView.setTextColor(getResources().getColor(R.color.green));
            donationlocTextView.setVisibility(View.VISIBLE);
            statusTextView2.setVisibility(View.VISIBLE);

            Log.d("DataFinding", "Matching user ID and Aadhaar number found");
            String donationLocation = donationSnapshot.child("donation_location").getValue(String.class);
            String donationStatus = donationSnapshot.child("donation_status").getValue(String.class);
            Log.d("DataFinding", "Donation status: found " + donationStatus);
            if(donationLocation != null) {
                donationlocTextView.setText("Donation location: ELECTED");
                if ("DONATED".equals(donationStatus)) {
                    badges++;
                    statusTextView2.setText("Donation Status: DONATED!");
                    fireworkAnimationView.setVisibility(View.VISIBLE);
                    textEarnedSomething.setText("You have earned " + badges + " badges!");
                    textEarnedSomething.setVisibility(View.VISIBLE);
                    fireworkAnimationView.playAnimation();
                    viewEarnedBadgesButton.setVisibility(View.VISIBLE);
                } else if ("ABSENT".equals(donationStatus)) {
                    statusTextView2.setTextColor(Color.RED);
                    statusTextView2.setText("Donation Status: ABSENT!");
                    rescheduleButton.setVisibility(View.VISIBLE);
                } else if ("DELETED".equals(donationStatus)) {
                    statusTextView2.setText("REQ WITHHELD CONTACT SUPPORT!");
                    statusTextView2.setTextColor(Color.parseColor("#FF0000"));
                } else {
                    statusTextView2.setText("Donation Status: Awaiting approval!");
                }
            }else {
                findBloodBanksButton.setVisibility(View.VISIBLE);
                donationlocTextView.setText("Donation location: NOT ELECTED");
            }
        }

        findBloodBanksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ApplicationStatus.this, FindClinics.class);
                SharedPreferences sharedPreferences = getSharedPreferences("DonationData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userid", userId);
                editor.putString("aadhaarNumber", aadhaarNumber);
                editor.putString("relationType", relationType);
                editor.putString("userName", fullName);
                editor.apply();
                intent.putExtra("requestType", "blooddonation");
                startActivity(intent);
            }
        });

        cardContainer.addView(cardView);
    }
}