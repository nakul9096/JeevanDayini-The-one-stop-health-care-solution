package com.example.unityapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Tab2Fragment extends Fragment {
    Button bt3, bt4, bt5, bt6, btnew, btnPrescription, btnTestReport, btn4new;
    private String retrievedFullName;
    private CustomLoadingBar loadingBar;
    private static final String PREFS_NAME2 = "ProfileData";
    public Tab2Fragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab2, container, false);
        btnPrescription = rootView.findViewById(R.id.btnPrescription);
        btnTestReport = rootView.findViewById(R.id.btnTestReport);
        bt3 = rootView.findViewById(R.id.btn3);
        bt5 = rootView.findViewById(R.id.btn5);
        bt6 = rootView.findViewById(R.id.btn6);
        btnew = rootView.findViewById(R.id.btnnew);
        btn4new = rootView.findViewById(R.id.btn4new);
        bt4 = rootView.findViewById(R.id.btn4);
        loadingBar = new CustomLoadingBar(getActivity());
        btnPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TestReport.class);
                startActivity(intent);
            }
        });
        btnTestReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TestReport2.class);
                startActivity(intent);
            }
        });
        btnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingIndicator();
                String userEmail = getCurrentUserEmail();
                Log.d("EmailCheck", "User Email Before Sanitization: " + userEmail);
                String sanitizedEmail = sanitizeEmail(userEmail);
                Log.d("EmailCheck", "User Email after sanitization: " + sanitizedEmail);
                SharedPreferences preferences = getContext().getSharedPreferences(PREFS_NAME2, MODE_PRIVATE);
                String userId = preferences.getString("userid", "");
                checkDataInDatabase(userEmail, userId);
            }
        });
        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FindClinics.class);
                intent.putExtra("requestType","findclinics");
                startActivity(intent);
            }
        });
        btn4new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Jeevancard.class);
                startActivity(intent);
            }
        });
        bt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MedicineReminder.class);
                startActivity(intent);
            }
        });
        bt5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Suggestion.showSuggestionDialog(getActivity());
            }
        });
        bt6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingIndicator();
                retrieveFullNameFromUsers();
            }
        });

        return rootView;
    }
    private String getCurrentUserEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null ? user.getEmail() : "";
    }

    private void showLoadingIndicator() {
        loadingBar.show();
    }

    private void retrieveFullNameFromUsers() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String userEmail = getCurrentUserEmail();

            if (userEmail != null) {
                String sanitizedEmail = userEmail.replace("@", "").replace(".", "");
                DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("users");
                usersReference.child(sanitizedEmail).child("fullName").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String fullName = dataSnapshot.getValue(String.class);
                            if (fullName != null) {
                                Log.d("FullName", "User's full name: " + fullName);
                                handleFullName(fullName);
                                showRatingDialog();
                            } else {
                                Log.d("FullName", "Full name is null");
                            }
                        } else {
                            Log.d("FullName", "No data for full name");
                        }
                        hideLoadingIndicator();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("FullName", "Error retrieving full name: " + databaseError.getMessage());
                        hideLoadingIndicator();
                    }
                });
            }
        }
    }
    private void hideLoadingIndicator() {
        loadingBar.dismiss();
    }
    private String sanitizeEmail(String email) {
        return email.replace("@", "").replace(".", "");
    }
    private void checkDataInDatabase(String email, String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("blooddonation").child(userId);
        reference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Intent intent = new Intent(getActivity(), ApplicationStatus.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), DonarType.class);
                    startActivity(intent);
                }
                hideLoadingIndicator();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideLoadingIndicator();
                Helper.showToast(getActivity(), "Error checking data in the database");
            }
        });
    }
    private void handleFullName(String fullName) {
        retrievedFullName = fullName;
    }
    private void showRatingDialog() {
        String userEmail = getCurrentUserEmail();
        RatingDialog.showRatingDialog(getActivity(), userEmail, retrievedFullName);
    }
}