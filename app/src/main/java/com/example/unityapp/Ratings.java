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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Ratings extends AppCompatActivity {
    private static final String TAG = "RatingsActivity";
    public Button btn1, btn2, btn3, btn4, btn5;
    private LinearLayout namesLayout;
    private ProgressBar loadingProgressBar;
    private float totalRating = 0;
    private int entryCount = 0;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratings);

        namesLayout = findViewById(R.id.namesLayout7);
        loadingProgressBar = findViewById(R.id.loadingProgressBar7);
        database = FirebaseDatabase.getInstance();
        btn1 = findViewById(R.id.ratebtn1);
        btn2 = findViewById(R.id.ratebtn2);
        btn3 = findViewById(R.id.ratebtn3);
        btn4 = findViewById(R.id.ratebtn4);
        btn5 = findViewById(R.id.ratebtn5);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle the selected state
                v.setSelected(!v.isSelected());
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle the selected state
                v.setSelected(!v.isSelected());
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle the selected state
                v.setSelected(!v.isSelected());
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle the selected state
                v.setSelected(!v.isSelected());
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle the selected state
                v.setSelected(!v.isSelected());
            }
        });
        ImageView refreshImageView = findViewById(R.id.refreshImageView7);
        refreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData();
            }
        });

        Log.d(TAG, "Database accessed");

        DatabaseReference ratingsRef = database.getReference("ratings");

        ratingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                handleDataSnapshot(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                handleDatabaseError(databaseError);
            }
        });
    }

    private void handleDataSnapshot(DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
            namesLayout.removeAllViews(); // Clear existing views before adding new ones
            totalRating = 0;
            entryCount = 0;

            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                String userName = userSnapshot.child("name").getValue(String.class);
                String userComment = userSnapshot.child("description").getValue(String.class);
                Float ratedValue = userSnapshot.child("rating").getValue(Float.class);

                if (userName != null && ratedValue != null) {
                    totalRating += ratedValue;
                    entryCount++;
                    createNameCard(userName, userComment, String.valueOf(ratedValue));
                }
            }

            loadingProgressBar.setVisibility(View.GONE);

            // Calculate overall rating and display
            float overallRating = entryCount > 0 ? totalRating / entryCount : 0;
            displayOverallRating(overallRating);
        } else {
            loadingProgressBar.setVisibility(View.GONE);
            Toast.makeText(Ratings.this, "No data available", Toast.LENGTH_SHORT).show();
        }
    }


    private void handleDatabaseError(DatabaseError databaseError) {
        Log.e(TAG, "Database error: " + databaseError.getMessage(), databaseError.toException());
        Toast.makeText(Ratings.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
        loadingProgressBar.setVisibility(View.GONE);
    }

    private void createNameCard(String userName, String userComment, String ratedValue) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View nameCardView = inflater.inflate(R.layout.rating_card_item, namesLayout, false);

        TextView userNameTextView = nameCardView.findViewById(R.id.userNameTextView);
        TextView userCommentTextView = nameCardView.findViewById(R.id.userCommentTextView);
        TextView ratedValueTextView = nameCardView.findViewById(R.id.ratedValueTextView);

        userNameTextView.setText(userName);
        userCommentTextView.setText(userComment);
        ratedValueTextView.setText(ratedValue);

        namesLayout.addView(nameCardView);
    }

    private void displayOverallRating(float overallRating) {
        // Assuming you have a TextView in your layout with the ID overallRatingTextView
        TextView overallRatingTextView = findViewById(R.id.overallRatingTextView);
        overallRatingTextView.setText("Overall Rating: " + overallRating);
    }

    public void refreshData() {
        // Reload the activity
        finish();
        startActivity(getIntent());
    }
}
