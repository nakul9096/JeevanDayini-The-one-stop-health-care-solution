package com.example.unityapp;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class UploadPrescription extends AppCompatActivity {
    private EditText editTextSearch;
    private Button buttonSearch;
    private TextView textViewNotFound;
    private LinearLayout linearLayout;
    private DatabaseReference databaseReference;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    String hospitalName;
    SharedPreferences sharedPreferences;
    DataSnapshot userSnapshot2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_prescription);
        sharedPreferences = getSharedPreferences("HospData", MODE_PRIVATE);
        hospitalName = sharedPreferences.getString("hospital_name", "DefaultHospital");
        editTextSearch = findViewById(R.id.editTextSearch);
        buttonSearch = findViewById(R.id.buttonSearch);
        textViewNotFound = findViewById(R.id.textViewNotFound);
        linearLayout = findViewById(R.id.linearLayout);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("prescriptions");
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchKey = editTextSearch.getText().toString().trim();
                if (!searchKey.isEmpty()) {
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean userFound = false;
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                String userId = userSnapshot.child("userid").getValue(String.class);
                                if (userId != null && userId.equals(searchKey)) {
                                    userSnapshot2 = userSnapshot;
                                    String userName = userSnapshot.child("fullName").getValue(String.class);
                                    String userEmail = userSnapshot.child("email").getValue(String.class);
                                    String userImageUrl = userSnapshot.child("profilePictureUrl").getValue(String.class);
                                    View cardView = getLayoutInflater().inflate(R.layout.custom_card_layout, null);
                                    TextView textViewName = cardView.findViewById(R.id.textViewName);
                                    TextView textViewEmail = cardView.findViewById(R.id.textViewEmail);
                                    ImageView imageView = cardView.findViewById(R.id.imageView);
                                    Button uploadButton = cardView.findViewById(R.id.buttonUpload);
                                    textViewName.setText(userName);
                                    textViewEmail.setText(userEmail);
                                    if (userImageUrl != null && !userImageUrl.isEmpty()) {
                                        imageView.setVisibility(View.VISIBLE);
                                    } else {
                                        imageView.setVisibility(View.GONE);
                                    }
                                    uploadButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // Launch file picker intent to select PDF file
                                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                            intent.setType("application/pdf");
                                            startActivityForResult(Intent.createChooser(intent, "Select PDF"), 123);
                                        }
                                    });

                                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    );
                                    layoutParams.setMargins(0, dpToPx(10), 0, 0); // Convert dp to pixels
                                    cardView.setLayoutParams(layoutParams);
                                    linearLayout.removeAllViews();
                                    linearLayout.addView(cardView);
                                    textViewNotFound.setVisibility(View.GONE);

                                    userFound = true;
                                    break;
                                }
                            }
                            if (!userFound) {
                                linearLayout.removeAllViews();
                                textViewNotFound.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("Firebase", "Error getting data", databaseError.toException());
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123 && resultCode == RESULT_OK && data != null) {
            // PDF file selected successfully
            Uri pdfUri = data.getData();
            if (pdfUri != null) {
                // Generate a timestamp for the current upload
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                String pdfFileName = hospitalName + "_" + timestamp + ".pdf";

                // Display progress bar and disable upload button
                Button uploadButton = findViewById(R.id.buttonUpload);
                ProgressBar progressBar = new ProgressBar(UploadPrescription.this, null, android.R.attr.progressBarStyleHorizontal);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                layoutParams.setMargins(0, dpToPx(10), 0, 0);
                progressBar.setLayoutParams(layoutParams);
                linearLayout.addView(progressBar);
                uploadButton.setEnabled(false);
                uploadButton.setText("Uploading...");
                StorageReference pdfRef = storageRef.child(hospitalName).child(timestamp).child(pdfFileName);
                UploadTask uploadTask = pdfRef.putFile(pdfUri);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get the download URL of the uploaded PDF
                                pdfRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String downloadUrl = uri.toString();
                                        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                                        String hospitalName = sharedPreferences.getString("hospital_name", "DefaultHospital");
                                        DatabaseReference userPrescriptionRef = userSnapshot2.getRef()
                                                .child("prescriptions")
                                                .child(hospitalName)
                                                .child(timestamp)
                                                .child("fileUrl");
                                        userPrescriptionRef.setValue(downloadUrl)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // Prescription URL stored successfully
                                                        Toast.makeText(UploadPrescription.this, "Prescription URL stored successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Failed to store prescription URL
                                                        Log.e("Firebase Database", "Prescription URL storing failed: " + e.getMessage());
                                                    }
                                                });

                                        // Continue with the rest of your success handling logic
                                        Toast.makeText(UploadPrescription.this, "PDF uploaded successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                        linearLayout.removeView(progressBar);
                                        uploadButton.setEnabled(true);
                                        uploadButton.setText("Upload Prescription");
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firebase Storage", "Upload failed: " + e.getMessage());
                        Toast.makeText(UploadPrescription.this, "Failed to upload PDF", Toast.LENGTH_SHORT).show();
                        linearLayout.removeView(progressBar);
                        uploadButton.setEnabled(true);
                        uploadButton.setText("Upload Prescription");
                    }
                });
            }
        }
    }
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}