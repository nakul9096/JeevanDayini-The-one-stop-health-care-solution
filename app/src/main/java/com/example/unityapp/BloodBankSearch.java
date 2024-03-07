package com.example.unityapp;

import android.content.Intent;
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

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BloodBankSearch extends AppCompatActivity {
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
        setContentView(R.layout.activity_blood_bank_search);
        namesLayout2 = findViewById(R.id.namesLayout2);
        progressBar = findViewById(R.id.loadingProgressBar);
        findButton = findViewById(R.id.findButton);
        stateSpinner = findViewById(R.id.stateSpinner);
        citySpinner = findViewById(R.id.citySpinner);
        t1 = findViewById(R.id.loadingMessage);
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
                DatabaseReference usersRef = database.getReference("hospitals");
                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            namesLayout2.removeAllViews();
                            progressBar.setVisibility(View.GONE);
                            String selectedCity = citySpinner.getSelectedItem().toString();
                            boolean dataFound = false;
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                if (!selectedCity.equals("select state first!")) {
                                    String Name = userSnapshot.child("name").getValue(String.class);
                                    String Email = userSnapshot.child("email").getValue(String.class);
                                    String Type = userSnapshot.child("type").getValue(String.class);
                                    String Address = userSnapshot.child("address").getValue(String.class);
                                    String State = userSnapshot.child("state").getValue(String.class);
                                    String City = userSnapshot.child("city").getValue(String.class);
                                    String img1 = userSnapshot.child("img1").getValue(String.class);
                                    String img2 = userSnapshot.child("img2").getValue(String.class);
                                    String mapurl = userSnapshot.child("mapurl").getValue(String.class);
                                    Log.d("FindClinics", "img1 from database: " + img1);
                                    String location = City + ", " + State;
                                    if (Name != null && City != null && City.equals(selectedCity)) {
                                        createNameCard(Name, Email, Type, Address, location, img1, img2, mapurl);
                                        dataFound = true;
                                    }
                                } else {
                                    Toast.makeText(BloodBankSearch.this, "Please select a city", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                            if (!dataFound) {
                                t1.setVisibility(View.VISIBLE);
                            } else {
                                t1.setVisibility(View.GONE);
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Debug3", "Database error: " + databaseError.getMessage(), databaseError.toException());
                        Toast.makeText(BloodBankSearch.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
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
                cityOptions = new String[0]; // Empty array
                break;
        }
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityOptions);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);
    }

    private void createNameCard(String Name, String Email, String Type, String Address, String location, String imageUrl, String imageUrl2, String mapurl) {
        Log.d("FindClinics", "Image URL from database: " + imageUrl);
        CardView nameCard = (CardView) LayoutInflater.from(this).inflate(R.layout.activity_hopsital_card, namesLayout2, false);
        ImageView previewImageView = nameCard.findViewById(R.id.previewCircularFrame);
        ImageView previewImageView2 = nameCard.findViewById(R.id.previewCircularFrame2);
        Glide.with(BloodBankSearch.this)
                .load(imageUrl)
                .circleCrop()
                .placeholder(R.drawable.circle_mask) // Use the circular mask as a placeholder
                .into(previewImageView);
        Glide.with(BloodBankSearch.this)
                .load(imageUrl2)
                .circleCrop()
                .placeholder(R.drawable.circle_mask) // Use the circular mask as a placeholder
                .into(previewImageView2);
        TextView nameTextView = nameCard.findViewById(R.id.hospitalNameTextView);
        nameTextView.setText(Name);
        Button bookmarkButton = nameCard.findViewById(R.id.bookmarkButton);
        bookmarkButton.setTag(Email);
        Button mapButton = nameCard.findViewById(R.id.mapButton);
        mapButton.setTag(mapurl);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Map" button click here
                String mapUrl = (String) v.getTag();
                openMap(mapUrl);
            }
        });
        TextView typeTextView = nameCard.findViewById(R.id.hospitalTypeTextView);
        typeTextView.setText(Type);
        TextView addressTextView = nameCard.findViewById(R.id.hospitalAddressTextView);
        addressTextView.setText(Address);
        TextView locationTextView = nameCard.findViewById(R.id.hospitalLocationTextView);
        locationTextView.setText(location);
        namesLayout2.addView(nameCard);
        viewmore = nameCard.findViewById(R.id.bookAppointmentButton);
        viewmore.setTag(Email);
        viewmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedUserName = (String) v.getTag(); // Get the user name from the tag
                Intent intent = new Intent(BloodBankSearch.this, ViewMore.class);
                intent.putExtra("selectedUser", selectedUserName); // Pass the selected user's name as an extra
                startActivity(intent);
            }
        });
    }

    private void openMap(String mapUrl) {
        if (mapUrl != null && !mapUrl.isEmpty()) {
            // Create an Intent with ACTION_VIEW and the map URL
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl));

            // Check if there's an app available to handle the Intent
            if (intent.resolveActivity(getPackageManager()) != null) {
                // Open the map URL
                startActivity(intent);
            } else {
                Toast.makeText(this, "No app available to handle the map request", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Map URL is not available", Toast.LENGTH_SHORT).show();
        }
    }
}