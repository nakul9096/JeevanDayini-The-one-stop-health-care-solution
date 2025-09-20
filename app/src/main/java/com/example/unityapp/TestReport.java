package com.example.unityapp;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.airbnb.lottie.LottieAnimationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TestReport extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private LinearLayout linearLayoutContainer;
    private TextView textViewTitle, textViewSubtitle;
    private LottieAnimationView lottieAnimationView;
    private ProgressBar progressBarTR; // Added ProgressBar
    private BroadcastReceiver onComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_report);

        linearLayoutContainer = findViewById(R.id.linearlayoutcontainer);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewSubtitle = findViewById(R.id.textViewSubtitle);
        lottieAnimationView = findViewById(R.id.lottieAnimationViewTR);
        progressBarTR = findViewById(R.id.progressBarTR); // Initialize ProgressBar

        // Initial state: Show progress bar, hide Lottie and text views
        progressBarTR.setVisibility(View.VISIBLE);
        lottieAnimationView.setVisibility(View.GONE);
        textViewTitle.setVisibility(View.GONE);
        textViewSubtitle.setVisibility(View.GONE);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            String sanitizedEmail = sanitizeEmail(userEmail);
            Log.e("Mytest", "sanitizedEmail: " + sanitizedEmail);
            fetchPrescriptions(sanitizedEmail);
        } else {
            // If no user, immediately show no prescription message and hide progress bar
            showNoPrescriptionMessage();
            progressBarTR.setVisibility(View.GONE);
        }
    }

    private void fetchPrescriptions(String sanitizedEmail) {
        databaseReference.child("users").child(sanitizedEmail).child("prescriptions")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        linearLayoutContainer.removeAllViews();
                        boolean foundPrescription = false;
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot hospitalSnapshot : dataSnapshot.getChildren()) {
                                String hospitalName = hospitalSnapshot.getKey();
                                for (DataSnapshot prescriptionSnapshot : hospitalSnapshot.getChildren()) {
                                    String timestamp = prescriptionSnapshot.getKey();
                                    if (hospitalName != null && timestamp != null) {
                                        String fileUrl = prescriptionSnapshot.child("fileUrl").getValue(String.class);
                                        if (fileUrl != null && !fileUrl.isEmpty()) {
                                            createCard(hospitalName, timestamp, fileUrl);
                                            foundPrescription = true;
                                        }
                                    }
                                }
                            }
                        }

                        // Hide progress bar once data is loaded/checked
                        progressBarTR.setVisibility(View.GONE);

                        if (!foundPrescription) {
                            showNoPrescriptionMessage(); // Show Lottie and text if no data
                        } else {
                            // Hide "no data" UI components if data is found
                            lottieAnimationView.setVisibility(View.GONE);
                            textViewTitle.setVisibility(View.GONE);
                            textViewSubtitle.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(TestReport.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
                        progressBarTR.setVisibility(View.GONE); // Hide progress bar on error
                        showNoPrescriptionMessage(); // Show "no data" UI on error
                    }
                });
    }

    private String sanitizeEmail(String email) {
        return email.replace("@", "").replace(".", "");
    }

    private void createCard(String hospitalName, String timestamp, String fileUrl) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.custom_prescription_layout, linearLayoutContainer, false);
        TextView textViewHospitalName = cardView.findViewById(R.id.textViewHospitalName);
        TextView textViewDate = cardView.findViewById(R.id.textViewDate);
        TextView textViewTime = cardView.findViewById(R.id.textViewTime);
        Button viewPrescription = cardView.findViewById(R.id.buttonView);
        Button downloadPrescription = cardView.findViewById(R.id.buttonDownload);

        viewPrescription.setOnClickListener(v -> openPdfInExternalViewer(fileUrl));
        downloadPrescription.setOnClickListener(v -> initiatePdfDownload(fileUrl));

        textViewHospitalName.setText(hospitalName);
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(timestamp);

            if (date != null) {
                textViewDate.setText("Date: " + dateFormat.format(date));
                textViewTime.setText("Time: " + timeFormat.format(date));
            }
        } catch (ParseException e) {
            Log.e("TestReport", "Error parsing timestamp: " + e.getMessage());
            e.printStackTrace();
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int marginInDp = dpToPx(10);
        layoutParams.setMargins(0, marginInDp, 0, 0);
        cardView.setLayoutParams(layoutParams);
        linearLayoutContainer.addView(cardView);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void showNoPrescriptionMessage() {
        lottieAnimationView.setVisibility(View.VISIBLE);
        textViewTitle.setVisibility(View.VISIBLE);
        textViewSubtitle.setVisibility(View.VISIBLE);
    }

    private void openPdfInExternalViewer(String pdfUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(pdfUrl), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        if (!resolveInfos.isEmpty()) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "No PDF viewer app available", Toast.LENGTH_SHORT).show();
        }
    }

    private void initiatePdfDownload(String pdfUrl) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(pdfUrl));
        request.setTitle("Downloading Prescription");
        request.setDescription("Downloading PDF");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "prescription.pdf");
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        long downloadId = downloadManager.enqueue(request);

        if (onComplete != null) {
            unregisterReceiver(onComplete);
        }

        onComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (id == downloadId) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(downloadId);
                    try (Cursor cursor = downloadManager.query(query)) {
                        if (cursor != null && cursor.moveToFirst()) {
                            int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                            if (columnIndex != -1) {
                                int status = cursor.getInt(columnIndex);
                                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                    Toast.makeText(TestReport.this, "Download successful", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(TestReport.this, "Download failed", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.e("Download", "Invalid columnIndex");
                            }
                        } else {
                            Log.e("Download", "Cursor is null or empty");
                        }
                    } catch (Exception e) {
                        Log.e("Download", "Error with cursor: " + e.getMessage());
                    } finally {
                        try {
                            unregisterReceiver(onComplete);
                            onComplete = null;
                        } catch (Exception e) {
                            Log.e("Download", "Error unregistering receiver: " + e.getMessage());
                        }
                    }
                }
            }
        };

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (onComplete != null) {
            unregisterReceiver(onComplete);
        }
    }
}