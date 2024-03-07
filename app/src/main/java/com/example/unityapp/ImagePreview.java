package com.example.unityapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ImagePreview extends AppCompatActivity {

    private ZoomageView zoomageView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        String selectedUserName = getIntent().getStringExtra("selectedUser");
        String profileImageUrl = getIntent().getStringExtra("profileimg");
        String aadharImageUrl = getIntent().getStringExtra("aadharimg");

        zoomageView = findViewById(R.id.zoomageView); // Initialize zoomageView
        progressBar = findViewById(R.id.progressBar); // Initialize progressBar

        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            loadImageWithProgress(profileImageUrl);
        } else if (aadharImageUrl != null && !aadharImageUrl.isEmpty()) {
            loadImageWithProgress(aadharImageUrl);
        } else {
            Toast.makeText(ImagePreview.this, "Image not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadImageWithProgress(String imageUrl) {
        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Load image with Picasso into ZoomageView
        Picasso.get()
                .load(imageUrl)
                .into(zoomageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        // Image loaded successfully
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        // Handle error (e.g., display a placeholder image or show an error message)
                        progressBar.setVisibility(View.GONE);
                        zoomageView.setImageResource(R.drawable.placeholder_image); // Replace with your placeholder image
                        Toast.makeText(ImagePreview.this, "Error loading image", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
