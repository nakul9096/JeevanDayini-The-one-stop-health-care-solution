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

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ViewMore extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView textRating;
    private TextView textMondayTimings, textTuesdayTimings, textWednesdayTimings, textThursdayTimings, textFridayTimings, textSaturdayTimings, textSundayTimings;
    private TextView textClinicName, textClinictype, textClinicAddress, textCliniclocation;
    private ImageView imagePreview1, imagePreview2, imagePreview3, imagePreview4;
    private Button btnViewMap, btnBookAppointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_more);

        initializeViews();
        ViewUtils.addPressEffect(btnBookAppointment);
        ViewUtils.addPressEffect(btnViewMap);
        btnBookAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = getIntent().getStringExtra("HospitalEmail");
                Log.e("ViewMore","Retrieved email: "+email);
                SharedPreferences sharedPreferences = getSharedPreferences("HospData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("HospitalEmail", email);
                editor.apply();
                Intent bookIntent = new Intent(ViewMore.this, Book.class);
                startActivity(bookIntent);
            }
        });

        btnViewMap.setOnClickListener(v -> {
            String mapUrl = getIntent().getStringExtra("mapurl");
            if (mapUrl != null && !mapUrl.isEmpty()) {
                openMap(mapUrl);
            } else {
                Toast.makeText(ViewMore.this, "Map URL not available.", Toast.LENGTH_SHORT).show();
            }
        });

        String selectedUserEmail = getIntent().getStringExtra("selectedUser");
        if (selectedUserEmail != null) {
            String sanitizedEmail = selectedUserEmail.replace("@", "").replace(".", "");
            Log.d("ViewMore", "Email received: " + selectedUserEmail);
            saveEmailToSharedPreferences(selectedUserEmail);
            getUserDetailsByEmail(selectedUserEmail);
        } else {
            Toast.makeText(this, "No user email provided.", Toast.LENGTH_SHORT).show();
            Log.e("ViewMore", "Error: No user email was passed to this activity.");
        }
    }

    // The rest of your code remains the same as the previous response...

    private void initializeViews() {
        progressBar = findViewById(R.id.progressBar);
        textRating = findViewById(R.id.textRating);
        textMondayTimings = findViewById(R.id.textMondayTimings);
        textTuesdayTimings = findViewById(R.id.textTuesdayTimings);
        textWednesdayTimings = findViewById(R.id.textWednesdayTimings);
        textThursdayTimings = findViewById(R.id.textThursdayTimings);
        textFridayTimings = findViewById(R.id.textFridayTimings);
        textSaturdayTimings = findViewById(R.id.textSaturdayTimings);
        textSundayTimings = findViewById(R.id.textSundayTimings);
        btnBookAppointment = findViewById(R.id.buttonBookAppointment);
        btnViewMap = findViewById(R.id.buttonViewOnMap);

        textClinicName = findViewById(R.id.textClinicName);
        textClinictype = findViewById(R.id.textClinictype);
        textClinicAddress = findViewById(R.id.textClinicAddress);
        textCliniclocation = findViewById(R.id.textCliniclocation);

        imagePreview1 = findViewById(R.id.imagePreview1);
        imagePreview2 = findViewById(R.id.imagePreview2);
        imagePreview3 = findViewById(R.id.imagePreview3);
        imagePreview4 = findViewById(R.id.imagePreview4);
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
                    DataSnapshot userSnapshot = dataSnapshot.getChildren().iterator().next();

                    String name = userSnapshot.child("name").getValue(String.class);
                    String type = userSnapshot.child("adminType").getValue(String.class);
                    String address = userSnapshot.child("address").getValue(String.class);
                    String state = userSnapshot.child("selectedState").getValue(String.class);
                    String city = userSnapshot.child("selectedCity").getValue(String.class);
                    String ratings = userSnapshot.child("ratings").getValue(String.class);

                    DataSnapshot timingsSnapshot = userSnapshot.child("timings");

                    String imageUrl1 = userSnapshot.child("imageUrlIdentity1").getValue(String.class);
                    String imageUrl2 = userSnapshot.child("imageUrlHorizontal1").getValue(String.class);
                    String imageUrl3 = userSnapshot.child("imageUrlHorizontal2").getValue(String.class);
                    String imageUrl4 = userSnapshot.child("imageUrlHorizontal3").getValue(String.class);

                    updateTextAndImages(name, type, address, city, state, ratings, imageUrl1, imageUrl2, imageUrl3, imageUrl4);
                    updateTimings(timingsSnapshot);
                } else {
                    Toast.makeText(ViewMore.this, "Hospital details not found.", Toast.LENGTH_SHORT).show();
                    Log.d("ViewMore", "User with email " + email + " does not exist.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ViewMore.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ViewMore", "Database error: ", databaseError.toException());
            }
        });
    }

    private String formatTiming(String start, String end) {
        if (start == null || end == null || start.isEmpty() || end.isEmpty()) {
            return "Closed";
        }
        try {
            int startHour = Integer.parseInt(start.split(":")[0]);
            int endHour = Integer.parseInt(end.split(":")[0]);
            String startMinute = start.split(":")[1];
            String endMinute = end.split(":")[1];

            String formattedStart = (startHour > 12) ? (startHour - 12) + ":" + startMinute + " PM" : startHour + ":" + startMinute + " AM";
            String formattedEnd = (endHour > 12) ? (endHour - 12) + ":" + endMinute + " PM" : endHour + ":" + endMinute + " AM";

            return formattedStart + " - " + formattedEnd;
        } catch (NumberFormatException e) {
            Log.e("ViewMore", "Error formatting time: " + e.getMessage());
            return "Invalid Time";
        }
    }

    private void updateTextAndImages(String name, String type, String address, String city, String state, String ratings, String url1, String url2, String url3, String url4) {
        textClinicName.setText(name);
        textClinictype.setText(type);
        textClinicAddress.setText(address);
        textCliniclocation.setText(city + ", " + state);

        if (ratings != null && !ratings.isEmpty()) {
            textRating.setText(ratings + " / 5.0");
        } else {
            textRating.setText("Not rated yet!");
        }

        loadImage(url1, imagePreview1);
        loadImage(url2, imagePreview2);
        loadImage(url3, imagePreview3);
        loadImage(url4, imagePreview4);
    }

    private void updateTimings(DataSnapshot timingsSnapshot) {
        textMondayTimings.setText(formatTiming(timingsSnapshot.child("Monday_start").getValue(String.class), timingsSnapshot.child("Monday_end").getValue(String.class)));
        textTuesdayTimings.setText(formatTiming(timingsSnapshot.child("Tuesday_start").getValue(String.class), timingsSnapshot.child("Tuesday_end").getValue(String.class)));
        textWednesdayTimings.setText(formatTiming(timingsSnapshot.child("Wednesday_start").getValue(String.class), timingsSnapshot.child("Wednesday_end").getValue(String.class)));
        textThursdayTimings.setText(formatTiming(timingsSnapshot.child("Thursday_start").getValue(String.class), timingsSnapshot.child("Thursday_end").getValue(String.class)));
        textFridayTimings.setText(formatTiming(timingsSnapshot.child("Friday_start").getValue(String.class), timingsSnapshot.child("Friday_end").getValue(String.class)));
        textSaturdayTimings.setText(formatTiming(timingsSnapshot.child("Saturday_start").getValue(String.class), timingsSnapshot.child("Saturday_end").getValue(String.class)));
        textSundayTimings.setText(formatTiming(timingsSnapshot.child("Sunday_start").getValue(String.class), timingsSnapshot.child("Sunday_end").getValue(String.class)));
    }

    private void loadImage(String url, ImageView imageView) {
        if (url != null && !url.isEmpty()) {
            Glide.with(this).load(url).into(imageView);
        } else {
            imageView.setImageResource(R.drawable.placeholder);
            imageView.setVisibility(View.GONE);
        }
    }

    private void openMap(String mapUrl) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl));
            startActivity(browserIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Could not open map.", Toast.LENGTH_SHORT).show();
            Log.e("ViewMore", "Failed to open map URL: " + e.getMessage());
        }
    }

    private void saveEmailToSharedPreferences(String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("hospital_email", email);
        editor.apply();
    }
}