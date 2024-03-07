package com.example.unityapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Tutorial_links extends AppCompatActivity {
    private static final String TAG = "EmailSender";
    private LinearLayout namesLayout;
    private EditText popupEditTextID;
    private EditText popupEditTextTitle;
    private EditText popupEditTextUrl;
    private Button popupSubmitBtn;
    private Button circleButton;
    private DatabaseReference tutorialsRef;
    private ProgressBar progressBar;
    private TextView t1;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_links);
        tutorialsRef = FirebaseDatabase.getInstance().getReference("tutorials");
        namesLayout = findViewById(R.id.namesLayoutT);
        circleButton = findViewById(R.id.circleButton);
        progressBar = findViewById(R.id.progressBar5);
        t1 = findViewById(R.id.textNoData);
        circleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTutorialPopup();
            }
        });
        database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("tutorials");
        Log.d(TAG, "Database accessed");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    t1.setVisibility(View.GONE);
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String title = userSnapshot.child("title").getValue(String.class);
                        String tutorialId = userSnapshot.child("tutorialId").getValue(String.class);
                        String url = userSnapshot.child("url").getValue(String.class);
                        Log.d(TAG, "title " + title);
                        Log.d(TAG, "id " + tutorialId);
                        Log.d(TAG, "url " + url);
                        if (title != null) {
                            createNameCard(tutorialId, title, url);
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                    t1.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                    t1.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage(), databaseError.toException());
                Toast.makeText(Tutorial_links.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void createNameCard(String id, String title, String url) {
        // Create a new CardView for each name
        CardView videoCard = (CardView) LayoutInflater.from(this).inflate(R.layout.video_card, namesLayout, false);
        TextView IDTextView = videoCard.findViewById(R.id.textViewVideoId);
        IDTextView.setText(id);
        TextView TitleTextView = videoCard.findViewById(R.id.textViewVideoTitle);
        TitleTextView.setText(title);
        TextView urlTextView = videoCard.findViewById(R.id.textViewVideoUrl);
        urlTextView.setText(url);
        namesLayout.addView(videoCard);
    }

    private void addTutorialToDatabase(String tutorialId, String title, String url) {
        TutorialVData tutorial = new TutorialVData(tutorialId, title, url);
        tutorialsRef.child(tutorialId).setValue(tutorial);
        Toast.makeText(Tutorial_links.this, "Video add successfull!", Toast.LENGTH_SHORT).show();
    }

    private void showAddTutorialPopup() {
        // Create a custom layout for the dialog
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_add_tutorial, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(popupView);
        AlertDialog dialog = dialogBuilder.create();

        // Find views in the custom layout
        popupEditTextID = popupView.findViewById(R.id.popupEditTextID);
        popupEditTextTitle = popupView.findViewById(R.id.popupEditTextTitle);
        popupEditTextUrl = popupView.findViewById(R.id.popupEditTextUrl);
        popupSubmitBtn = popupView.findViewById(R.id.popupSubmitButton);
        popupSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get data from EditText fields in the popup
                String id = popupEditTextID.getText().toString().trim();
                String title = popupEditTextTitle.getText().toString().trim();
                String url = popupEditTextUrl.getText().toString().trim();
                if (!id.isEmpty() && !title.isEmpty() && !url.isEmpty()) {
                    addTutorialToDatabase(id, title, url);
                    dialog.dismiss();
                } else {
                    Toast.makeText(Tutorial_links.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }
}
