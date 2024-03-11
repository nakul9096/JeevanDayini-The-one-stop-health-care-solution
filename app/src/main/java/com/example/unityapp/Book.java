package com.example.unityapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class Book extends AppCompatActivity {

    String email;
    private TextView selectedDateTextView, estimatedtimetv;
    private Button updateDateBtn;
    private LinearLayout tokenButtonsContainer;
    private Button paybtn;
    private FirebaseDatabase database;
    private ProgressDialog progressDialog;
    private ArrayList<Button> disabledButtonIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        selectedDateTextView = findViewById(R.id.selectedDateTextView);
        updateDateBtn = findViewById(R.id.updateDateBtn);
        tokenButtonsContainer = findViewById(R.id.slotsContainer);
        paybtn = findViewById(R.id.continuebtn);
        estimatedtimetv = findViewById(R.id.estimatedDurationTextView);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Gathering information...");
        database = FirebaseDatabase.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("hospital_email", "");
        Log.d("Hospital email", email);
        showDatePicker();
        updateDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
        paybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button selectedButton = findSelectedButton();
                if (selectedButton != null) {
                    String buttonText = selectedButton.getText().toString();
                    int tokenNumber = Integer.parseInt(buttonText.split(" ")[1]);
                    String date = selectedDateTextView.getText().toString();
                    if (!date.isEmpty()) {
                        Intent intent = new Intent(Book.this, Booking.class);
                        SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).edit();
                        editor.putInt("tokenNumber", tokenNumber);
                        editor.putString("selectedDate", date);
                        editor.apply();
                        Log.d("TokenNumber", "Token number: " + tokenNumber);
                        Log.d("TokenNumber", "date: " + date);
                        startActivity(intent);
                    } else {
                        Helper.showToast("Please select date!");
                    }
                } else {
                   Helper.showToast("Please select a token to proceed.");
                }
            }
        });
        paybtn.setEnabled(false);
        addTokenButtons(50);
        getBaseAmount(email);
    }

    public void getBaseAmount(String email) {
        DatabaseReference baseAmountRef = database.getReference("hospitals").child(email).child("base_amount");
        baseAmountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String baseAmount = dataSnapshot.getValue(String.class);
                if (baseAmount != null) {
                    Log.d("BaseAmount", "Retrieved base amount: " + baseAmount);
                    saveBaseAmountToSharedPreferences(baseAmount);
                } else {
                    Log.d("BaseAmount", "Base amount not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("BaseAmount", "Failed to retrieve base amount: " + databaseError.getMessage());
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        Calendar maxCalendar = Calendar.getInstance();
        maxCalendar.add(Calendar.DAY_OF_MONTH, 7);
        int maxYear = maxCalendar.get(Calendar.YEAR);
        int maxMonth = maxCalendar.get(Calendar.MONTH);
        int maxDayOfMonth = maxCalendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(Book.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                selectedDateTextView.setText(selectedDate);
                updateDateBtn.setText(selectedDate);
                resetAllButtons();
                checkBookingStatus(email, selectedDate);
            }
        }, year, month, dayOfMonth);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(maxCalendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void resetAllButtons() {
        for (int i = 0; i < tokenButtonsContainer.getChildCount(); i++) {
            View rowLayout = tokenButtonsContainer.getChildAt(i);
            if (rowLayout instanceof LinearLayout) {
                LinearLayout row = (LinearLayout) rowLayout;
                for (int j = 0; j < row.getChildCount(); j++) {
                    View child = row.getChildAt(j);
                    if (child instanceof Button) {
                        Button button = (Button) child;
                        button.setEnabled(true);
                        button.setBackgroundResource(R.drawable.rounded_button_background);
                        disabledButtonIds.clear();
                        paybtn.setEnabled(false);
                    }
                }
            }
        }
    }

    private void addTokenButtons(int count) {
        int numRows = (int) Math.ceil((double) count / 3);
        float weight = 1f / 3;
        for (int i = 0; i < numRows; i++) {
            LinearLayout rowLayout = new LinearLayout(this);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            rowLayout.setLayoutParams(rowParams);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            for (int j = 0; j < 3; j++) {
                int index = i * 3 + j;
                if (index < count) {
                    Button tokenButton = new Button(this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            weight
                    );
                    params.setMargins(10, 10, 10, 10);
                    tokenButton.setLayoutParams(params);
                    tokenButton.setText("Token " + (index + 1));
                    tokenButton.setBackgroundResource(R.drawable.rounded_button_background);
                    tokenButton.setTextColor(getResources().getColor(R.color.white));
                    tokenButton.setPadding(20, 20, 20, 20);
                    tokenButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            paybtn.setEnabled(true);
                            deselectAllButtons();
                            v.setSelected(true);
                            v.setBackgroundResource(R.drawable.rounded_button_green_background);
                            checkSelectedButtons();
                            restoreDisabledButtonsState();
                        }
                    });
                    rowLayout.addView(tokenButton);
                }
            }
            tokenButtonsContainer.addView(rowLayout);
        }
    }

    private void checkSelectedButtons() {
        for (int i = 0; i < tokenButtonsContainer.getChildCount(); i++) {
            View rowLayout = tokenButtonsContainer.getChildAt(i);
            if (rowLayout instanceof LinearLayout) {
                LinearLayout row = (LinearLayout) rowLayout;
                for (int j = 0; j < row.getChildCount(); j++) {
                    View child = row.getChildAt(j);
                    if (child instanceof Button) {
                        Button button = (Button) child;
                        if (button.isSelected()) {
                            button.setBackgroundResource(R.drawable.rounded_button_green_background);
                        } else {
                            button.setBackgroundResource(R.drawable.rounded_button_background);
                        }
                    }
                }
            }
        }
    }

    private void deselectAllButtons() {
        for (int i = 0; i < tokenButtonsContainer.getChildCount(); i++) {
            View rowLayout = tokenButtonsContainer.getChildAt(i);
            if (rowLayout instanceof LinearLayout) {
                LinearLayout row = (LinearLayout) rowLayout;
                for (int j = 0; j < row.getChildCount(); j++) {
                    View child = row.getChildAt(j);
                    if (child instanceof Button) {
                        Button button = (Button) child;
                        button.setSelected(false);
                        button.setBackgroundResource(R.drawable.rounded_button_background);
                    }
                }
            }
        }
    }

    private void checkBookingStatus(String email, String selectedDate) {
        Log.d("BookingStatus", "Checking email: " + email);
        Log.d("BookingStatus", "Checking date: " + selectedDate);
        progressDialog.setMessage("Checking booking status...");
        progressDialog.show();
        DatabaseReference hospitalsRef = database.getReference("hospitals").child(email).child("booked_slots");
        hospitalsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                for (DataSnapshot bookingSnapshot : dataSnapshot.getChildren()) {
                    String bookedDate = bookingSnapshot.child("date").getValue(String.class);
                    Log.d("BookingStatus", "Retrieved date: " + bookedDate);
                    String tokenNumber = bookingSnapshot.child("tokenNumber").getValue(String.class);
                    if (bookedDate != null && bookedDate.equals(selectedDate)) {
                        Log.d("BookingStatus", "Token " + tokenNumber + " is booked for " + selectedDate);
                        String buttonText = "Token " + tokenNumber;
                        for (int i = 0; i < tokenButtonsContainer.getChildCount(); i++) {
                            View rowLayout = tokenButtonsContainer.getChildAt(i);
                            if (rowLayout instanceof LinearLayout) {
                                LinearLayout row = (LinearLayout) rowLayout;
                                for (int j = 0; j < row.getChildCount(); j++) {
                                    View child = row.getChildAt(j);
                                    if (child instanceof Button) {
                                        Button tokenButton = (Button) child;
                                        if (tokenButton.getText().toString().equals(buttonText)) {
                                            Log.d("BookingStatus", "Disabling button: " + buttonText);
                                            tokenButton.setEnabled(false);
                                            tokenButton.setBackgroundResource(R.drawable.disabled_button_background);
                                            disabledButtonIds.add(tokenButton);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                restoreDisabledButtonsState();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Helper.showToast("Failed to retrieve data: ");
            }
        });
    }

    private void restoreDisabledButtonsState() {
        for (Button button : disabledButtonIds) {
            button.setEnabled(false);
            button.setBackgroundResource(R.drawable.disabled_button_background);
        }
    }

    private Button findSelectedButton() {
        for (int i = 0; i < tokenButtonsContainer.getChildCount(); i++) {
            View rowLayout = tokenButtonsContainer.getChildAt(i);
            if (rowLayout instanceof LinearLayout) {
                LinearLayout row = (LinearLayout) rowLayout;
                for (int j = 0; j < row.getChildCount(); j++) {
                    View child = row.getChildAt(j);
                    if (child instanceof Button) {
                        Button button = (Button) child;
                        if (button.isSelected()) {
                            return button;
                        }
                    }
                }
            }
        }
        return null;
    }

    private void saveBaseAmountToSharedPreferences(String baseAmount) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("base_amount", baseAmount);
        editor.apply();
        Log.d("BaseAmount", "Base amount saved to SharedPreferences: " + baseAmount);
    }
}