package com.example.unityapp;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.unityapp.SendNotificationPack.Client;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestBlood extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    APIService apiService;
    String serverBaseUrl = "http://192.168.227.40:5000";
    Spinner citySpinner, stateSpinner, bloodGroupSpinner, deliveryTimeSpinner, mlSpinner;
    private Button chooseLocationButton;
    private TextView locationTextView;
    private CityUtils cityUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_blood);
        stateSpinner = findViewById(R.id.stateSpinner);
        citySpinner = findViewById(R.id.citySpinner);
        bloodGroupSpinner = findViewById(R.id.bloodGroupSpinner);
        deliveryTimeSpinner = findViewById(R.id.deliveryTimeSpinner);
        mlSpinner = findViewById(R.id.mlSpinner);
        apiService = Client.getClient(serverBaseUrl).create(APIService.class);
        chooseLocationButton = findViewById(R.id.buttonViewOnMap);
        locationTextView = findViewById(R.id.locationTextView);
        String[] deliveryTimeValues = {"Emergency (Now)", "Within 30 min", "Within 1 hr",
                "Within 3 hr", "Within 6 hr", "Within 12 hr",
                "Within 24 hr"};
        ArrayAdapter<String> deliveryTimeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, deliveryTimeValues);
        deliveryTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deliveryTimeSpinner.setAdapter(deliveryTimeAdapter);
        String[] mlValues = {"L (litres)", "ML (Millilitre)"};
        ArrayAdapter<String> mlAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, mlValues);
        mlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mlSpinner.setAdapter(mlAdapter);
        String[] bloodGroupValues = {"A+", "B+", "AB+", "O+", "A-", "B-", "AB-", "O-"};
        ArrayAdapter<String> bloodGroupAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, bloodGroupValues);
        bloodGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodGroupSpinner.setAdapter(bloodGroupAdapter);
        String[] stateOptions = getResources().getStringArray(R.array.states);
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, stateOptions);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);
        stateSpinner.setSelection(0);
        cityUtils = new CityUtils(); // Create an instance of CityUtils

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Call updateCitySpinner from CityUtils
                String[] cityOptions = cityUtils.updateCitySpinner(RequestBlood.this, position);
                // Update citySpinner adapter
                ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(RequestBlood.this, android.R.layout.simple_spinner_item, cityOptions);
                cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                citySpinner.setAdapter(cityAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        Button broadcastButton = findViewById(R.id.broadcastButton);
        broadcastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBroadcastAlertDialog();
            }
        });
    }
    private void getLocation() {
        chooseLocationButton.setVisibility(View.GONE);
        locationTextView.setVisibility(View.VISIBLE);
        locationTextView.setText("Location gathered successfully!");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {

            }
        }
    }
    private void showBroadcastAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning!")
                .setMessage("Misinformation or fake information may lead you to a serious situation. For more info, read terms and conditions properly.")
                .setPositiveButton("OK, Got it", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showConfirmationDialog();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation!!")
                .setMessage("Are you sure you want to broadcast?")
                .setPositiveButton("Broadcast", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendBroadcastNotification();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog confirmationDialog = builder.create();
        confirmationDialog.show();
    }

    private void sendBroadcastNotification() {
        DatabaseReference broadcastRef = FirebaseDatabase.getInstance().getReference("broadcast");
        broadcastRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int recipients = 0;
                for (DataSnapshot broadcastSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot userSnapshot : broadcastSnapshot.getChildren()) {
                        DataSnapshot fcmTokenSnapshot = userSnapshot.child("fcmToken");
                        String fcmToken = fcmTokenSnapshot.getValue(String.class);
                        if (fcmToken != null) {
                            NotificationData notificationData = new NotificationData(fcmToken, "Alert!", "new blood request");
                            sendDataToServer(notificationData);
                            recipients++;
                        }
                    }
                }
                Toast.makeText(RequestBlood.this, "Notification sent to " + recipients + " recipients", Toast.LENGTH_SHORT).show();
                Log.d("Notification", "Sent to " + recipients + " recipients");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void sendDataToServer(NotificationData notificationData) {
        apiService.sendDataToServer(notificationData).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("Notification", "Data sent to server successfully");
                } else {
                    Log.d("Notification", "Failed to send data to server");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(RequestBlood.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}