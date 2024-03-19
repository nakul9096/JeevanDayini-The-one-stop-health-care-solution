package com.example.unityapp;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserDataView extends AppCompatActivity {

    private DatabaseReference databaseReference;

    // Define your TextViews
    private TextView textViewName1;
    private TextView textViewRelation1;
    private TextView textViewAddress1;
    private TextView textViewBloodGroup1;
    private TextView textViewPostalCode1;
    private TextView textViewBirthDate1;
    private TextView textViewEmail1;
    private TextView textViewPhone1;
    private TextView textViewAadhaar1;
    private TextView textViewID1;
    private TextView textViewTimestamp1;
    private TextView textViewType1;
    private TextView textViewState1;
    private TextView textViewCity1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data2);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference().child("blooddonation");

        // Initialize TextViews
        textViewName1 = findViewById(R.id.textViewName1);
        textViewRelation1 = findViewById(R.id.textViewRelation1);
        textViewAddress1 = findViewById(R.id.textViewAddress1);
        textViewBloodGroup1 = findViewById(R.id.textViewbloodgroup1);
        textViewPostalCode1 = findViewById(R.id.textViewpostalcode1);
        textViewBirthDate1 = findViewById(R.id.textViewBirthDate1);
        textViewEmail1 = findViewById(R.id.textViewEmail1);
        textViewPhone1 = findViewById(R.id.textViewPhone1);
        textViewAadhaar1 = findViewById(R.id.textViewAadhaar1);
        textViewID1 = findViewById(R.id.textViewID1);
        textViewTimestamp1 = findViewById(R.id.textViewtimestamp1);
        textViewType1 = findViewById(R.id.textViewtype1);
        textViewState1 = findViewById(R.id.textViewState1);
        textViewCity1 = findViewById(R.id.textViewcity1);
        String userID = getIntent().getStringExtra("userID");
        if (userID != null) {
            DatabaseReference userRef = databaseReference.child(userID);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String name = childSnapshot.child("fullName").getValue(String.class);
                        String relation = childSnapshot.child("relationType").getValue(String.class);
                        String address = childSnapshot.child("address").getValue(String.class);
                        String bloodGroup = childSnapshot.child("bloodGroup").getValue(String.class);
                        String postalCode = childSnapshot.child("postalCode").getValue(String.class);
                        String birthDate = childSnapshot.child("birthDate").getValue(String.class);
                        String email = childSnapshot.child("email").getValue(String.class);
                        String phone = childSnapshot.child("phoneNumber").getValue(String.class);
                        String aadhaar = childSnapshot.child("aadhaaRNumber").getValue(String.class);
                        String userID = childSnapshot.child("userId").getValue(String.class);
                        String timestamp = childSnapshot.child("timestamp").getValue(String.class);
                        String type = childSnapshot.child("type").getValue(String.class);
                        String state = childSnapshot.child("state").getValue(String.class);
                        String city = childSnapshot.child("city").getValue(String.class);
                        textViewName1.setText("Name: " + name);
                        textViewRelation1.setText("Relation type: " + relation);
                        textViewAddress1.setText("Address: " + address);
                        textViewBloodGroup1.setText("Blood group: " + bloodGroup);
                        textViewPostalCode1.setText("Postal code: " + postalCode);
                        textViewBirthDate1.setText("Birth date: " + birthDate);
                        textViewEmail1.setText("Email: " + email);
                        textViewPhone1.setText("Phone number: " + phone);
                        textViewAadhaar1.setText("Aadhaar Number: " + aadhaar);
                        textViewID1.setText("User ID: " + userID);
                        textViewTimestamp1.setText("Filled on: " + timestamp);
                        textViewType1.setText("Diease history: "+aadhaar);
                        textViewState1.setText("State: " + state);
                        textViewCity1.setText("City: " + city);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                }
            });
        }
    }
}
