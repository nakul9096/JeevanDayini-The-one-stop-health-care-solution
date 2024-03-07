package com.example.unityapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ViewMore extends AppCompatActivity {
    private ProgressBar progressBar;
    private CardView cardClinic;
    private Button btnViewMap;
    private Button btnBookAppointment;
    private TextView textMonday;
    private TextView textTuesday;
    private TextView textWednesday;
    private TextView textThursday;
    private TextView textFriday;
    private TextView textSaturday;
    private TextView textSunday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_more);
        progressBar = findViewById(R.id.progressBar);
        cardClinic = findViewById(R.id.cardClinic);
        cardClinic.setVisibility(View.GONE);
        textMonday = findViewById(R.id.textMondayTimings);
        textTuesday = findViewById(R.id.textTuesdayTimings);
        textWednesday = findViewById(R.id.textWednesdayTimings);
        textThursday = findViewById(R.id.textThursdayTimings);
        textFriday = findViewById(R.id.textFridayTimings);
        textSaturday = findViewById(R.id.textSaturdayTimings);
        textSunday = findViewById(R.id.textSundayTimings);
        btnBookAppointment = findViewById(R.id.buttonBookAppointment);
        btnBookAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bookIntent = new Intent(ViewMore.this, Book.class);
                startActivity(bookIntent);
            }
        });
        btnViewMap = findViewById(R.id.buttonViewOnMap);
        btnViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedUserEmail = getIntent().getStringExtra("selectedUser");
                getMapUrlByEmail(selectedUserEmail);
            }
        });
        String selectedUserEmail = getIntent().getStringExtra("selectedUser");
        String sanitizedEmail = (selectedUserEmail != null) ? selectedUserEmail.replace("@", "").replace(".", "") : "";
        Log.d("ViewMore", "email received: " + sanitizedEmail);
        saveEmailToSharedPreferences(sanitizedEmail);
        getUserDetailsByEmail(selectedUserEmail);
    }

    private void getUserDetailsByEmail(String email) {
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("hospitals");
        Query query = usersRef.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String name = userSnapshot.child("name").getValue(String.class);
                        String email = userSnapshot.child("email").getValue(String.class);
                        String imageUrl1 = userSnapshot.child("imageUrlIdentity1").getValue(String.class);
                        String imageUrl2 = userSnapshot.child("imageUrlHorizontal1").getValue(String.class);
                        String imageUrl3 = userSnapshot.child("imageUrlHorizontal2").getValue(String.class);
                        String imageUrl4 = userSnapshot.child("imageUrlHorizontal3").getValue(String.class);
                        String state = userSnapshot.child("selectedState").getValue(String.class);
                        String city = userSnapshot.child("selectedCity").getValue(String.class);
                        String address = userSnapshot.child("address").getValue(String.class);
                        String type = userSnapshot.child("adminType").getValue(String.class);
                        DataSnapshot timingsSnapshot = userSnapshot.child("timings");
                        String mondaystart = timingsSnapshot.child("Monday_start").getValue(String.class);
                        String mondayend = timingsSnapshot.child("Monday_end").getValue(String.class);
                        String tuesdaystart = timingsSnapshot.child("Tuesday_start").getValue(String.class);
                        String tuesdayend = timingsSnapshot.child("Tuesday_end").getValue(String.class);
                        String wednesdaystart = timingsSnapshot.child("Wednesday_start").getValue(String.class);
                        String wednesdayend = timingsSnapshot.child("Wednesday_end").getValue(String.class);
                        String thursdaystart = timingsSnapshot.child("Thursday_start").getValue(String.class);
                        String thursdayend = timingsSnapshot.child("Thursday_end").getValue(String.class);
                        String fridaystart = timingsSnapshot.child("Friday_start").getValue(String.class);
                        String fridayend = timingsSnapshot.child("Friday_end").getValue(String.class);
                        String saturdaystart = timingsSnapshot.child("Saturday_start").getValue(String.class);
                        String saturdaysend = timingsSnapshot.child("Saturday_end").getValue(String.class);
                        String sundaystart = timingsSnapshot.child("Sunday_start").getValue(String.class);
                        String sundayend = timingsSnapshot.child("Sunday_end").getValue(String.class);
                        Log.d("ViewMore", "Name: " + name);
                        String location = city + ", " + state;
                        updateUI(name, type, imageUrl1, imageUrl2, imageUrl3, imageUrl4, address, location, mondaystart, mondayend, tuesdaystart, tuesdayend, wednesdaystart, wednesdayend, thursdaystart, thursdayend, fridaystart, fridayend, saturdaystart, saturdaysend, sundaystart, sundayend);
                        cardClinic.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(ViewMore.this, "error user not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ViewMore.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String formatTiming(String start, String end) {
        String[] startSplit = start.split(":");
        String[] endSplit = end.split(":");
        int startHour = Integer.parseInt(startSplit[0]);
        int endHour = Integer.parseInt(endSplit[0]);
        String startTimePeriod = (startHour < 12) ? "AM" : "PM";
        String endTimePeriod = (endHour < 12) ? "AM" : "PM";

        // Convert 24-hour to 12-hour format
        startHour = (startHour > 12) ? startHour - 12 : startHour;
        endHour = (endHour > 12) ? endHour - 12 : endHour;

        // Handle midnight (12:00 AM) and noon (12:00 PM)
        if (startHour == 0) {
            startHour = 12;
        }
        if (endHour == 0) {
            endHour = 12;
        }
        String formattedStart = String.format("%d:%s %s", startHour, startSplit[1], startTimePeriod);
        String formattedEnd = String.format("%d:%s %s", endHour, endSplit[1], endTimePeriod);

        return formattedStart + " To " + formattedEnd;
    }

    private void updateUI(String name, String type, String imageUrl1, String imageUrl2, String imageUrl3, String imageUrl4, String address, String location, String mondayStart, String mondayEnd, String tuesdayStart, String tuesdayEnd, String wednesdayStart, String wednesdayEnd, String thursdayStart, String thursdayEnd, String fridayStart, String fridayEnd, String saturdayStart, String saturdayEnd, String sundayStart, String sundayEnd) {
        TextView nameTextView = findViewById(R.id.textClinicName1);
        TextView typeTextView = findViewById(R.id.textClinictype);
        TextView addressTextView = findViewById(R.id.textClinicAddress1);
        TextView locationTextView = findViewById(R.id.textCliniclocation);
        ImageView previewImageView1 = findViewById(R.id.imagePreview1);
        ImageView previewImageView2 = findViewById(R.id.imagePreview2);
        ImageView previewImageView3 = findViewById(R.id.imagePreview3);
        ImageView previewImageView4 = findViewById(R.id.imagePreview4);
        nameTextView.setText(name);
        typeTextView.setText(type);
        addressTextView.setText(address);
        locationTextView.setText(location);
        textMonday.setText(formatTiming(mondayStart, mondayEnd));
        textTuesday.setText(formatTiming(tuesdayStart, tuesdayEnd));
        textWednesday.setText(formatTiming(wednesdayStart, wednesdayEnd));
        textThursday.setText(formatTiming(thursdayStart, thursdayEnd));
        textFriday.setText(formatTiming(fridayStart, fridayEnd));
        textSaturday.setText(formatTiming(saturdayStart, saturdayEnd));
        textSunday.setText(formatTiming(sundayStart, sundayEnd));
        Glide.with(this).load(imageUrl1).into(previewImageView1);
        Glide.with(this).load(imageUrl2).into(previewImageView2);
        Glide.with(this).load(imageUrl3).into(previewImageView3);
        Glide.with(this).load(imageUrl4).into(previewImageView4);
    }

    private void getMapUrlByEmail(String email) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("hospitals");
        Query query = usersRef.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String mapUrl = userSnapshot.child("mapurl").getValue(String.class);
                        if (mapUrl != null && !mapUrl.isEmpty()) {
                            openMap(mapUrl);
                        } else {
                            Toast.makeText(ViewMore.this, "Error: 404 Not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(ViewMore.this, "Error: Request not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewMore.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openMap(String mapUrl) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl));
        startActivity(browserIntent);
    }

    private void saveEmailToSharedPreferences(String email) {
        String encodedEmail = email.replace("@", "").replace(".", "");
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("hospital_email", encodedEmail);
        editor.apply();
    }
}