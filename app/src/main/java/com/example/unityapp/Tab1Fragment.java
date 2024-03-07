package com.example.unityapp;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Timer;
import java.util.TimerTask;
public class Tab1Fragment extends Fragment {
    private TextView userDetailsTextView;
    private Button logoutButton, btnPremium;
    private ImageView chatIcon;
    private ViewPager viewPager;
    private int[] banners = {R.drawable.banner1, R.drawable.banner2, R.drawable.banner3};
    private int currentPage = 0;
    private String currentUserEmail;
    private DatabaseReference usersRef;
    private ImageView imageView2;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private Button passbtn;
    private CardView adView;
    private CustomLoadingBar loadingBar;
    public Tab1Fragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab1, container, false);
        checkAndUpdatePlanType();
        chatIcon = view.findViewById(R.id.chatIcon);
        viewPager = view.findViewById(R.id.viewPager);
        userDetailsTextView = view.findViewById(R.id.user_details);
        btnPremium = view.findViewById(R.id.btnPremium);
        imageView2 = view.findViewById(R.id.imageView2);
        adView = view.findViewById(R.id.adView);
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        currentUserEmail = getCurrentUserEmail();
        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        loadingBar = new CustomLoadingBar(getActivity());
        loadingBar.show();
        AdRequest adRequest = new AdRequest.Builder().build();
        if (currentUserEmail != null) {
            retrieveUserName(currentUserEmail);
        } else {
            Toast.makeText(getActivity(), "User email is null", Toast.LENGTH_SHORT).show();
        }
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
        chatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUserEmail != null) {
                    Intent intent = new Intent(getActivity(), Assistant.class);
                    intent.putExtra("userEmail", currentUserEmail);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "User email is null", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnPremium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Premium.class);
                startActivity(intent);
            }
        });
        BannerAdapter bannerAdapter = new BannerAdapter(banners, requireContext());
        viewPager.setAdapter(bannerAdapter);
        final Handler handler = new Handler();
        final Runnable update = new Runnable() {
            public void run() {
                if (currentPage == banners.length) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, 500, 3000);
        return view;
    }
    private String getCurrentUserEmail() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            return user.getEmail();
        } else {
            return null;
        }
    }
    private void retrieveUserName(String email) {
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userName = userSnapshot.child("fullName").getValue(String.class);
                    String userid = userSnapshot.child("userid").getValue(String.class);
                    Log.e("UserID", "User ID retrieved: " + userid);
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ProfileData", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userid", userid);
                    editor.apply();
                    userDetailsTextView.setText("Hello, \n" + userName);
                    loadingBar.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to retrieve user's name", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void changePass() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Are you sure you want to reset your password? You will be logged out.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            String email = currentUser.getEmail();
                            Log.d("Changed1", "password reset email sent to: " + email);
                            mAuth.signOut();
                            mAuth.sendPasswordResetEmail(email)
                                    .addOnCompleteListener(requireActivity(), task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(requireContext(), "Password reset email sent to your registered email address.", Toast.LENGTH_SHORT).show();
                                            FirebaseAuth.getInstance().signOut();
                                            SharedPreferences preferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.remove("userUid");
                                            editor.apply();
                                            Intent intent = new Intent(requireContext(), HomeDashboard.class);
                                            startActivity(intent);
                                            requireActivity().finish();
                                        } else {
                                            Toast.makeText(requireContext(), "Failed to send password reset email. Please check your email address.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.create().show();
    }
    public void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_my_profile) {
                    return true;
                } else if (item.getItemId() == R.id.menu_badges) {
                    return true;
                } else if (item.getItemId() == R.id.menu_change_password) {
                    changePass();
                    return true;
                } else if (item.getItemId() == R.id.menu_logout) {
                    logoutUser();
                    return true;
                } else {
                    return false;
                }
            }
        });

        popupMenu.show();
    }
    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), HomeDashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }
    private void checkAndUpdatePlanType() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            String sanitizedUserUid = userEmail.replace("@", "").replace(".", "");
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users")
                    .child(sanitizedUserUid)
                    .child("planType");
            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String planType = dataSnapshot.getValue(String.class);

                        updatePremiumButton(planType);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
    private void updatePremiumButton(String planType) {
        if ("Silver".equals(planType)) {
            adView.setVisibility(View.GONE);
            btnPremium.setBackgroundColor(Color.parseColor("#C0C0C0"));
            btnPremium.setTextColor(Color.parseColor("#000000"));
            btnPremium.setText("Premium");
        } else if ("Gold".equals(planType)) {
            btnPremium.setEnabled(false);
            adView.setVisibility(View.GONE);
            btnPremium.setBackgroundColor(Color.parseColor("#E5B80B"));
            btnPremium.setText("Premium");
            fadeInButtonBackground(btnPremium);
        }
    }
        private void fadeInButtonBackground(Button button) {
            // Create fade-in animation
            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(button, "alpha", 0f, 1f);
            fadeIn.setDuration(1000); // Set duration of the animation (in milliseconds)

            // Add listener to reset alpha value after animation completes
            fadeIn.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    button.setAlpha(1f); // Reset alpha value to 1 after animation completes
                }
            });

            // Start the animation
            fadeIn.start();
        }

}