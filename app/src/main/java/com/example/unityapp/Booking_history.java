package com.example.unityapp;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class Booking_history extends AppCompatActivity {
    private LinearLayout cardContainer;
    private DatabaseReference bookingsRef;
    private FirebaseAuth mAuth;
    private ProgressBar loadingProgressBar80;
    private TextView noRequestsTextView80;
    private Button btnrate;
    private ImageView refreshImageView80;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);
        cardContainer = findViewById(R.id.namesLayout80);
        loadingProgressBar80 = findViewById(R.id.loadingProgressBar80);
        noRequestsTextView80 = findViewById(R.id.noRequestsTextView80);
        refreshImageView80 = findViewById(R.id.refreshImageView80);
        mAuth = FirebaseAuth.getInstance();
        refreshImageView80.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });
        bookingsRef = FirebaseDatabase.getInstance().getReference().child("bookings");
        retrieveBookingData();
    }
    private void retrieveBookingData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail().replace("@", "").replace(".","");
            bookingsRef.child(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean hasData = false;
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Log.d("BookingHistory", "onDataChange: Data retrieval successful");
                        String hospitalName = dataSnapshot1.child("hospitalName").getValue(String.class);
                        String transactionId = dataSnapshot1.child("transactionId").getValue(String.class);
                        String tokenNumber = dataSnapshot1.child("tokenNumber").getValue(String.class);
                        String timestamp = dataSnapshot1.child("timestamp").getValue(String.class);
                        String hospEmail = dataSnapshot1.child("hospEmail").getValue(String.class);
                        Log.d("BookingHistory", "Hospital Name: " + hospitalName);
                        Log.d("BookingHistory", "Transaction ID: " + transactionId);
                        Log.d("BookingHistory", "Token Number: " + tokenNumber);
                        Log.d("BookingHistory", "Timestamp: " + timestamp);
                        loadingProgressBar80.setVisibility(View.GONE);
                        noRequestsTextView80.setVisibility(View.GONE);
                        if(hospEmail != null && transactionId != null && tokenNumber != null) {
                            hasData = true;
                            createBookingCard(hospitalName, transactionId, tokenNumber, timestamp, hospEmail, dataSnapshot1);
                        }
                    }
                    if (!hasData) {
                        loadingProgressBar80.setVisibility(View.GONE);
                        noRequestsTextView80.setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    loadingProgressBar80.setVisibility(View.GONE);
                    noRequestsTextView80.setText("Error: "+databaseError);
                    noRequestsTextView80.setVisibility(View.VISIBLE);
                    Log.e("BookingHistory", "onCancelled: Data retrieval cancelled", databaseError.toException());
                }
            });
        }
    }
    private void createBookingCard(String hospitalName, String transactionId, String tokenNumber, String timestamp, String hospEmail, DataSnapshot snapshot) {
        View bookingCard = LayoutInflater.from(this).inflate(R.layout.booking_historycard, cardContainer, false);
        TextView serviceNameTextView = bookingCard.findViewById(R.id.textServiceName);
        TextView hospitalNameTextView = bookingCard.findViewById(R.id.textHospClinicName);
        TextView transactionIdTextView = bookingCard.findViewById(R.id.textTransactionId);
        TextView tokenNumberTextView = bookingCard.findViewById(R.id.textTokenNumber);
        TextView timestampTextView = bookingCard.findViewById(R.id.textTimestamp);
        btnrate = bookingCard.findViewById(R.id.btnRateHospital);
        if (snapshot.hasChild("rated")) {
            btnrate.setText("Already rated!");
            btnrate.setEnabled(false);
        }
        btnrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog(hospEmail, snapshot);
            }
        });
        serviceNameTextView.setText("Slot booking");
        hospitalNameTextView.setText("Hospital Name: "+hospitalName);
        transactionIdTextView.setText("Transaction ID: "+transactionId);
        tokenNumberTextView.setText("Token number: "+tokenNumber);
        timestampTextView.setText("Date & Time: "+timestamp);
        cardContainer.addView(bookingCard);
    }
    private void showRatingDialog(String hospitalEmail, DataSnapshot snapshot) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rate Hospital");
        builder.setMessage("You can rate only once per booking! Please be sure before submitting.");
        View layout = LayoutInflater.from(this).inflate(R.layout.rating_dialog_layout, null);
        builder.setView(layout);
        RatingBar ratingBar = layout.findViewById(R.id.ratingBar);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                float newRating = ratingBar.getRating();
                DatabaseReference hospitalRef = FirebaseDatabase.getInstance().getReference().child("hospitals").child(sanitizeEmail(hospitalEmail)).child("ratings");
                DatabaseReference ratedRef = snapshot.getRef().child("rated");
                hospitalRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String existingRating = dataSnapshot.getValue(String.class);
                            float averageRating = (Float.parseFloat(existingRating) + newRating) / 2;
                            hospitalRef.setValue(String.valueOf(averageRating));
                            ratedRef.setValue("yes");
                            recreate();
                        } else {
                            hospitalRef.setValue(String.valueOf(newRating));
                            ratedRef.setValue("yes");
                            recreate();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    private String sanitizeEmail(String email) {
        return email.replace("@", "").replace(".","");
    }
}