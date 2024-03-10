package com.example.unityapp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class DonateBlood extends AppCompatActivity {
    ProgressBar progressBarnew; //Hi this is tanisha
    private static final int REQUEST_IMAGE_CAPTURE = 1; // ruk mai mere isme ek bar check kar ta ki change hua kya to
    private static final int REQUEST_UPLOAD_AADHAAR = 2;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String PREFS_NAME2 = "ProfileData";
    private static final String IMAGE_URL_KEY = "imageUrl"; //this is nakul
    private static final String DOCUMENT_URL_KEY = "documentUrl";
    public Spinner statespinner, cityspinner;
    private TextView textViewRelationType;
    private ImageView imageViewUploadedPhoto;
    private TextView textViewUploadStatus;
    private StorageReference storageReference;
    private String currentPhotoPath;
    private Button uploadButton;
    private Button uploadAadhaarButton;
    private Button textViewChooseDate;
    private Button submitButton;
    private EditText etFullname;
    private EditText etPostalCode;
    private EditText etphone;
    private EditText etAadhaarNumber;
    private RadioGroup radioGroupBloodDonation;
    private RadioButton radioButtonBloodDonationYes;
    private RadioButton radioButtonBloodDonationNo;
    private RadioGroup radioGroupDiseaseHistory;
    private RadioButton radioButtonDiseaseHistoryYes;
    private RadioButton radioButtonDiseaseHistoryNo;
    private TextView tvAdditionalInfo;
    private String documentUrl, imageUrl;
    private EditText editTextAdditionalInfo, etoccupation, etemail, etaddress;
    private TextView textViewAgeWarning;
    private ProgressBar progressBaraadhar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate_blood);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        textViewRelationType = findViewById(R.id.textViewRelationType);
        imageViewUploadedPhoto = findViewById(R.id.imageViewPhotoProof);
        progressBaraadhar = findViewById(R.id.progressBarAadhar);
        textViewUploadStatus = findViewById(R.id.textViewUploadStatus);
        uploadAadhaarButton = findViewById(R.id.buttonUploadAadhaarCard);
        etFullname = findViewById(R.id.etFullname);
        TextView textViewDescribeDonation = findViewById(R.id.textViewDescribeDonation);
        Button buttonChooseDate = findViewById(R.id.buttonChooseDate);
        etemail = findViewById(R.id.eEmail);
        if (user != null) {
            String userEmail = user.getEmail();
            String sanitizedEmail = userEmail.replace("@", "").replace(".", "");
            etemail.setText(userEmail);
            etemail.setEnabled(false);
            SharedPreferences preferences = getSharedPreferences(PREFS_NAME2, MODE_PRIVATE);
            String userId = preferences.getString("userid", "");
            DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("users");
            usersReference.child(sanitizedEmail).child("aadharNumber").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String userAadharNumber = dataSnapshot.getValue(String.class);
                        if (userAadharNumber != null && !userAadharNumber.isEmpty()) {
                            checkBloodDonationData(userAadharNumber, userId);
                        } else {
                            Toast.makeText(DonateBlood.this, "Aadhar number not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(DonateBlood.this, "Aadhar number not found in user data", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(DonateBlood.this, "Error retrieving Aadhar number", Toast.LENGTH_SHORT).show();
                }
            });
        }
        storageReference = FirebaseStorage.getInstance().getReference();
        cityspinner = findViewById(R.id.SpinnerCity);
        statespinner = findViewById(R.id.SpinnerState);
        etPostalCode = findViewById(R.id.etpostalcode);
        etaddress = findViewById(R.id.eAddress);
        etoccupation = findViewById(R.id.eOccupation);
        etemail = findViewById(R.id.eEmail);
        imageUrl = getFromSharedPreferences(IMAGE_URL_KEY);
        documentUrl = getFromSharedPreferences(DOCUMENT_URL_KEY);
        etAadhaarNumber = findViewById(R.id.eAadhaarNumber);
        etphone = findViewById(R.id.ePhoneNumber);
        radioGroupBloodDonation = findViewById(R.id.radioGroupBloodDonation);
        radioButtonBloodDonationYes = findViewById(R.id.radioButtonBloodDonationYes);
        radioButtonBloodDonationNo = findViewById(R.id.radioButtonBloodDonationNo);
        radioGroupDiseaseHistory = findViewById(R.id.radioGroupDiseaseHistory);
        radioButtonDiseaseHistoryYes = findViewById(R.id.radioButtonDiseaseHistoryYes);
        radioButtonDiseaseHistoryNo = findViewById(R.id.radioButtonDiseaseHistoryNo);
        tvAdditionalInfo = findViewById(R.id.tvadditionalinfo);
        editTextAdditionalInfo = findViewById(R.id.editTextAdditionalInfo);
        textViewAgeWarning = findViewById(R.id.textViewAgeWarning);
        CityUtils cityUtils = new CityUtils();
        String[] cityOptions = cityUtils.updateCitySpinner(this, 0);
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, R.layout.city_spinner_item_layout, cityOptions);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cityspinner.setAdapter(cityAdapter);
        String[] stateOptions = getResources().getStringArray(R.array.states);
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, stateOptions);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statespinner.setAdapter(stateAdapter);
        statespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String[] cityOptions = cityUtils.updateCitySpinner(DonateBlood.this, position);
                ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(DonateBlood.this,  android.R.layout.simple_spinner_item, cityOptions);
                cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                cityspinner.setAdapter(cityAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        RadioGroup radioGroupDiseaseHistory = findViewById(R.id.radioGroupDiseaseHistory);
        TextView tvAdditionalInfo = findViewById(R.id.tvadditionalinfo);
        EditText editTextAdditionalInfo = findViewById(R.id.editTextAdditionalInfo);
        radioGroupDiseaseHistory.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButtonDiseaseHistoryYes) {
                    tvAdditionalInfo.setVisibility(View.VISIBLE);
                    editTextAdditionalInfo.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.radioButtonDiseaseHistoryNo) {
                    tvAdditionalInfo.setVisibility(View.GONE);
                    editTextAdditionalInfo.setVisibility(View.GONE);
                }
            }
        });
        radioGroupBloodDonation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButtonBloodDonationYes) {
                    // If Yes is selected, make the TextView and Button visible
                    textViewDescribeDonation.setVisibility(View.VISIBLE);
                    buttonChooseDate.setVisibility(View.VISIBLE);
                    buttonChooseDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Call the showDatePicker() method when the button is clicked
                            showUnrestrictedDatePicker(buttonChooseDate);
                        }
                    });
                } else if (checkedId == R.id.radioButtonBloodDonationNo) {
                    // If No is selected, hide the TextView and Button
                    textViewDescribeDonation.setVisibility(View.GONE);
                    buttonChooseDate.setVisibility(View.GONE);
                }
            }
        });
        Intent intent = getIntent();
        if (intent != null) {
            String relationType = intent.getStringExtra("relationType");
            if (relationType != null) {
                textViewRelationType.setText("Relation Type: " + relationType);
            }
        }
        uploadButton = findViewById(R.id.buttonUploadPhotoProof);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.buttonUploadPhotoProof) {
                    if (requestCameraPermission()) {
                        dispatchTakePictureIntent();
                    }
                }
            }
        });
        submitButton = findViewById(R.id.buttonSubmit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TermsConditionsDialog dialog = new TermsConditionsDialog(DonateBlood.this);
                dialog.show();
            }
        });
        textViewChooseDate = findViewById(R.id.textViewChooseDate);
        textViewChooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(textViewChooseDate);
            }
        });
        uploadAadhaarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });
    }
    private void showUnrestrictedDatePicker(final Button textViewChooseDate) {
        Calendar today = Calendar.getInstance();
        int currentYear = today.get(Calendar.YEAR);
        int currentMonth = today.get(Calendar.MONTH);
        int currentDay = today.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                textViewChooseDate.setText(selectedDate);
            }
        }, currentYear, currentMonth, currentDay);

        datePickerDialog.show();
    }

    public boolean validateForm() {
        return !getEditTextText(etFullname).isEmpty()
                && !getEditTextText(etphone).isEmpty()
                && !getEditTextText(etaddress).isEmpty()
                && !getEditTextText(etPostalCode).isEmpty()
                && radioGroupBloodDonation.getCheckedRadioButtonId() != -1
                && radioGroupDiseaseHistory.getCheckedRadioButtonId() != -1
                && statespinner.getSelectedItemPosition() != 0
                && cityspinner.getSelectedItemPosition() != 0
                && imageUrl != null
                && documentUrl != null;
    }

    private String getEditTextText(EditText editText) {
        return editText.getText().toString().trim();
    }

    private String getSpinnerSelectedItem(Spinner spinner) {
        return spinner.getSelectedItem().toString().trim();
    }
    public void onSubmitButtonClick() {
        if (validateForm()) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String userEmail = user.getEmail();
                String fullName = etFullname.getText().toString();
                RadioGroup radioGroupBloodGroup = findViewById(R.id.radioGroupBloodGroup);
                String bloodGroup = getSelectedRadioButtonText(radioGroupBloodGroup);
                String birthDate = textViewChooseDate.getText().toString();
                RadioGroup radioGroupGender = findViewById(R.id.radioGroupGender);
                String gender = getSelectedRadioButtonText(radioGroupGender);
                String occupation = etoccupation.getText().toString();
                String phoneNumber = etphone.getText().toString();
                String email = etemail.getText().toString();
                String address = etaddress.getText().toString();
                String state = statespinner.getSelectedItem().toString();
                String city = cityspinner.getSelectedItem().toString();
                String postalCode = etPostalCode.getText().toString();
                String aadhaarNumber = etAadhaarNumber.getText().toString();
                String bloodDonationHistory = getSelectedRadioButtonValue(radioGroupBloodDonation);
                String diseaseHistory = getSelectedRadioButtonValue(radioGroupDiseaseHistory);
                String additionalInfo = editTextAdditionalInfo.getText().toString();
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs5", MODE_PRIVATE);
                String profilePhoto = sharedPreferences.getString("profilePhoto", "");
                String relationType = textViewRelationType.getText().toString().replace("Relation Type: ", "");
                String status = "under scrutiny";
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String timestamp = dateFormat.format(new Date());
                SharedPreferences preferences = getSharedPreferences(PREFS_NAME2, MODE_PRIVATE);
                String userId = preferences.getString("userid", "");
                DonorInfo donorInfo = new DonorInfo(
                        fullName, bloodGroup, birthDate, gender, occupation,
                        phoneNumber, email, address, state, city, postalCode,
                        aadhaarNumber, bloodDonationHistory, diseaseHistory, additionalInfo,
                        imageUrl, documentUrl, relationType, timestamp, status, userId, profilePhoto
                );
                saveDonorInfo(userEmail, donorInfo, userId);
            } else {
                Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
        }
    }
    public String getSelectedRadioButtonText(RadioGroup radioGroup) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        return (radioButton != null) ? radioButton.getText().toString() : "";
    }
    public String getSelectedRadioButtonValue(RadioGroup radioGroup) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        return (radioButton != null) ? radioButton.getText().toString() : "";
    }
    private void checkBloodDonationData(String aadharNumber, String userId) {
        DatabaseReference bloodDonationReference = FirebaseDatabase.getInstance().getReference("blooddonation").child(userId);
        bloodDonationReference.child(aadharNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    etAadhaarNumber.setEnabled(true);
                } else {
                    etAadhaarNumber.setText(aadharNumber);
                    etAadhaarNumber.setEnabled(false);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DonateBlood.this, "Error checking blood donation data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public boolean requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
            return false;
        } else {
            return true;
        }
    }
    private void saveDonorInfo(String userEmail, DonorInfo donorInfo, String userId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("blooddonation");
        String userKey = userEmail.replace("@", "").replace(".", "");
        String aadharNumber = donorInfo.getAadhaarNumber();
        if (aadharNumber != null && !aadharNumber.isEmpty()) {
            databaseReference.child(userId).child(aadharNumber).setValue(donorInfo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Details submitted successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(DonateBlood.this, Session.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to submit details", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Aadhar number is required", Toast.LENGTH_SHORT).show();
        }
    }
    private void showDatePicker(final Button textViewChooseDate) {
        Calendar today = Calendar.getInstance();
        today.add(Calendar.YEAR, -18);
        int currentYear = today.get(Calendar.YEAR);
        int currentMonth = today.get(Calendar.MONTH);
        int currentDay = today.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                textViewChooseDate.setText(selectedDate);
            }
        }, currentYear, currentMonth, currentDay);
        datePickerDialog.getDatePicker().setMaxDate(today.getTimeInMillis());

        datePickerDialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Permission denied! Go to app settings to enable it", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a Document"),
                    REQUEST_UPLOAD_AADHAAR
            );
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(
                    this,
                    "Please install a File Manager.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap photoBitmap = BitmapFactory.decodeFile(currentPhotoPath);
            uploadImageToFirebase(photoBitmap);
        } else if (requestCode == REQUEST_UPLOAD_AADHAAR && resultCode == RESULT_OK && data != null) {
            Uri documentUri = data.getData();
            if (documentUri != null) {
                uploadDocumentToFirebase(documentUri);
            }
        }
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.unityapp.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void uploadImageToFirebase(Bitmap photoBitmap) {
        progressBarnew = findViewById(R.id.progressBarnew);
        progressBarnew.setVisibility(View.VISIBLE);
        String filename = "image_" + UUID.randomUUID().toString() + ".jpg";
        StorageReference imageRef = storageReference.child("blood_donation/" + filename);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();
        UploadTask uploadTask = imageRef.putBytes(imageData);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUrl) {
                        String imageUrl = downloadUrl.toString();
                        Log.d("UploadSuccess", "Image URL: " + imageUrl);
                        saveToSharedPreferences(IMAGE_URL_KEY, imageUrl);
                        Toast.makeText(getApplicationContext(), "Image upload successful", Toast.LENGTH_SHORT).show();
                        progressBarnew.setVisibility(View.GONE);
                        Glide.with(getApplicationContext())
                                .asBitmap()
                                .load(photoBitmap)
                                .circleCrop()
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        imageViewUploadedPhoto.setImageBitmap(resource);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                        // Handle the placeholder if needed
                                    }
                                });
                        uploadButton.setVisibility(View.GONE);
                        textViewUploadStatus.setVisibility(View.VISIBLE);
                        textViewUploadStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("UploadFailure", "Error uploading image", e);
                progressBarnew.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
                textViewUploadStatus.setText("Photo upload failed");
                textViewUploadStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }
        });
    }
    private void uploadDocumentToFirebase(Uri documentUri) {
        progressBaraadhar.setVisibility(View.VISIBLE);
        StorageReference documentRef = storageReference.child("documents/" + UUID.randomUUID().toString());
        UploadTask uploadTask = documentRef.putFile(documentUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                documentRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUrl) {
                        String documentUrl = downloadUrl.toString();
                        Log.d("UploadSuccess", "Document URL: " + documentUrl);
                        saveToSharedPreferences(DOCUMENT_URL_KEY, documentUrl);
                        Toast.makeText(getApplicationContext(), "Document upload successful", Toast.LENGTH_SHORT).show();
                        progressBaraadhar.setVisibility(View.GONE);
                        uploadAadhaarButton.setText("View Document");
                        uploadAadhaarButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (documentUrl != null && !documentUrl.isEmpty()) {
                                    displayImageDialog(documentUrl);
                                } else {
                                    Toast.makeText(DonateBlood.this, "Aadhar image not available", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("UploadFailure", "Error uploading document", e);
                Toast.makeText(
                        getApplicationContext(),
                        "Document upload failed",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
    private void displayImageDialog(String imageUrl) {
        Dialog dialog = new Dialog(DonateBlood.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_image_viewer);
        ImageView imageView = dialog.findViewById(R.id.imageView);
        Glide.with(this).load(imageUrl).into(imageView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File imageFile = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }
    private void saveToSharedPreferences(String key, String value) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
    private String getFromSharedPreferences(String key) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return preferences.getString(key, "");
    }
    private void showToast(String message)
    {
        Toast.makeText(DonateBlood.this, message,Toast.LENGTH_SHORT).show();
    }
}