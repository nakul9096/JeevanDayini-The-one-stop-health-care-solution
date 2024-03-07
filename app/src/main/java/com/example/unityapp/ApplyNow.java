package com.example.unityapp;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;
public class ApplyNow extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_now);
        TextView hospitalNameTextView = findViewById(R.id.hospitalNameTextView);
        TextView userIdTextView = findViewById(R.id.userIdTextView);
        TextView aadharNumberTextView = findViewById(R.id.aadharNumberTextView);
        TextView relationTypeTextView = findViewById(R.id.relationTypeTextView);
        Button applyButton = findViewById(R.id.applyButton);
        Button goBackButton = findViewById(R.id.goBackButton);
        String hospitalName = getIntent().getStringExtra("hospitalName");
        String hospitalEmail = getIntent().getStringExtra("hospEmail");
        SharedPreferences sharedPreferences = getSharedPreferences("DonationData", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userid","");
        String aadhaarNumber = sharedPreferences.getString("aadhaarNumber","");
        String relationType = sharedPreferences.getString("relationType","");
        hospitalNameTextView.setText(hospitalName);
        userIdTextView.setText("User ID: "+userId);
        aadharNumberTextView.setText("Aadhaar Number: "+aadhaarNumber);
        relationTypeTextView.setText("Relation Type: "+relationType);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ApplyNow.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.custom_dialog_layout, null);
                CheckBox proceedCheckbox = dialogView.findViewById(R.id.proceedCheckbox);
                builder.setView(dialogView)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (proceedCheckbox.isChecked()) {
                                    SharedPreferences sharedPreferences = getSharedPreferences("DonationData", Context.MODE_PRIVATE);
                                    String userId = sharedPreferences.getString("userid", "");
                                    String aadharNumber = sharedPreferences.getString("aadhaarNumber","");
                                    String userName = sharedPreferences.getString("userName", "");
                                    String relationType = sharedPreferences.getString("relationType","");
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference hospitalsRef = database.getReference().child("hospitals");
                                    String sanitizedHospEmail = hospitalEmail.replace("@","").replace(".","");
                                    DatabaseReference hospEmailRef = hospitalsRef.child(sanitizedHospEmail);
                                    DatabaseReference donationRequestsRef = hospEmailRef.child("donation_requests");
                                    DatabaseReference userRef = donationRequestsRef.child(userId).child(aadharNumber);
                                    Map<String, Object> donationRequest = new HashMap<>();
                                    donationRequest.put("userId", userId);
                                    donationRequest.put("aadharNumber", aadharNumber);
                                    donationRequest.put("userName", userName);
                                    donationRequest.put("relationType", relationType);
                                    userRef.setValue(donationRequest);
                                    Toast.makeText(ApplyNow.this, "Donation request submitted successfuly!",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ApplyNow.this, Session.class);
                                    startActivity(intent);
                                    finishAffinity();
                                } else {
                                    Toast.makeText(ApplyNow.this,"Please check the checkbox to proceed!",Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss(); // Dismiss the dialog if canceled
                            }
                        })
                        .show();
            }
        });
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}