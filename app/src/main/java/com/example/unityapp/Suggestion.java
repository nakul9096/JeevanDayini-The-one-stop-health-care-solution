package com.example.unityapp;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class Suggestion {

    public static void showSuggestionDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.suggestion_dialog, null);

        final EditText suggestionEditText = view.findViewById(R.id.suggestionEditText);

        builder.setView(view)
                .setTitle("Suggestion box")
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "Thank you! for contributing your views", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null);
        final AlertDialog dialog = builder.create();
        dialog.show();
    }
}
