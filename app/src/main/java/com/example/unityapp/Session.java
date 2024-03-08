package com.example.unityapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class Session extends AppCompatActivity {
    private ViewPager2 viewPager;
    private DatabaseReference userRef;
    private TabLayout tabLayout;
    private TextView noInternetTextView;
    private BroadcastReceiver connectivityReceiver;
    private int[] tabIcons = {
            R.raw.home,
            R.raw.notify,
            R.raw.settings,
            R.drawable.placeholder_image
    };
    private String[] tabTitles = {
            "Home",
            "Actions",
            "config",
            "Profile"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            navigateToLoginScreen();
            return;
        }
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout1);
        noInternetTextView = findViewById(R.id.noInternetTextView);
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setUserInputEnabled(false);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(tabTitles[position]);
            tab.setIcon(tabIcons[position]);
        }).attach();
        connectivityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action != null && action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                    boolean isConnected = isNetworkAvailable();
                    if (isConnected) {
                        noInternetTextView.setVisibility(View.GONE); // Hide the TextView when internet is available
                        noInternetTextView.setBackgroundColor(ContextCompat.getColor(Session.this, R.color.selected_color));
                        noInternetTextView.setText("Internet connection is now live");
                    } else {
                        noInternetTextView.setVisibility(View.VISIBLE);
                        noInternetTextView.setBackgroundColor(Color.parseColor("#B2FF3D00"));
                        noInternetTextView.setText("No internet connection");
                    }
                }
            }
        };
        if (!isNetworkAvailable()) {
            noInternetTextView.setVisibility(View.VISIBLE);
            noInternetTextView.setText("No internet connection");
        } else {
            noInternetTextView.setVisibility(View.GONE);
        }
    }

    private void updateTabIcon(int index, String profilePictureUrl) {
        tabIcons[index] = R.drawable.placeholder_image;
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            String sanitizedEmail = sanitizeEmail(email);
            userRef = FirebaseDatabase.getInstance().getReference("users").child(sanitizedEmail).child("login_info");
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.e("Test2", "Email : " + sanitizedEmail);
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String androidId = childSnapshot.child("androidId").getValue(String.class);
                        String logoutRequest = childSnapshot.child("logout_request").getValue(String.class);
                        Log.e("Test2", "androidId db: " + androidId);
                        Log.e("test2", "logout req db:" + logoutRequest);
                        if (androidId != null && androidId.equals(getAndroidId()) && "yes".equals(logoutRequest)) {
                            Log.e("Test2", "Logout trigged!");
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(Session.this, HomeDashboard.class);
                            startActivity(intent);
                            childSnapshot.getRef().removeValue();
                            finish();
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                    Log.e("MainActivity", "Error reading user data: " + databaseError.getMessage());
                }
            });
        }
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivityReceiver, filter);

        // Check initial network status
        if (!isNetworkAvailable()) {
            noInternetTextView.setVisibility(View.VISIBLE);
            noInternetTextView.setText("No internet connection");
        } else {
            noInternetTextView.setVisibility(View.GONE);
        }
    }
    private void navigateToLoginScreen() {
        finish();
    }
    private String getAndroidId() {
        return Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }
    private String sanitizeEmail(String email) {
        return email.replaceAll("@", "").replace(".", "");
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
    @Override
    protected void onStop() {
        super.onStop();
        // Unregister the receiver
        unregisterReceiver(connectivityReceiver);
    }
}