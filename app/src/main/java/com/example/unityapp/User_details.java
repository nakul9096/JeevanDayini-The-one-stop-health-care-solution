package com.example.unityapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class User_details extends AppCompatActivity {
    String type, aadhaarimg, orgimg1, orgimg2, orgimg3, orgimg4, adminImage;
    Button imageview3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        String selectedUserName = getIntent().getStringExtra("selectedUser");
        TextView textViewName = findViewById(R.id.textViewName1);
        TextView textViewEmail = findViewById(R.id.textViewEmail1);
        TextView textViewPhone = findViewById(R.id.textViewPhone1);
        TextView textViewID = findViewById(R.id.textViewID1);
        TextView textViewtype = findViewById(R.id.textViewtype1);
        TextView textViewState = findViewById(R.id.textViewState1);
        TextView textViewCity = findViewById(R.id.textViewcity1);
        TextView aadhaarNumber = findViewById(R.id.textViewAadhaar1);
        Button imageview = findViewById(R.id.buttonViewImage1);
        Button imageview2 = findViewById(R.id.buttonViewImage2);
        imageview3 = findViewById(R.id.buttonViewImage3);
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Admin_requests");
        usersRef.orderByChild("organizationName").equalTo(selectedUserName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String fullName = userSnapshot.child("organizationName").getValue(String.class);
                        String email = userSnapshot.child("email").getValue(String.class);
                        String phoneNumber = userSnapshot.child("organizationPhone").getValue(String.class);
                        String ID = userSnapshot.child("adminId").getValue(String.class);
                        type = userSnapshot.child("adminType").getValue(String.class);
                        adminImage = userSnapshot.child("adminImage").getValue(String.class);
                        String selectedState = userSnapshot.child("selectedState").getValue(String.class);
                        String selectedCity = userSnapshot.child("selectedCity").getValue(String.class);
                        String aadharNumber = userSnapshot.child("aadhaarNumber").getValue(String.class);
                        aadhaarimg = userSnapshot.child("aadharImageUrl").getValue(String.class);
                        if (type.equals("Clinic") || type.equals("Hospital")) {
                            orgimg1 = userSnapshot.child("imageUrlIdentity1").getValue(String.class);
                            orgimg2 = userSnapshot.child("imageUrlHorizontal1").getValue(String.class);
                            orgimg3 = userSnapshot.child("imageUrlHorizontal2").getValue(String.class);
                            orgimg4 = userSnapshot.child("imageUrlHorizontal3").getValue(String.class);
                            imageview3.setVisibility(View.VISIBLE);
                        }
                        textViewName.setText("Name: " + fullName);
                        textViewEmail.setText("Email: " + email);
                        textViewPhone.setText("Phone number: " + phoneNumber);
                        textViewID.setText("Admin ID: " + ID);
                        textViewtype.setText("Admin type: " + type);
                        textViewState.setText("State: " + selectedState);
                        textViewCity.setText("City: " + selectedCity);
                        aadhaarNumber.setText("Aadhaar Number: " + aadharNumber);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        Button buttonViewAnotherData = findViewById(R.id.buttonViewAnotherData1);
        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User_details.this, ImagePreview.class);
                intent.putExtra("selectedUser", selectedUserName);
                intent.putExtra("profileimg", adminImage);
                startActivity(intent);
            }
        });
        imageview2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User_details.this, ImagePreview.class);
                intent.putExtra("selectedUser", selectedUserName);
                intent.putExtra("aadharimg", aadhaarimg);
                startActivity(intent);
            }
        });
        imageview3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User_details.this, OrgImagePreview.class);
                intent.putExtra("selectedUser", selectedUserName);
                intent.putExtra("orgimg1", orgimg1);
                intent.putExtra("orgimg2", orgimg2);
                intent.putExtra("orgimg3", orgimg3);
                intent.putExtra("orgimg4", orgimg4);
                startActivity(intent);
            }
        });
        buttonViewAnotherData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User_details.this, Management.class);
                startActivity(intent);
                finish();
            }
        });
    }
}