package com.example.unityapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class Premium extends AppCompatActivity implements PaymentResultListener {
    private Button paybtn1, paybtn2;
    private String planType; // Variable to store the current plan
    private Button getPremiumButtonSilver;
    private Button freeUserButton;
    private Button getPremiumButtonGold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);

        Checkout.preload(getApplicationContext());

        paybtn1 = findViewById(R.id.btnPremiumsilver);
        paybtn2 = findViewById(R.id.btnPremiumgold);
        getPremiumButtonSilver = findViewById(R.id.btnPremiumsilver);
        getPremiumButtonGold = findViewById(R.id.btnPremiumgold);
        freeUserButton = findViewById(R.id.btnfreeuser);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = currentUser.getEmail();
        String sanitizedUserUid = userEmail.replace("@", "").replace(".", "");
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users")
                .child(sanitizedUserUid)
                .child("planType");
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // PlanType exists in the database
                    String latestPlanType = dataSnapshot.getValue(String.class);
                    updateUI(latestPlanType);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if any
            }
        });
        paybtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                planType = "Silver"; // Set the current plan
                int amount = 29900;
                startRazorpayPayment("Jeevandayini - Premium silver plan", amount);
            }
        });

        paybtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                planType = "Gold"; // Set the current plan
                int amount = 49900;
                startRazorpayPayment("Jeevandayini - Premium gold plan", amount);
            }
        });
    }

    private void startRazorpayPayment(String description, int amount) {
        Checkout checkout = new Checkout();
        checkout.setKeyID(""); // Replace with your Razorpay key

        try {
            // Set your order details
            JSONObject options = new JSONObject();
            options.put("name", "Jeevandayini");
            options.put("description", description);
            options.put("currency", "INR"); // Change to your currency code
            options.put("amount", amount);

            // Use your own callback URL or leave it empty
            options.put("prefill", new JSONObject().put("email", "nakul@gmail.com"));

            // Open Razorpay Checkout activity
            checkout.open(Premium.this, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        // Payment successful, display a toast message
        if (planType != null) {
            showToast(planType + " plan purchased successfully");
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String userEmail = currentUser.getEmail();
                String sanitizedUserUid = userEmail.replace("@", "").replace(".", "");
                DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users")
                        .child(sanitizedUserUid)
                        .child("planType");
                userReference.setValue(planType)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("Premium", "planType updated successfully " + planType);
                                Intent intent = getIntent();
                                finish(); // Finish the current activity
                                startActivity(intent);
                            } else {
                                Log.d("Premium", "plan update successfull " + planType);
                            }
                        });
            }
        }
    }

    @Override
    public void onPaymentError(int code, String description) {
        // Payment failed or was canceled by the user
        showToast("Payment failed or canceled");
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void updateUI(String planType) {
        if (planType.equals("free")) {
            // For free users, do nothing
        } else if (planType.equals("Silver")) {
            // For Silver plan users
            getPremiumButtonSilver.setBackgroundColor(Color.parseColor("#00C853"));
            getPremiumButtonSilver.setText("Active");
            freeUserButton.setVisibility(View.GONE);
        } else if (planType.equals("Gold")) {
            // For Gold plan users
            freeUserButton.setVisibility(View.GONE);
            getPremiumButtonGold.setBackgroundColor(Color.parseColor("#00C853"));
            getPremiumButtonGold.setText("Active");
        }
    }
}
