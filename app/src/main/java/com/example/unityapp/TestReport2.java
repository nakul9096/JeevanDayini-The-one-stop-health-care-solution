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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TestReport2 extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProgressBar progressBar;
    private TextView textView;
    private TextView textViewTitle;
    private LottieAnimationView lottieAnimationView; // New LottieAnimationView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_report2);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        progressBar = findViewById(R.id.progressBarTR);
        textView = findViewById(R.id.textViewTR);
        lottieAnimationView = findViewById(R.id.lottieAnimationViewTR); // Initialize Lottie view
        textViewTitle = findViewById(R.id.textViewTitle);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            Log.d("TestReport", "user Email: " + userEmail);
            String sanitizedEmail = userEmail.replace("@", "").replace(".", "");

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
                                        lottieAnimationView.setVisibility(View.GONE); // Hide Lottie if data is found
                                        textView.setVisibility(View.GONE);
                                        textViewTitle.setVisibility(View.GONE);
                                        createHospitalCard(hospitalName, hospitalEmail, testreporturl);
                                        hospitalDataFound = true;
                                    }
                                }
                                if (!hospitalDataFound) {
                                    progressBar.setVisibility(View.GONE);
                                    lottieAnimationView.setVisibility(View.VISIBLE); // Show Lottie
                                    textView.setVisibility(View.VISIBLE);
                                    textViewTitle.setVisibility(View.VISIBLE);
                                    Log.e("TestReport", "Hospital name is null");
                                }
                            } else {
                                progressBar.setVisibility(View.GONE);
                                lottieAnimationView.setVisibility(View.VISIBLE);
                                textView.setVisibility(View.VISIBLE);
                                textViewTitle.setVisibility(View.VISIBLE);
                                Log.e("TestReport", "No data found for this user.");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.w("TestReport", "loadChildren:onCancelled", databaseError.toException());
                            progressBar.setVisibility(View.GONE);
                            lottieAnimationView.setVisibility(View.VISIBLE);
                            textView.setVisibility(View.VISIBLE);
                            textViewTitle.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            Log.d("TestReport", "No user signed in.");
            progressBar.setVisibility(View.GONE);
            lottieAnimationView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            textViewTitle.setVisibility(View.VISIBLE);
        }
    }

    private void createHospitalCard(String hospitalName, String hospitalEmail, String reporturl) {
        View cardView = getLayoutInflater().inflate(R.layout.card_hospital, null);
        TextView textViewHospitalName = cardView.findViewById(R.id.textViewHospitalName);
        TextView textViewHospitalEmail = cardView.findViewById(R.id.textViewHospitalEmail);
        Button buttonViewPrescription = cardView.findViewById(R.id.buttonViewPrescription);
        Button buttonDownloadPrescription = cardView.findViewById(R.id.buttonDownloadPrescription);
        textViewHospitalName.setText(hospitalName);
        textViewHospitalEmail.setText(hospitalEmail);

        buttonViewPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPDFViewer(reporturl);
            }
        });
        buttonDownloadPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadPDF(reporturl, hospitalName);
            }
        });
        LinearLayout container = findViewById(R.id.linearlayoutcontainer);
        container.addView(cardView);
    }

    private void openPDFViewer(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(url), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Helper.showToast(TestReport2.this, "No PDF viewer app found");
        }
    }

    private void downloadPDF(String url, String hospName) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(hospName + "_prescription");
        request.setDescription("Downloading PDF file...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "file.pdf");
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
    }
}