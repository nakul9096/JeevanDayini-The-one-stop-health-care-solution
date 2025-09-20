package com.example.unityapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.List;

public class Badge extends AppCompatActivity {
    private final String TAG = "BadgeActivity";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private CustomLoadingBar loadingBar;

    private List<ImageView> donationBadges;
    private List<ImageView> helpBadges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge);

        initializeViews();

        mAuth = FirebaseAuth.getInstance();
        loadingBar = new CustomLoadingBar(this);
        loadingBar.show();

        fetchBadgeData();
    }

    private void initializeViews() {
        donationBadges = new ArrayList<>();
        donationBadges.add(findViewById(R.id.imageviewa1));
        donationBadges.add(findViewById(R.id.imageviewa2));
        donationBadges.add(findViewById(R.id.imageviewa3));
        donationBadges.add(findViewById(R.id.imageviewa4));

        helpBadges = new ArrayList<>();
        helpBadges.add(findViewById(R.id.imageviewb1));
        helpBadges.add(findViewById(R.id.imageviewb2));
        helpBadges.add(findViewById(R.id.imageviewb3));
        helpBadges.add(findViewById(R.id.imageviewb4));
    }

    private void fetchBadgeData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null || currentUser.getEmail() == null) {
            Log.e(TAG, "onCreate: Current user is null");
            Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
            return;
        }

        String email = currentUser.getEmail();
        String sanitizedEmail = email.replace("@", "").replace(".", "");
        Log.d(TAG, "Sanitized Email: " + sanitizedEmail);

        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(sanitizedEmail)
                .child("badge");

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                if (!dataSnapshot.exists()) {
                    Log.d(TAG, "Badge data not found for user.");
                    return;
                }

                try {
                    String donationCount = String.valueOf(dataSnapshot.child("donationBadge").getValue());
                    String helpCount = String.valueOf(dataSnapshot.child("helpBadge").getValue());

                    Log.d(TAG, "Donation Badges: " + donationCount);
                    Log.d(TAG, "Help Badges: " + helpCount);

                    // Update UI with the fetched counts
                    updateBadges(donationBadges, donationCount);
                    updateBadges(helpBadges, helpCount);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to parse badge data: " + e.getMessage());
                    Toast.makeText(Badge.this, "Error loading badges.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                Log.e(TAG, "onCancelled: Database error: " + databaseError.getMessage());
                Toast.makeText(Badge.this, "Failed to load badges.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateBadges(List<ImageView> imageViews, String badgeCountStr) {
        int count = 0;
        try {
            count = Integer.parseInt(badgeCountStr);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid number format for badge count: " + badgeCountStr);
        }

        for (int i = 0; i < imageViews.size(); i++) {
            if (i < count) {
                // Show a filled badge
                imageViews.get(i).setImageResource(R.drawable.donationherobadge);
            } else {
                imageViews.get(i).setImageResource(R.drawable.placeholder_image);
            }
        }
    }
}