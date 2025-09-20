package com.example.unityapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.WriterException;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
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
    private Button passbtn, logoutButton, EditProfileButton, btnEarnedBadges, nfcButton, readNfcButton, getQrButton;
    private Switch profilePrivacySwitch;
    private NfcAdapter nfcAdapter;
    private static final String TAG = "Tab4FragmentNFC";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab4, container, false);

        // UI Initialization
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
        passbtn = view.findViewById(R.id.changePasswordButton);
        nfcButton = view.findViewById(R.id.nfcButton);
        readNfcButton = view.findViewById(R.id.readNfcButton);
        getQrButton = view.findViewById(R.id.getQrButton); // Initialize the new button

        mAuth = FirebaseAuth.getInstance();
        nfcAdapter = NfcAdapter.getDefaultAdapter(getContext());
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Add press effect to all buttons
        ViewUtils.addPressEffect(passbtn);
        ViewUtils.addPressEffect(logoutButton);
        ViewUtils.addPressEffect(EditProfileButton);
        ViewUtils.addPressEffect(btnEarnedBadges);
        ViewUtils.addPressEffect(nfcButton);
        ViewUtils.addPressEffect(readNfcButton);
        ViewUtils.addPressEffect(getQrButton);

        // Set up click listeners
        logoutButton.setOnClickListener(v -> {
            Activity activity = getActivity();
            if (activity != null) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), HomeDashboard.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });

        EditProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), LoginDevices.class);
            startActivity(intent);
        });

        btnEarnedBadges.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Badge.class);
            startActivity(intent);
        });

        nfcButton.setOnClickListener(v -> {
            Log.d(TAG, "NFC Write button clicked. Calling handleUpdateNfcClick.");
            handleUpdateNfcClick(textViewFullName, textViewUserid, textViewGender, textViewState, textViewCity, textViewPhone, textViewBG, textViewAadhar);
        });

        readNfcButton.setOnClickListener(v -> {
            Log.d(TAG, "NFC Read button clicked. Calling handleReadNfcClick.");
            handleReadNfcClick();
        });

        getQrButton.setOnClickListener(v -> {
            if (currentUser != null && currentUser.getEmail() != null) {
                String email = currentUser.getEmail();
                String sanitizedEmail = email.replace("@", "").replace(".", "");

                try {
                    Bitmap qrCodeBitmap = QrCodeGenerator.generateQrCode(sanitizedEmail);
                    if (qrCodeBitmap != null) {
                        showQrCodeDialog(qrCodeBitmap);
                    } else {
                        Toast.makeText(getContext(), "Failed to generate QR code.", Toast.LENGTH_SHORT).show();
                    }
                } catch (WriterException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error generating QR code.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "User email not available.", Toast.LENGTH_SHORT).show();
            }
        });

        profilePrivacySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (currentUser != null && currentUser.getEmail() != null) {
                String visibility = isChecked ? "private" : "public";
                String email = currentUser.getEmail();
                String sanitizedEmail = email.replace("@", "").replace(".", "");
                DatabaseReference usersRefLocal = database.getReference().child("users");
                DatabaseReference userRefLocal = usersRefLocal.child(sanitizedEmail);
                Map<String, Object> updates = new HashMap<>();
                updates.put("visibility", visibility);
                userRefLocal.updateChildren(updates, (databaseError, databaseReference) -> {
                    if (databaseError == null) {
                        Helper.showToast(getActivity(), "Preferences updated successfully!");
                    } else {
                        Helper.showToast(getActivity(), "Failed to update preferences: ");
                    }
                });
            }
        });

        if (currentUser != null) {
            String email = currentUser.getEmail();
            String sanitizedEmail = email.replace("@", "").replace(".", "");
            DatabaseReference userRef = mDatabaseReference.child("users").child(sanitizedEmail);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String fullName = dataSnapshot.child("fullName").getValue(String.class);
                        String phone = dataSnapshot.child("phoneNumber").getValue(String.class);
                        String emailVal = dataSnapshot.child("email").getValue(String.class);
                        String gender = dataSnapshot.child("gender").getValue(String.class);
                        String bg = dataSnapshot.child("selectedBloodGroup").getValue(String.class);
                        String city = dataSnapshot.child("selectedCity").getValue(String.class);
                        String state = dataSnapshot.child("selectedState").getValue(String.class);
                        String aadhar = dataSnapshot.child("aadharNumber").getValue(String.class);
                        String profile = dataSnapshot.child("profilePictureUrl").getValue(String.class);
                        String userid = dataSnapshot.child("userid").getValue(String.class);
                        textViewFullName.setText(fullName);
                        textViewEmail.setText(emailVal);
                        textViewGender.setText(gender);
                        textViewState.setText(state);
                        textViewCity.setText(city);
                        textViewPhone.setText(phone);
                        textViewBG.setText(bg);
                        textViewAadhar.setText(aadhar);
                        textViewUserid.setText(userid);
                        Activity activity = getActivity();
                        if (activity != null) {
                            Glide.with(activity)
                                    .load(profile)
                                    .circleCrop()
                                    .placeholder(R.drawable.circle_mask)
                                    .into(imageView);
                        }
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
    private void showQrCodeDialog(Bitmap qrCodeBitmap) {
        Dialog qrDialog = new Dialog(getContext());
        qrDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        qrDialog.setContentView(R.layout.dialog_show_qr_code);

        ImageView qrCodeImageView = qrDialog.findViewById(R.id.qrCodeImageView);
        Button closeButton = qrDialog.findViewById(R.id.closeButton);

        qrCodeImageView.setImageBitmap(qrCodeBitmap);

        closeButton.setOnClickListener(v -> qrDialog.dismiss());

        qrDialog.show();
    }
    private void handleUpdateNfcClick(TextView textViewFullName, TextView textViewUserid, TextView textViewGender, TextView textViewState, TextView textViewCity, TextView textViewPhone, TextView textViewBG, TextView textViewAadhar) {
        if (nfcAdapter == null || !nfcAdapter.isEnabled()) {
            Toast.makeText(getContext(), "NFC is not available or turned off.", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullName = textViewFullName.getText().toString();
        String userId = textViewUserid.getText().toString();
        String gender = textViewGender.getText().toString();
        String state = textViewState.getText().toString();
        String city = textViewCity.getText().toString();
        String phone = textViewPhone.getText().toString();
        String bg = textViewBG.getText().toString();
        String aadhar = textViewAadhar.getText().toString();

        if (fullName.isEmpty() || userId.isEmpty()) {
            Toast.makeText(getContext(), "Profile data not loaded yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(getContext(), NfcActivity.class);
        intent.putExtra("NFC_MODE", "write");
        intent.putExtra("FULL_NAME", fullName);
        intent.putExtra("USER_ID", userId);
        intent.putExtra("GENDER", gender);
        intent.putExtra("STATE", state);
        intent.putExtra("CITY", city);
        intent.putExtra("PHONE", phone);
        intent.putExtra("BLOOD_GROUP", bg);
        intent.putExtra("AADHAR", aadhar);
        startActivity(intent);
    }

    private void handleReadNfcClick() {
        if (nfcAdapter == null || !nfcAdapter.isEnabled()) {
            Toast.makeText(getContext(), "NFC is not available or turned off.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(getContext(), NfcActivity.class);
        intent.putExtra("NFC_MODE", "read");
        startActivity(intent);
    }
}