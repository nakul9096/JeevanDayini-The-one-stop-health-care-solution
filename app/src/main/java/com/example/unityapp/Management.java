package com.example.unityapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class Management extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String userEmail;
    private Button buttonViewRequests;
    private Button buttonmanageusers;
    private Button buttonLogout;
    private Button btnlinks;
    private Button cbbtn, ratebtn;
    private Button documentreqbtn;
    private ProgressBar progressBar;
    private TextView adminIdTv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);
        FirebaseApp.initializeApp(this);
        if (!isAdminLoggedIn()) {
            Intent intent = new Intent(Management.this, Login.class);
            startActivity(intent);
            finish();
        }
        buttonViewRequests = findViewById(R.id.requestsButton);
        buttonmanageusers = findViewById(R.id.manageusersButton);
        ratebtn = findViewById(R.id.ratingButton);
        buttonLogout = findViewById(R.id.logoutButton);
        documentreqbtn = findViewById(R.id.docreqButton);
        progressBar = findViewById(R.id.progressBar);
        btnlinks = findViewById(R.id.TutorialButton);
        cbbtn = findViewById(R.id.chatbotButton);
        adminIdTv = findViewById(R.id.adminIDTV);
        progressBar.setVisibility(View.VISIBLE);
        Intent docreqintent = new Intent(Management.this, DocumentRequest.class);
        if (user != null) {
            userEmail = user.getEmail();
            if (userEmail != null) {
                Log.d("Debug2", "User Email: " + userEmail);
            }
        } else {
            Log.d("Debug2", "No user is currently logged in");
        }
        buttonViewRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Management.this, Requests.class);
                startActivity(intent);
            }
        });
        ratebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Management.this, Ratings.class);
                startActivity(intent);
            }
        });
        retrieveUserData(userEmail);
        buttonmanageusers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Management.this, Manage.class);
                startActivity(intent);
            }
        });
        documentreqbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView userNameTextView = findViewById(R.id.userNameTextView);
                startActivity(docreqintent);
            }
        });
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSession();
                Intent intent = new Intent(Management.this, HomeDashboard.class);
                startActivity(intent);
                finish();
            }
        });
        btnlinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSession();
                Intent intent = new Intent(Management.this, Tutorial_links.class);
                startActivity(intent);
                finish();
            }
        });
        cbbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSession();
                Intent intent = new Intent(Management.this, ChatBotData.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private boolean isAdminLoggedIn() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String userUid = preferences.getString("userUid", null);
        return userUid != null && !userUid.isEmpty();
    }
    private String getUserUid() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String UserUid = preferences.getString("userUid", null);
        Log.d("Debug2", "User ID: " + UserUid);
        return preferences.getString("userUid", null);
    }
    private void clearSession() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
    private void retrieveUserData(String userEmail) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("Admin");
        usersRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String name = userSnapshot.child("organizationName").getValue(String.class);
                        String adminType = userSnapshot.child("adminType").getValue(String.class);
                        String adminId = userSnapshot.child("adminId").getValue(String.class);
                        String adminImage = userSnapshot.child("adminImage").getValue(String.class);
                        Log.d("Debug2", "Name: " + name);
                        Log.d("Debug2", "Admin Type: " + adminType);
                        Log.d("Debug2", "AdminId:" + adminId);
                        Log.d("Debug2", "AdminImage: " + adminImage);
                        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs3", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("adminId", adminId);
                        editor.putString("adminName", name);
                        editor.apply();
                        updateUserInfo(name, adminType, adminId, adminImage);
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    public void updateUserInfo(String name, String adminType, String adminId, String adminImage) {
        TextView userNameTextView = findViewById(R.id.userNameTextView);
        TextView adminTypeTextView = findViewById(R.id.adminTypeTextView);
        ImageView userImageView = findViewById(R.id.userImageView);
        if (name != null) {
            String firstName = name.split("\\s+")[0];
            userNameTextView.setText("Hello! " + firstName);
        }
        if (adminType != null) {
            adminTypeTextView.setText("Admin Type: " + adminType);
        }
        if (adminId != null) {
            adminIdTv.setText("ID: " + adminId);
            Hospital hosp = new Hospital();
            hosp.setCreated_by(adminId);
        }
        if (adminImage != null) {
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.raw.profile1)
                    .error(R.raw.profile1);
            Glide.with(this)
                    .load(adminImage)
                    .apply(requestOptions)
                    .into(userImageView);
        }
    }
}