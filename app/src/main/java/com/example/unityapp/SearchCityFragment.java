package com.example.unityapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchCityFragment extends Fragment {
    private LinearLayout namesLayout2;
    private ProgressBar progressBar;
    private FirebaseDatabase database;
    private Button findButton;
    private Spinner stateSpinner, citySpinner;
    private TextView noDataMessage;
    // The SearchView is removed as it is no longer in the layout.
    private ArrayList<Hospital> hospitalList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_city, container, false);

        // Initialize views
        namesLayout2 = view.findViewById(R.id.namesLayout2);
        progressBar = view.findViewById(R.id.loadingProgressBar);
        findButton = view.findViewById(R.id.findButton);
        stateSpinner = view.findViewById(R.id.stateSpinner);
        citySpinner = view.findViewById(R.id.citySpinner);
        noDataMessage = view.findViewById(R.id.noDataMessage);

        database = FirebaseDatabase.getInstance();

        setupSpinners();
        ViewUtils.addPressEffect(findButton);
        findButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String selectedCity = citySpinner.getSelectedItem().toString();
            fetchHospitals(selectedCity);
        });

        return view;
    }

    private void setupSpinners() {
        String[] stateOptions = getResources().getStringArray(R.array.states);
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, stateOptions);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);
        stateSpinner.setSelection(0);

        final CityUtils cityUtils = new CityUtils();
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String[] cityOptions = cityUtils.updateCitySpinner(requireContext(), position);
                ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, cityOptions);
                cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                citySpinner.setAdapter(cityAdapter);
                updateFindButtonState();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateFindButtonState();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        updateFindButtonState();
    }

    private void updateFindButtonState() {
        boolean isStateSelected = stateSpinner.getSelectedItemPosition() > 0;
        boolean isCitySelected = citySpinner.getSelectedItemPosition() > 0;
        findButton.setEnabled(isStateSelected && isCitySelected);
        findButton.setBackgroundTintList(isStateSelected && isCitySelected ?
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.find_button_color)) :
                ColorStateList.valueOf(Color.GRAY));
    }

    // The setupSearch() method is removed as the SearchView no longer exists.

    private void fetchHospitals(String city) {
        DatabaseReference adminRef = database.getReference("hospitals");
        adminRef.orderByChild("selectedCity").equalTo(city).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hospitalList.clear();
                namesLayout2.removeAllViews();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String status = userSnapshot.child("status").getValue(String.class);
                        if (status != null && status.equals("Active")) {
                            Hospital hospital = userSnapshot.getValue(Hospital.class);
                            if (hospital != null) {
                                hospital.setKey(userSnapshot.getKey());
                                hospitalList.add(hospital);
                            }
                        }
                    }
                }
                progressBar.setVisibility(View.GONE);
                displayHospitals(hospitalList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Debug3", "Database error: " + databaseError.getMessage(), databaseError.toException());
                Toast.makeText(requireContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    // The filterHospitals() method is removed as the SearchView no longer exists.

    private void displayHospitals(ArrayList<Hospital> hospitals) {
        namesLayout2.removeAllViews();
        if (hospitals.isEmpty()) {
            noDataMessage.setVisibility(View.VISIBLE);
        } else {
            noDataMessage.setVisibility(View.GONE);
            for (Hospital hospital : hospitals) {
                createCard(hospital);
            }
        }
    }

    private void createCard(Hospital hospital) {
        String requestType = requireActivity().getIntent().getStringExtra("requestType");
        int layoutResId;

        if ("blooddonation".equals(requestType) && "Yes".equals(hospital.getBdf())) {
            layoutResId = R.layout.donation_card;
        } else if ("findclinics".equals(requestType)) {
            layoutResId = R.layout.activity_hopsital_card;
        } else {
            return;
        }

        CardView nameCard = (CardView) LayoutInflater.from(requireContext()).inflate(layoutResId, namesLayout2, false);

        if (layoutResId == R.layout.activity_hopsital_card) {
            setupHospitalCard(nameCard, hospital);
        } else if (layoutResId == R.layout.donation_card) {
            setupDonationCard(nameCard, hospital);
        }

        namesLayout2.addView(nameCard);
    }

    private void setupHospitalCard(CardView nameCard, Hospital hospital) {
        ImageView previewImageView = nameCard.findViewById(R.id.previewCircularFrame);
        ImageView previewImageView2 = nameCard.findViewById(R.id.previewCircularFrame2);
        TextView nameTextView = nameCard.findViewById(R.id.hospitalNameTextView);
        TextView typeTextView = nameCard.findViewById(R.id.hospitalTypeTextView);
        TextView addressTextView = nameCard.findViewById(R.id.hospitalAddressTextView);
        TextView locationTextView = nameCard.findViewById(R.id.hospitalLocationTextView);
        TextView textRating = nameCard.findViewById(R.id.textRating);
        ImageView starIcon = nameCard.findViewById(R.id.starIcon);
        TextView textViewReport = nameCard.findViewById(R.id.reportButton);
        Button bookmarkButton = nameCard.findViewById(R.id.bookmarkButton);
        Button mapButton = nameCard.findViewById(R.id.mapButton);
        Button viewMoreButton = nameCard.findViewById(R.id.bookAppointmentButton);

        Glide.with(this).load(hospital.getImageUrlHorizontal1()).circleCrop().into(previewImageView);
        Glide.with(this).load(hospital.getImageUrlHorizontal2()).circleCrop().into(previewImageView2);

        nameTextView.setText(hospital.getName());
        typeTextView.setText(hospital.getAdminType());
        addressTextView.setText(hospital.getAddress());
        locationTextView.setText(hospital.getSelectedState() + ", " + hospital.getSelectedCity());

        if(hospital.getRatings() != null) {
            textRating.setVisibility(View.VISIBLE);
            starIcon.setVisibility(View.VISIBLE);
            double parsedRating = Double.parseDouble(hospital.getRatings());
            String formattedRating = String.format("%.2f", parsedRating);
            textRating.setText(formattedRating + " / 5.0");
        } else {
            textRating.setVisibility(View.GONE);
            starIcon.setVisibility(View.GONE);
        }

        textViewReport.setOnClickListener(v -> showReportDialog(hospital.getKey(), textViewReport));

        boolean isBookmarked = checkIfBookmarked(hospital.getEmail());
        updateBookmarkButton(bookmarkButton, isBookmarked, hospital.getEmail(), hospital.getName());
        bookmarkButton.setOnClickListener(v -> {
            boolean toggledStatus = toggleBookmarkStatus(hospital.getEmail());
            updateBookmarkButton(bookmarkButton, toggledStatus, hospital.getEmail(), hospital.getName());
        });

        mapButton.setOnClickListener(v -> openMap(hospital.getMapUrl()));
        viewMoreButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ViewMore.class);
            intent.putExtra("selectedUser", hospital.getEmail());
            intent.putExtra("HospitalEmail", hospital.getEmail());
            intent.putExtra("mapurl", hospital.getMapUrl());
            startActivity(intent);
        });
    }

    private void setupDonationCard(CardView nameCard, Hospital hospital) {
        ImageView previewImageView = nameCard.findViewById(R.id.imageView1);
        ImageView previewImageView2 = nameCard.findViewById(R.id.imageView2);
        ImageView previewImageView3 = nameCard.findViewById(R.id.imageView3);
        ImageView previewImageView4 = nameCard.findViewById(R.id.imageView4);
        TextView nameTextView = nameCard.findViewById(R.id.bloodbankNameTextView);
        Button buttonDonate = nameCard.findViewById(R.id.donateBtn);

        Glide.with(this).load(hospital.getImageUrlHorizontal1()).circleCrop().into(previewImageView);
        Glide.with(this).load(hospital.getImageUrlHorizontal2()).circleCrop().into(previewImageView2);
        Glide.with(this).load(hospital.getImageUrlHorizontal1()).circleCrop().into(previewImageView3);
        Glide.with(this).load(hospital.getImageUrlHorizontal2()).circleCrop().into(previewImageView4);

        nameTextView.setText(hospital.getName());

        buttonDonate.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ApplyNow.class);
            intent.putExtra("hospitalName", hospital.getName());
            intent.putExtra("hospEmail", hospital.getEmail());
            startActivity(intent);
        });
    }

    private void showReportDialog(String key, TextView textViewReport) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to report this?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    DatabaseReference ratedRef = database.getReference("hospitals").child(key).child("reports");
                    ratedRef.runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                            String currentValue = mutableData.getValue(String.class);
                            if (currentValue == null) {
                                mutableData.setValue("1");
                            } else {
                                int intValue = Integer.parseInt(currentValue);
                                intValue++;
                                mutableData.setValue(String.valueOf(intValue));
                            }
                            return Transaction.success(mutableData);
                        }
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                            if (databaseError != null) {
                                Log.e("Firebase", "Transaction failed: " + databaseError.getMessage());
                            } else if (committed) {
                                Helper.showToast(requireActivity(), "Hospital reported!");
                                textViewReport.setText("Reported");
                                textViewReport.setEnabled(false);
                            } else {
                                Log.d("Firebase", "Report aborted.");
                            }
                        }
                    });
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private boolean checkIfBookmarked(String email) {
        SharedPreferences preferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
        return preferences.getBoolean(email, false);
    }

    private boolean toggleBookmarkStatus(String email) {
        SharedPreferences preferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
        boolean isBookmarked = preferences.getBoolean(email, false);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(email, !isBookmarked);
        editor.apply();
        return !isBookmarked;
    }

    private void updateBookmarkButton(Button bookmarkButton, boolean isBookmarked, String email, String nameh) {
        int buttonColor = isBookmarked ? ContextCompat.getColor(requireContext(), android.R.color.darker_gray) : Color.RED;
        bookmarkButton.setBackgroundTintList(ColorStateList.valueOf(buttonColor));
        bookmarkButton.setText(isBookmarked ? "Bookmarked" : "Bookmark");
    }

    private void openMap(String mapUrl) {
        if (mapUrl != null && !mapUrl.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl));
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(requireContext(), "No app available to handle the map request", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "Map URL is not available", Toast.LENGTH_SHORT).show();
        }
    }
}