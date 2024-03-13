package com.example.unityapp;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
public class ViewData2 extends AppCompatActivity {

    private TextView textViewName1;
    private TextView textViewRelation1;
    private TextView textViewAddress1;
    private TextView textViewbloodgroup1;
    private TextView textViewpostalcode1;
    private TextView textViewBirthDate1;
    private TextView textViewEmail1;
    private TextView textViewPhone1;
    private TextView textViewAadhaar1;
    private TextView textViewID1;
    private TextView textViewtimestamp1;
    private TextView textViewtype1;
    private TextView textViewState1;
    private TextView textViewcity1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data2); // Replace 'your_layout' with the name of your XML layout file

        // Initialize TextViews
        textViewName1 = findViewById(R.id.textViewName1);
        textViewRelation1 = findViewById(R.id.textViewRelation1);
        textViewAddress1 = findViewById(R.id.textViewAddress1);
        textViewbloodgroup1 = findViewById(R.id.textViewbloodgroup1);
        textViewpostalcode1 = findViewById(R.id.textViewpostalcode1);
        textViewBirthDate1 = findViewById(R.id.textViewBirthDate1);
        textViewEmail1 = findViewById(R.id.textViewEmail1);
        textViewPhone1 = findViewById(R.id.textViewPhone1);
        textViewAadhaar1 = findViewById(R.id.textViewAadhaar1);
        textViewID1 = findViewById(R.id.textViewID1);
        textViewtimestamp1 = findViewById(R.id.textViewtimestamp1);
        textViewtype1 = findViewById(R.id.textViewtype1);
        textViewState1 = findViewById(R.id.textViewState1);
        textViewcity1 = findViewById(R.id.textViewcity1);

        // Retrieve data from the database (example)
        String name = "John Doe";
        String relation = "Friend";
        String address = "123 Main St";
        String bloodGroup = "A+";
        String postalCode = "12345";
        String birthDate = "01/01/1990";
        String email = "john@example.com";
        String phone = "123-456-7890";
        String aadhaar = "123456789012";
        String userID = "ABC123";
        String timestamp = "2024-03-12 15:30:00";
        String type = "Admin";
        String state = "California";
        String city = "Los Angeles";

        // Set retrieved data to TextViews
        textViewName1.setText(name);
        textViewRelation1.setText(relation);
        textViewAddress1.setText(address);
        textViewbloodgroup1.setText(bloodGroup);
        textViewpostalcode1.setText(postalCode);
        textViewBirthDate1.setText(birthDate);
        textViewEmail1.setText(email);
        textViewPhone1.setText(phone);
        textViewAadhaar1.setText(aadhaar);
        textViewID1.setText(userID);
        textViewtimestamp1.setText(timestamp);
        textViewtype1.setText(type);
        textViewState1.setText(state);
        textViewcity1.setText(city);
    }
}
