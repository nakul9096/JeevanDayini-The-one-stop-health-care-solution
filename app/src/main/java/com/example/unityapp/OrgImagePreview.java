package com.example.unityapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class OrgImagePreview extends AppCompatActivity {
    private Spinner spinner;
    private ZoomageView zoomageView;
    private ProgressBar progressBar;
    private Button viewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_image_preview);
        spinner = findViewById(R.id.spinner);
        zoomageView = findViewById(R.id.zoomageView);
        progressBar = findViewById(R.id.progressBar);
        viewButton = findViewById(R.id.buttonView);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageUrl = getImageUrlForSelectedItem(spinner.getSelectedItemPosition());
                loadImageWithProgress(imageUrl);
            }
        });
    }

    private String getImageUrlForSelectedItem(int position) {
        Intent intent = getIntent();
        if (intent != null) {
            String imageUrl;
            switch (position) {
                case 0:
                    imageUrl = intent.getStringExtra("orgimg1");
                    break;
                case 1:
                    imageUrl = intent.getStringExtra("orgimg2");
                    break;
                case 2:
                    imageUrl = intent.getStringExtra("orgimg3");
                    break;
                case 3:
                    imageUrl = intent.getStringExtra("orgimg4");
                    break;
                default:
                    imageUrl = "";
            }
            return imageUrl;
        }
        return "";
    }

    private void loadImageWithProgress(String imageUrl) {
        progressBar.setVisibility(View.VISIBLE);
        Picasso.get()
                .load(imageUrl)
                .into(zoomageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        progressBar.setVisibility(View.GONE);
                        zoomageView.setImageResource(R.drawable.placeholder_image);
                        Toast.makeText(OrgImagePreview.this, "Error loading image", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}