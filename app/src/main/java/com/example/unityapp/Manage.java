package com.example.unityapp;

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

public class Manage extends AppCompatActivity {
    private static final String TAG = "ManageActivity";
    private LinearLayout namesLayout;
    private ProgressBar loadingProgressBar;
    private FirebaseDatabase database;
    private CardView nameCard;
    private TextView t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        ImageView refreshImageView = findViewById(R.id.refreshImageView2);
        namesLayout = findViewById(R.id.namesLayout2);
        loadingProgressBar = findViewById(R.id.loadingProgressBar2);
        t1 = findViewById(R.id.loadingMessage2);
        database = FirebaseDatabase.getInstance();
        DatabaseReference trustedUsersRef = database.getReference("users");

        refreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData();
            }
        });
        loadUsersFromDatabase(trustedUsersRef);
    }

    private void loadUsersFromDatabase(DatabaseReference ref) {
        loadingProgressBar.setVisibility(View.VISIBLE);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    t1.setVisibility(View.GONE);
                    namesLayout.removeAllViews(); // Clear the existing cards

                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userName = userSnapshot.child("fullName").getValue(String.class);
                        String userEmail = userSnapshot.child("email").getValue(String.class);
                        String userPassword = userSnapshot.child("password").getValue(String.class);
                        String userUID = userSnapshot.getKey(); // Get the user's UID

                        if (userName != null) {
                            createNameCard(userName, userEmail, userPassword, userUID);
                        }
                    }

                    loadingProgressBar.setVisibility(View.GONE);
                } else {
                    loadingProgressBar.setVisibility(View.GONE);
                    t1.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage(), databaseError.toException());
                Toast.makeText(Manage.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                loadingProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void createNameCard(String userName, String userEmail, String userPassword, String userUID) {
        View nameCardView = LayoutInflater.from(this).inflate(R.layout.user, namesLayout, false);
        CardView nameCard = nameCardView.findViewById(R.id.nameCard);
        TextView nameTextView = nameCardView.findViewById(R.id.nameTextView);
        nameTextView.setText(userName);

        Button disableAccountButton = nameCardView.findViewById(R.id.disableAccountButton);
        disableAccountButton.setTag(userUID); // Store the user UID in the button's tag

        disableAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userUID = (String) disableAccountButton.getTag();
                disableUserAccount(userUID);
            }
        });
        namesLayout.addView(nameCardView);
    }

    public void refreshData() {
        loadUsersFromDatabase(database.getReference("users"));
    }

    private void disableUserAccount(String userUID) {
        FirebaseAuth.getInstance().signInAnonymously(); // Sign in anonymously to use Firebase Authentication
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.delete() // This will delete the user account
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Successfully disabled the account
                            // You can perform additional actions or display a message here
                            Toast.makeText(Manage.this, "User account disabled", Toast.LENGTH_SHORT).show();
                        } else {
                            // Failed to disable the account
                            // You can handle this case or display an error message
                            Toast.makeText(Manage.this, "Failed to disable user account", Toast.LENGTH_SHORT).show();
                            Log.e("Debug2", "Failed to disable user account: " + task.getException().getMessage());
                        }
                    });
        } else {
            Toast.makeText(Manage.this, "Failed to get the current user", Toast.LENGTH_SHORT).show();
        }
    }
}
