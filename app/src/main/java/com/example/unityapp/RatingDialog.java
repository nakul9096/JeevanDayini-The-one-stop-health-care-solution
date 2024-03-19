// RatingDialog.java

package com.example.unityapp;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RatingDialog {

    public static void showRatingDialog(final Context context, final String userEmail, final String userName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_rating_dialog, null);

        final RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        final EditText descriptionEditText = view.findViewById(R.id.descriptionEditText);

        builder.setView(view)
                .setTitle("Rate Our App")
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        float rating = ratingBar.getRating();
                        String description = descriptionEditText.getText().toString().trim();

                        // Store the rating data in Firebase Realtime Database
                        saveRatingData(userEmail, userName, rating, description);

                        // Do something with the rating and description, for example, show a Toast
                        if (!TextUtils.isEmpty(description)) {
                            Toast.makeText(context, "You rated: " + rating + "\nComment: " + description, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "You rated: " + rating, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null);

        final AlertDialog dialog = builder.create();

        // Show the dialog
        dialog.show();
    }

    private static void saveRatingData(String userEmail, String userName, float rating, String description) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ratings");

        // Use the user's email as the key
        String userKey = userEmail.replace("@", "").replace(".", "");

        // Create a Rating object with user details
        Rating ratingObject = new Rating(userName, userEmail, rating, description);

        // Save data to the database
        databaseReference.child(userKey).setValue(ratingObject);
    }

    // Rating class to represent the data structure
    public static class Rating {
        public String name;
        public String email;
        public float rating;
        public String description;

        public Rating() {
            // Default constructor required for Firebase
        }

        public Rating(String name, String email, float rating, String description) {
            this.name = name;
            this.email = email;
            this.rating = rating;
            this.description = description;
        }
    }
}
