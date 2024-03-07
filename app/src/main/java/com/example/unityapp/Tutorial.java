package com.example.unityapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class Tutorial extends AppCompatActivity {
    private static final String VIDEO_ID = "Ih769E5HZpg";
    private LinearLayout namesLayout;
    private DatabaseReference tutorialsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        namesLayout = findViewById(R.id.namesLayoutTL);
        tutorialsRef = FirebaseDatabase.getInstance().getReference().child("tutorials");
        fetchTutorials();
    }

    private void fetchTutorials() {
        tutorialsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                namesLayout.removeAllViews(); // Clear existing views
                for (DataSnapshot tutorialSnapshot : dataSnapshot.getChildren()) {
                    String id = tutorialSnapshot.child("tutorialId").getValue(String.class);
                    String title = tutorialSnapshot.child("title").getValue(String.class);
                    String videoId = tutorialSnapshot.child("url").getValue(String.class);

                    createNameCard(id, title, videoId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    private void createNameCard(String id, String title, final String videoId) {
        // Create a new CardView for each tutorial
        CardView videoCard = (CardView) LayoutInflater.from(this).inflate(R.layout.tutorial_video_cards, namesLayout, false);
        TextView IDTextView = videoCard.findViewById(R.id.textViewVideoId2);
        IDTextView.setText("Video ID: " + id);
        TextView TitleTextView = videoCard.findViewById(R.id.textViewTitle2);
        TitleTextView.setText("Video Title: " + title);
        namesLayout.addView(videoCard);
        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtubePlayerView);
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0);
            }
        });
    }
}
