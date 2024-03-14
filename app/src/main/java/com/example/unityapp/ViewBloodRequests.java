package com.example.unityapp;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class ViewBloodRequests extends AppCompatActivity {
    private LinearLayout namesLayout;
    private DatabaseReference bloodRequestsRef;
    private ProgressBar loadingProgressBar;
    private TextView loadingMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_blood_requests);
        namesLayout = findViewById(R.id.namesLayoutRequests);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        loadingMessage = findViewById(R.id.loadingMessage);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        bloodRequestsRef = firebaseDatabase.getReference("blood_requests");

        retrieveBloodRequests();
    }

    private void retrieveBloodRequests() {
        loadingProgressBar.setVisibility(View.VISIBLE); // Show progress bar

        bloodRequestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String bloodAmount = snapshot.child("bloodAmount").getValue(String.class);
                        String address = snapshot.child("address").getValue(String.class);
                        String unit = snapshot.child("unit").getValue(String.class);
                        String state = snapshot.child("state").getValue(String.class);
                        String longitude = snapshot.child("longitude").getValue(String.class);
                        String latitude = snapshot.child("latitude").getValue(String.class);
                        String city = snapshot.child("city").getValue(String.class);
                        String bloodGroup = snapshot.child("bloodGroup").getValue(String.class);
                        String time = snapshot.child("time").getValue(String.class);
                        createNameCard(bloodAmount, unit, state, city, time, bloodGroup, latitude, longitude, address);
                    }
                } else {
                    // No requests available
                    loadingMessage.setVisibility(View.VISIBLE); // Show message
                }
                loadingProgressBar.setVisibility(View.GONE); // Hide progress bar
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error", "Failed to retrieve blood requests: " + databaseError.getMessage());
                loadingProgressBar.setVisibility(View.GONE); // Hide progress bar in case of error
            }
        });
    }
    public void createNameCard(String bloodAmount, String unit, String state, String city, String time, String bloodGroup, String latitude, String longitude, String address) {
        CardView nameCard = (CardView) LayoutInflater.from(this).inflate(R.layout.view_bloodreq_card, namesLayout, false);
        TextView textViewBloodAmount = nameCard.findViewById(R.id.textViewTitle);
        TextView stateTV = nameCard.findViewById(R.id.textViewStateCity);
        TextView cityTv = nameCard.findViewById(R.id.textViewAddress);
        Button buttonDonate = nameCard.findViewById(R.id.buttonDonate);
        Button buttonViewOnMap = nameCard.findViewById(R.id.buttonViewOnMap);
        TextView textViewTime = nameCard.findViewById(R.id.textViewTime);
        TextView textViewGroup = nameCard.findViewById(R.id.textViewGroup);
        textViewGroup.setText("Blood group: "+bloodGroup);
        textViewTime.setText("Delivery time: "+time);
        buttonDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBloodDonationDialog("Blood requirement: "+bloodAmount + " " + unit, state, city, bloodGroup );
            }
        });
        buttonViewOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!latitude.isEmpty() || !longitude.isEmpty()) {
                    openLocationInMap(latitude, longitude);
                }else {
                    Helper.showToast(ViewBloodRequests.this,"Map location not found!");
                }
            }
        });
        textViewBloodAmount.setText("Blood requirement: "+bloodAmount + " " + unit);
        stateTV.setText(city+", "+state);
        cityTv.setText("Address: "+address);
        namesLayout.addView(nameCard);
    }
    private void openLocationInMap(String latitude, String longitude) {
        Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(ViewBloodRequests.this, "Error: No map application found!",Toast.LENGTH_SHORT).show();
        }
    }
    private void showBloodDonationDialog(String requirement, String state, String city, String bloodGroup) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_donate_blood, null);
        builder.setView(dialogView);

        EditText editTextName = dialogView.findViewById(R.id.editTextName);
        EditText editTextState = dialogView.findViewById(R.id.editTextState);
        EditText editTextCity = dialogView.findViewById(R.id.editTextCity);
        EditText editTextBloodGroup = dialogView.findViewById(R.id.editTextBloodGroup);
        editTextState.setText(state);
        editTextCity.setText(city);
        editTextBloodGroup.setText(bloodGroup);
        builder.setPositiveButton("Donate", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = editTextName.getText().toString();

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}