package com.example.unityapp;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FragmentAReg2 extends Fragment {
    private static final String IMAGE_MIME_TYPE = "image/*";
    private static final String JPEG_MIME_TYPE = "image/jpeg";
    private static final String JPG_MIME_TYPE = "image/jpg";
    private static final String PNG_MIME_TYPE = "image/png";
    private TextView selectedFileNameVertical, selectedFileNameHorizontal1, selectedFileNameHorizontal2, selectedFileNameHorizontal3;
    private ImageView selectedImageVertical, selectedImageHorizontal1, selectedImageHorizontal2, selectedImageHorizontal3;
    private Button uploadVerticalButton, uploadHorizontalButton1, uploadHorizontalButton2, uploadHorizontalButton3;
    private ProgressBar progressBar;
    private Button submitButton;
    private TextView adminIdTextView;
    private UserData userData = new UserData();
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ActivityResultLauncher<String> mGetContent;
    private ActivityResultLauncher<String> mGetContentHorizontal1;
    private ActivityResultLauncher<String> mGetContentHorizontal2;
    private ActivityResultLauncher<String> mGetContentHorizontal3;
    private boolean backPressedOnce = false;

    public static FragmentAReg2 newInstance(UserData userData) {
        FragmentAReg2 fragment = new FragmentAReg2();
        Bundle args = new Bundle();
        args.putParcelable("userData", userData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_a_reg2, container, false);
        if (getArguments() != null) {
            userData = getArguments().getParcelable("userData");
        }
        initializeViews(view);
        initializeListeners();
        initializeFirebase();

        if (getArguments() != null) {
            userData = getArguments().getParcelable("userData");
            if (userData.getAdminType().equals("Admin") || userData.getAdminType().equals("FC Admin")) {
                TextView tvtitle = view.findViewById(R.id.tvtitle);
                TextView tv1 = view.findViewById(R.id.selectedFileNameHorizontal1);
                TextView tv2 = view.findViewById(R.id.selectedFileNameHorizontal2);
                TextView tv3 = view.findViewById(R.id.selectedFileNameHorizontal3);
                ImageView img1 = view.findViewById(R.id.selectedImageVertical);
                ImageView img2 = view.findViewById(R.id.selectedImageHorizontal1);
                ImageView img3 = view.findViewById(R.id.selectedImageHorizontal2);
                ImageView img4 = view.findViewById(R.id.selectedImageHorizontal3);
                Button btn1 = view.findViewById(R.id.uploadVerticalButton);
                Button btn2 = view.findViewById(R.id.uploadHorizontalButton1);
                Button btn3 = view.findViewById(R.id.uploadHorizontalButton2);
                Button btn4 = view.findViewById(R.id.uploadHorizontalButton3);
                tvtitle.setVisibility(View.GONE);
                tv1.setVisibility(View.GONE);
                tv2.setVisibility(View.GONE);
                tv3.setVisibility(View.GONE);
                img1.setVisibility(View.GONE);
                img2.setVisibility(View.GONE);
                img3.setVisibility(View.GONE);
                img4.setVisibility(View.GONE);
                btn1.setVisibility(View.GONE);
                btn2.setVisibility(View.GONE);
                btn3.setVisibility(View.GONE);
                btn4.setVisibility(View.GONE);
            }
        }
        adminIdTextView.setText("Admin ID: " + userData.getAdminId());
        submitButton.setOnClickListener(v -> handleSubmitButtonClick());
        mGetContent = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                this::handleImageSelection
        );
        mGetContentHorizontal1 = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result -> handleHorizontalPhotoSelection(result, 1)
        );
        mGetContentHorizontal2 = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result -> handleHorizontalPhotoSelection(result, 2)
        );
        mGetContentHorizontal3 = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result -> handleHorizontalPhotoSelection(result, 3)
        );
        uploadVerticalButton.setOnClickListener(v -> openFilePicker(mGetContent));
        uploadHorizontalButton1.setOnClickListener(v -> openFilePickerForHorizontalPhoto(1));
        uploadHorizontalButton2.setOnClickListener(v -> openFilePickerForHorizontalPhoto(2));
        uploadHorizontalButton3.setOnClickListener(v -> openFilePickerForHorizontalPhoto(3));
        return view;
    }

    private void initializeViews(View view) {
        progressBar = view.findViewById(R.id.progressbar99);
        submitButton = view.findViewById(R.id.submitButton2);
        adminIdTextView = view.findViewById(R.id.adminIdTextView);

        selectedFileNameVertical = view.findViewById(R.id.selectedFileNameVertical);
        selectedImageVertical = view.findViewById(R.id.selectedImageVertical);
        uploadVerticalButton = view.findViewById(R.id.uploadVerticalButton);

        selectedFileNameHorizontal1 = view.findViewById(R.id.selectedFileNameHorizontal1);
        selectedImageHorizontal1 = view.findViewById(R.id.selectedImageHorizontal1);
        uploadHorizontalButton1 = view.findViewById(R.id.uploadHorizontalButton1);

        selectedFileNameHorizontal2 = view.findViewById(R.id.selectedFileNameHorizontal2);
        selectedImageHorizontal2 = view.findViewById(R.id.selectedImageHorizontal2);
        uploadHorizontalButton2 = view.findViewById(R.id.uploadHorizontalButton2);

        selectedFileNameHorizontal3 = view.findViewById(R.id.selectedFileNameHorizontal3);
        selectedImageHorizontal3 = view.findViewById(R.id.selectedImageHorizontal3);
        uploadHorizontalButton3 = view.findViewById(R.id.uploadHorizontalButton3);
    }

    private void initializeListeners() {
    }

    private void initializeFirebase() {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    private void handleHorizontalPhotoSelection(Uri result, int photoNumber) {
        uploadHorizontalImageToFirebaseStorage(result, photoNumber);
    }

    private void handleSubmitButtonClick() {
        boolean isVerticalImageUploaded = false;
        boolean isHorizontalImage1Uploaded = false;
        boolean isHorizontalImage2Uploaded = false;
        boolean isHorizontalImage3Uploaded = false;
        if (userData.getAdminType().equals("Admin") || userData.getAdminType().equals("FC Admin")) {
            submitWithoutImages();
            return;
        }
        if (userData.getImageUrlIdentity1() != null) {
            isVerticalImageUploaded = true;
        }

        // Upload Horizontal Images
        if (userData.getImageUrlHorizontal1() != null) {
            isHorizontalImage1Uploaded = true;
        }

        if (userData.getImageUrlHorizontal2() != null) {
            isHorizontalImage2Uploaded = true;
        }

        if (userData.getImageUrlHorizontal3() != null) {
            isHorizontalImage3Uploaded = true;
        }

        // Check if all images are uploaded successfully
        if (isVerticalImageUploaded && isHorizontalImage1Uploaded && isHorizontalImage2Uploaded && isHorizontalImage3Uploaded) {
            Toast.makeText(requireContext(), "All images uploaded successfully!", Toast.LENGTH_SHORT).show();
            // Display toast only when all images are uploaded successfully
            Log.d("UserData", "AdminType: " + userData.getAdminType());
            Log.d("UserData", "OrganizationName: " + userData.getOrganizationName());
            Log.d("UserData", "OrganizationPhone: " + userData.getOrganizationPhone());
            Log.d("UserData", "State: " + userData.getSelectedState());
            Log.d("UserData", "City: " + userData.getSelectedCity());
            Log.d("UserData", "Email: " + userData.getEmail());
            Log.d("UserData", "URL1: " + userData.getImageUrlIdentity1());
            String adminTypeText = userData.getAdminType();
            String stateText = userData.getSelectedState();
            String cityText = userData.getSelectedCity();
            String orgName = userData.getOrganizationName();
            String orgPhone = userData.getOrganizationPhone();
            String emailText = userData.getEmail();
            String url = userData.getImageUrlIdentity1();
            String adminId = userData.getAdminId();

            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    String fcmToken = task.getResult();
                    userData.setFcmToken(fcmToken);
                    String uniqueKey = emailText.replaceAll("\\s", "").replaceAll("[^a-zA-Z0-9]", "");
                    saveDataToFirebase(uniqueKey, adminTypeText, orgName, orgPhone, stateText, cityText, emailText, url, adminId, fcmToken);
                } else {
                    Toast.makeText(requireContext(), "Failed to retrieve FCM token", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // If any image is missing, show a toast indicating that all images are required
            Toast.makeText(requireContext(), "All images are required to proceed!", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleImageSelection(Uri uri) {
        if (uri != null) {
            String mimeType = getActivity().getContentResolver().getType(uri);

            if (isValidImageMimeType(mimeType)) {
                String fileName = getFileNameFromUri(uri);
                selectedFileNameVertical.setText("Selected Identity Proof: " + fileName);
                uploadVerticalButton.setVisibility(View.GONE);
                selectedImageVertical.setImageURI(uri);
                uploadImageToFirebaseStorage(uri);
            } else {
                Toast.makeText(requireContext(), "Invalid image file type. Please select a valid image.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle the case where the selected URI is null (optional)
            // You may want to show a message to the user or take appropriate action
        }
    }

    private void saveDataToFirebase(String uniqueKey, String adminType, String orgName, String orgPhone, String state, String city, String email, String url, String adminId, String fcmToken) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("Admin_requests");
        DatabaseReference broadcastRef = database.getReference("broadcast");
        userData.setAdminType(adminType);
        userData.setOrganizationName(orgName);
        userData.setOrganizationPhone(orgPhone);
        userData.setSelectedState(state);
        userData.setSelectedCity(city);
        userData.setEmail(email);
        userData.setImageUrlIdentity1(url);
        userData.setAdminId(adminId);
        userData.setFcmToken(fcmToken);

        usersRef.child(uniqueKey).setValue(userData, (error, ref) -> {
            if (error == null) {
                Log.d("UserData", "Data Upload Successful");
                showSuccessDialog();
            } else {
                String errorMessage = "Data Upload Failed: " + error.getMessage();
                Log.e("UserData", errorMessage);
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
        broadcastRef.child(adminType).child("fcmToken").setValue(fcmToken);
    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        String uniqueFilename = generateUniqueFilename();
        StorageReference imageRef = storageReference.child("Hospital_Images/" + uniqueFilename);

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Image Upload Successful", Toast.LENGTH_SHORT).show();

                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String url = uri.toString();
                        Log.d("UserData", url);
                        userData.setImageUrlIdentity1(url);
                    }).addOnFailureListener(this::handleImageUploadFailure);
                })
                .addOnFailureListener(this::handleImageUploadFailure);
    }

    private String generateUniqueFilename() {
        long timestamp = System.currentTimeMillis();
        String randomString = generateRandomString(10);
        return timestamp + "_" + randomString;
    }

    private String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            stringBuilder.append(characters.charAt(index));
        }
        return stringBuilder.toString();
    }

    private void handleImageUploadFailure(Exception e) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(requireContext(), "Image Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        Log.e("UserData", "Image Upload Failed: " + e.getMessage());
        e.printStackTrace();
    }

    private void showSuccessDialog() {
        SuccessDialog2 successDialog = new SuccessDialog2(requireContext());
        successDialog.setCancelable(false);
        successDialog.show();
    }

    private void openFilePicker(ActivityResultLauncher<String> mGetContent) {
        mGetContent.launch(IMAGE_MIME_TYPE);
    }


    private void openFilePickerForHorizontalPhoto(int photoNumber) {
        switch (photoNumber) {
            case 1:
                mGetContentHorizontal1.launch(IMAGE_MIME_TYPE);
                break;
            case 2:
                mGetContentHorizontal2.launch(IMAGE_MIME_TYPE);
                break;
            case 3:
                mGetContentHorizontal3.launch(IMAGE_MIME_TYPE);
                break;
        }
    }

    private void uploadHorizontalImageToFirebaseStorage(Uri result, int photoNumber) {
        String uniqueFilename = generateUniqueFilename();
        StorageReference imageRef = storageReference.child("Hospital_Images/" + uniqueFilename);
        imageRef.putFile(result)
                .addOnSuccessListener(taskSnapshot -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Horizontal Photo " + photoNumber + " Upload Successful", Toast.LENGTH_SHORT).show();

                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String url = uri.toString();
                        switch (photoNumber) {
                            case 1:
                                userData.setImageUrlHorizontal1(url);
                                selectedFileNameHorizontal1.setText("Selected Horizontal Photo 1: " + getFileNameFromUri(result));
                                uploadHorizontalButton1.setVisibility(View.GONE);
                                selectedImageHorizontal1.setImageURI(result);
                                break;
                            case 2:
                                userData.setImageUrlHorizontal2(url);
                                selectedFileNameHorizontal2.setText("Selected Horizontal Photo 2: " + getFileNameFromUri(result));
                                uploadHorizontalButton2.setVisibility(View.GONE);
                                selectedImageHorizontal2.setImageURI(result);
                                break;
                            case 3:
                                userData.setImageUrlHorizontal3(url);
                                selectedFileNameHorizontal3.setText("Selected Horizontal Photo 3: " + getFileNameFromUri(result));
                                uploadHorizontalButton3.setVisibility(View.GONE);
                                selectedImageHorizontal3.setImageURI(result);
                                break;
                        }
                    }).addOnFailureListener(this::handleImageUploadFailure);
                })
                .addOnFailureListener(this::handleImageUploadFailure);
    }

    private void showHorizontalPhotoSelectionDialog(int photoNumber) {
        ActivityResultLauncher<String> mGetContentHorizontal = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result -> uploadHorizontalImageToFirebaseStorage(result, photoNumber)
        );
        mGetContentHorizontal.launch(IMAGE_MIME_TYPE);
    }

    private boolean isValidImageMimeType(String mimeType) {
        return mimeType != null && mimeType.startsWith("image/") &&
                (mimeType.equals(JPEG_MIME_TYPE) || mimeType.equals(JPG_MIME_TYPE) || mimeType.equals(PNG_MIME_TYPE));
    }

    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = requireActivity().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    result = (displayNameIndex != -1) ? cursor.getString(displayNameIndex) : null;
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    private void submitWithoutImages() {
        String adminTypeText = userData.getAdminType();
        String stateText = userData.getSelectedState();
        String cityText = userData.getSelectedCity();
        String orgName = userData.getOrganizationName();
        String orgPhone = userData.getOrganizationPhone();
        String emailText = userData.getEmail();
        String adminId = userData.getAdminId();

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String fcmToken = task.getResult();
                userData.setFcmToken(fcmToken);
                String uniqueKey = emailText.replaceAll("\\s", "").replaceAll("[^a-zA-Z0-9]", "");
                saveDataToFirebase(uniqueKey, adminTypeText, orgName, orgPhone, stateText, cityText, emailText, null, adminId, fcmToken);
            } else {
                Toast.makeText(requireContext(), "Failed to retrieve FCM token", Toast.LENGTH_SHORT).show();
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