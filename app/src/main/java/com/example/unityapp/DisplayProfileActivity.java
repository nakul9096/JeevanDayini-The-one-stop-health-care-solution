package com.example.unityapp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DisplayProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_profile);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String name = getSafeString(extras, "name");
            String userId = getSafeString(extras, "userId");
            String email = getSafeString(extras, "email");
            String gender = getSafeString(extras, "gender");
            String state = getSafeString(extras, "state");
            String city = getSafeString(extras, "city");
            String phone = getSafeString(extras, "phone");
            String bloodGroup = getSafeString(extras, "bloodGroup");
            String emergencyContactName = getSafeString(extras, "emergencyContactName");
            String emergencyContactPhone = getSafeString(extras, "emergencyContactPhone");

            // Find TextViews in your layout
            TextView tvName = findViewById(R.id.tvName);
            TextView tvUserId = findViewById(R.id.tvUserId);
            TextView tvEmail = findViewById(R.id.tvEmail);
            TextView tvGender = findViewById(R.id.tvGender);
            TextView tvState = findViewById(R.id.tvState);
            TextView tvCity = findViewById(R.id.tvCity);
            TextView tvPhone = findViewById(R.id.tvPhone);
            TextView tvBloodGroup = findViewById(R.id.tvBloodGroup);
            TextView tvEmergencyContactName = findViewById(R.id.tvEmergencyContactName);
            TextView tvEmergencyContactPhone = findViewById(R.id.tvEmergencyContactPhone);

            tvName.setText(name);
            tvUserId.setText(userId);
            tvEmail.setText(email);
            tvGender.setText(gender);
            tvState.setText(state);
            tvCity.setText(city);
            tvPhone.setText(phone);
            tvBloodGroup.setText(bloodGroup);
            tvEmergencyContactName.setText(emergencyContactName);
            tvEmergencyContactPhone.setText(emergencyContactPhone);
        }
    }

    private String getSafeString(Bundle bundle, String key) {
        String value = bundle.getString(key);
        return value != null ? value : "N/A";
    }
}