package com.example.unityapp;

import static android.content.Context.MODE_PRIVATE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
public class Tab1Fragment extends Fragment {
    private TextView userDetailsTextView;
    private Button logoutButton, btnPremium;
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
    int count = 0;
    public ImageView donar1, donar2, donar3, donar4, donar5;
    public TextView donarname1, donarname2, donarname3, donarname4, donarname5;
    public TextView textViewViewMore;
    public Tab1Fragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab1, container, false);
        checkAndUpdatePlanType();
        viewPager = view.findViewById(R.id.viewPager);
        userDetailsTextView = view.findViewById(R.id.user_details);
        btnPremium = view.findViewById(R.id.btnPremium);
        imageView2 = view.findViewById(R.id.imageView2);
        adView = view.findViewById(R.id.adView);
        donar1 = view.findViewById(R.id.donar1);
        donar2 = view.findViewById(R.id.donar2);
        donar3 = view.findViewById(R.id.donar3);
        donar4 = view.findViewById(R.id.donar4);
        donar5 = view.findViewById(R.id.donar5);
        donarname1 = view.findViewById(R.id.donarname1);
        donarname2 = view.findViewById(R.id.donarname2);
        donarname3 = view.findViewById(R.id.donarname3);
        donarname4 = view.findViewById(R.id.donarname4);
        donarname5 = view.findViewById(R.id.donarname5);
        textViewViewMore = view.findViewById(R.id.textViewViewMore);
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        currentUserEmail = getCurrentUserEmail();
        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Activity activity = getActivity();
        if (activity != null) {
            loadingBar = new CustomLoadingBar(activity);
            loadingBar.show();
        }
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
        textViewViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DonarListView.class);
                startActivity(intent);
            }
        });
        btnPremium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Premium.class);
                startActivity(intent);
            }
        });
        getDonorsByDonationStatus();
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
                    String aadharNumber = userSnapshot.child("aadharNumber").getValue(String.class);
                    String profile = dataSnapshot.child("profilePictureUrl").getValue(String.class);
                    SharedPreferences sharedPreferences5 = getContext().getSharedPreferences("MyPrefs5", MODE_PRIVATE);
                    SharedPreferences.Editor editor5 = sharedPreferences5.edit();
                    editor5.putString("profilePhoto", profile);
                    editor5.apply();
                    Log.e("UserID", "User ID retrieved: " + userid);
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ProfileData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userid", userid);
                    editor.putString("aadharNumber",aadharNumber);
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
                                            SharedPreferences preferences = requireContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
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
    public void logoutUser() {
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
            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(button, "alpha", 0f, 1f);
            fadeIn.setDuration(1000); // Set duration of the animation (in milliseconds)
            fadeIn.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    button.setAlpha(1f);
                }
            });
            fadeIn.start();
        }
    public void getDonorsByDonationStatus() {
        DatabaseReference bloodDonationRef = FirebaseDatabase.getInstance().getReference().child("blooddonation");
        bloodDonationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Firebase", "Data retrieval started");
                for (DataSnapshot donorSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot childSnapshot : donorSnapshot.getChildren()) {
                        if (count < 5 && childSnapshot.child("donation_status").exists() && childSnapshot.child("donation_status").getValue(String.class).equals("DONATED")) {
                            String fullName = childSnapshot.child("fullName").getValue(String.class);
                            String profilePhoto = childSnapshot.child("profilePhoto").getValue(String.class);
                            if (fullName != null && profilePhoto != null) {
                                String capitalizedFullName = fullName.substring(0, 1).toUpperCase() + fullName.substring(1).toLowerCase();
                                count++;
                                // Get the TextView and ImageView using helper methods
                                TextView nameTextView = findDonorNameTextViewById(count);
                                ImageView photoImageView = findProfilePhotoImageViewById(count);
                                nameTextView.setText(capitalizedFullName);
                                if (!profilePhoto.isEmpty()) {
                                    // If profile photo exists, load it with Glide
                                    Glide.with(Tab1Fragment.this)
                                            .load(profilePhoto)
                                            .apply(RequestOptions.circleCropTransform())
                                            .into(photoImageView);
                                } else {
                                    // If profile photo doesn't exist, set default image
                                    photoImageView.setImageResource(R.drawable.profi);
                                }
                                Log.d("Firebase", "Donor #" + count + " retrieved: " + capitalizedFullName);
                            }
                        }
                    }
                }
                Log.d("Firebase", "Data retrieval completed");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
                Log.e("Debug", "Database error: " + databaseError.getMessage());
            }
        });
    }
    private TextView findDonorNameTextViewById(int donorCounter) {
        String donorNameId = "donarname" + donorCounter;
        int donorNameResId = getResources().getIdentifier(donorNameId, "id", getActivity().getPackageName());
        TextView textView = getActivity().findViewById(donorNameResId);
        Log.d("Firebase", "TextView found: " + textView);
        return textView;
    }

    private ImageView findProfilePhotoImageViewById(int donorCounter) {
        String profilePhotoId = "donar" + donorCounter;
        int profilePhotoResId = getResources().getIdentifier(profilePhotoId, "id", getActivity().getPackageName());
        ImageView imageView = getActivity().findViewById(profilePhotoResId);
        Log.d("Firebase", "ImageView found: " + imageView);
        return imageView;
    }
}