package com.example.unityapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class DonarType extends AppCompatActivity {

    private RadioGroup radioGroupDonationOptions;
    private RadioButton radioMyself, radioFamilyMember, radioFriend;
    private Spinner familyMemberSpinner;
    private Button btnProceed;
    private CustomLoadingBar loadingBar;
    private DatabaseReference bloodDonationRef;
    private FirebaseUser currentUser;
    ArrayAdapter<CharSequence> adapter;
    List<CharSequence> items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donar_type);
        radioGroupDonationOptions = findViewById(R.id.radioGroupDonationOptions);
        radioMyself = findViewById(R.id.radioMyself);
        radioFamilyMember = findViewById(R.id.radioFamilyMember);
        radioFriend = findViewById(R.id.radioFriend);
        familyMemberSpinner = findViewById(R.id.familyMemberSpinner);
        btnProceed = findViewById(R.id.btnProceed);
        bloodDonationRef = FirebaseDatabase.getInstance().getReference("blooddonation");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        loadingBar = new CustomLoadingBar(this);
        loadingBar.show();
        checkRecentDonationAndProceed();
        items = new ArrayList<>(Arrays.asList(getResources().getTextArray(R.array.family_members)));
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        familyMemberSpinner.setAdapter(adapter);
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroupDonationOptions.getCheckedRadioButtonId();

                if (selectedId != -1) {
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    String selectedOption = selectedRadioButton.getText().toString();
                    if (selectedOption.equals("Register as Friend")) {
                        openSharingPage("https://www.mediafire.com/");
                    } else {
                        Intent donateBloodIntent = new Intent(DonarType.this, DonateBlood.class);
                        if (selectedOption.equals("Register as Myself")) {
                            donateBloodIntent.putExtra("relationType", "myself");
                        } else if (selectedOption.equals("Register as Family Member")) {
                            String selectedFamilyMember = familyMemberSpinner.getSelectedItem().toString();
                            donateBloodIntent.putExtra("relationType", selectedFamilyMember);
                        }
                        startActivity(donateBloodIntent);
                    }
                }
            }
        });
        radioGroupDonationOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioFamilyMember) {
                    familyMemberSpinner.setVisibility(View.VISIBLE);
                } else {
                    familyMemberSpinner.setVisibility(View.GONE);
                }
            }
        });
    }
    private void openSharingPage(String link) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, link);
        startActivity(Intent.createChooser(sharingIntent, "Share link via"));
    }
    private void checkRecentDonationAndProceed() {
        Log.d("DonarType", "Checking recent donation...");
        if (currentUser != null) {
            SharedPreferences sharedPreferences = getSharedPreferences("ProfileData", MODE_PRIVATE);
            String userId = sharedPreferences.getString("userid", "");
            String aadharNumber = sharedPreferences.getString("aadharNumber", "");
            Log.e("DonarType", "UserID: " + userId + ", AadharNumber: " + aadharNumber);
            bloodDonationRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("DonarType", "Data retrieval successful");
                    loadingBar.hide(); // Hide the loading bar when data is retrieved
                    if (dataSnapshot.exists()) {
                        String userID = dataSnapshot.getKey();
                        Log.e("DonarType2", "snapshot: "+userID);
                        for(DataSnapshot newSnapshot: dataSnapshot.getChildren()) {
                            Log.d("DonarType", "Data exists in database");
                            String aadhaarNumber = dataSnapshot.child("aadhaarNumber").getValue(String.class);
                            String relationType = newSnapshot.child("relationType").getValue(String.class);
                            Log.e("DonarType2","type: "+relationType);
                            Log.d("DonarType", "Snapshot: " + newSnapshot.getKey());
                                Log.e("DonarType2","type again: "+relationType);
                                if (relationType.equals("myself")) {
                                    Log.d("DonarType", "Recent donation found");
                                    radioMyself.setEnabled(false);
                                    Helper.showToast("You recently donated! You need to wait for 3 months to donate again");
                                    Helper.showToast("But, still you can register family members to donate");
                                    return;
                                }
                                if (relationType.equals("Mother") || relationType.equals("Father") || relationType.equals("Son") || relationType.equals("Daughter")) {
                                    List<CharSequence> itemsToRemove = new ArrayList<>();
                                    if (relationType.equals("Mother")) {
                                        items.remove("Mother");
                                    }
                                    if (relationType.equals("Father")) {
                                        items.remove("Father");
                                    }
                                    if (relationType.equals("Son")) {
                                        items.remove("Son");
                                    }
                                    if (relationType.equals("Daughter")) {
                                        items.remove("Daughter");
                                    }

                                    ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(DonarType.this, android.R.layout.simple_spinner_item, items);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    familyMemberSpinner.setAdapter(adapter);

                                }
                        }
                    } else {
                        Log.d("DonarType", "Data not found in database");
                    }
                    Log.d("DonarType", "Proceeding to donate blood");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("DonarType", "Database error: " + databaseError.getMessage());
                    loadingBar.hide();
                }
            });
        } else {
            Log.e("DonarType", "Current user is null");
        }
    }
}
