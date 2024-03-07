package com.example.unityapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                updateCitySpinner(position);
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
                                        if(intent.getStringExtra("requestType").equals("blooddonation"))
                                        {
                                            if(bloodDonationField != null && bloodDonationField.equals("Yes")) {
                                                createNameCard2(name, email, type, address, state, city, img1, img2, mapUrl);
                                            }
                                        }else {
                                            createNameCard(name, email, type, address, state, city, img1, img2, mapUrl);
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
                    Toast.makeText(FindClinics.this, "Please select a city", Toast.LENGTH_SHORT).show();
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
    private void updateCitySpinner(int statePosition) {
        String[] cityOptions;
        switch (statePosition) {
            case 0:
                cityOptions = getResources().getStringArray(R.array.selectoptions);
                break;
            case 1:
                cityOptions = getResources().getStringArray(R.array.AndamanNicobar);
                break;
            case 2:
                cityOptions = getResources().getStringArray(R.array.Andhrapradesh);
                break;
            case 3:
                cityOptions = getResources().getStringArray(R.array.Arunachalpradesh);
                break;
            case 4:
                cityOptions = getResources().getStringArray(R.array.Assam);
                break;
            case 5:
                cityOptions = getResources().getStringArray(R.array.Bihar);
                break;
            case 6:
                cityOptions = getResources().getStringArray(R.array.Chandigarh);
                break;
            case 7:
                cityOptions = getResources().getStringArray(R.array.Chattisgarh);
                break;
            case 8:
                cityOptions = getResources().getStringArray(R.array.Dadra_Nagar_Haveli_Daman_Diu);
                break;
            case 9:
                cityOptions = getResources().getStringArray(R.array.Delhi_NCR);
                break;
            case 10:
                cityOptions = getResources().getStringArray(R.array.Goa);
                break;
            case 11:
                cityOptions = getResources().getStringArray(R.array.Gujarat);
                break;
            case 12:
                cityOptions = getResources().getStringArray(R.array.Haryana);
                break;
            case 13:
                cityOptions = getResources().getStringArray(R.array.Himachal_Pradesh);
                break;
            case 14:
                cityOptions = getResources().getStringArray(R.array.Jammu_and_Kashmir);
                break;
            case 15:
                cityOptions = getResources().getStringArray(R.array.Jharkhand);
                break;
            case 16:
                cityOptions = getResources().getStringArray(R.array.Karnataka);
                break;
            case 17:
                cityOptions = getResources().getStringArray(R.array.Kerala);
                break;
            case 18:
                cityOptions = getResources().getStringArray(R.array.Ladakh);
                break;
            case 19:
                cityOptions = getResources().getStringArray(R.array.Madhya_Pradesh);
                break;
            case 20:
                cityOptions = getResources().getStringArray(R.array.Maharashtra);
                break;
            case 21:
                cityOptions = getResources().getStringArray(R.array.Manipur);
                break;
            case 22:
                cityOptions = getResources().getStringArray(R.array.Meghalaya);
                break;
            case 23:
                cityOptions = getResources().getStringArray(R.array.Mizoram);
                break;
            case 24:
                cityOptions = getResources().getStringArray(R.array.Nagaland);
                break;
            case 25:
                cityOptions = getResources().getStringArray(R.array.Odisha);
                break;
            case 26:
                cityOptions = getResources().getStringArray(R.array.Puducherry);
                break;
            case 27:
                cityOptions = getResources().getStringArray(R.array.Punjab);
                break;
            case 28:
                cityOptions = getResources().getStringArray(R.array.Rajasthan);
                break;
            case 29:
                cityOptions = getResources().getStringArray(R.array.Sikkim);
                break;
            case 30:
                cityOptions = getResources().getStringArray(R.array.Tamil_Nadu);
                break;
            case 31:
                cityOptions = getResources().getStringArray(R.array.Telangana);
                break;
            case 32:
                cityOptions = getResources().getStringArray(R.array.Tripura);
                break;
            case 33:
                cityOptions = getResources().getStringArray(R.array.Uttar_Pradesh);
                break;
            case 34:
                cityOptions = getResources().getStringArray(R.array.Uttarakhand);
                break;
            case 35:
                cityOptions = getResources().getStringArray(R.array.West_Bengal);
                break;
            default:
                cityOptions = new String[0];
                break;
        }
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityOptions);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);
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