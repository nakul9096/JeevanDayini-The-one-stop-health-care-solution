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
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class Session extends AppCompatActivity {
    private static final String TAG = "SessionActivity";
    private ViewPager2 viewPager;
    private DatabaseReference userRef;
    private SmoothBottomBar bottomBar;
    private TextView noInternetTextView;
    private BroadcastReceiver connectivityReceiver;
    private FloatingActionButton chatIcon;

    private static class MyPagerAdapter extends FragmentStateAdapter {
        public MyPagerAdapter(@NonNull AppCompatActivity activity) {
            super(activity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return new Tab1Fragment();
                case 1: return new Tab2Fragment();
                case 2: return new Tab3Fragment();
                case 3: return new Tab4Fragment();
                default: return new Tab1Fragment();
            }
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            navigateToLoginScreen();
            return;
        }

        initializeViews();
        setupViewPager();
        setupSmoothBottomBar();
        setupConnectivityReceiver();
        setupChatFab();
        setupLogoutListener(currentUser);
    }

    private void initializeViews() {
        viewPager = findViewById(R.id.view_pager);
        bottomBar = findViewById(R.id.bottomBar);
        chatIcon = findViewById(R.id.chatIcon);
        noInternetTextView = findViewById(R.id.noInternetTextView);
    }

    private void setupViewPager() {
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setUserInputEnabled(true);
    }

    private void setupSmoothBottomBar() {
        bottomBar.setOnItemSelectedListener((OnItemSelectedListener) position -> {
            viewPager.setCurrentItem(position, true);
            return true;
        });
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                bottomBar.setItemActiveIndex(position);
            }
        });
    }

    private void setupChatFab() {
        chatIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Session.this, Assistant.class);
            startActivity(intent);
        });
    }

    private void setupConnectivityReceiver() {
        connectivityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action != null && action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                    boolean isConnected = isNetworkAvailable();
                    if (isConnected) {
                        noInternetTextView.setVisibility(View.GONE);
                        noInternetTextView.setBackgroundColor(ContextCompat.getColor(Session.this, R.color.green));
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

    private void setupLogoutListener(FirebaseUser currentUser) {
        String email = currentUser.getEmail();
        if (email != null) {
            String sanitizedEmail = sanitizeEmail(email);
            userRef = FirebaseDatabase.getInstance().getReference("users").child(sanitizedEmail).child("login_info");
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String androidId = childSnapshot.child("androidId").getValue(String.class);
                        String logoutRequest = childSnapshot.child("logout_request").getValue(String.class);
                        if (androidId != null && androidId.equals(getAndroidId()) && "yes".equals(logoutRequest)) {
                            Log.d(TAG, "Logout triggered for current device.");
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
                    Log.e(TAG, "Error reading user data: " + databaseError.getMessage());
                }
            });
        }
    }

    private void navigateToLoginScreen() {
        Intent intent = new Intent(Session.this, HomeDashboard.class);
        startActivity(intent);
        finish();
    }

    private String getAndroidId() {
        return Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private String sanitizeEmail(String email) {
        return email != null ? email.replaceAll("[^a-zA-Z0-9]", "") : "";
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
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivityReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectivityReceiver);
    }
}