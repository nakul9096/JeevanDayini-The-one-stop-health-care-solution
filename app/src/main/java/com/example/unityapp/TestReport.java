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
import android.widget.ImageView;
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

import java.util.List;

public class TestReport extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private LinearLayout linearLayoutContainer;
    private TextView textViewNoPrescription;
    private ProgressBar progressBarTR;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_report);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        linearLayoutContainer = findViewById(R.id.linearlayoutcontainer);
        textViewNoPrescription = findViewById(R.id.textViewPrescription);
        progressBarTR = findViewById(R.id.progressBarTR);
        imageView = findViewById(R.id.imageViewTR);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            String sanitizedEmail = sanitizeEmail(userEmail);
            Log.e("Mytest","santizedEmail: "+sanitizedEmail);
            databaseReference.child("users").child(sanitizedEmail).child("prescriptions")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                boolean foundPrescription = false; // Flag to track if at least one prescription was found
                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                    String hospitalName = childSnapshot.getKey();
                                    for (DataSnapshot childSnapshot2 : childSnapshot.getChildren()) {
                                        String timestamp = childSnapshot2.getKey();
                                        if (hospitalName != null && timestamp != null) {
                                            String fileUrl = childSnapshot2.child("fileUrl").getValue(String.class);
                                            if (fileUrl != null && !fileUrl.isEmpty()) {
                                                createCard(hospitalName, timestamp, fileUrl);
                                                foundPrescription = true; // Set flag to true since at least one prescription was found
                                            }
                                        }
                                    }
                                }
                                if (!foundPrescription) {
                                    showNoPrescriptionMessage(); // Display "No Prescription" message if no prescriptions were found
                                }
                            } else {
                                showNoPrescriptionMessage(); // Display "No Prescription" message if no prescriptions node exists
                            }
                            progressBarTR.setVisibility(View.GONE); // Hide progress bar after processing prescriptions
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
        }
    }
    private String sanitizeEmail(String email) {
        return email.replace("@", "").replace(".", "");
    }
    private void createCard(String hospitalName, String timestamp, String fileUrl) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.custom_prescription_layout, null);
        TextView textViewHospitalName = cardView.findViewById(R.id.textViewHospitalName);
        TextView textViewDate = cardView.findViewById(R.id.textViewDate);
        TextView textViewTime = cardView.findViewById(R.id.textViewTime);
        Button viewPrescription = cardView.findViewById(R.id.buttonView);
        Button downloadPrescription = cardView.findViewById(R.id.buttonDownload);
        viewPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPdfInExternalViewer(fileUrl);
            }
        });
        downloadPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiatePdfDownload(fileUrl);
            }
        });
        textViewHospitalName.setText(hospitalName);
        try {
            String date = timestamp.substring(0, 4) + "/" + timestamp.substring(4, 6) + "/" + timestamp.substring(6, 8);
            String time = timestamp.substring(9, 11) + ":" + timestamp.substring(11, 13) + ":" + timestamp.substring(13, 15);
            textViewDate.setText("Date: " + date);
            textViewTime.setText("Time: " + time);
        } catch (Exception e) {
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
        textViewNoPrescription.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);
        progressBarTR.setVisibility(View.GONE);
    }
    private void openPdfInExternalViewer(String pdfUrl) {
        // Create an intent with ACTION_VIEW and the PDF URL
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(pdfUrl), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Check if there's an app to handle this intent
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        if (resolveInfos != null && !resolveInfos.isEmpty()) {
            // Start the activity if there's at least one app to handle the intent
            startActivity(intent);
        } else {
            // Handle scenario where no PDF viewer app is available
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
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (id == downloadId) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(downloadId);
                    Cursor cursor = downloadManager.query(query);
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
                            Toast.makeText(TestReport.this, "Error processing download status", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("Download", "Cursor is null or empty");
                        Toast.makeText(TestReport.this, "Error processing download status", Toast.LENGTH_SHORT).show();
                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        };
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
}