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
        updateCitySpinner(0);
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                updateCitySpinner(position);
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

    private void updateCitySpinner(int statePosition) {
        String[] cityOptions;
        switch (statePosition) {
            case 0:
                cityOptions = getResources().getStringArray(R.array.selectoptions);
                break;
            case 1:
                cityOptions = getResources().getStringArray(R.array.AndamanNicobar);
                break;
            case 2:
                cityOptions = getResources().getStringArray(R.array.Andhrapradesh);
                break;
            case 3:
                cityOptions = getResources().getStringArray(R.array.Arunachalpradesh);
                break;
            case 4:
                cityOptions = getResources().getStringArray(R.array.Assam);
                break;
            case 5:
                cityOptions = getResources().getStringArray(R.array.Bihar);
                break;
            case 6:
                cityOptions = getResources().getStringArray(R.array.Chandigarh);
                break;
            case 7:
                cityOptions = getResources().getStringArray(R.array.Chattisgarh);
                break;
            case 8:
                cityOptions = getResources().getStringArray(R.array.Dadra_Nagar_Haveli_Daman_Diu);
                break;
            case 9:
                cityOptions = getResources().getStringArray(R.array.Delhi_NCR);
                break;
            case 10:
                cityOptions = getResources().getStringArray(R.array.Goa);
                break;
            case 11:
                cityOptions = getResources().getStringArray(R.array.Gujarat);
                break;
            case 12:
                cityOptions = getResources().getStringArray(R.array.Haryana);
                break;
            case 13:
                cityOptions = getResources().getStringArray(R.array.Himachal_Pradesh);
                break;
            case 14:
                cityOptions = getResources().getStringArray(R.array.Jammu_and_Kashmir);
                break;
            case 15:
                cityOptions = getResources().getStringArray(R.array.Jharkhand);
                break;
            case 16:
                cityOptions = getResources().getStringArray(R.array.Karnataka);
                break;
            case 17:
                cityOptions = getResources().getStringArray(R.array.Kerala);
                break;
            case 18:
                cityOptions = getResources().getStringArray(R.array.Ladakh);
                break;
            case 19:
                cityOptions = getResources().getStringArray(R.array.Madhya_Pradesh);
                break;
            case 20:
                cityOptions = getResources().getStringArray(R.array.Maharashtra);
                break;
            case 21:
                cityOptions = getResources().getStringArray(R.array.Manipur);
                break;
            case 22:
                cityOptions = getResources().getStringArray(R.array.Meghalaya);
                break;
            case 23:
                cityOptions = getResources().getStringArray(R.array.Mizoram);
                break;
            case 24:
                cityOptions = getResources().getStringArray(R.array.Nagaland);
                break;
            case 25:
                cityOptions = getResources().getStringArray(R.array.Odisha);
                break;
            case 26:
                cityOptions = getResources().getStringArray(R.array.Puducherry);
                break;
            case 27:
                cityOptions = getResources().getStringArray(R.array.Punjab);
                break;
            case 28:
                cityOptions = getResources().getStringArray(R.array.Rajasthan);
                break;
            case 29:
                cityOptions = getResources().getStringArray(R.array.Sikkim);
                break;
            case 30:
                cityOptions = getResources().getStringArray(R.array.Tamil_Nadu);
                break;
            case 31:
                cityOptions = getResources().getStringArray(R.array.Telangana);
                break;
            case 32:
                cityOptions = getResources().getStringArray(R.array.Tripura);
                break;
            case 33:
                cityOptions = getResources().getStringArray(R.array.Uttar_Pradesh);
                break;
            case 34:
                cityOptions = getResources().getStringArray(R.array.Uttarakhand);
                break;
            case 35:
                cityOptions = getResources().getStringArray(R.array.West_Bengal);
                break;
            default:
                cityOptions = new String[0];
                break;
        }
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityOptions);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);
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