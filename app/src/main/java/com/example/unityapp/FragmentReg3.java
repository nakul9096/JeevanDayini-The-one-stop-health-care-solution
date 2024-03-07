package com.example.unityapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class FragmentReg3 extends Fragment {
    private EditText aadharNumberEditText;
    private EditText passwordEditText;
    private SharedPreferences sharedPreferences;
    private EditText reenteredPassword;
    private UserData userData;
    private Button submitButton;
    private TextView pwd1, pwd3, pwd4, userIdTextView;
    private boolean isUppercaseValid;
    private boolean isDigitValid;
    private boolean isSpecialCharValid;
    private boolean backPressedOnce = false;
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            isUppercaseValid = false;
            isDigitValid = false;
            isSpecialCharValid = false;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Check Aadhar number length and password length
            String aadharNumber = aadharNumberEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            boolean isAadharValid = aadharNumber.length() == 12;
            boolean isPasswordValid = password.length() >= 8;
            isUppercaseValid = containsUppercase(password);
            isDigitValid = containsDigit(password);
            isSpecialCharValid = containsSpecialCharacter(password);
            // Change text color of TextViews based on validation
            updateTextColor(containsUppercase(password), pwd1);  // Use atleast 1 uppercase character
            updateTextColor(containsDigit(password), pwd3);  // Use atleast 1 numeric digit
            updateTextColor(containsSpecialCharacter(password), pwd4);  // Use atleast 1 special character
            submitButton.setEnabled(isAadharValid && isPasswordValid && isUppercaseValid && isDigitValid && isSpecialCharValid);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }

        // Helper method to update text color of TextView
        private void updateTextColor(boolean condition, TextView textView) {
            int color = condition ? ContextCompat.getColor(requireContext(), R.color.green) : Color.RED;
            textView.setTextColor(color);
        }
    };

    public static FragmentReg3 newInstance(UserData userData) {
        FragmentReg3 fragment = new FragmentReg3();
        Bundle args = new Bundle();
        args.putParcelable("userData", userData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userData = getArguments().getParcelable("userData");
        }
        sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("email", null);
        if (userEmail != null) {
            userData.setEmail(userEmail);
        }
        String imageUrl = sharedPreferences.getString("downloadUrl", null);
        if (imageUrl != null) {
            userData.setProfilePictureUrl(imageUrl);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reg3, container, false);
        aadharNumberEditText = view.findViewById(R.id.adhar_number);
        passwordEditText = view.findViewById(R.id.password);
        reenteredPassword = view.findViewById(R.id.re_password);
        pwd1 = view.findViewById(R.id.pwdtv1);
        pwd3 = view.findViewById(R.id.pwdtv3);
        pwd4 = view.findViewById(R.id.pwdtv4);
        userIdTextView = view.findViewById(R.id.userIdTextView);
        aadharNumberEditText.addTextChangedListener(textWatcher);
        passwordEditText.addTextChangedListener(textWatcher);
        submitButton = view.findViewById(R.id.btn_submit);
        String userid = sharedPreferences.getString("userid", "");
        userIdTextView.setText("User ID: " + userid);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String aadharNumber = aadharNumberEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String repassword = reenteredPassword.getText().toString();
                if (!aadharNumber.isEmpty() && !password.isEmpty() && !repassword.isEmpty()) {
                    if (password.equals(repassword)) {
                        String fullName = userData.getFullName();
                        String uniqueKey = fullName.replaceAll("\\s", "").replaceAll("[^a-zA-Z0-9]", "");
                        final String[] fcmToken = new String[1];
                        FirebaseMessaging.getInstance().getToken()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && task.getResult() != null) {
                                        fcmToken[0] = task.getResult();
                                        Log.d("FCM Token", fcmToken[0]);
                                    }
                                    saveDataToFirebase(userData.getEmail(), uniqueKey, aadharNumber, password, fcmToken[0]);
                                });
                    } else {
                        Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "All fields are mandatory", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private void saveDataToFirebase(String email, String uniqueKey, String aadharNumber, String password, String fcmToken) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference broadcastRef = database.getReference("broadcast");
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String uid = user.getUid();
                        Log.d("UserCreation", "User created with UID: " + uid);
                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(email.replace("@", "").replace(".", ""));
                        userData.setAadharNumber(aadharNumber);
                        userData.setPassword(password);
                        String imageUrl = userData.getProfilePictureUrl();
                        userData.setProfilePictureUrl(imageUrl);
                        userData.setFcmToken(fcmToken);
                        userData.setPlanType("free");
                        usersRef.setValue(userData);
                        String useremail = userData.getEmail();
                        String sanitizedemail = useremail.replace("@", "").replace(".", "");
                        broadcastRef.child("user").child(sanitizedemail).child("fcmToken").setValue(fcmToken);
                        showSuccessDialog();
                        Toast.makeText(requireContext(), "Registeration succcessfull!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("UserCreation", "User creation failed: " + task.getException().getMessage());
                        Toast.makeText(requireContext(), "User creation failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showSuccessDialog() {
        SuccessDialog successDialog = new SuccessDialog(requireContext());
        successDialog.setCancelable(false);
        successDialog.show();
    }

    private boolean containsUppercase(String str) {
        for (char ch : str.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsDigit(String str) {
        for (char ch : str.toCharArray()) {
            if (Character.isDigit(ch)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsSpecialCharacter(String str) {
        String specialCharacters = "!@#$%^&*()_-+=<>?/{}|";
        for (char ch : str.toCharArray()) {
            if (specialCharacters.contains(String.valueOf(ch))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Handle back button press
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (backPressedOnce) {
                    requireActivity().finish(); // Exit the activity
                } else {
                    backPressedOnce = true;
                    Toast.makeText(requireContext(), "Press back again to exit form", Toast.LENGTH_SHORT).show();
                    new android.os.Handler().postDelayed(() -> backPressedOnce = false, 2000);
                }
            }
        });
    }
}
