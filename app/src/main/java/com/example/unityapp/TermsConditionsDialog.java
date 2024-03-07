package com.example.unityapp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class TermsConditionsDialog extends Dialog {

    private Context context;

    public TermsConditionsDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_terms_conditions_dialog);

        TextView headingTextView = findViewById(R.id.textViewHeading);
        headingTextView.setText("Confirmation!");
        headingTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

        TextView termsAndConditionsTextView = findViewById(R.id.textViewTermsConditions);
        termsAndConditionsTextView.setText(R.string.terms_and_conditions);

        CheckBox acceptCheckBox = findViewById(R.id.checkBoxAccept);

        Button confirmButton = findViewById(R.id.buttonConfirm);
        Button cancelButton = findViewById(R.id.buttonCancel);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (acceptCheckBox.isChecked()) {
                    // User accepted terms and conditions, perform confirmation action
                    dismiss();
                    DonateBlood donateBloodActivity = (DonateBlood) context;
                    donateBloodActivity.onSubmitButtonClick();
                } else {
                    Toast.makeText(context, "Please accept terms and conditions", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // User canceled, close the dialog
                dismiss();
                Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
