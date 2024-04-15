package com.example.unityapp;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;

public class HospDashboard extends AppCompatActivity {
    private final Handler handler = new Handler();
    private TextView textViewAdminId, helloTextView;
    private ImageView imageView1, imageView2, imageView3, imageView4;
    private Button[] dayButtons;
    private TextView marqueetextview;
    private TextView[] textTimings;
    private Button setClinicName;
    private TextView organizationNameTV;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Button setMapUrlbtn;
    private Button bdbutton;
    private Runnable updateDatabaseRunnable;
    private Button setDetailsBtn;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hosp_dashboard);
        setClinicName = findViewById(R.id.setClinicNameButton);
        textViewAdminId = findViewById(R.id.adminIdTextView4);
        helloTextView = findViewById(R.id.helloTextView);
        imageView1 = findViewById(R.id.imageView18);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);
        imageView4 = findViewById(R.id.imageView4);
        marqueetextview = findViewById(R.id.marqueeText);
        marqueetextview.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        marqueetextview.setSelected(true);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        organizationNameTV = findViewById(R.id.organizationNameTV);
        mAuth = FirebaseAuth.getInstance();
        setMapUrlbtn = findViewById(R.id.buttonSetMapUrl);
        bdbutton = findViewById(R.id.buttonSetBloodDonation);
        EditText ongoingAppointmentEditText = findViewById(R.id.ongoingAppointmentEditText);
        setDetailsBtn = findViewById(R.id.buttonSetOtherInformation);
        progressDialog = new ProgressDialog(HospDashboard.this);
        progressDialog.setMessage("Loading data...");
        setDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetailsDialog();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadData();
            }
        });
        ongoingAppointmentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Nothing to do here
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                handler.removeCallbacks(updateDatabaseRunnable);
                updateDatabaseRunnable = () -> {
                    String currentEmail = getCurrentUserEmail();
                    String encodedEmail = currentEmail.replace("@", "").replace(".", "");
                    String newValue = charSequence.toString();
                    if (newValue.isEmpty()) {
                        removeOngoingAppointmentFromDatabase(encodedEmail);
                    } else {
                        updateDatabase(encodedEmail, newValue);
                    }
                };

                handler.postDelayed(updateDatabaseRunnable, 3000);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        checkAndSetClinicName();
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_home) {
                // Handle Home click
            } else if (id == R.id.menu_donation_requests) {
                Intent intent = new Intent(HospDashboard.this, Donation_req_hosp.class);
                startActivity(intent);
            } else if (id == R.id.menu_upload_prescription) {
                Intent intent = new Intent(HospDashboard.this, UploadPrescription.class);
                startActivity(intent);
            }
            else if (id == R.id.menu_upload_test_report) {
                Intent intent = new Intent(HospDashboard.this, UploadTestReport.class);
                startActivity(intent);
            } else if (id == R.id.requestbloodbtn) {
                Intent intent = new Intent(HospDashboard.this, RequestBlood.class);
                startActivity(intent);
            }
            else if (id == R.id.menu_viewbr) {
                Intent intent = new Intent(HospDashboard.this, ViewBloodRequests.class);
                startActivity(intent);
            }else if (id == R.id.menu_lco) {
                Intent intent = new Intent(HospDashboard.this, ListOnline.class);
                startActivity(intent);
            } else if (id == R.id.menu_logout) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(HospDashboard.this, HomeDashboard.class);
                startActivity(intent);
                finishAffinity();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
        dayButtons = new Button[]{
                findViewById(R.id.buttonMonday),
                findViewById(R.id.buttonTuesday),
                findViewById(R.id.buttonWednesday),
                findViewById(R.id.buttonThursday),
                findViewById(R.id.buttonFriday),
                findViewById(R.id.buttonSaturday),
                findViewById(R.id.buttonSunday)
        };
        textTimings = new TextView[]{
                findViewById(R.id.textMondayTimings),
                findViewById(R.id.textTuesdayTimings),
                findViewById(R.id.textWednesdayTimings),
                findViewById(R.id.textThursdayTimings),
                findViewById(R.id.textFridayTimings),
                findViewById(R.id.textSaturdayTimings),
                findViewById(R.id.textSundayTimings)
        };
        setupDayButtons();
        checkPremiumUser();
        ImageView menuPlaceholder = findViewById(R.id.menuPlaceholder);
        menuPlaceholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLeftMenuDrawer();
            }
        });
        setClinicName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClinicNameButtonClick();
            }
        });
        setMapUrlbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMapUrl();
            }
        });
        bdbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBloodDonationDialog();
            }
        });
    }

    private void showDetailsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HospDashboard.this);
        builder.setTitle("Enter Clinic Details");
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_clinic_details, null);
        final EditText addressEditText = dialogView.findViewById(R.id.addressEditText);
        final EditText pincodeEditText = dialogView.findViewById(R.id.pincodeEditText);
        final EditText specialtyEditText = dialogView.findViewById(R.id.specialtyEditText);
        final EditText appointmentFeeEditText = dialogView.findViewById(R.id.appointmentFeeEditText);
        final EditText tokenslotsEditText = dialogView.findViewById(R.id.tokenslotsEditText);
        builder.setView(dialogView);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String address = addressEditText.getText().toString().trim();
                String pincode = pincodeEditText.getText().toString().trim();
                String specialty = specialtyEditText.getText().toString().trim();
                String appointmentFee = appointmentFeeEditText.getText().toString().trim();
                String tokenslots = tokenslotsEditText.getText().toString().trim();
                if (!address.isEmpty() && !pincode.isEmpty() && !specialty.isEmpty() && !appointmentFee.isEmpty() && !tokenslots.isEmpty()) {
                    updateClinicDetails(address, pincode, specialty, appointmentFee, tokenslots);
                } else {
                    Toast.makeText(HospDashboard.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void updateClinicDetails(String address, String pincode, String specialty, String base_amount, String tokenslots) {
        String currentUserEmail = getCurrentUserEmail();
        if (currentUserEmail != null) {
            DatabaseReference hospitalsRef = FirebaseDatabase.getInstance().getReference("hospitals");
            String childPath = sanitizeEmailForChildPath(currentUserEmail);
            DatabaseReference hospitalRef = hospitalsRef.child(childPath);
            hospitalRef.child("address").setValue(address);
            hospitalRef.child("pincode").setValue(pincode);
            hospitalRef.child("specialty").setValue(specialty);
            hospitalRef.child("base_amount").setValue(base_amount);
            hospitalRef.child("slots").setValue(tokenslots);
            setDetailsBtn.setText("Update");
            Toast.makeText(HospDashboard.this, "Clinic details updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("HospDashboard", "User email is null");
        }
    }

    private void setupDayButtons() {
        for (int i = 0; i < dayButtons.length; i++) {
            final String day = getDayString(i);
            setButtonClickListener(dayButtons[i], day);
        }
    }

    private String getDayString(int dayIndex) {
        switch (dayIndex) {
            case 0:
                return "Monday";
            case 1:
                return "Tuesday";
            case 2:
                return "Wednesday";
            case 3:
                return "Thursday";
            case 4:
                return "Friday";
            case 5:
                return "Saturday";
            case 6:
                return "Sunday";
            default:
                return "";
        }
    }

    private void setButtonClickListener(Button button, final String day) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(day);
            }
        });
    }

    private void checkAndSetClinicName() {
        String currentUserEmail = getCurrentUserEmail();
        if (currentUserEmail != null) {
            DatabaseReference hospitalsRef = FirebaseDatabase.getInstance().getReference("hospitals");
            String childPath = sanitizeEmailForChildPath(currentUserEmail);
            DatabaseReference hospitalRef = hospitalsRef.child(childPath);

            hospitalRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("mapUrl")) {
                        setMapUrlbtn.setText("Update");
                    }
                    if (dataSnapshot.hasChild("address")) {
                        setDetailsBtn.setText("Update");
                    }
                    if (dataSnapshot.hasChild("bdf")) {
                        bdbutton.setText("Update");
                    }
                    if (dataSnapshot.hasChild("timings")) {
                        for (int i = 0; i < dayButtons.length; i++) {
                            String day = getDayString(i);
                            String startTime = dataSnapshot.child("timings").child(day + "_start").getValue(String.class);
                            String endTime = dataSnapshot.child("timings").child(day + "_end").getValue(String.class);
                            if (startTime != null && endTime != null) {
                                textTimings[i].setText("Start: " + startTime + "\nEnd: " + endTime);
                                dayButtons[i].setText("Update");
                            }
                        }
                    }
                    if (dataSnapshot.hasChild("status")) {
                        marqueetextview.setText("Congratulations! Your clinic is live and visible to all users on JEEVANDAYINI app.");
                        marqueetextview.setTextColor(Color.GREEN);
                    }
                    if (dataSnapshot.hasChild("name")) {
                        String clinicName = dataSnapshot.child("name").getValue(String.class);
                        String htmlText = "<html><body style=\"text-align:center;\">Hello,<br>" + clinicName + "</body></html>";
                        String currentUserEmail = getCurrentUserEmail();
                        SharedPreferences preferences = getSharedPreferences("HospData", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("hospital_email", currentUserEmail);
                        editor.putString("hospital_name", clinicName);
                        Log.d("newdebug","hosp_email"+currentUserEmail);
                        Log.d("newdebug","hosp_email"+clinicName);
                        editor.apply();
                        organizationNameTV.setText(Html.fromHtml(htmlText));
                        setClinicName.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("HospDashboard", "Database error: " + databaseError.getMessage());
                }
            });
        } else {
            Log.d("HospDashboard", "User email is null");
        }
    }
    private void setClinicNameButtonClick() {
        String currentUserEmail = getCurrentUserEmail();
        if (currentUserEmail != null) {
            DatabaseReference hospitalsRef = FirebaseDatabase.getInstance().getReference("hospitals");
            String childPath = sanitizeEmailForChildPath(currentUserEmail);
            DatabaseReference hospitalRef = hospitalsRef.child(childPath);
            hospitalRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HospDashboard.this);
                    builder.setTitle("Enter Clinic Name");
                    final EditText input = new EditText(HospDashboard.this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    input.setHint("Clinic Name");
                    builder.setView(input);
                    builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String clinicName = input.getText().toString().trim();
                            if (!clinicName.isEmpty()) {
                                progressDialog.dismiss();
                                // Handle the clinic name submission
                                processClinicName(clinicName);
                            } else {
                                // Display a toast or alert that the clinic name cannot be empty
                                progressDialog.dismiss();
                                Toast.makeText(HospDashboard.this, "Please enter a valid clinic name", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    progressDialog.dismiss();
                    Log.e("HospDashboard", "Database error: " + databaseError.getMessage());
                }
            });
        } else {
            progressDialog.dismiss();
            Log.d("HospDashboard", "User email is null");
        }
    }

    private void processClinicName(String clinicName) {
        String currentUserEmail = getCurrentUserEmail();
        if (currentUserEmail != null) {
            String childPath = sanitizeEmailForChildPath(currentUserEmail);
            DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference("hospitals").child(childPath);
            adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        adminRef.child("name").setValue(clinicName);
                        organizationNameTV.setText(clinicName);
                        setClinicName.setVisibility(View.GONE);
                        Toast.makeText(HospDashboard.this, "Clinic name updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        dataSnapshot.getRef().child("name").setValue(clinicName);
                        helloTextView.setText("Hello, \n" + clinicName);
                        reloadData();
                        Log.d("HospDashboard", "No data found for email: " + currentUserEmail);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("HospDashboard", "Database error: " + databaseError.getMessage());
                }
            });
        } else {
            Log.d("HospDashboard", "User email is null");
        }
    }

    private String sanitizeEmailForChildPath(String email) {
        return email.replace("@", "").replace(".", "");
    }

    private void checkPremiumUser() {
        progressDialog.show();
        String currentUserEmail = getCurrentUserEmail();
        if (currentUserEmail != null) {
            DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference("Admin");
            adminRef.orderByChild("email").equalTo(currentUserEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot adminSnapshot : dataSnapshot.getChildren()) {
                            String bannerImageUrl1 = adminSnapshot.child("imageUrlHorizontal1").getValue(String.class);
                            String bannerImageUrl2 = adminSnapshot.child("imageUrlHorizontal2").getValue(String.class);
                            String bannerImageUrl3 = adminSnapshot.child("imageUrlHorizontal3").getValue(String.class);
                            String bannerImageUrl4 = adminSnapshot.child("imageUrlIdentity1").getValue(String.class);
                            loadImageWithGlide(bannerImageUrl4, imageView1);
                            loadImageWithGlide(bannerImageUrl1, imageView2);
                            loadImageWithGlide(bannerImageUrl2, imageView3);
                            loadImageWithGlide(bannerImageUrl3, imageView4);
                            String adminId = adminSnapshot.child("adminId").getValue(String.class);
                            textViewAdminId.setText("Admin ID: " + adminId);
                            String name = adminSnapshot.child("organizationName").getValue(String.class);
                            helloTextView.setText("Hello, \n" + name);
                            progressDialog.dismiss();
                        }
                    } else {
                        Log.d("HospDashboard", "No data found for email: " + currentUserEmail);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("HospDashboard", "Database error: " + databaseError.getMessage());
                }
            });
        } else {
            Log.d("HospDashboard", "User email is null");
        }
    }

    private void showTimePicker(final String day) {
        final Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        TimePickerDialog startTimePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, final int startHourOfDay, final int startMinute) {
                        // Start Time selected, show End Time picker
                        TimePickerDialog endTimePickerDialog = new TimePickerDialog(HospDashboard.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int endHourOfDay, int endMinute) {
                                        // End Time selected, process the times
                                        String startTime = formatTime(startHourOfDay, startMinute);
                                        String endTime = formatTime(endHourOfDay, endMinute);
                                        processStartAndEndTime(day, startTime, endTime);
                                    }
                                }, currentHour, currentMinute, false);

                        endTimePickerDialog.show();
                    }
                }, currentHour, currentMinute, false);

        startTimePickerDialog.show();
    }

    private int getDayIndex(String day) {
        for (int i = 0; i < dayButtons.length; i++) {
            if (getDayString(i).equals(day)) {
                return i;
            }
        }
        return -1;
    }

    private void processStartAndEndTime(String day, String startTime, String endTime) {
        int dayIndex = getDayIndex(day);
        if (dayIndex != -1) {
            textTimings[dayIndex].setText("Start: " + startTime + "\nEnd: " + endTime);
            dayButtons[dayIndex].setText("Update");
        }
        DatabaseReference hospitalsRef = FirebaseDatabase.getInstance().getReference("hospitals");
        String currentUserEmail = getCurrentUserEmail();
        String childPath = currentUserEmail.replace("@", "").replace(".", "");
        DatabaseReference hospitalRef = hospitalsRef.child(childPath);
        hospitalRef.child("timings").child(day + "_start").setValue(startTime);
        hospitalRef.child("timings").child(day + "_end").setValue(endTime);
        Toast.makeText(HospDashboard.this, "Timings updated successfully", Toast.LENGTH_SHORT).show();
    }

    private String formatTime(int hour, int minute) {
        return String.format("%02d:%02d", hour, minute);
    }

    private void updateButtonAndSaveTimings(final String day, final String selectedTime) {
        DatabaseReference hospitalsRef = FirebaseDatabase.getInstance().getReference("hospitals");
        String currentUserEmail = getCurrentUserEmail();
        String childPath = currentUserEmail.replace("@", "").replace(".", "");
        DatabaseReference hospitalRef = hospitalsRef.child(childPath);
        hospitalRef.child("timings").child(day).setValue(selectedTime, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@NonNull DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null) {
                    String buttonText = "Update: " + selectedTime;
                    for (Button button : dayButtons) {
                        String buttonDay = getDayString(dayButtons, button);
                        if (buttonDay.equals(day)) {
                            button.setText(buttonText);
                            break;
                        }
                    }
                    Toast.makeText(HospDashboard.this, "Timings updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HospDashboard.this, "Failed to update timings", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getDayString(Button[] dayButtons, Button button) {
        for (int i = 0; i < dayButtons.length; i++) {
            if (dayButtons[i] == button) {
                return getDayString(i);
            }
        }
        return "";
    }

    private void loadImageWithGlide(String imageUrl, ImageView imageView) {
        if (!isDestroyed()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.home)
                    .centerCrop()
                    .into(imageView);
        }
    }

    private String getCurrentUserEmail() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            return user.getEmail();
        } else {
            return null;
        }
    }

    public void openLeftMenuDrawer() {
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        drawerLayout.openDrawer(GravityCompat.START);
    }

    private void setMapUrl() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HospDashboard.this);
        builder.setTitle("Set Map URL");
        final EditText input = new EditText(HospDashboard.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Map URL");
        builder.setView(input);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mapUrl = input.getText().toString().trim();
                if (!mapUrl.isEmpty()) {
                    if (isValidUrl(mapUrl)) {
                        processMapUrl(mapUrl);
                    } else {
                        Toast.makeText(HospDashboard.this, "Invalid URL format", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HospDashboard.this, "Please enter a map URL", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void processMapUrl(String mapUrl) {
        DatabaseReference hospitalsRef = FirebaseDatabase.getInstance().getReference("hospitals");
        String currentUserEmail = getCurrentUserEmail();
        String childPath = currentUserEmail.replace("@", "").replace(".", "");
        DatabaseReference hospitalRef = hospitalsRef.child(childPath);
        hospitalRef.child("mapUrl").setValue(mapUrl, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@NonNull DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null) {
                    Toast.makeText(HospDashboard.this, "Map URL updated successfully", Toast.LENGTH_SHORT).show();
                    recreate();
                } else {
                    Toast.makeText(HospDashboard.this, "Failed to update map URL", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void showBloodDonationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Blood Donation Facility")
                .setMessage("Is your hospital/clinic equipped with the facility to donate blood?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setBloodDonationFacility("Yes");
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setBloodDonationFacility("No");
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .show();
    }

    public void setBloodDonationFacility(String response) {
        String currentUserEmail = getCurrentUserEmail();
        String childPath = currentUserEmail.replace("@", "").replace(".", "");
        if (currentUserEmail != null) {
            DatabaseReference userHospitalsRef = FirebaseDatabase.getInstance().getReference()
                    .child("hospitals").child(childPath);
            userHospitalsRef.child("bdf").setValue(response);
            Toast.makeText(HospDashboard.this, "Blood donation preference updated as: " + response, Toast.LENGTH_SHORT).show();
            bdbutton.setText("Update");
        }
    }

    private void updateDatabase(String currentEmail, String newValue) {
        if (!newValue.isEmpty()) {
            DatabaseReference hospitalsRef = FirebaseDatabase.getInstance().getReference("hospitals");
            DatabaseReference hospitalRef = hospitalsRef.child(currentEmail);
            hospitalRef.child("ongoingAppointment").setValue(newValue, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@NonNull DatabaseError error, @NonNull DatabaseReference ref) {
                    if (error == null) {
                        Toast.makeText(HospDashboard.this, "Ongoing appointment updated successfully: " + newValue, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(HospDashboard.this, "Failed to update ongoing appointment", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.logout_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_logout_btn) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(HospDashboard.this, HomeDashboard.class);
                startActivity(intent);
                finishAffinity();
                return true;
            } else if (item.getItemId() == R.id.menu_change_password_btn) {
                changePass();
            }
            return false;
        });
        popupMenu.show();
    }

    private void reloadData() {
        recreate();
        swipeRefreshLayout.setRefreshing(false);
    }

    public void changePass() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to reset your password? You will be logged out.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            String email = currentUser.getEmail();
                            Log.d("Changed1", "password reset email sent to: " + email);
                            mAuth.signOut();
                            mAuth.sendPasswordResetEmail(email)
                                    .addOnCompleteListener(HospDashboard.this, task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(HospDashboard.this, "Password reset email sent to your registered email address.", Toast.LENGTH_SHORT).show();
                                            FirebaseAuth.getInstance().signOut();
                                            SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.remove("userUid");
                                            editor.apply();
                                            Intent intent = new Intent(HospDashboard.this, HomeDashboard.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(HospDashboard.this, "Failed to send password reset email. Please check your email address.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.create().show();
    }
    private boolean isValidUrl(String url) {
        if (URLUtil.isValidUrl(url)) {
            try {
                new URL(url).toURI();
                return true;
            } catch (MalformedURLException | URISyntaxException e) {
                return false;
            }
        }
        return false;
    }
    private void removeOngoingAppointmentFromDatabase(String currentEmail) {
        DatabaseReference hospitalsRef = FirebaseDatabase.getInstance().getReference("hospitals");
        DatabaseReference hospitalRef = hospitalsRef.child(currentEmail).child("ongoingAppointment");
        hospitalRef.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null) {
                    Toast.makeText(HospDashboard.this, "Ongoing appointment removed successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HospDashboard.this, "Failed to remove ongoing appointment", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}