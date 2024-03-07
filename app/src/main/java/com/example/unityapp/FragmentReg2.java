package com.example.unityapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class FragmentReg2 extends Fragment {
    private UserData userData;
    private FirebaseAuth firebaseAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String verificationId;
    private EditText phoneNumberEditText;
    private EditText otpEditText;
    private SharedPreferences sharedPreferences;
    private Button nextButton;
    private boolean isOtpButtonEnabled = true;
    private boolean backPressedOnce = false;

    public static FragmentReg2 newInstance(UserData userData) {
        FragmentReg2 fragment = new FragmentReg2();
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
        setUpOTPVerificationCallback();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reg2, container, false);
        phoneNumberEditText = view.findViewById(R.id.phone_number);
        nextButton = view.findViewById(R.id.btnNextStep2);
        nextButton.setEnabled(false);
        phoneNumberEditText.setText("+91");
        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        otpEditText = view.findViewById(R.id.phone_otp);
        Button otpButton = view.findViewById(R.id.sendotp_phone);
        otpButton.setEnabled(false);
        final AtomicBoolean isPhoneNumberValid = new AtomicBoolean(false);
        phoneNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phoneNumber = s.toString();
                isPhoneNumberValid.set(isValidPhoneNumber(phoneNumber));
                if (isPhoneNumberValid.get() && !otpButton.isEnabled()) {
                    otpButton.setEnabled(true);
                } else if (!isPhoneNumberValid.get() && otpButton.isEnabled()) {
                    otpButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        otpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOtpButtonEnabled && isPhoneNumberValid.get()) {
                    String phoneNumber = phoneNumberEditText.getText().toString();
                    userData.setPhoneNumber(phoneNumber);
                    sendOTP(phoneNumber);
                    isOtpButtonEnabled = false;
                    otpButton.setEnabled(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isOtpButtonEnabled = true;
                            if (isPhoneNumberValid.get()) {
                                otpButton.setEnabled(true);
                            }
                        }
                    }, 30000); // Re-enable the button after 30 seconds
                } else {
                    Toast.makeText(requireContext(), "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                }
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = otpEditText.getText().toString();
                if (!otp.isEmpty()) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
                    signInWithPhoneAuthCredential(credential);
                    Log.d("FragmentReg2", "OTP verification initiated");
                } else {
                    Log.e("FragmentReg2", "Empty OTP");
                    Toast.makeText(requireContext(), "Please enter the OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private void sendOTP(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)           // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS)    // Timeout duration
                        .setActivity(requireActivity())         // Activity (for callback binding)
                        .setCallbacks(callbacks)                // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void setUpOTPVerificationCallback() {
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                String phoneNumber = phoneNumberEditText.getText().toString();
                Log.d("FragmentReg2", "Verification completed. Phone number: " + phoneNumber);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("phoneNumber", phoneNumber);
                editor.apply();
                Log.d("FragmentReg2", "Phone number stored in SharedPreferences: " + phoneNumber);
                FragmentReg3 fragmentReg3 = new FragmentReg3();
                Bundle args = new Bundle();
                args.putParcelable("userData", userData);
                userData.setPhoneNumber(phoneNumberEditText.getText().toString());
                fragmentReg3.setArguments(args);
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.registerFragmentContainer, fragmentReg3);
                transaction.addToBackStack(null);
                transaction.commit();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Log.e("FragmentReg2", "Verification failed due to invalid phone number format");
                    Toast.makeText(requireContext(), "Invalid phone number format", Toast.LENGTH_SHORT).show();
                } else {
                    // Other error, handle as needed
                    Log.e("FragmentReg2", "Verification failed: " + e.getMessage());
                    Toast.makeText(requireContext(), "Verification failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationId = s;
                Log.d("FragmentReg2", "OTP sent successfully");
                Toast.makeText(requireContext(), "OTP sent successfully", Toast.LENGTH_SHORT).show();
                nextButton.setEnabled(true);
            }
        };
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String pattern = "^\\+91[0-9]{10}$";
        return phoneNumber.matches(pattern);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Verification successful, proceed to the next fragment
                        FragmentReg3 fragmentReg3 = new FragmentReg3();
                        Bundle args = new Bundle();
                        args.putParcelable("userData", userData);
                        fragmentReg3.setArguments(args);
                        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("phoneNumber", userData.getPhoneNumber());
                        editor.apply();
                        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.registerFragmentContainer, fragmentReg3);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    } else {
                        Exception e = task.getException();
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(requireContext(), "Invalid OTP", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("VerificationFailed", "Verification failed: " + e.getMessage());
                        }
                    }
                });
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
