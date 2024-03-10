package com.example.unityapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FindClinics extends AppCompatActivity {
    private LinearLayout namesLayout2;
    private ProgressBar progressBar;
    private FirebaseDatabase database;
    private Button findButton;
    private Spinner stateSpinner, citySpinner;
    private TextView t1;
    private Button viewmore;
    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_find_clinics);
        namesLayout2 = findViewById(R.id.namesLayout2);
        progressBar = findViewById(R.id.loadingProgressBar);
        findButton = findViewById(R.id.findButton);
        stateSpinner = findViewById(R.id.stateSpinner);
        citySpinner = findViewById(R.id.citySpinner);
        t1 = findViewById(R.id.loadingMessage);
        ImageView backArrowImageView = findViewById(R.id.backArrowImageView);
        backArrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        database = FirebaseDatabase.getInstance();
        String[] stateOptions = getResources().getStringArray(R.array.states);
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, stateOptions);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);
        stateSpinner.setSelection(0);
        final CityUtils cityUtils = new CityUtils();
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String[] cityOptions = cityUtils.updateCitySpinner(FindClinics.this, position);
                ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(FindClinics.this, android.R.layout.simple_spinner_item, cityOptions);
                cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                citySpinner.setAdapter(cityAdapter);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String selectedCity = citySpinner.getSelectedItem().toString();
                if (!selectedCity.equals("select state first!")) {
                    DatabaseReference adminRef = database.getReference("hospitals");
                    adminRef.orderByChild("selectedCity").equalTo(selectedCity).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                namesLayout2.removeAllViews();
                                progressBar.setVisibility(View.GONE);
                                boolean dataFound = false;
                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                    String status = userSnapshot.child("status").getValue(String.class);
                                    if (status != null && status.equals("Active")) {
                                        String email = userSnapshot.child("email").getValue(String.class);
                                        String type = userSnapshot.child("adminType").getValue(String.class);
                                        String address = userSnapshot.child("address").getValue(String.class);
                                        String state = userSnapshot.child("selectedState").getValue(String.class);
                                        String city = userSnapshot.child("selectedCity").getValue(String.class);
                                        String location = "Location of hospital";
                                        String name = userSnapshot.child("name").getValue(String.class);
                                        String mapUrl = userSnapshot.child("mapUrl").getValue(String.class);
                                        String img1 = userSnapshot.child("imageUrlHorizontal1").getValue(String.class);
                                        String img2 = userSnapshot.child("imageUrlHorizontal2").getValue(String.class);
                                        String bloodDonationField = userSnapshot.child("bdf").getValue(String.class);
                                        Intent intent = getIntent();
                                        if(intent!=null && intent.getStringExtra("requestType").equals("findclinics"))
                                        {
                                            createNameCard(name, email, type, address, state, city, img1, img2, mapUrl);
                                        }else if(intent != null && intent.getStringExtra("requestType").equals("blooddonation")) {
                                            if(bloodDonationField != null && bloodDonationField.equals("Yes")) {
                                                createNameCard2(name, email, type, address, state, city, img1, img2, mapUrl);
                                            }
                                        }
                                        dataFound = true;
                                    }
                                }
                                if (!dataFound) {
                                    t1.setVisibility(View.VISIBLE);
                                } else {
                                    t1.setVisibility(View.GONE);
                                }
                            } else {
                                progressBar.setVisibility(View.GONE);
                                t1.setVisibility(View.VISIBLE);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("Debug3", "Database error: " + databaseError.getMessage(), databaseError.toException());
                            Toast.makeText(FindClinics.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                } else {
                    Helper.showToast(FindClinics.this, "Please select city first!");
                }
            }
        });
    }
    private void createNameCard2(String Name, String Email, String Type, String Address, String state, String city, String imageUrl, String imageUrl2, String mapurl) {
        CardView nameCard = (CardView) LayoutInflater.from(this).inflate(R.layout.donation_card, namesLayout2, false);
        ImageView previewImageView = nameCard.findViewById(R.id.imageView1);
        ImageView previewImageView2 = nameCard.findViewById(R.id.imageView2);
        ImageView previewImageView3 = nameCard.findViewById(R.id.imageView3);
        ImageView previewImageView4 = nameCard.findViewById(R.id.imageView4);
        Button buttonDonate = nameCard.findViewById(R.id.donateBtn);
        Glide.with(FindClinics.this)
                .load(imageUrl)
                .circleCrop()
                .placeholder(R.drawable.circle_mask)
                .into(previewImageView);
        Glide.with(FindClinics.this)
                .load(imageUrl2)
                .circleCrop()
                .placeholder(R.drawable.circle_mask)
                .into(previewImageView2);
        Glide.with(FindClinics.this)
                .load(imageUrl)
                .circleCrop()
                .placeholder(R.drawable.circle_mask)
                .into(previewImageView3);
        Glide.with(FindClinics.this)
                .load(imageUrl2)
                .circleCrop()
                .placeholder(R.drawable.circle_mask)
                .into(previewImageView4);
        TextView nameTextView = nameCard.findViewById(R.id.bloodbankNameTextView);
        nameTextView.setText(Name);
        buttonDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FindClinics.this, ApplyNow.class);
                intent.putExtra("hospitalName", Name);
                intent.putExtra("hospEmail",Email);
                startActivity(intent);
            }
        });
        namesLayout2.addView(nameCard);
    }
    private void createNameCard(String Name, String Email, String Type, String Address, String state, String city, String imageUrl, String imageUrl2, String mapurl) {
        Log.d("FindClinics", "Image URL from database: " + imageUrl);
        CardView nameCard = (CardView) LayoutInflater.from(this).inflate(R.layout.activity_hopsital_card, namesLayout2, false);
        ImageView previewImageView = nameCard.findViewById(R.id.previewCircularFrame);
        ImageView previewImageView2 = nameCard.findViewById(R.id.previewCircularFrame2);
        Glide.with(FindClinics.this)
                .load(imageUrl)
                .circleCrop()
                .placeholder(R.drawable.circle_mask)
                .into(previewImageView);
        Glide.with(FindClinics.this)
                .load(imageUrl2)
                .circleCrop()
                .placeholder(R.drawable.circle_mask)
                .into(previewImageView2);
        TextView nameTextView = nameCard.findViewById(R.id.hospitalNameTextView);
        nameTextView.setText(Name);
        Button bookmarkButton = nameCard.findViewById(R.id.bookmarkButton);
        bookmarkButton.setTag(Email);
        boolean isBookmarked = checkIfBookmarked(Email);
        updateBookmarkButton(bookmarkButton, isBookmarked, Name);
        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isBookmarked = toggleBookmarkStatus(Email);
                updateBookmarkButton((Button) v, isBookmarked, Name);
            }
        });
        Button mapButton = nameCard.findViewById(R.id.mapButton);
        mapButton.setTag(mapurl);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mapUrl = (String) v.getTag();
                openMap(mapUrl);
            }
        });
        TextView typeTextView = nameCard.findViewById(R.id.hospitalTypeTextView);
        typeTextView.setText(Type);
        TextView addressTextView = nameCard.findViewById(R.id.hospitalAddressTextView);
        addressTextView.setText(Address);
        TextView locationTextView = nameCard.findViewById(R.id.hospitalLocationTextView);
        String location = state + ", " + city;
        locationTextView.setText(location);
        namesLayout2.addView(nameCard);
        viewmore = nameCard.findViewById(R.id.bookAppointmentButton);
        viewmore.setTag(Email);
        viewmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedUserName = (String) v.getTag();
                Intent intent = new Intent(FindClinics.this, ViewMore.class);
                intent.putExtra("selectedUser", selectedUserName);
                startActivity(intent);
            }
        });
    }
    private boolean checkIfBookmarked(String email) {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        return preferences.getBoolean(email, false);
    }
    private boolean toggleBookmarkStatus(String email) {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean isBookmarked = preferences.getBoolean(email, false);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(email, !isBookmarked);
        editor.apply();
        return !isBookmarked;
    }

    private void updateBookmarkButton(Button bookmarkButton, boolean isBookmarked, String nameh) {
        int buttonColor = isBookmarked ? ContextCompat.getColor(this, android.R.color.darker_gray) : Color.RED;
        bookmarkButton.setBackgroundTintList(ColorStateList.valueOf(buttonColor));
        bookmarkButton.setText(isBookmarked ? "Bookmarked" : "Bookmark");
    }

    private void openMap(String mapUrl) {
        if (mapUrl != null && !mapUrl.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "No app available to handle the map request", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Map URL is not available", Toast.LENGTH_SHORT).show();
        }
    }
}