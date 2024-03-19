package com.example.unityapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Badge extends AppCompatActivity {
    public ImageView imageView1,imageView2,imageView3,imageView4;
    public ImageView imageViewB1, imageViewB2, imageViewB3, imageViewB4;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private CustomLoadingBar loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        imageView1 = findViewById(R.id.imageviewa1);
        imageView2 = findViewById(R.id.imageviewa2);
        imageView3 = findViewById(R.id.imageviewa3);
        imageView4 = findViewById(R.id.imageviewa4);
        imageViewB1 = findViewById(R.id.imageviewb1);
        imageViewB2 = findViewById(R.id.imageviewb2);
        imageViewB3 = findViewById(R.id.imageviewb3);
        imageViewB4 = findViewById(R.id.imageviewb4);
        loadingBar = new CustomLoadingBar(Badge.this);
        loadingBar.show();
        Log.d("BadgeActivity", "onCreate: Activity created");
        if (currentUser != null) {
            String email = currentUser.getEmail();
            String sanitizedEmail = email.replace("@","").replace(".","");
            Log.d("BadgeActivity", "Sanitized Email: " + sanitizedEmail);
            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(sanitizedEmail).child("badge");
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Check if the dataSnapshot value is a HashMap
                        if (dataSnapshot.getValue() instanceof HashMap) {
                            // Handle the HashMap data appropriately
                            HashMap<String, Object> badgeData = (HashMap<String, Object>) dataSnapshot.getValue();
                            // Extract badge count from HashMap
                            Object badgeCountObject = badgeData.get("badgeCount");
                            Object memberTypeObject = badgeData.get("memberType");
                            if (badgeCountObject != null && memberTypeObject != null) {
                                String badgeCount = badgeCountObject.toString();
                                String memberType = memberTypeObject.toString();
                                boolean isMyself = "myself".equals(memberType);
                                updateBadgeImage(Integer.parseInt(badgeCount), isMyself);
                                loadingBar.dismiss();
                            } else {
                                // Log which value is null
                                if (badgeCountObject == null) {
                                    Log.e("BadgeActivity", "Badge count is null");
                                }
                                if (memberTypeObject == null) {
                                    Log.e("BadgeActivity", "Member type is null");
                                }
                                loadingBar.dismiss();
                            }
                        } else {
                            // Handle the case where data is not in the expected format
                            Log.e("BadgeActivity", "Invalid data format: Expected HashMap");
                            loadingBar.dismiss();
                        }
                    } else {
                        Log.d("BadgeActivity", "Data not found");
                        loadingBar.dismiss();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("BadgeActivity", "onCancelled: Database error: " + databaseError.getMessage());
                    loadingBar.dismiss();
                }
            });
        } else {
            Log.e("BadgeActivity", "onCreate: Current user is null");
            loadingBar.dismiss();
        }
    }

    private void updateBadgeImage(int badgeCount, boolean isMyself) {
        Log.d("BadgeActivity", "updateBadgeImage: Badge count: " + badgeCount + ", isMyself: " + isMyself);

        if (isMyself) {
            imageView1.setImageResource(badgeCount >= 1 ? R.drawable.donationherobadge : R.drawable.placeholder_image);
            imageView2.setImageResource(badgeCount >= 2 ? R.drawable.donationherobadge : R.drawable.placeholder_image);
            imageView3.setImageResource(badgeCount >= 3 ? R.drawable.donationherobadge : R.drawable.placeholder_image);
            imageView4.setImageResource(badgeCount >= 4 ? R.drawable.donationherobadge : R.drawable.placeholder_image);
        } else {
            imageViewB1.setImageResource(badgeCount >= 1 ? R.drawable.donationherobadge : R.drawable.placeholder_image);
            imageViewB2.setImageResource(badgeCount >= 2 ? R.drawable.donationherobadge : R.drawable.placeholder_image);
            imageViewB3.setImageResource(badgeCount >= 3 ? R.drawable.donationherobadge : R.drawable.placeholder_image);
            imageViewB4.setImageResource(badgeCount >= 4 ? R.drawable.donationherobadge : R.drawable.placeholder_image);
        }
    }
}
