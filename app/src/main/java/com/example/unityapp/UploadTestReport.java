package com.example.unityapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UploadTestReport extends AppCompatActivity {
    private static final int PICK_PDF_REQUEST = 1;
    EditText editTextSearch;
    Button buttonSearch;
    LinearLayout linearLayout;
    DatabaseReference usersRef;
    private TextView textViewNotFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_test_report);
        textViewNotFound = findViewById(R.id.textViewNotFound);
        editTextSearch = findViewById(R.id.editTextSearch);
        buttonSearch = findViewById(R.id.buttonSearch);
        linearLayout = findViewById(R.id.linearLayout);
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = editTextSearch.getText().toString().trim();
                searchUserByUserid(searchQuery);
                Log.d("UploadTestReport", "search button hit!");
            }
        });
    }

    private void searchUserByUserid(String userid) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String emailid = currentUser.getEmail();
            Log.d("UploadTestReport", "Current User Email: " + emailid);
            if (emailid != null) {
                String sanitizedEmail = sanitizeEmail(emailid);
                Log.d("UploadTestReport", "Sanitized Email: " + sanitizedEmail);
                DatabaseReference hospitalsRef = FirebaseDatabase.getInstance().getReference("hospitals")
                        .child(sanitizedEmail)
                        .child("booked_slots");
                hospitalsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        linearLayout.removeAllViews();
                        boolean found = false;
                        for (DataSnapshot slotSnapshot : dataSnapshot.getChildren()) {
                            String bookedUserId = slotSnapshot.getKey();
                            Log.d("UploadTestReport", "Child key: " + bookedUserId);
                            Log.d("UploadTestReport", "Search query: " + userid);
                            if (bookedUserId.equals(userid)) {
                                found = true;
                                getUserInfoAndAddCard(userid);
                                break;
                            }
                        }
                        if (!found) {
                            textViewNotFound.setVisibility(View.VISIBLE);
                            Log.d("UploadTestReport", "No booked slots found for this user!");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("UploadTestReport", "Database Error: " + databaseError.getMessage());
                    }
                });
            } else {
                Log.e("UploadTestReport", "Email is null for current user.");
            }
        } else {
            Log.e("UploadTestReport", "Current user is null.");
        }
    }

    private void getUserInfoAndAddCard(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
        Query query = userRef.orderByChild("userid").equalTo(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String name = userSnapshot.child("fullName").getValue(String.class);
                        String phoneNumber = userSnapshot.child("phoneNumber").getValue(String.class);
                        String profileimg = userSnapshot.child("profilePictureUrl").getValue(String.class);
                        addCard(name, phoneNumber, profileimg);
                    }
                } else {
                    Log.d("UploadTestReport", "User not found!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UploadTestReport", "Database Error: " + databaseError.getMessage());
            }
        });
    }

    private String sanitizeEmail(String email) {
        return email.replace("@", "").replace(".", "");
    }

    private void addCard(String name, String phoneNumber, String img) {
        CardView cardView = (CardView) LayoutInflater.from(this).inflate(R.layout.card_view, linearLayout, false);
        TextView textViewName = cardView.findViewById(R.id.textViewName);
        TextView textViewPhoneNumber = cardView.findViewById(R.id.textViewPhoneNumber);
        ImageView profileimg = cardView.findViewById(R.id.imageViewProfile);
        Button buttonUploadTestReport = cardView.findViewById(R.id.buttonUploadTestReport);
        textViewName.setText("Name: " + name);
        textViewPhoneNumber.setText("Phone Number: " + phoneNumber);
        Glide.with(this)
                .load(img)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.home)
                .into(profileimg);
        buttonUploadTestReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_REQUEST);
            }
        });
        linearLayout.addView(cardView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the URI of the selected PDF file
            Uri uri = data.getData();
            uploadPDFToFirebase(uri);
        }
    }

    private void uploadPDFToFirebase(Uri pdfUri) {
        // Create a storage reference with a filename including date and timestamp
        String fileName = "filename_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".pdf";
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("reports").child(fileName);

        // Upload the PDF file
        storageRef.putFile(pdfUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // File uploaded successfully
                    // Handle success, e.g., show a toast
                    Toast.makeText(getApplicationContext(), "File uploaded successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle unsuccessful uploads
                    // Display an error message, e.g., show a toast
                    Toast.makeText(getApplicationContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}