package com.example.unityapp;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unityapp.SendNotificationPack.Client;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class PaymentSuccess extends AppCompatActivity {
    APIService apiService;
    String serverBaseUrl = "http://192.168.94.40:3000";
    SharedPreferences.Editor editor2;
    private TextView nameTextView, phoneTextView, emailTextView, transactionIdTextView, tokenNumberTextView, DateTextView;
    private SharedPreferences sharedPreferences;
    private Button goToHomeButton;
    private Button downloadReceiptButton;
    private ProgressDialog progressDialog;
    private TextView textViewDisclaimer;
    String invoiceNumber;
    private Button viewReceiptButton2;
    private FirebaseAuth mAuth;
    private DatabaseReference bookingsRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);
        nameTextView = findViewById(R.id.nameTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        emailTextView = findViewById(R.id.emailTextView);
        textViewDisclaimer = findViewById(R.id.textViewDisclaimer);
        downloadReceiptButton = findViewById(R.id.downloadReceiptButton);
        transactionIdTextView = findViewById(R.id.transactionIdTextView);
        viewReceiptButton2 = findViewById(R.id.viewReceiptButton2);
        tokenNumberTextView = findViewById(R.id.tokenNumberTextView);
        DateTextView = findViewById(R.id.DateTextView);
        invoiceNumber = generateInvoiceNumber();
        goToHomeButton = findViewById(R.id.goToHomeButton);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Generating invoice...");
        apiService = Client.getClient(serverBaseUrl).create(APIService.class);
        sharedPreferences = getSharedPreferences("PaymentInfo", Context.MODE_PRIVATE);
        editor2 = sharedPreferences.edit();
        editor2.putString("invoiceNumber", invoiceNumber);
        String name = sharedPreferences.getString("fullName", "");
        String phone = sharedPreferences.getString("phoneNumber", "");
        String email = sharedPreferences.getString("email", "");
        String transactionId = sharedPreferences.getString("transactionId", "");
        String tokenNumber = sharedPreferences.getString("tokenNumber", "");
        String date = sharedPreferences.getString("date", "");
        String gstAmount = sharedPreferences.getString("gstAmount", "");
        String baseAmount = sharedPreferences.getString("baseAmount", "");
        String totalAmount = sharedPreferences.getString("totalAmount", "");
        String platformFee = sharedPreferences.getString("platformFee", "");
        mAuth = FirebaseAuth.getInstance();
        String mode = sharedPreferences.getString("mode", "");
        String hospitalName = "jeevan";
        String description = "Slot booked for " + hospitalName;
        String productname = "Appointment booking";
        String balancedue = null;
        if (mode.equals("offline")) {
            balancedue = baseAmount;
            baseAmount = platformFee;
        }
        generateReceipt(name, phone, email, transactionId, tokenNumber, date, gstAmount, baseAmount, totalAmount, productname, description, balancedue, platformFee, invoiceNumber);
        nameTextView.setText("Name: " + name);
        phoneTextView.setText("Phone: " + phone);
        emailTextView.setText("Email: " + email);
        transactionIdTextView.setText("Transaction ID: " + transactionId);
        tokenNumberTextView.setText("Token Number: " + tokenNumber);
        DateTextView.setText("Date: " + date);
        uploadFileToFirebaseStorage();
        storeBookingData(hospitalName, phone, email, transactionId, tokenNumber, date);
        goToHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentSuccess.this, Session.class);
                startActivity(intent);
                finishAffinity();
            }
        });
        downloadReceiptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = "invoice_" + invoiceNumber + ".pdf";
                downloadPdfFile(filename);
            }
        });
        viewReceiptButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = "invoice_" + invoiceNumber + ".pdf";
                openFileViaServer(filename);
            }
        });
    }

    public void generateReceipt(String name, String phone, String email, String transactionid, String tokenNumber, String date, String gstAmount, String baseAmount, String totalAmount, String productname, String description, String balancedue, String platformFee, String invoicenumber) {
        Log.e("ReceiptData", "Name: " + name);
        Log.e("ReceiptData", "Phone: " + phone);
        Log.e("ReceiptData", "Email: " + email);
        Log.e("ReceiptData", "Transaction ID: " + transactionid);
        Log.e("ReceiptData", "Token Number: " + tokenNumber);
        Log.e("ReceiptData", "Date: " + date);
        Log.e("ReceiptData", "GST Amount: " + gstAmount);
        Log.e("ReceiptData", "Base Amount: " + baseAmount);
        Log.e("ReceiptData", "Total Amount: " + totalAmount);
        Log.e("ReceiptData", "Product Name: " + productname);
        Log.e("ReceiptData", "Description: " + description);
        Log.e("ReceiptData", "Balance Due: " + balancedue);
        Log.e("ReceiptData", "Platform Fee: " + platformFee);
        Log.e("ReceiptData", "Invoice Number: " + invoicenumber);
        String address = "NGP, MH, IN";
        User user = new User(name, phone, email, invoicenumber, balancedue, address, productname, transactionid, tokenNumber, baseAmount, gstAmount, totalAmount, description, platformFee);
        sendDataToServer(user);
    }
    private String generateInvoiceNumber() {
        long timestamp = System.currentTimeMillis();
        Random random = new Random();
        int randomNum = random.nextInt(900) + 100;
        String invoiceNumber = String.valueOf(timestamp) + String.valueOf(randomNum);
        if (invoiceNumber.length() > 6) {
            invoiceNumber = invoiceNumber.substring(invoiceNumber.length() - 6);
        }
        return invoiceNumber;
    }
    private void storeBookingData(String name, String phone, String email, String transactionId, String tokenNumber, String date) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            SharedPreferences sharedPreferences = getSharedPreferences("HospData", Context.MODE_PRIVATE);
            String savedEmail = sharedPreferences.getString("HospitalEmail", null);
            String sanitizedEmail = currentUser.getEmail().replace(".", "").replace("@", "");
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            bookingsRef = database.getReference("bookings");
            long timestamp = System.currentTimeMillis();
            String timestampString = String.valueOf(timestamp);
            DatabaseReference userBookingRef = bookingsRef.child(sanitizedEmail).child(transactionId);
            Bookingdata booking = new Bookingdata("Appointment slot", name, transactionId, tokenNumber, date, savedEmail, timestampString);
            userBookingRef.setValue(booking);
        }
    }

    private void sendDataToServer(User user) {
        apiService.sendInvoiceDataToServer(user).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PaymentSuccess.this, "Data sent successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PaymentSuccess.this, "Failed to send data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(PaymentSuccess.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void downloadPdfFile(String filename) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference fileRef = storageRef.child("invoice/" + filename);
        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
            request.setTitle("Downloading " + filename);
            request.setDescription("Please wait while the file is downloaded.");
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            long downloadId = downloadManager.enqueue(request);
            Toast.makeText(this, "Download started", Toast.LENGTH_SHORT).show();
            BroadcastReceiver onComplete = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    if (id == downloadId) {
                        Log.d("Download", "Download of " + filename + " successful!");
                        downloadReceiptButton.setText("Download Again");
                        Toast.makeText(context, "Download completed!", Toast.LENGTH_SHORT).show();
                    }
                }
            };
            registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }).addOnFailureListener(exception -> {
            Toast.makeText(this, "Invoice generation in progress. Please wait for a minute.", Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadFileToFirebaseStorage() {
        String filename = "invoice_" + invoiceNumber + ".pdf";
        String serverUrl = "http://192.168.94.40:4000";
        String fullFileUrl = serverUrl + "/" + filename;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference fileRef = storageRef.child("invoice/" + filename);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fullFileUrl));
        request.setTitle("Downloading " + filename);
        request.setDescription("Please wait while the file is downloaded.");
        request.setDestinationInExternalFilesDir(getApplicationContext(), Environment.DIRECTORY_DOWNLOADS, filename);
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = downloadManager.enqueue(request);
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (id == downloadId) {
                    File downloadedFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), filename);
                    Uri fileUri = Uri.fromFile(downloadedFile);
                    fileRef.putFile(fileUri)
                            .addOnSuccessListener(taskSnapshot -> {
                                // File upload succeeded
                                Toast.makeText(PaymentSuccess.this, "Invoice uploaded Successfully! click to view", Toast.LENGTH_SHORT).show();
                                textViewDisclaimer.setText("Invoice created successfully! it's better to close now");
                                textViewDisclaimer.setTextColor(Color.parseColor("#00FF00"));
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(PaymentSuccess.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });

                    // Unregister the broadcast receiver
                    unregisterReceiver(this);
                }
            }
        };
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
    private void openFileViaServer(String filename) {
        String serverUrl = "http://192.168.94.40:4000";
        String fullFileUrl = serverUrl + "/" + filename;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(fullFileUrl), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        Intent chooserIntent = Intent.createChooser(intent, "Open PDF");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooserIntent);
        } else {
            Toast.makeText(this, "No application found to open PDF", Toast.LENGTH_SHORT).show();
        }
    }
}