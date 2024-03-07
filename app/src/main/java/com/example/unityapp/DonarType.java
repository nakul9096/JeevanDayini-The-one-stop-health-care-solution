package com.example.unityapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DonarType extends AppCompatActivity {

    private RadioGroup radioGroupDonationOptions;
    private RadioButton radioMyself, radioFamilyMember, radioFriend;
    private Spinner familyMemberSpinner;
    private Button btnProceed;
    private CustomLoadingBar loadingBar;
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
        loadingBar = new CustomLoadingBar(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.family_members,
                android.R.layout.simple_spinner_item
        );
        // Filter out the "Select" option
        List<CharSequence> items = new ArrayList<>(Arrays.asList(getResources().getTextArray(R.array.family_members)));
        items.remove(0); // Assuming "Select" is the first item in your array
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
                        // Open sharing page with the specified link
                        openSharingPage("https://www.mediafire.com/");
                    } else {
                        // For other options, navigate to DonateBlood
                        Intent donateBloodIntent = new Intent(DonarType.this, DonateBlood.class);

                        // Add extra information based on the selected option
                        if (selectedOption.equals("Register as Myself")) {
                            donateBloodIntent.putExtra("relationType", "myself");
                        } else if (selectedOption.equals("Register as Family Member")) {
                            // Get the selected family member from the spinner
                            String selectedFamilyMember = familyMemberSpinner.getSelectedItem().toString();
                            donateBloodIntent.putExtra("relationType", selectedFamilyMember);
                        }
                        startActivity(donateBloodIntent);
                    }
                }
            }
        });

        // Set up a listener to show/hide the spinner based on the selected radio button
        radioGroupDonationOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioFamilyMember) {
                    familyMemberSpinner.setVisibility(View.VISIBLE);
                } else {
                    familyMemberSpinner.setVisibility(View.GONE);
                    // Disable the "Proceed" button for other options
                }
            }
        });
    }

    private void openSharingPage(String link) {
        // Create an Intent to send a message
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, link);

        // Start the sharing activity
        startActivity(Intent.createChooser(sharingIntent, "Share link via"));
    }
}
