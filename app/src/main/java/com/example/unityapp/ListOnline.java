package com.example.unityapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ListOnline extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private TextView textViewClinicName, textViewTimings, textViewMapUrl, textViewBloodDonation, textViewOther;
    private Button btnGoLive;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_online);
        textViewClinicName = findViewById(R.id.textViewClinicNameValue);
        textViewTimings = findViewById(R.id.textViewTimingsValue);
        textViewMapUrl = findViewById(R.id.textViewMapUrlValue);
        textViewOther = findViewById(R.id.textViewOtherValue);
        textViewBloodDonation = findViewById(R.id.textViewBloodDonationValue);
        btnGoLive = findViewById(R.id.btnGoLive);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Data...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        fetchData();
    }

    private void fetchData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            String encodedEmail = userEmail.replace("@", "").replace(".", "");
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("hospitals").child(encodedEmail);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    progressDialog.dismiss();
                    if (dataSnapshot.exists()) {
                        updateUI(dataSnapshot);
                    } else {
                        updateUI(dataSnapshot);
                        Log.e("ListOnline", "Data not found");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    progressDialog.dismiss();
                    Toast.makeText(ListOnline.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI(DataSnapshot dataSnapshot) {
        int found1 = 0, found2 = 0, found3 = 0, found4 = 0, found5 = 0;
        if (dataSnapshot.hasChild("status")) {
            btnGoLive.setText("Make clinic offline");
            btnGoLive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    makeClinicOffline();
                }
            });
        } else {
            btnGoLive.setText("Go Live");
            btnGoLive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    copyDataFromAdminToHospitals();
                    finish();
                }
            });
        }
        if (dataSnapshot.child("name").exists()) {
            found1 = 1;
            setFieldStatus(textViewClinicName, "Clinic Name", "name", R.color.green, R.color.red);
        } else {
            textViewClinicName.setTextColor(getResources().getColor(R.color.red));
            textViewClinicName.setText("NOT SET");
        }
        DataSnapshot timingsSnapshot = dataSnapshot.child("timings");
        if (timingsSnapshot.exists()) {
            // Check if "timings" has exactly 7 fields
            if (timingsSnapshot.getChildrenCount() == 14) {
                found2 = 1;
                setFieldStatus(textViewTimings, "Timings", "timings", R.color.green, R.color.red);
            } else {
                textViewTimings.setTextColor(getResources().getColor(R.color.red));
                textViewTimings.setText("NOT SET - All day timings required!");
            }
        } else {
            textViewTimings.setTextColor(getResources().getColor(R.color.red));
            textViewTimings.setText("NOT SET");
        }
        if (dataSnapshot.child("mapUrl").exists()) {
            found3 = 1;
            setFieldStatus(textViewMapUrl, "Map URL", "mapUrl", R.color.green, R.color.red);
        } else {
            textViewMapUrl.setTextColor(getResources().getColor(R.color.red));
            textViewMapUrl.setText("NOT SET");
        }
        if (dataSnapshot.child("bdf").exists()) {
            found4 = 1;
            setFieldStatus(textViewBloodDonation, "Blood Donation Facility", "bdf", R.color.green, R.color.red);
        } else {
            textViewBloodDonation.setTextColor(getResources().getColor(R.color.red));
            textViewBloodDonation.setText("NOT SET");
        }
        if (dataSnapshot.child("address").exists()) {
            found5 = 1;
            setFieldStatus(textViewOther, "Other details", "address", R.color.green, R.color.red);
        } else {
            textViewOther.setTextColor(getResources().getColor(R.color.red));
            textViewOther.setText("NOT SET");
        }
        btnGoLive.setEnabled(found1 == 1 && found2 == 1 && found3 == 1 && found4 == 1 && found5 == 1);
    }

    private void setFieldStatus(TextView textView, String fieldName, String databaseField, int colorTrue, int colorFalse) {
        if (textView != null) {
            textView.setText(fieldName + " is Set " + textView.getText().toString());
            textView.setTextColor(getResources().getColor(colorTrue));
        }
    }

    private void copyDataFromAdminToHospitals() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            String encodedEmail = userEmail.replace("@", "").replace(".", "");
            DatabaseReference adminReference = FirebaseDatabase.getInstance().getReference("Admin").child(encodedEmail);
            DatabaseReference hospitalsReference = FirebaseDatabase.getInstance().getReference("hospitals").child(encodedEmail);
            adminReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot fieldSnapshot : dataSnapshot.getChildren()) {
                            String fieldName = fieldSnapshot.getKey();
                            Object fieldValue = fieldSnapshot.getValue();
                            hospitalsReference.child(fieldName).setValue(fieldValue);
                        }
                        hospitalsReference.child("status").setValue("Active");
                        hospitalsReference.child("booked_slots").child("email").setValue(userEmail);
                        Toast.makeText(ListOnline.this, "To apply changes! restart the app.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(ListOnline.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(ListOnline.this, "No user is currently signed in", Toast.LENGTH_SHORT).show();
        }
    }

    private void makeClinicOffline() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            String encodedEmail = userEmail.replace("@", "").replace(".", "");
            DatabaseReference hospitalsReference = FirebaseDatabase.getInstance().getReference("hospitals").child(encodedEmail);
            hospitalsReference.child("status").removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        Toast.makeText(ListOnline.this, "To apply changes! restart the app.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ListOnline.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(ListOnline.this, "No user is currently signed in", Toast.LENGTH_SHORT).show();
        }
    }
}