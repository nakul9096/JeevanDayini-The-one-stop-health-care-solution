package com.example.unityapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DocumentRequest extends AppCompatActivity {
    private static final String TAG = "ManageActivity";
    private LinearLayout namesLayout;
    private ProgressBar loadingProgressBar;
    private TextView noRequestsTextView;
    private FirebaseDatabase database;
    private CardView nameCard;
    APIService apiService;
    String serverBaseUrl = "http://192.168.54.40:5000";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_request);
        ImageView refreshImageView = findViewById(R.id.refreshImageView8);
        namesLayout = findViewById(R.id.namesLayout8);
        loadingProgressBar = findViewById(R.id.loadingProgressBar8);
        database = FirebaseDatabase.getInstance();
        DatabaseReference trustedUsersRef = database.getReference("blooddonation");
        noRequestsTextView = findViewById(R.id.noRequestsTextView);
        refreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData();
            }
        });
        loadUsersFromDatabase(trustedUsersRef);
    }
    public void loadUsersFromDatabase(DatabaseReference ref) {
        loadingProgressBar.setVisibility(View.VISIBLE);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    noRequestsTextView.setVisibility(View.GONE);
                    namesLayout.removeAllViews();
                    for (DataSnapshot firstLayerSnapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot userSnapshot : firstLayerSnapshot.getChildren()) {
                        String fullName = userSnapshot.child("fullName").getValue(String.class);
                        String userEmail = userSnapshot.child("email").getValue(String.class);
                        String timestamp = userSnapshot.child("timestamp").getValue(String.class);
                        String relationType = userSnapshot.child("relationType").getValue(String.class);
                        String approvalStatus = userSnapshot.child("status").getValue(String.class);
                        String aadharNumber = userSnapshot.child("aadhaarNumber").getValue(String.class);
                        String userId = userSnapshot.child("userId").getValue(String.class);
                        if (fullName != null && "under scrutiny".equals(approvalStatus)) {
                            createNameCard(fullName, userEmail, timestamp, relationType, aadharNumber, userId);
                        }
                        }
                    }
                    loadingProgressBar.setVisibility(View.GONE);
                    noRequestsTextView.setVisibility(namesLayout.getChildCount() == 0 ? View.VISIBLE : View.GONE);
                } else {
                    loadingProgressBar.setVisibility(View.GONE);
                    noRequestsTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage(), databaseError.toException());
                Toast.makeText(DocumentRequest.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                loadingProgressBar.setVisibility(View.GONE);
                noRequestsTextView.setVisibility(View.GONE);
            }
        });
    }
    private void createNameCard(String userName, String userEmail, String timestamp, String applicationtype, String aadharNumber, String userId) {
        CardView nameCard = (CardView) LayoutInflater.from(this).inflate(R.layout.document_request_layout, namesLayout, false);
        if (nameCard != null) {
            TextView nameTextView = nameCard.findViewById(R.id.usernameTextView);
            TextView applicationTypeTextView = nameCard.findViewById(R.id.applicationTypeTextView);
            TextView uploadTimeTextView = nameCard.findViewById(R.id.uploadTimeTextView);
            if (nameTextView != null) {
                nameTextView.setText(userName);
            }
            if (applicationTypeTextView != null) {
                applicationTypeTextView.setText(applicationtype);
            }
            if (uploadTimeTextView != null) {
                uploadTimeTextView.setText(timestamp);
            }
            Button acceptButton = nameCard.findViewById(R.id.acceptButton);
            Button declineButton = nameCard.findViewById(R.id.declineButton);
            Button viewDataButton = nameCard.findViewById(R.id.viewDataButton);
            AdminData adminData = new AdminData(userEmail, timestamp);
            acceptButton.setTag(adminData);
            declineButton.setTag(adminData);
            viewDataButton.setTag(userName);
            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AdminData userCredentials = (AdminData) v.getTag();
                    if (userCredentials != null) {
                        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs3", Context.MODE_PRIVATE);
                        String adminId = sharedPreferences.getString("adminId", "");
                        String adminName = sharedPreferences.getString("adminName", "");
                        updateApprovalStatus(aadharNumber, "approved", adminId, adminName, userId);
                        Toast.makeText(DocumentRequest.this, "Form approved successfully!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            declineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AdminData userCredentials = (AdminData) v.getTag();
                    if (userCredentials != null) {
                        String selectedUserEmail = userCredentials.getEmail2();
                        String selectedUserPassword = userCredentials.getOrganizationPhone();
                    }
                }
            });
            viewDataButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String selectedUserName = (String) v.getTag();
                    Intent intent = new Intent(DocumentRequest.this, User_details.class);
                    intent.putExtra("selectedUser", selectedUserName);
                    startActivity(intent);
                }
            });
            namesLayout.addView(nameCard);
        } else {
            Log.e(TAG, "nameCard is null");
        }
    }
    public void refreshData() {
        loadUsersFromDatabase(database.getReference("blooddonation"));
    }
    private void disableUserAccount(String userUID) {
        FirebaseAuth.getInstance().signInAnonymously();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Helper.showToast( DocumentRequest.this,"User account disabled");
                        } else {
                            Helper.showToast( DocumentRequest.this,"Failed to disable user account");
                            Log.e("Debug2", "Failed to disable user account: " + task.getException().getMessage());
                        }
                    });
        } else {
            Helper.showToast( DocumentRequest.this,"Failed to get the current user");
        }
    }

    private void updateApprovalStatus(String aadharNumber, String status, String adminId, String adminName, String userId) {
        DatabaseReference userRef = database.getReference("blooddonation").child(userId).child(aadharNumber);
        userRef.child("status").setValue(status);
        userRef.child("approvedBy").setValue(adminId);
        userRef.child("adminName").setValue(adminName)
                .addOnSuccessListener(aVoid -> {
                    Helper.showToast( DocumentRequest.this,"Request accepted by " + adminName);
                    finish();
                    startActivity(getIntent());
                })
                .addOnFailureListener(e -> {
                    Helper.showToast(DocumentRequest.this,"Failed to update approval status");
                    Log.e(TAG, "Failed to update approval status: " + e.getMessage(), e);
                });
    }
    private void sendDataToServer(NotificationData notificationData) {
        apiService.sendDataToServer(notificationData).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Helper.showToast(DocumentRequest.this,"Data sent to server successfully");
                } else {
                    Helper.showToast(DocumentRequest.this,"Failed to send data to server");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Helper.showToast(DocumentRequest.this,"Error: " + t.getMessage());
            }
        });
    }
}