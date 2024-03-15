package com.example.unityapp;

import android.content.Intent;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.unityapp.SendNotificationPack.Client;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Requests extends AppCompatActivity {
    private static final String TAG = "EmailSender";
    APIService apiService;
    String serverBaseUrl = "http://192.168.54.40:5000";
    private LinearLayout namesLayout;
    private ProgressBar loadingprogressbar;
    private FirebaseDatabase database;
    private CardView nameCard;
    private TextView t1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        ImageView refreshImageView = findViewById(R.id.refreshImageView);
        namesLayout = findViewById(R.id.namesLayout); // The LinearLayout to hold the cards
        loadingprogressbar = findViewById(R.id.loadingProgressBar);
        t1 = findViewById(R.id.loadingMessage);
        database = FirebaseDatabase.getInstance();
        apiService = Client.getClient(serverBaseUrl).create(APIService.class);
        DatabaseReference usersRef = database.getReference("Admin_requests");
        refreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData();
            }
        });
        Log.d(TAG, "Database accessed");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    t1.setVisibility(View.GONE);
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userName = userSnapshot.child("organizationName").getValue(String.class);
                        String userEmail = userSnapshot.child("email").getValue(String.class);
                        String userPassword = userSnapshot.child("organizationPhone").getValue(String.class);
                        if (userName != null) {
                            createNameCard(userName, userEmail, userPassword);
                        }
                    }
                    loadingprogressbar.setVisibility(View.GONE);
                    t1.setVisibility(View.VISIBLE);
                } else {
                    loadingprogressbar.setVisibility(View.GONE);
                    t1.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage(), databaseError.toException());
                Helper.showToast(Requests.this, "Database error: " + databaseError.getMessage());
                loadingprogressbar.setVisibility(View.GONE);
            }
        });
    }

    private void createNameCard(String userName, String userEmail, String userPassword) {
        CardView nameCard = (CardView) LayoutInflater.from(this).inflate(R.layout.name_card, namesLayout, false);
        TextView nameTextView = nameCard.findViewById(R.id.nameTextView);
        nameTextView.setText(userName);
        Button viewDataButton = nameCard.findViewById(R.id.button3);
        viewDataButton.setTag(userName);
        viewDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedUserName = (String) v.getTag();
                Intent intent = new Intent(Requests.this, User_details.class);
                intent.putExtra("selectedUser", selectedUserName);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("userPassword", userPassword);
                startActivity(intent);
            }
        });
        Button declineRequestButton = nameCard.findViewById(R.id.button2);
        AdminData declineCredentials = new AdminData(userEmail, userPassword);
        declineRequestButton.setTag(declineCredentials);
        declineRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminData userCredentials = (AdminData) v.getTag();
                if (userCredentials != null) {
                    String selectedUserEmail = userCredentials.getEmail2();
                    String selectedUserPassword = userCredentials.getOrganizationPhone();
                    declineRequest(selectedUserEmail, selectedUserPassword);
                }
            }
        });
        Button acceptRequestButton = nameCard.findViewById(R.id.button1);
        AdminData acceptCredentials = new AdminData(userEmail, userPassword);
        acceptRequestButton.setTag(acceptCredentials);
        acceptRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminData userCredentials = (AdminData) v.getTag();
                if (userCredentials != null) {
                    String selectedUserEmail = userCredentials.getEmail2();
                    String phonePassword = userCredentials.getOrganizationPhone();
                    acceptRequest(selectedUserEmail, phonePassword);
                }
            }
        });
        namesLayout.addView(nameCard);
    }

    public void refreshData() {
        reloadRequestsActivity();
    }

    public void acceptRequest(String userEmail, String userPassword) {
        Log.d(TAG, "Email retrieved from database: " + userEmail);
        Log.d(TAG, "Password retrieved from database: " + userPassword);
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        try {
                            if (task.isSuccessful()) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                String uid = user.getUid();
                                Log.d(TAG, "User created with UID: " + uid);
                                String successMessage = "User created successfully with UID: " + uid;
                                Helper.showToast(Requests.this, successMessage);
                                Log.d(TAG, "Sending email to: " + userEmail);
                                DatabaseReference originalUserRef = database.getReference("Admin_requests");
                                Log.d(TAG, "Email sent to: " + userEmail);
                                Query query = originalUserRef.orderByChild("email").equalTo(userEmail);
                                retrieveFCMToken(userEmail);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                                String userKey = userSnapshot.getKey();
                                                DatabaseReference newUserRef = database.getReference("Admin").child(userKey);
                                                newUserRef.setValue(userSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                        if (error == null) {
                                                            Log.d(TAG, "User data saved to new reference successfully.");
                                                            String fcmToken = userSnapshot.child("fcmToken").getValue(String.class);
                                                            if (fcmToken != null) {
                                                                NotificationData notificationData = new NotificationData(fcmToken, "Congratulations! account approved", "Click here! to login");
                                                                sendDataToServer(notificationData);
                                                            }
                                                            originalUserRef.child(userKey).removeValue();
                                                            reloadRequestsActivity();
                                                            namesLayout.removeView(nameCard);
                                                        } else {
                                                            Log.e(TAG, "Error saving user data to new reference: " + error.getMessage());
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.e(TAG, "Database error: " + databaseError.getMessage(), databaseError.toException());
                                    }
                                });
                            } else {
                                // User creation failed
                                Log.e(TAG, "User creation failed: " + task.getException().getMessage());
                                String errorMessage = "User creation failed: " + task.getException().getMessage();
                                Toast.makeText(Requests.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            // Handle exceptions here
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void reloadRequestsActivity() {
        Intent intent = getIntent();
        finish(); // Finish the current activity
        startActivity(intent); // Start the activity again
    }

    private void declineRequest(String userEmail, String userPassword) {
        Log.d(TAG, "Declining request for email: " + userEmail);
        DatabaseReference originalUserRef = database.getReference("users");
        Query query = originalUserRef.orderByChild("email").equalTo(userEmail);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userKey = userSnapshot.getKey();
                        // Create a new reference for the user data in "restricted_users"
                        DatabaseReference restrictedUserRef = database.getReference("restricted_admin").child(userKey);
                        restrictedUserRef.setValue(userSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                if (error == null) {
                                    Log.d(TAG, "User data moved to restricted_users reference successfully.");
                                    originalUserRef.child(userKey).removeValue(new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            if (error == null) {
                                                Log.d(TAG, "User data removed from the original reference successfully.");
                                                reloadRequestsActivity();
                                                namesLayout.removeView(nameCard);
                                                Toast.makeText(Requests.this, "User data deleted successfully", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.e(TAG, "Error removing user data from the original reference: " + error.getMessage());
                                                Toast.makeText(Requests.this, "User data deletion unsuccessful: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Log.e(TAG, "Error moving user data to restricted_users reference: " + error.getMessage());
                                    Toast.makeText(Requests.this, "User data deletion unsuccessful: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage(), databaseError.toException());
                Toast.makeText(Requests.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void retrieveFCMToken(String userEmail) {
        DatabaseReference usersRef = database.getReference("Admin_requests");
        Query query = usersRef.orderByChild("email").equalTo(userEmail);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String fcmToken = userSnapshot.child("fcmToken").getValue(String.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage(), databaseError.toException());
                Toast.makeText(Requests.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendDataToServer(NotificationData notificationData) {
        apiService.sendDataToServer(notificationData).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Requests.this, "Data sent to server successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Requests.this, "Failed to send data to server", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(Requests.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}