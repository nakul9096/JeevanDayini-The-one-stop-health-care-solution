package com.example.unityapp;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class Donation_req_hosp extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private LinearLayout cardContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_req_hosp);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }
        String email = currentUser.getEmail();
        String sanitizedEmail = sanitizeEmail(email);
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("hospitals").child(sanitizedEmail).child("donation_requests");
        cardContainer = findViewById(R.id.namesLayout80);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cardContainer.removeAllViews();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String userId = childSnapshot.getKey();
                        String donorName = childSnapshot.child("userName").getValue(String.class);
                        String donationType = childSnapshot.child("relationType").getValue(String.class);
                        String aadharNumber = childSnapshot.child("aadharNumber").getValue(String.class);
                        createCard(userId, donorName, donationType, aadharNumber, childSnapshot);
                    }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void createCard(String userId, String donorName, String donationType, String aadharNumber, DataSnapshot childSnapshot) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.donation_req_card, null);
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
                        childSnapshot.getRef().child("donation_status").setValue("donated")
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Donation_req_hosp.this, "Preference updated successfully!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Handle error
                                        Toast.makeText(Donation_req_hosp.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                        Log.e("Donation_req_hosp", "Error updating donation status: " + e.getMessage());
                                    }
                                });
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