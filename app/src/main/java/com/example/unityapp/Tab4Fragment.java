package com.example.unityapp;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Tab4Fragment extends Fragment {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private Button passbtn;
    private Button logoutButton;
    private Button EditProfileButton;
    Button btnEarnedBadges;
    Switch profilePrivacySwitch;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab4, container, false);
        btnEarnedBadges = view.findViewById(R.id.btnEarnedBadges);
        ImageView imageView = view.findViewById(R.id.profilePhoto);
        profilePrivacySwitch = view.findViewById(R.id.profilePrivacySwitch);
        TextView textViewFullName = view.findViewById(R.id.nameTextView);
        TextView textViewGender = view.findViewById(R.id.genderTextView);
        TextView textViewPhone = view.findViewById(R.id.phoneTextView);
        TextView textViewEmail = view.findViewById(R.id.emailTextView);
        TextView textViewCity = view.findViewById(R.id.cityTextView);
        TextView textViewState = view.findViewById(R.id.stateTextView);
        TextView textViewUserid = view.findViewById(R.id.userIdTextView);
        EditProfileButton = view.findViewById(R.id.EditProfileButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        TextView textViewBG = view.findViewById(R.id.bloodGroupTextView);
        TextView textViewAadhar = view.findViewById(R.id.aadharNumberTextView);
        ProgressBar progressBar1 = view.findViewById(R.id.progressBar1);
        ProgressBar progressBar2 = view.findViewById(R.id.progressBar2);
        passbtn = view.findViewById(R.id.changePasswordButton);
        passbtn.setOnClickListener(v -> changePass());
        progressBar1.setVisibility(View.VISIBLE);
        progressBar2.setVisibility(View.VISIBLE);
        mAuth = FirebaseAuth.getInstance();
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                if(activity!=null){
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getContext(), HomeDashboard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
        EditProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LoginDevices.class);
                startActivity(intent);
            }
        });
        btnEarnedBadges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),Badge.class);
                startActivity(intent);
            }
        });
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");
        profilePrivacySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String visibility = isChecked ? "private" : "public";
                String email = currentUser.getEmail();
                // Assuming currentUser.getEmail() is non-null; otherwise, you should add a null check.
                String sanitizedEmail = email.replace("@", "").replace(".", "");
                DatabaseReference usersRef = database.getReference().child("users");
                DatabaseReference userRef = usersRef.child(sanitizedEmail); // Correctly reference the specific user

                Map<String, Object> updates = new HashMap<>();
                updates.put("visibility", visibility);

                userRef.updateChildren(updates, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            Toast.makeText(getContext(), "Preferences updated successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to update preferences: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        if (currentUser != null) {
            String userUid = currentUser.getUid();
            Log.d("UserData1", "useruid: " + userUid);
            String email = currentUser.getEmail();
            String sanitizedEmail = email.replace("@", "").replace(".", "");
            DatabaseReference userRef = mDatabaseReference.child("users").child(sanitizedEmail);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        progressBar2.setVisibility(View.GONE);
                        String fullName = dataSnapshot.child("fullName").getValue(String.class);
                        String phone = dataSnapshot.child("phoneNumber").getValue(String.class);
                        String email = dataSnapshot.child("email").getValue(String.class);
                        String gender = dataSnapshot.child("gender").getValue(String.class);
                        String bg = dataSnapshot.child("selectedBloodGroup").getValue(String.class);
                        String city = dataSnapshot.child("selectedCity").getValue(String.class);
                        String state = dataSnapshot.child("selectedState").getValue(String.class);
                        String aadhar = dataSnapshot.child("aadharNumber").getValue(String.class);
                        String profile = dataSnapshot.child("profilePictureUrl").getValue(String.class);
                        String userid = dataSnapshot.child("userid").getValue(String.class);
                        Log.d("UserData1", "URL: " + profile);
                        Log.d("UserData1", "Full Name: " + fullName);
                        Log.d("UserData1", "email: " + email);
                        textViewFullName.setText(fullName);
                        textViewEmail.setText(email);
                        textViewGender.setText(gender);
                        textViewState.setText(state);
                        textViewCity.setText(city);
                        textViewPhone.setText(phone);
                        textViewBG.setText(bg);
                        textViewAadhar.setText(aadhar);
                        textViewUserid.setText(userid);
                        String imageUrl = profile;
                        Activity activity = getActivity();
                        if (activity != null) {
                            Glide.with(activity)
                                    .load(imageUrl)
                                    .circleCrop()
                                    .placeholder(R.drawable.circle_mask)
                                    .into(imageView);
                            progressBar1.setVisibility(View.GONE);
                        }
                    } else {
                        progressBar1.setVisibility(View.GONE);
                        progressBar2.setVisibility(View.GONE);
                        Log.d("UserData1", "No data available at this moment!.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("UserData1", "Error fetching data: " + databaseError.getMessage());
                }
            });
        }
        return view;
    }

    public void changePass() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Are you sure you want to reset your password? You will be logged out.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked "Yes," log out and send the password reset email
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
                        // User clicked "No," do nothing
                    }
                });
        // Create and show the AlertDialog
        builder.create().show();
    }
}
