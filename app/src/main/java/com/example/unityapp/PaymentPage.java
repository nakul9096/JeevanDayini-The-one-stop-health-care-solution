package com.example.unityapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PaymentPage extends AppCompatActivity implements PaymentResultListener {
    private static final int RAZOR_PAY_REQUEST_CODE = 123;
    private static final String PREFS_NAME = "MyPrefs";
    SharedPreferences sharedPreferencesreceipt;
    SharedPreferences.Editor editor2;
    private TextView paymentModePromptTextView, baseAmountTextView, gstBreakdownTextView, cgstTextView, sgstTextView,
            convenienceFeeTextView, totalAmountTextView, amountAtClinicTitleTextView;
    private TextView paymentModePromptTextView1, baseAmountTextView1, gstBreakdownTextView1, cgstTextView1, sgstTextView1, amountAtClinicTitleTextView1, convenienceFeeTextView1, totalAmountTextView1;
    private RadioGroup paymentRadioGroup;
    private Button payAndBookButton;
    private double totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paymentpage);
        sharedPreferencesreceipt = getSharedPreferences("PaymentInfo", Context.MODE_PRIVATE);
        editor2 = sharedPreferencesreceipt.edit();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String baseAmount = sharedPreferences.getString("base_amount", "");
        editor2.putString("baseAmount", baseAmount);
        payAndBookButton = findViewById(R.id.payAndBookButton);
        paymentModePromptTextView = findViewById(R.id.paymentModePromptTextView);
        baseAmountTextView = findViewById(R.id.baseAmountValueTextView);
        gstBreakdownTextView = findViewById(R.id.gstValueTextView);
        cgstTextView = findViewById(R.id.cgstValueTextView);
        sgstTextView = findViewById(R.id.sgstValueTextView);
        convenienceFeeTextView = findViewById(R.id.convenienceFeeValueTextView);
        totalAmountTextView = findViewById(R.id.totalAmountValueTextView);
        paymentRadioGroup = findViewById(R.id.paymentRadioGroup);
        amountAtClinicTitleTextView = findViewById(R.id.amountAtClinicValueTextView);
        baseAmountTextView1 = findViewById(R.id.baseAmountTextView);
        gstBreakdownTextView1 = findViewById(R.id.gstBreakdownTextView);
        cgstTextView1 = findViewById(R.id.cgstTextView);
        sgstTextView1 = findViewById(R.id.sgstTextView);
        convenienceFeeTextView1 = findViewById(R.id.convenienceFeeTextView);
        amountAtClinicTitleTextView1 = findViewById(R.id.amountAtClinicTitleTextView);
        totalAmountTextView1 = findViewById(R.id.totalAmountTextView);
        paymentRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                String paymentMode = radioButton.getText().toString();
                if (paymentMode.equals("Pay on Clinic")) {
                    handlePayOnClinicSelection();
                    payAndBookButton.setEnabled(true);
                } else if (paymentMode.equals("Pay Now (Online)")) {
                    handlePayNowOnlineSelection();
                    payAndBookButton.setEnabled(true);
                }
            }
        });
    }

    private void handlePayOnClinicSelection() {
        // Set visibility
        paymentModePromptTextView.setVisibility(View.GONE);
        baseAmountTextView.setVisibility(View.VISIBLE);
        gstBreakdownTextView.setVisibility(View.VISIBLE);
        cgstTextView.setVisibility(View.VISIBLE);
        sgstTextView.setVisibility(View.VISIBLE);
        convenienceFeeTextView.setVisibility(View.VISIBLE);
        totalAmountTextView.setVisibility(View.VISIBLE);
        baseAmountTextView1.setVisibility(View.VISIBLE);
        gstBreakdownTextView1.setVisibility(View.VISIBLE);
        cgstTextView1.setVisibility(View.VISIBLE);
        sgstTextView1.setVisibility(View.VISIBLE);
        convenienceFeeTextView1.setVisibility(View.VISIBLE);
        totalAmountTextView1.setVisibility(View.VISIBLE);
        amountAtClinicTitleTextView1.setVisibility(View.VISIBLE);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String baseAmount = sharedPreferences.getString("base_amount", "");
        double BaseAmount = Double.parseDouble(baseAmount);
        // Set values for Pay on Clinic
        baseAmountTextView.setText("₹5");
        gstBreakdownTextView.setText("₹0.90");
        cgstTextView.setText("₹0.45");
        sgstTextView.setText("₹0.45");
        convenienceFeeTextView.setText("₹0.10");
        convenienceFeeTextView.setPaintFlags(convenienceFeeTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        totalAmountTextView.setText("₹5.90");
        amountAtClinicTitleTextView.setText("₹" + baseAmount);
        double totalpayableamount = 6;
        double gstAmount = 0.90;
        double platformfee = 5;
        payAndBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mode = "offline";
                startRazorpayPayment(totalpayableamount, gstAmount, platformfee, BaseAmount, mode);
            }
        });
    }

    private void handlePayNowOnlineSelection() {
        paymentModePromptTextView.setVisibility(View.GONE);
        baseAmountTextView.setVisibility(View.VISIBLE);
        gstBreakdownTextView.setVisibility(View.VISIBLE);
        cgstTextView.setVisibility(View.VISIBLE);
        sgstTextView.setVisibility(View.VISIBLE);
        convenienceFeeTextView.setVisibility(View.VISIBLE);
        totalAmountTextView.setVisibility(View.VISIBLE);
        baseAmountTextView1.setVisibility(View.VISIBLE);
        gstBreakdownTextView1.setVisibility(View.VISIBLE);
        cgstTextView1.setVisibility(View.VISIBLE);
        sgstTextView1.setVisibility(View.VISIBLE);
        convenienceFeeTextView1.setVisibility(View.VISIBLE);
        totalAmountTextView1.setVisibility(View.VISIBLE);
        amountAtClinicTitleTextView1.setVisibility(View.VISIBLE);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String baseAmount = sharedPreferences.getString("base_amount", "");
        String platformFee = "5.00";
        if (!baseAmount.isEmpty()) {
            double baseAmountValue = Double.parseDouble(baseAmount);
            double platformFeeValue = Double.parseDouble(platformFee);
            double gst = (baseAmountValue + platformFeeValue) * 0.18;
            String gstAmount = String.valueOf(gst);
            double cgst = gst / 2;
            double sgst = gst / 2;
            double totalAmount = baseAmountValue + platformFeeValue + gst;
            String total = String.valueOf(totalAmount);
            baseAmountTextView.setText("₹" + baseAmount);
            gstBreakdownTextView.setText("₹" + String.format("%.2f", gst));
            cgstTextView.setText("₹" + String.format("%.2f", cgst));
            sgstTextView.setText("₹" + String.format("%.2f", sgst));
            convenienceFeeTextView1.setText("Platform fee");
            convenienceFeeTextView.setText("₹" + platformFee);
            totalAmountTextView.setText("₹" + String.format("%.2f", totalAmount));
            double totalpayableamount = Double.parseDouble(String.format("%.2f", totalAmount));
            amountAtClinicTitleTextView.setText("N.A");
            // Inside handlePayNowOnlineSelection() method
            payAndBookButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mode = "online";
                    startRazorpayPayment(totalpayableamount, gst, platformFeeValue, baseAmountValue, mode);
                }
            });
        } else {
            Toast.makeText(this, "Base amount not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void startRazorpayPayment(double totalAmount, double gstAmount, double platformfee, double baseAmount, String mode) {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_FY7GOm27Z0Zc8P");
        try {
            editor2.putString("totalAmount", String.valueOf(totalAmount));
            editor2.putString("baseAmount", String.valueOf(baseAmount));
            editor2.putString("platformFee", String.valueOf(platformfee));
            editor2.putString("gstAmount", String.valueOf(gstAmount));
            editor2.putString("mode", mode);
            editor2.apply();
            JSONObject options = new JSONObject();
            options.put("name", "Jeevandayini ");
            options.put("description", "Payment for slot booking");
            options.put("currency", "INR");
            options.put("amount", totalAmount * 100);
            checkout.open(this, options);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handlePaymentSuccess(String transactionId) {
        SharedPreferences profileDataSharedPreferences = getSharedPreferences("ProfileData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = getSharedPreferences("PaymentInfo", Context.MODE_PRIVATE).edit();
        String userID = profileDataSharedPreferences.getString("userid", "");
        if (userID.isEmpty()) {
            Log.e("PaymentPage", "User ID not found in SharedPreferences");
            return;
        }
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String hospitalEmail = sharedPreferences.getString("hospital_email", "");
        Log.d("PaymentPage", "Hospital email: " + hospitalEmail);
        SharedPreferences userDataSharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String fullName = userDataSharedPreferences.getString("fullName", "");
        String phoneNumber = userDataSharedPreferences.getString("phone", "");
        String email = userDataSharedPreferences.getString("email", "");
        String token = userDataSharedPreferences.getString("tokenNumber", "");
        String date = userDataSharedPreferences.getString("date", "");
        editor2.putString("fullName", fullName); // Replace fullName with the actual value you want to save
        editor2.putString("phoneNumber", phoneNumber); // Replace phoneNumber with the actual value you want to save
        editor2.putString("email", email); // Replace email with the actual value you want to save
        editor2.putString("transactionId", transactionId); // Replace transactionId with the actual value you want to save
        editor2.putString("tokenNumber", token); // Replace tokenNumber with the actual value you want to save
        editor2.putString("date", date);
        editor.putString("fullName", fullName);
        editor.putString("phoneNumber", phoneNumber);
        editor.putString("email", email);
        editor.putString("tokenNumber", token);
        editor.putString("transactionId", transactionId);
        editor.putString("date", date);
        editor.apply();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference bookedSlotsRef = database.getReference().child("hospitals").child(hospitalEmail).child("booked_slots").child(userID); // Storing under user ID
        Map<String, Object> userData = new HashMap<>();
        userData.put("fullName", fullName);
        userData.put("phoneNumber", phoneNumber);
        userData.put("email", email);
        userData.put("tokenNumber", token);
        userData.put("date", date);
        bookedSlotsRef.setValue(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Payment successful! Slot booked", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("PaymentPage", "Failed to store data: " + e.getMessage());
                    Toast.makeText(this, "Failed to store data. Please try again later.", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        handlePaymentSuccess(razorpayPaymentID);
        Intent intent = new Intent(PaymentPage.this, PaymentSuccess.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onPaymentError(int code, String response) {
        Toast.makeText(this, "Payment unsuccessful! Please try again later.", Toast.LENGTH_SHORT).show();
    }
}