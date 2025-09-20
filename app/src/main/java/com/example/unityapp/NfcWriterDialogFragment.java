package com.example.unityapp;

import android.app.Dialog;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.airbnb.lottie.LottieAnimationView;

import java.nio.charset.Charset;
import java.util.Locale;

public class NfcWriterDialogFragment extends DialogFragment {
    private static final String ARG_DATA = "arg_data";
    private String userData;
    private TextView statusTextView;
    private LottieAnimationView lottieAnimationView;
    private TextView countdownTextView; // New TextView for the countdown
    private Button closeButton; // New Button for closing
    private final Handler handler = new Handler();

    public static NfcWriterDialogFragment newInstance(String userData) {
        NfcWriterDialogFragment fragment = new NfcWriterDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATA, userData);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            userData = getArguments().getString(ARG_DATA);
        }

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_nfc_writer, null);
        statusTextView = view.findViewById(R.id.statusTextView);
        lottieAnimationView = view.findViewById(R.id.lottieAnimationView);
        countdownTextView = view.findViewById(R.id.countdownTextView); // Initialize the new TextView
        closeButton = view.findViewById(R.id.closeButton); // Initialize the new Button

        // Set the close button to be hidden by default
        closeButton.setVisibility(View.GONE);

        // Set a click listener for the close button
        closeButton.setOnClickListener(v -> dismiss());

        // Start animation
        lottieAnimationView.setAnimation(R.raw.nfc_available_animation);
        lottieAnimationView.playAnimation();

        if (NfcAdapter.getDefaultAdapter(requireContext()) == null) {
            statusTextView.setText("NFC unavailable ❌\nPlease use a device with NFC.");
        } else if (!NfcAdapter.getDefaultAdapter(requireContext()).isEnabled()) {
            statusTextView.setText("NFC disabled ⚠️\nTurn it on in Settings.");
        } else {
            statusTextView.setText("Tap your health card on mobile to update…");
        }

        // Return a new AlertDialog.Builder with the updated view
        return new AlertDialog.Builder(requireContext())
                .setView(view)
                .setCancelable(false) // This line prevents dismissing by clicking outside
                .create();
    }

    // Called from Activity when a new NFC intent is received
    public void onNfcIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            boolean success = writeToTag(tag, userData);
            if (success) {
                statusTextView.setText("Health card updated successfully ✅");
                Toast.makeText(requireContext(), "NFC write success!", Toast.LENGTH_SHORT).show();
                // Start the countdown and show the close button
                startDismissalCountdown();
                closeButton.setVisibility(View.VISIBLE);
            } else {
                statusTextView.setText("Error writing to NFC card ❌");
                Toast.makeText(requireContext(), "NFC write failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startDismissalCountdown() {
        final int[] countdown = {3};
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (countdown[0] > 0) {
                    countdownTextView.setText("Closing in " + countdown[0] + "...");
                    countdown[0]--;
                    handler.postDelayed(this, 1000); // Delay for 1 second
                } else {
                    countdownTextView.setText("Closing...");
                    // Optional: auto-dismiss after a moment
                    handler.postDelayed(() -> dismiss(), 1000);
                }
            }
        });
    }

    private boolean writeToTag(Tag tag, String data) {
        try {
            NdefRecord textRecord = createTextRecord(data);
            NdefMessage message = new NdefMessage(new NdefRecord[]{textRecord});

            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    return false;
                }
                ndef.writeNdefMessage(message);
                ndef.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private NdefRecord createTextRecord(String text) {
        byte[] langBytes = Locale.ENGLISH.getLanguage().getBytes(Charset.forName("US-ASCII"));
        byte[] textBytes = text.getBytes(Charset.forName("UTF-8"));
        int langLength = langBytes.length;
        int textLength = textBytes.length;
        byte[] payload = new byte[1 + langLength + textLength];
        payload[0] = (byte) langLength;
        System.arraycopy(langBytes, 0, payload, 1, langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], payload);
    }
}