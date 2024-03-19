package com.example.unityapp;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Donation_req_hosp extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private LinearLayout cardContainer;
    ProgressBar progressBar;
    TextView noRequestsTextView80, infoTv;
    ImageView refreshImageView80;
    String hosp_email;
    String email;
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
        email = currentUser.getEmail();
        hosp_email = sanitizeEmail(email);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("blooddonation");
        cardContainer = findViewById(R.id.namesLayout80);
        fetchDonationRequestsFromDatabase();
        refreshImageView80.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Donation_req_hosp.this, Donation_req_hosp.class);
                finish();
                startActivity(intent);
            }
        });
    }
    private void fetchDonationRequestsFromDatabase() {
        noRequestsTextView80.setVisibility(View.GONE);
        Log.d("fetchDonationRequests", "Fetching donation requests from database...");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("fetchDonationRequests", "Data received from database.");
                cardContainer.removeAllViews();
                boolean requestsFound = false;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String userId = childSnapshot.getKey();
                    Log.e("Testing","userid nretreived: "+userId);

                    for (DataSnapshot snapshot2 : childSnapshot.getChildren()) {
                        String aadharNumber = snapshot2.getKey();
                        Log.e("Testing","aadhar nretreived: "+aadharNumber);
                        String donorName = snapshot2.child("fullName").getValue(String.class);
                        String donationType = snapshot2.child("relationType").getValue(String.class);
                        String donationStatus = snapshot2.child("donation_status").getValue(String.class);
                        String status = snapshot2.child("status").getValue(String.class);
                        String donation_location = snapshot2.child("donation_location").getValue(String.class);
                        String emaildb = snapshot2.child("email").getValue(String.class);
                        Log.e("Testing","DOnation location: "+donation_location);
                        if (donorName != null && donationType != null && donationStatus == null && status.equals("approved") && donation_location != null && donation_location.equals(email) && emaildb != null) {
                            createCard(userId, donorName, donationType, aadharNumber, emaildb);
                            requestsFound = true;
                        }
                    }
                }
                if (!requestsFound) {
                    noRequestsTextView80.setText("No requests available at this moment!");
                    noRequestsTextView80.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("fetchDonationRequests", "Error fetching donation requests: " + databaseError.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    private void createCard(String userId, String donorName, String donationType, String aadharNumber, String emaildb) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.donation_req_card, cardContainer, false);
        TextView requestIdTextView = cardView.findViewById(R.id.userIdTextView);
        TextView donorNameTextView = cardView.findViewById(R.id.nameTextView);
        TextView adharTextView = cardView.findViewById(R.id.adharTextView);
        TextView donationTypeTextView = cardView.findViewById(R.id.relationTypeTextView);
        Button donatedButton = cardView.findViewById(R.id.donatedButton);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("blooddonation")
                .child(userId)
                .child(aadharNumber);
        donatedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        ref.child("donation_status").setValue("DONATED")
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Helper.showToast(Donation_req_hosp.this,"Preference updated successfully!");
                                        infoTv.setText("Refreshing in 3 seconds");
                                        infoTv.setVisibility(View.VISIBLE);
                                        assignBadgeToUser(emaildb, donationType);
                                        new CountDownTimer(3000, 1000) {
                                            public void onTick(long millisUntilFinished) {
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
                                        Helper.showToast(Donation_req_hosp.this,"Something went wrong!");
                                        Log.e("Donation_req_hosp", "Error updating donation status: " + e.getMessage());
                                    }
                                });
            }
        });
        Button absentButton = cardView.findViewById(R.id.absentButton);
        absentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.child("donation_status").setValue("ABSENT")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Helper.showToast( Donation_req_hosp.this,"Preference updated successfully!");
                                infoTv.setText("Refreshing in 3 seconds");
                                infoTv.setVisibility(View.VISIBLE);
                                new CountDownTimer(3000, 1000) {
                                    public void onTick(long millisUntilFinished) {
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
                                Helper.showToast(Donation_req_hosp.this,"Something went wrong!");
                                Log.e("Donation_req_hosp", "Error updating donation status: " + e.getMessage());
                            }
                        });
            }
        });
        Button declineButton = cardView.findViewById(R.id.deleteRequestButton);
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.child("donation_status").setValue("DELETED")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
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
    private void assignBadgeToUser(String email, String memberType) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String sanitizedEmail = email.replace("@", "").replace(".", "");
        DatabaseReference usersRef = database.getReference().child("users");
        DatabaseReference userRef = usersRef.child(sanitizedEmail);
        DatabaseReference badgeRef = userRef.child("badge").child(memberType);
        badgeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String currentBadgeCount = dataSnapshot.getValue(String.class);
                    int updatedBadgeCount = Integer.parseInt(currentBadgeCount) + 1;

                    badgeRef.setValue(String.valueOf(updatedBadgeCount))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Helper.showToast(Donation_req_hosp.this, "Badge allotted successfully!");
                                    } else {
                                        Helper.showToast(Donation_req_hosp.this, "Failed to update badge: " + task.getException().getMessage());
                                    }
                                }
                            });
                } else {
                    badgeRef.setValue("1")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Helper.showToast(Donation_req_hosp.this, "Badge allotted successfully!");
                                    } else {
                                        Helper.showToast(Donation_req_hosp.this, "Failed to update badge: " + task.getException().getMessage());
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Helper.showToast(Donation_req_hosp.this, "Failed to fetch badge count: " + databaseError.getMessage());
            }
        });
    }
}