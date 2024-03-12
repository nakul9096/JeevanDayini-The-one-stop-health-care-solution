package com.example.unityapp;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
public class ApplyNow extends AppCompatActivity {
    String hospitalEmail;
    String hospitalName;
    private DatabaseReference bloodDonationRef;
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
        bloodDonationRef = FirebaseDatabase.getInstance().getReference().child("blooddonation");
        hospitalName = getIntent().getStringExtra("hospitalName");
        hospitalEmail = getIntent().getStringExtra("hospEmail");
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
                                Map<String, Object> userDataUpdates = new HashMap<>();
                                userDataUpdates.put("donation_location", hospitalEmail);
                                userDataUpdates.put("hospitalName", hospitalName);
                                bloodDonationRef.child(userId).child(aadhaarNumber).updateChildren(userDataUpdates)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Helper.showToast(ApplyNow.this,"Apply successfull!");
                                                Intent intent = new Intent(ApplyNow.this,Session.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("UserData", "Failed to update data: " + e.getMessage());
                                                Helper.showToast(ApplyNow.this,"Failed to update data: ");
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
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