package com.example.unityapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unityapp.SendNotificationPack.Client;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingHistory extends AppCompatActivity {
    EditText nameInput, phoneInput, emailInput, invoiceNumberInput, dueBalanceInput, addressInput, productNameInput, transactionIdInput, tokenNumberInput, baseAmountInput, gstAmountInput, totalAmountInput, descriptionInput, platformfee;
    Button sendButton;
    APIService apiService;
    String serverBaseUrl = "http://192.168.54.40:3000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);
        nameInput = findViewById(R.id.editTextName8);
        phoneInput = findViewById(R.id.editTextPhone8);
        emailInput = findViewById(R.id.editTextEmail8);
        invoiceNumberInput = findViewById(R.id.editTextInvoiceNumber);
        dueBalanceInput = findViewById(R.id.editTextDuebalance);
        addressInput = findViewById(R.id.editTextAddress);
        productNameInput = findViewById(R.id.editTextProductname);
        transactionIdInput = findViewById(R.id.editTextTransactionid);
        tokenNumberInput = findViewById(R.id.editTexttokenNumber);
        baseAmountInput = findViewById(R.id.editTextbaseamount);
        gstAmountInput = findViewById(R.id.editTextgstamount);
        totalAmountInput = findViewById(R.id.editTextamount);
        descriptionInput = findViewById(R.id.editTextdescription);
        platformfee = findViewById(R.id.editTextplatformfee);
        sendButton = findViewById(R.id.buttonSend8);
        apiService = Client.getClient(serverBaseUrl).create(APIService.class);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get input values
                String name = nameInput.getText().toString();
                String phone = phoneInput.getText().toString();
                String email = emailInput.getText().toString();
                String invoiceNumber = invoiceNumberInput.getText().toString();
                String dueBalance = dueBalanceInput.getText().toString();
                String address = addressInput.getText().toString();
                String productName = productNameInput.getText().toString();
                String transactionId = transactionIdInput.getText().toString();
                String tokenNumber = tokenNumberInput.getText().toString();
                String baseAmount = baseAmountInput.getText().toString();
                String gstAmount = gstAmountInput.getText().toString();
                String totalAmount = totalAmountInput.getText().toString();
                String description = descriptionInput.getText().toString();
                String platformfeetv = platformfee.getText().toString();
                User user = new User(name, phone, email, invoiceNumber, dueBalance, address, productName, transactionId, tokenNumber, baseAmount, gstAmount, totalAmount, description, platformfeetv);
                sendDataToServer(user);
            }
        });
    }

    private void sendDataToServer(User user) {
        apiService.sendInvoiceDataToServer(user).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Helper.showToast(BookingHistory.this,"Data sent successfully");
                } else {
                    Helper.showToast(BookingHistory.this,"Failed to send data");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Helper.showToast( BookingHistory.this,"Error: " + t.getMessage());
            }
        });
    }
}