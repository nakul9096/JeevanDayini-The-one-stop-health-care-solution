package com.example.unityapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FragmentReg1 extends Fragment {
    private static final String PREFERENCES_NAME = "MyPreferences";
    UserData userData = new UserData();
    private SharedPreferences sharedPreferences;
    private Spinner stateSpinner;
    private Spinner citySpinner;
    private Spinner bloodGroupSpinner;
    private EditText fullNameEditText;
    private EditText emailEditText;
    private RadioGroup genderRadioGroup;
    private ImageView profilePictureImageView;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri;
    private boolean isImageSelected = false;
    private ProgressBar progressBar;
    private boolean isImageUploadSuccessful = false;
    private boolean backPressedOnce = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reg1, container, false);
        stateSpinner = view.findViewById(R.id.spinnerState2);
        citySpinner = view.findViewById(R.id.spinnerCity2);
        bloodGroupSpinner = view.findViewById(R.id.spinnerBloodGroup);
        fullNameEditText = view.findViewById(R.id.FullName);
        emailEditText = view.findViewById(R.id.etEmail);
        profilePictureImageView = view.findViewById(R.id.FullNameIcon);
        genderRadioGroup = view.findViewById(R.id.radioGroupGender);
        Button nextButton = view.findViewById(R.id.btnNextStep1);
        progressBar = view.findViewById(R.id.progressBar);
        String[] stateOptions = getResources().getStringArray(R.array.states);
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, stateOptions);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                selectedImageUri = data.getData();
                                isImageSelected = true;
                                profilePictureImageView.setImageURI(selectedImageUri);
                                String localFileName = getLocalFileNameFromUri(selectedImageUri);
                                userData.setLocalFileName(localFileName);
                                showConfirmationDialog();
                                nextButton.setEnabled(isFormValid() && isImageSelected);
                            }
                        }
                    }
                });
        profilePictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Upload", "Image picker button clicked");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                imagePickerLauncher.launch(Intent.createChooser(intent, "Select Picture"));
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
            }
        });
        String[] bloodGroupOptions = getResources().getStringArray(R.array.blood_group_options);
        ArrayAdapter<String> bloodGroupAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, bloodGroupOptions);
        bloodGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodGroupSpinner.setAdapter(bloodGroupAdapter);
        fullNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nextButton.setEnabled(isFormValid());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Update the "Next" button's state when the email field changes
                nextButton.setEnabled(isFormValid());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                nextButton.setEnabled(isFormValid());
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFormValid() && isImageSelected && isImageUploadSuccessful) {
                    String randomizedString = generateRandomString(3) + generateRandomNumber(3);
                    userData.setFullName(fullNameEditText.getText().toString());
                    userData.setSelectedState(stateSpinner.getSelectedItem().toString());
                    userData.setSelectedCity(citySpinner.getSelectedItem().toString());
                    userData.setSelectedBloodGroup(bloodGroupSpinner.getSelectedItem().toString());
                    userData.setEmail(emailEditText.getText().toString());
                    int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
                    RadioButton selectedGenderRadioButton = view.findViewById(selectedGenderId);
                    String selectedGender = selectedGenderRadioButton.getText().toString();
                    userData.setGender(selectedGender);
                    userData.setUserid(randomizedString);
                    Log.d("UserData", "Full Name: " + userData.getFullName());
                    Log.d("UserData", "Selected State: " + userData.getSelectedState());
                    Log.d("UserData", "Selected City: " + userData.getSelectedCity());
                    Log.d("UserData", "Selected Blood Group: " + userData.getSelectedBloodGroup());
                    Log.d("UserData", "Email: " + userData.getEmail());
                    Log.d("UserData", "User id: " + userData.getUserid());
                    sharedPreferences = requireActivity().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userid", userData.getUserid());
                    editor.apply();
                    FragmentReg2 fragmentReg2 = FragmentReg2.newInstance(userData);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.registerFragmentContainer, fragmentReg2);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else {
                    Toast.makeText(requireContext(), "Please fill all the required fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        nextButton.setEnabled(isFormValid());
        return view;
    }

    private void saveSelectedState(int statePosition) {
        sharedPreferences = requireActivity().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("selectedStatePosition", statePosition);
        editor.apply();
    }

    private boolean isFormValid() {
        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        int genderRadioButtonId = genderRadioGroup.getCheckedRadioButtonId();
        return !fullName.isEmpty() && !email.isEmpty() && isValidEmail(email) && genderRadioButtonId != -1;
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Are you sure you want to upload this image?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        uploadProfilePictureToFirebase();
                    }
                })
                .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    private void uploadProfilePictureToFirebase() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String uniqueFilename = generateUniqueFilename();
        StorageReference profilePictureRef = storageRef.child("profile_pictures/" + uniqueFilename + ".jpg");
        progressBar.setVisibility(View.VISIBLE);

        profilePictureRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Handle a successful upload here
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Upload Successful", Toast.LENGTH_SHORT).show();
                    isImageUploadSuccessful = true;
                    profilePictureRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        Log.d("Upload", "Download URL: " + downloadUrl);
                        userData.setProfilePictureUrl(downloadUrl);
                        Log.d("Upload", "Image URL stored in user data: " + userData.getProfilePictureUrl());
                        saveDownloadUrl(downloadUrl);
                    });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Upload", "Upload Failed: " + e.getMessage());
                    e.printStackTrace();
                });
    }

    private String getLocalFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = requireActivity().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                cursor.moveToFirst();
                result = cursor.getString(nameIndex);
                cursor.close();
            }
        } else if (uri.getScheme().equals("file")) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    private String generateUniqueFilename() {
        long timestamp = System.currentTimeMillis();
        String randomString = generateRandomString(10);
        String uniqueFilename = timestamp + "_" + randomString;
        return uniqueFilename;
    }

    private String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            stringBuilder.append(characters.charAt(index));
        }
        return stringBuilder.toString();
    }

    private String generateRandomNumber(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomNumber = (int) (Math.random() * 10);
            stringBuilder.append(randomNumber);
        }
        return stringBuilder.toString();
    }

    private void saveDownloadUrl(String downloadUrl) {
        sharedPreferences = requireActivity().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("downloadUrl", downloadUrl);
        editor.apply();
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (backPressedOnce) {
                    requireActivity().finish();
                } else {
                    backPressedOnce = true;
                    Toast.makeText(requireContext(), "Press back again to exit form", Toast.LENGTH_SHORT).show();
                    new android.os.Handler().postDelayed(() -> backPressedOnce = false, 2000);
                }
            }
        });
    }
}