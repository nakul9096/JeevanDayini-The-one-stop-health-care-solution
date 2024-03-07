package com.example.unityapp;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class FragmentAReg1 extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 10;
    private static final String PREFERENCES_NAME = "MyPreferences";
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    UserData userData = new UserData();
    private Spinner adminTypeSpinner;
    private Spinner stateSpinner;
    private Spinner citySpinner;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String verificationId;
    private TextInputEditText etOrganizationName;
    private TextInputEditText etOrganizationPhone;
    private TextInputEditText etEmail;
    private TextInputEditText etaadhaar;
    private Button nextButton;
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;
    private Uri imageUri;
    private TextView textviewprofile;
    private StorageReference storageReference;
    private EditText etOtp;
    private Button btnVerifyOtp;
    private Button btnAadharImageUpload;
    private ProgressBar aadharProgressBar;
    private Button sendOTPButton;
    private TextView succestv;
    private ImageView FullNameIcon;
    private ActivityResultLauncher<Intent> aadharImagePickerLauncher;
    private CardView profilePictureCard;
    private LinearLayout layoutViewAadharImage;
    private Button btnViewAadharImage;
    private ImageButton btnRemoveAadharImage;
    private boolean backPressedOnce = false;
    private AtomicBoolean isPhoneNumberVerified = new AtomicBoolean(false);
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            nextButton.setEnabled(isFormValid());
        }

        @Override
        public void afterTextChanged(Editable s) {
            // Implement actions to be taken after text is changed.
        }
    };

    public FragmentAReg1() {
    }

    public static FragmentAReg2 newInstance(UserData userData) {
        FragmentAReg2 fragment = new FragmentAReg2();
        Bundle args = new Bundle();
        args.putParcelable("userData", userData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_a_reg1, container, false);
        adminTypeSpinner = view.findViewById(R.id.spinnerAdminType);
        setUpOTPVerificationCallback();
        succestv = view.findViewById(R.id.successtv);
        sendOTPButton = view.findViewById(R.id.btnSendOTP);
        layoutViewAadharImage = view.findViewById(R.id.layoutViewAadharImage);
        btnViewAadharImage = view.findViewById(R.id.btnViewAadharImage);
        btnRemoveAadharImage = view.findViewById(R.id.btnRemoveAadharImage);
        sendOTPButton.setVisibility(View.VISIBLE);
        FullNameIcon = view.findViewById(R.id.FullNameIcon);
        profilePictureCard = view.findViewById(R.id.FullNameIcon5);
        progressBar = view.findViewById(R.id.progressBar);
        textviewprofile = view.findViewById(R.id.textviewprofile);
        storageReference = FirebaseStorage.getInstance().getReference("Aadhaar_Documents/");
        stateSpinner = view.findViewById(R.id.spinnerState2);
        sharedPreferences = requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        citySpinner = view.findViewById(R.id.spinnerCity2);
        etOrganizationName = view.findViewById(R.id.etOrganizationName);
        btnAadharImageUpload = view.findViewById(R.id.btnAadharImageUpload);
        aadharProgressBar = view.findViewById(R.id.aadharProgressBar);
        etOtp = view.findViewById(R.id.etOtp);
        btnVerifyOtp = view.findViewById(R.id.btnVerifyOtp);
        etOrganizationPhone = view.findViewById(R.id.etOrganizationPhone);
        etEmail = view.findViewById(R.id.etEmail);
        etaadhaar = view.findViewById(R.id.etAdharNumber);
        nextButton = view.findViewById(R.id.btnNextStep1);
        ArrayAdapter<CharSequence> adminTypeAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.admin_type_options, android.R.layout.simple_spinner_item);
        adminTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adminTypeSpinner.setAdapter(adminTypeAdapter);
        adminTypeSpinner.setSelection(0);
        String[] stateOptions = getResources().getStringArray(R.array.states);
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, stateOptions);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);
        stateSpinner.setSelection(0);
        etOrganizationName.addTextChangedListener(textWatcher);
        etOrganizationPhone.addTextChangedListener(textWatcher);
        etEmail.addTextChangedListener(textWatcher);
        etOrganizationName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Update the "Next" button's state when the full name field changes
                nextButton.setEnabled(isFormValid());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        etOrganizationPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Update the "Next" button's state when the full name field changes
                nextButton.setEnabled(isFormValid());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Update the "Next" button's state when the full name field changes
                nextButton.setEnabled(isFormValid());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                CityUtils cityUtils = new CityUtils();
                String[] cityOptions = cityUtils.updateCitySpinner(requireContext(), position);
                ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, cityOptions);
                cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                citySpinner.setAdapter(cityAdapter);
                nextButton.setEnabled(isFormValid());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });
        etaadhaar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Update the "Next" button's state when the full name field changes
                nextButton.setEnabled(isFormValid());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        etOrganizationPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phoneNumber = s.toString();
                nextButton.setEnabled(isFormValid());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Check if the length of the entered text is 10
                if (s.length() == 10) {
                    // Enable the "Send OTP" button
                    sendOTPButton.setEnabled(true);
                } else {
                    // Disable the "Send OTP" button
                    sendOTPButton.setEnabled(false);
                }
            }
        });

        sendOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = etOrganizationPhone.getText().toString();
                isPhoneNumberVerified.set(false);
                sendOTP(phoneNumber);
                Boolean isOtpButtonEnabled = false;
                sendOTPButton.setEnabled(false);
                etOtp.setVisibility(View.VISIBLE);
                btnVerifyOtp.setVisibility(View.VISIBLE);
                // Handle UI updates, such as showing progress on the button
                // and hiding the button after sending OTP.
                // You can implement this based on your UI requirements.
            }
        });
        btnVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String enteredOtp = etOtp.getText().toString().trim();

                    if (!enteredOtp.isEmpty()) {
                        // Verify the entered OTP
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, enteredOtp);
                        signInWithPhoneAuthCredential(credential);
                    } else {
                        Toast.makeText(requireContext(), "Please enter the OTP", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("VerifyButtonClick", "Error in Verify button click: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        profilePictureCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        aadharImagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                Uri selectedImageUri = data.getData();
                                // Handle the selected Aadhar image URI
                                imageUri = selectedImageUri;
                                uploadAadharImage(); // New method for Aadhar image upload
                            }
                        }
                    }
                });

        btnAadharImageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                aadharImagePickerLauncher.launch(Intent.createChooser(intent, "Select Aadhar Image"));
            }
        });
        btnViewAadharImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the Aadhar image URL from the UserData object
                String aadharImageUrl = userData.getAadharImageUrl();

                if (aadharImageUrl != null && !aadharImageUrl.isEmpty()) {
                    // Display a dialog to view the Aadhar image
                    displayImageDialog(aadharImageUrl);
                } else {
                    // If the Aadhar image URL is null or empty, show a toast
                    Toast.makeText(requireContext(), "Aadhar image not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnRemoveAadharImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userData.setAadharImageUrl(null);
                layoutViewAadharImage.setVisibility(View.GONE);
                btnAadharImageUpload.setVisibility(View.VISIBLE);
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (isPhoneNumberVerified.get()) {
                        if (isFormValid()) {
                            if (userData.getAdminImage() == null || userData.getAdminImage().isEmpty()) {
                                Toast.makeText(requireContext(), "Please upload your profile picture", Toast.LENGTH_SHORT).show();
                                return; // Exit the method if the profile picture is not uploaded
                            }

                            // Check if the Aadhar image is uploaded
                            if (userData.getAadharImageUrl() == null || userData.getAadharImageUrl().isEmpty()) {
                                Toast.makeText(requireContext(), "Please upload your Aadhar image", Toast.LENGTH_SHORT).show();
                                return; // Exit the method if the Aadhar image is not uploaded
                            }
                            Log.d("UserData", "Next button clicked, form is valid");
                            generateAdminId();
                            String adminType = adminTypeSpinner.getSelectedItem().toString();
                            String organizationName = etOrganizationName.getText().toString().trim();
                            String organizationPhone = etOrganizationPhone.getText().toString().trim();
                            String email = etEmail.getText().toString().trim();
                            String selectedState = stateSpinner.getSelectedItem().toString();
                            String selectedCity = citySpinner.getSelectedItem().toString();
                            String aadhaarnumber = etaadhaar.getText().toString().trim();
                            userData.setAdminType(adminType);
                            userData.setOrganizationName(organizationName);
                            userData.setOrganizationPhone(organizationPhone);
                            userData.setEmail(email);
                            userData.setSelectedState(selectedState);
                            userData.setSelectedCity(selectedCity);
                            userData.setAadhaarNumber(aadhaarnumber);
                            String adminID = userData.getAdminId();
                            Log.d("UserData", "Admin Type: " + adminType);
                            Log.d("UserData", "Organization Name: " + organizationName);
                            Log.d("UserData", "Organization Phone: " + organizationPhone);
                            Log.d("UserData", "Email: " + email);
                            Log.d("UserData", "Selected State: " + selectedState);
                            Log.d("UserData", "Selected City: " + selectedCity);
                            Log.d("UserData", "AdminID: " + adminID);
                            Log.d("UserData", "Aadhaar Number: " + aadhaarnumber);
                            FragmentAReg2 fragmentaReg2 = FragmentAReg2.newInstance(userData);
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.registerFragmentContainer30, fragmentaReg2);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        } else {
                            Toast.makeText(requireContext(), "Please fill all the required fields!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Display a toast indicating that phone number verification is pending
                        Toast.makeText(requireContext(), "Please verify your phone number first!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("Frag1", "Exception in Next button click: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    private boolean isFormValid() {
        String adminType = adminTypeSpinner.getSelectedItem().toString();
        String organizationName = etOrganizationName.getText().toString().trim();
        String organizationPhone = etOrganizationPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String selectedState = stateSpinner.getSelectedItem().toString();
        String selectedCity = citySpinner.getSelectedItem().toString();
        String aadhaarNumber = etaadhaar.getText().toString().trim();
        return !adminType.equals("Select options") && !organizationName.isEmpty() &&
                !organizationPhone.isEmpty() && !email.isEmpty() && !aadhaarNumber.isEmpty() &&
                !selectedState.equals("Select options") && !selectedCity.equals("select state first!");
    }

    private void displayImageDialog(String imageUrl) {
        // Create a dialog with a custom layout to display the image
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_image_viewer);

        // Load the image into an ImageView using Glide or Picasso
        ImageView imageView = dialog.findViewById(R.id.imageView);
        Glide.with(this).load(imageUrl).into(imageView);

        // Close the dialog when the user clicks outside of the image
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void generateAdminId() {
        // Get the first character of the selected state
        String selectedState = stateSpinner.getSelectedItem().toString();
        char stateChar = selectedState.charAt(0);

        // Get the first character of the selected city
        String selectedCity = citySpinner.getSelectedItem().toString();
        char cityChar = selectedCity.charAt(0);

        // Generate 4 random digits
        Random random = new Random();
        int randomDigits = random.nextInt(10000); // Random 4-digit number

        // Format the admin ID as per your requirement (e.g., MHXXXX)
        String adminId = String.format("%c%c%04d", stateChar, cityChar, randomDigits);

        // Set the generated admin ID in your UserData object
        userData.setAdminId(adminId);
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Handle the result for the image picker
            imageUri = data.getData();
            uploadImage();
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            progressBar.setVisibility(View.VISIBLE);
            String imageName = "profile_picture_" + System.currentTimeMillis() + ".jpg";
            StorageReference imageReference = storageReference.child(imageName);
            imageReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully
                        progressBar.setVisibility(View.GONE);
                        imageReference.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            String imageUrl = downloadUri.toString();
                            Toast.makeText(getContext(), "Image upload success!", Toast.LENGTH_SHORT).show();
                            textviewprofile.setVisibility(View.GONE);
                            Glide.with(requireContext())
                                    .load(imageUrl)
                                    .apply(new RequestOptions()
                                            .placeholder(R.drawable.placeholder_image)
                                            .error(R.drawable.placeholder)
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .skipMemoryCache(true)
                                            .transform(new CircleCrop())) // Apply circular transformation
                                    .into(new CustomTarget<Drawable>() {
                                        @Override
                                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                            // Set the background of the CardView with the loaded image drawable
                                            FullNameIcon.setImageDrawable(null);
                                            profilePictureCard.setBackground(resource);
                                        }

                                        @Override
                                        public void onLoadCleared(@Nullable Drawable placeholder) {
                                            // Placeholder cleanup if needed
                                        }
                                    });

                            userData.setAdminImage(imageUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void uploadAadharImage() {
        if (imageUri != null) {
            aadharProgressBar.setVisibility(View.VISIBLE);
            String imageName = "aadhar_image_" + System.currentTimeMillis() + ".jpg";
            StorageReference imageReference = storageReference.child(imageName);
            imageReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        aadharProgressBar.setVisibility(View.GONE);
                        imageReference.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            String imageUrl = downloadUri.toString();
                            Toast.makeText(getContext(), "Aadhar Image upload success!", Toast.LENGTH_SHORT).show();
                            userData.setAadharImageUrl(imageUrl);
                            btnAadharImageUpload.setVisibility(View.GONE);
                            layoutViewAadharImage.setVisibility(View.VISIBLE);
                        });
                    })
                    .addOnFailureListener(e -> {
                        aadharProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Aadhar Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }).addOnProgressListener(taskSnapshot -> {
                        long totalBytes = taskSnapshot.getTotalByteCount();
                        if (totalBytes > 0) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / totalBytes;
                            aadharProgressBar.setProgress((int) progress);
                        }
                    });
        }
    }

    private void sendOTP(String phoneNumber) {
        String formattedPhoneNumber = "+91" + phoneNumber;
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(formattedPhoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(requireActivity())
                .setCallbacks(callbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void setUpOTPVerificationCallback() {
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                String phoneNumber = etOrganizationPhone.getText().toString();
                Log.d("FragmentReg2", "Verification completed. Phone number: " + phoneNumber);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("phoneNumber", phoneNumber);
                editor.apply();
                Log.d("FragmentReg2", "Phone number stored in SharedPreferences: " + phoneNumber);
                FragmentAReg2 fragmentAReg2 = new FragmentAReg2();
                Bundle args = new Bundle();
                args.putParcelable("userData", userData);
                userData.setPhoneNumber(etOrganizationPhone.getText().toString());
                fragmentAReg2.setArguments(args);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Log.e("VerificationFailed", "Invalid credentials: " + e.getMessage());
                    Toast.makeText(requireContext(), "Invalid phone number format", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("VerificationFailed", "Verification failed: " + e.getMessage());
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
                        FragmentAReg2 fragmentAReg2 = new FragmentAReg2();
                        Bundle args = new Bundle();
                        args.putParcelable("userData", userData);
                        fragmentAReg2.setArguments(args);
                        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("phoneNumber", userData.getPhoneNumber());
                        editor.apply();
                        etOtp.setVisibility(View.GONE);
                        btnVerifyOtp.setVisibility(View.GONE);
                        sendOTPButton.setVisibility(View.GONE);
                        etOrganizationPhone.setEnabled(false);
                        succestv.setVisibility(View.VISIBLE);
                        isPhoneNumberVerified.set(true);
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