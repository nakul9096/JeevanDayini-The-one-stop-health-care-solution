package com.example.unityapp;

import static android.content.Context.MODE_PRIVATE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Tab1Fragment extends Fragment {
    private static final String TAG = "Tab1Fragment";
    private TextView userDetailsTextView;
    private Button btnPremium;
    private ViewPager viewPager;
    private int[] banners = {R.drawable.banner1, R.drawable.banner2, R.drawable.banner3};
    private int currentPage = 0;
    private String currentUserEmail;
    private DatabaseReference usersRef;
    private ImageView profileIcon, downArrow;
    private CardView adView;
    private CustomLoadingBar loadingBar;
    private LinearLayout topDonorsContainer, topDiagnosticContainer;

    // Use a ScheduledExecutorService for the automatic slider
    private ScheduledExecutorService scheduledExecutorService;

    public Tab1Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab1, container, false);

        // UI Initialization
        viewPager = view.findViewById(R.id.viewPager);
        userDetailsTextView = view.findViewById(R.id.user_details);
        btnPremium = view.findViewById(R.id.btnPremium);

        profileIcon = view.findViewById(R.id.profileIcon);
        topDonorsContainer = view.findViewById(R.id.top_donors_container);
        topDiagnosticContainer = view.findViewById(R.id.top_diagnostic_container);
        view.findViewById(R.id.btnScanQr).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), QrScannerActivity.class);
            startActivity(intent);
        });

        if (downArrow != null) {
            downArrow.setOnClickListener(v -> showPopupMenu(v));
        }
        if (profileIcon != null) {
            profileIcon.setOnClickListener(v -> showPopupMenu(v));
        }
        ViewUtils.addPressEffect(view.findViewById(R.id.btnScanQr));
        view.findViewById(R.id.textViewViewMore).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DonarListView.class);
            startActivity(intent);
        });

        view.findViewById(R.id.btnPremium).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Premium.class);
            startActivity(intent);
        });

        view.findViewById(R.id.textViewViewMoreDiagnosticCenters).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), HospitalListView.class);
            startActivity(intent);
        });
        if (getActivity() != null) {
            loadingBar = new CustomLoadingBar(getActivity());
            loadingBar.show();
        }

        currentUserEmail = getCurrentUserEmail();
        if (currentUserEmail != null) {
            retrieveUserData(currentUserEmail);
        } else {
            Toast.makeText(getContext(), "User email is null", Toast.LENGTH_SHORT).show();
            if (loadingBar != null) loadingBar.dismiss();
        }

        setupClickListeners(view);
        setupViewPager();
        checkAndUpdatePlanType();
        getTopDonors();
        getTopDiagnosticCenters();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Start the automatic slider when the fragment is visible
        if (scheduledExecutorService == null || scheduledExecutorService.isShutdown()) {
            startAutoSlider();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop the automatic slider to prevent memory leaks and unnecessary work
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
        }
    }

    private void setupClickListeners(View view) {
        if (downArrow != null) {
            downArrow.setOnClickListener(v -> showPopupMenu(v));
        }
        if (profileIcon != null) {
            profileIcon.setOnClickListener(v -> showPopupMenu(v));
        }

        view.findViewById(R.id.textViewViewMore).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DonarListView.class);
            startActivity(intent);
        });

        view.findViewById(R.id.btnPremium).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Premium.class);
            startActivity(intent);
        });

        view.findViewById(R.id.textViewViewMoreDiagnosticCenters).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), HospitalListView.class);
            startActivity(intent);
        });
    }

    private void setupViewPager() {
        BannerAdapter bannerAdapter = new BannerAdapter(banners, requireContext());
        viewPager.setAdapter(bannerAdapter);
    }

    private void startAutoSlider() {
        // Use a new Handler for the UI updates on the main thread
        final Handler handler = new Handler(Looper.getMainLooper());
        final Runnable update = () -> {
            if (currentPage == banners.length) {
                currentPage = 0;
            }
            viewPager.setCurrentItem(currentPage++, true);
        };

        // Create a single thread executor to run the task
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        // Schedule the task to run every 3 seconds
        scheduledExecutorService.scheduleAtFixedRate(update, 500, 3000, TimeUnit.MILLISECONDS);
    }
    private String getCurrentUserEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return (user != null) ? user.getEmail() : null;
    }

    private void retrieveUserData(String email) {
        String sanitizedEmail = email.replace("@", "").replace(".", "");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(sanitizedEmail);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!isAdded()) return;

                String userName = dataSnapshot.child("fullName").getValue(String.class);
                String userid = dataSnapshot.child("userid").getValue(String.class);
                String aadharNumber = dataSnapshot.child("aadharNumber").getValue(String.class);
                String profilePicUrl = dataSnapshot.child("profilePictureUrl").getValue(String.class);

                if (userName != null && userid != null) {
                    userDetailsTextView.setText("Hello, \n" + userName);

                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("ProfileData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userid", userid);
                    editor.putString("aadharNumber", aadharNumber);
                    editor.putString("profilePhoto", profilePicUrl);
                    editor.apply();

                    if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                        Glide.with(requireContext())
                                .load(profilePicUrl)
                                .apply(RequestOptions.circleCropTransform())
                                .into(profileIcon);
                    }
                }
                if (loadingBar != null) loadingBar.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to retrieve user's name: " + databaseError.getMessage());
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
                if (loadingBar != null) loadingBar.dismiss();
            }
        });
    }

    public void showPopupMenu(View view) {
        if (!isAdded()) return;

        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_my_profile) {
                // Navigate to profile fragment or activity
                return true;
            } else if (item.getItemId() == R.id.menu_badges) {
                // Navigate to badges activity
                return true;
            } else if (item.getItemId() == R.id.menu_change_password) {
                changePassword();
                return true;
            } else if (item.getItemId() == R.id.menu_logout) {
                logoutUser();
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    public void changePassword() {
        if (!isAdded()) return;

        new AlertDialog.Builder(requireContext())
                .setMessage("Are you sure you want to reset your password? You will be logged out.")
                .setPositiveButton("Yes", (dialog, id) -> {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null && currentUser.getEmail() != null) {
                        FirebaseAuth.getInstance().sendPasswordResetEmail(currentUser.getEmail())
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && isAdded()) {
                                        Toast.makeText(getContext(), "Password reset email sent.", Toast.LENGTH_SHORT).show();
                                        logoutUser();
                                    } else if (isAdded()) {
                                        Toast.makeText(getContext(), "Failed to send reset email. " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("No", (dialog, id) -> dialog.dismiss())
                .show();
    }

    public void logoutUser() {
        if (!isAdded()) return;

        FirebaseAuth.getInstance().signOut();
        SharedPreferences preferences = requireContext().getSharedPreferences("ProfileData", MODE_PRIVATE);
        preferences.edit().clear().apply();

        Intent intent = new Intent(getActivity(), HomeDashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private void checkAndUpdatePlanType() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null || currentUser.getEmail() == null) {
            return;
        }

        String sanitizedUserEmail = currentUser.getEmail().replace("@", "").replace(".", "");
        DatabaseReference userPlanRef = FirebaseDatabase.getInstance().getReference("users").child(sanitizedUserEmail).child("planType");

        userPlanRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!isAdded()) return;
                String planType = dataSnapshot.getValue(String.class);

                if ("Silver".equals(planType)) {
                    if (adView != null) adView.setVisibility(View.GONE);
                    btnPremium.setBackgroundColor(Color.parseColor("#C0C0C0"));
                    btnPremium.setText("Premium");
                } else if ("Gold".equals(planType)) {
                    if (adView != null) adView.setVisibility(View.GONE);
                    btnPremium.setBackgroundColor(Color.parseColor("#E5B80B"));
                    btnPremium.setText("Premium");
                    fadeInButtonBackground(btnPremium);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to get plan type: " + databaseError.getMessage());
            }
        });
    }

    private void fadeInButtonBackground(Button button) {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(button, "alpha", 0f, 1f);
        fadeIn.setDuration(1000);
        fadeIn.start();
    }

    private void getTopDonors() {
        FirebaseDatabase.getInstance().getReference().child("blooddonation")
                .limitToFirst(5)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!isAdded() || topDonorsContainer == null) return;
                        topDonorsContainer.removeAllViews(); // Clear existing views

                        LayoutInflater inflater = LayoutInflater.from(getContext());
                        int donorCount = 0;

                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            for (DataSnapshot donationSnapshot : userSnapshot.getChildren()) {
                                String donationStatus = donationSnapshot.child("donation_status").getValue(String.class);

                                if ("DONATED".equals(donationStatus) && donorCount < 5) {
                                    String fullName = donationSnapshot.child("fullName").getValue(String.class);
                                    String profilePhotoUrl = donationSnapshot.child("imageUrl").getValue(String.class);

                                    if (fullName != null && profilePhotoUrl != null) {
                                        View donorView = inflater.inflate(R.layout.donor_item, topDonorsContainer, false);
                                        ImageView donorImage = donorView.findViewById(R.id.donor_profile_image);
                                        TextView donorName = donorView.findViewById(R.id.donor_name_text);

                                        donorName.setText(capitalizeName(fullName));
                                        if (isAdded()) {
                                            Glide.with(Tab1Fragment.this)
                                                    .load(profilePhotoUrl)
                                                    .apply(RequestOptions.circleCropTransform())
                                                    .placeholder(R.drawable.profi)
                                                    .into(donorImage);
                                        }
                                        topDonorsContainer.addView(donorView);
                                        donorCount++;
                                    }
                                }
                                if (donorCount >= 5) break;
                            }
                            if (donorCount >= 5) break;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "Failed to load top donors: " + databaseError.getMessage());
                    }
                });
    }

    private void getTopDiagnosticCenters() {
        FirebaseDatabase.getInstance().getReference().child("hospitals")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!isAdded() || topDiagnosticContainer == null) return;
                        topDiagnosticContainer.removeAllViews(); // Clear existing views

                        LayoutInflater inflater = LayoutInflater.from(getContext());
                        int centerCount = 0;

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (centerCount >= 5) break;
                            String imageUrl = snapshot.child("imageUrlIdentity1").getValue(String.class);
                            String name = snapshot.child("name").getValue(String.class);
                            if (name == null || imageUrl == null) continue;

                            View diagnosticView = inflater.inflate(R.layout.diagnostic_item, topDiagnosticContainer, false);
                            ImageView centerImage = diagnosticView.findViewById(R.id.diagnostic_center_image);
                            TextView centerName = diagnosticView.findViewById(R.id.diagnostic_center_name);

                            String truncatedName = (name.length() > 15) ? name.substring(0, 15) + ".." : name;
                            centerName.setText(truncatedName);

                            if (isAdded()) {
                                Glide.with(Tab1Fragment.this)
                                        .load(imageUrl)
                                        .placeholder(R.drawable.placeholder_building)
                                        .into(centerImage);
                            }
                            topDiagnosticContainer.addView(diagnosticView);
                            centerCount++;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "Failed to load diagnostic centers: " + databaseError.getMessage());
                    }
                });
    }

    private String capitalizeName(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }
}