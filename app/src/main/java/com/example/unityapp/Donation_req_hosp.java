package com.example.unityapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

public class Donation_req_hosp extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private LinearLayout cardContainer;
    ProgressBar progressBar;
    TextView noRequestsTextView80, infoTv;
    ImageView refreshImageView80;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_req_hosp);
        progressBar = findViewById(R.id.loadingProgressBar80);
        noRequestsTextView80 = findViewById(R.id.noRequestsTextView80);
        refreshImageView80 = findViewById(R.id.refreshImageView80);
        infoTv = findViewById(R.id.infoTV);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }
        String email = currentUser.getEmail();
        String sanitizedEmail = sanitizeEmail(email);
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("hospitals").child(sanitizedEmail).child("donation_requests");
        cardContainer = findViewById(R.id.namesLayout80);
        fetchDonationRequestsFromDatabase();
        refreshImageView80.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Donation_req_hosp.this, Donation_req_hosp.class);
                finish(); // Finish the current activity
                startActivity(intent);
            }
        });
    }
    private void fetchDonationRequestsFromDatabase() {
        noRequestsTextView80.setVisibility(View.GONE);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cardContainer.removeAllViews();
                boolean requestsFound = false;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot snapshot2 : childSnapshot.getChildren()) {
                        String userId = childSnapshot.getKey();
                        String donorName = snapshot2.child("userName").getValue(String.class);
                        String donationType = snapshot2.child("relationType").getValue(String.class);
                        String aadharNumber = snapshot2.child("aadharNumber").getValue(String.class);
                        if (!snapshot2.hasChild("donation_status")) {
                            createCard(userId, donorName, donationType, aadharNumber, snapshot2);
                            requestsFound = true;
                        }
                    }
                }
                if (!requestsFound) {
                    noRequestsTextView80.setText("No requests available at this moment!");
                    noRequestsTextView80.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                } else {
                    // Otherwise, hide the message
                    noRequestsTextView80.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    private void createCard(String userId, String donorName, String donationType, String aadharNumber, DataSnapshot childSnapshot) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.donation_req_card, cardContainer, false);
        TextView requestIdTextView = cardView.findViewById(R.id.userIdTextView);
        TextView donorNameTextView = cardView.findViewById(R.id.nameTextView);
        TextView adharTextView = cardView.findViewById(R.id.adharTextView);
        TextView donationTypeTextView = cardView.findViewById(R.id.relationTypeTextView);
        Button donatedButton = cardView.findViewById(R.id.donatedButton);
        donatedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                donatedButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Update donation_status to "donated" in Firebase
                        childSnapshot.getRef().child("donation_status").setValue("DONATED")
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Donation_req_hosp.this, "Preference updated successfully!", Toast.LENGTH_SHORT).show();
                                        infoTv.setText("Refreshing in 3 seconds");
                                        infoTv.setVisibility(View.VISIBLE);
                                        new CountDownTimer(3000, 1000) {
                                            public void onTick(long millisUntilFinished) {
                                                // Update the TextView with the remaining time
                                                infoTv.setText("Refreshing in " + millisUntilFinished / 1000 + " seconds");
                                            }

                                            public void onFinish() {
                                                Intent intent = new Intent(Donation_req_hosp.this, Donation_req_hosp.class);
                                                finish();
                                                startActivity(intent);
                                            }
                                        }.start();
                                    }

                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Donation_req_hosp.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                        Log.e("Donation_req_hosp", "Error updating donation status: " + e.getMessage());
                                    }
                                });
                    }
                });

            }
        });
        Button absentButton = cardView.findViewById(R.id.absentButton);
        absentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update donation_status to "donated" in Firebase
                childSnapshot.getRef().child("donation_status").setValue("ABSENT")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Donation_req_hosp.this, "Preference updated successfully!", Toast.LENGTH_SHORT).show();
                                infoTv.setText("Refreshing in 3 seconds");
                                infoTv.setVisibility(View.VISIBLE);
                                new CountDownTimer(3000, 1000) {
                                    public void onTick(long millisUntilFinished) {
                                        // Update the TextView with the remaining time
                                        infoTv.setText("Refreshing in " + millisUntilFinished / 1000 + " seconds");
                                    }

                                    public void onFinish() {
                                        Intent intent = new Intent(Donation_req_hosp.this, Donation_req_hosp.class);
                                        finish();
                                        startActivity(intent);
                                    }
                                }.start();
                            }

                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Donation_req_hosp.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                Log.e("Donation_req_hosp", "Error updating donation status: " + e.getMessage());
                            }
                        });
            }
        });
        Button declineButton = cardView.findViewById(R.id.deleteRequestButton);
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update donation_status to "donated" in Firebase
                childSnapshot.getRef().child("donation_status").setValue("DELETED")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Donation_req_hosp.this, "Preference updated successfully!", Toast.LENGTH_SHORT).show();
                                infoTv.setText("Refreshing in 3 seconds");
                                infoTv.setVisibility(View.VISIBLE);
                                new CountDownTimer(3000, 1000) {
                                    public void onTick(long millisUntilFinished) {
                                        // Update the TextView with the remaining time
                                        infoTv.setText("Refreshing in " + millisUntilFinished / 1000 + " seconds");
                                    }

                                    public void onFinish() {
                                        Intent intent = new Intent(Donation_req_hosp.this, Donation_req_hosp.class);
                                        finish();
                                        startActivity(intent);
                                    }
                                }.start();
                            }

                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Donation_req_hosp.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                Log.e("Donation_req_hosp", "Error updating donation status: " + e.getMessage());
                            }
                        });
            }
        });
        requestIdTextView.setText("Request ID: " + userId);
        donorNameTextView.setText("Donor Name: " + donorName);
        adharTextView.setText("Aadhar Number: " + aadharNumber);
        donationTypeTextView.setText("Donation Type: " + donationType);
        cardContainer.addView(cardView);
    }
    private String sanitizeEmail(String email) {
        return email.replaceAll("@", "").replace(".", "");
    }
}