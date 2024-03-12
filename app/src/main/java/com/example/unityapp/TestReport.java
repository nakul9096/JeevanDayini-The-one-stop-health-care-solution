package com.example.unityapp;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TestReport extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProgressBar progressBar;
    private TextView textView;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_report);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        progressBar = findViewById(R.id.progressBarTR);
        textView = findViewById(R.id.textViewTR);
        imageView = findViewById(R.id.imageViewTR);
        // Get the currently signed-in user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            Log.d("TestReport","user Email: "+userEmail);
            String sanitizedEmail = userEmail.replace("@","").replace(".","");
            mDatabase.child("users").child(sanitizedEmail)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                boolean hospitalDataFound = false;
                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                    Log.d("TestReport", "snapshot 1: " + childSnapshot.getKey());
                                    String hospitalName = childSnapshot.child("hospitalName").getValue(String.class);
                                    String hospitalEmail = childSnapshot.child("hospitalEmail").getValue(String.class);
                                    String testreporturl = childSnapshot.child("test_report").getValue(String.class);
                                    if (hospitalName != null) {
                                        progressBar.setVisibility(View.GONE);
                                        createHospitalCard(hospitalName, hospitalEmail, testreporturl);
                                        hospitalDataFound = true;
                                        break;
                                    } }
                                    if (!hospitalDataFound) {
                                        progressBar.setVisibility(View.GONE);
                                        textView.setVisibility(View.VISIBLE);
                                        imageView.setVisibility(View.VISIBLE);
                                        Log.e("TestReport", "Hospital name is null");
                                    }

                            } else {
                                Log.e("TestReport", "No data found for this user.");
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.w("TestReport", "loadChildren:onCancelled", databaseError.toException());
                        }
                    });
        } else {
            Log.d("TestReport", "No user signed in.");
        }
    }
    private void createHospitalCard(String hospitalName, String hospitalEmail, String reporturl) {
        // Inflate the card layout
        View cardView = getLayoutInflater().inflate(R.layout.card_hospital, null);

        // Find views inside the card
        TextView textViewHospitalName = cardView.findViewById(R.id.textViewHospitalName);
        TextView textViewHospitalEmail = cardView.findViewById(R.id.textViewHospitalEmail);
        Button buttonViewPrescription = cardView.findViewById(R.id.buttonViewPrescription);
        Button buttonDownloadPrescription = cardView.findViewById(R.id.buttonDownloadPrescription);
        // Set hospital name and email
        textViewHospitalName.setText(hospitalName);
        textViewHospitalEmail.setText(hospitalEmail);

        // Add OnClickListener to the button
        buttonViewPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPDFViewer(reporturl);
            }
        });
        buttonDownloadPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadPDF(reporturl);
            }
        });
        // Add the card to your layout
        LinearLayout container = findViewById(R.id.linearlayoutcontainer); // Assuming you have a layout with id containerLayout
        container.addView(cardView);
    }
    private void openPDFViewer(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(url), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Handle exception if no PDF viewer app is installed
            Toast.makeText(this, "No PDF viewer app found", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to download the file with the URL
    private void downloadPDF(String url) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle("Downloading PDF");
        request.setDescription("Downloading PDF file...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "file.pdf");

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
    }
}