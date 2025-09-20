package com.example.unityapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class HospitalListView extends AppCompatActivity {

    private RecyclerView recyclerView;
    // You will need to create a HospitalAdapter class
    // private HospitalAdapter adapter;
    private List<String> hospitalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_list_view);

        recyclerView = findViewById(R.id.hospital_list_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize and populate your list of hospitals
        hospitalList = new ArrayList<>();
        // Example: Add some dummy data
        hospitalList.add("City General Hospital");
        hospitalList.add("Unity Blood Bank");
        hospitalList.add("Community Health Center");

        // Set up the adapter (You need to create the HospitalAdapter class)
        // adapter = new HospitalAdapter(hospitalList);
        // recyclerView.setAdapter(adapter);
    }
}