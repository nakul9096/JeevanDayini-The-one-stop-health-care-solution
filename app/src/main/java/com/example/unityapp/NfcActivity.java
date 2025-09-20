package com.example.unityapp;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.airbnb.lottie.LottieAnimationView;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class NfcActivity extends AppCompatActivity {
    private static final String TAG = "NfcActivity";
    private NfcAdapter nfcAdapter;
    private AlertDialog nfcDialog;
    private TextView nfcMessageText;
    private LottieAnimationView nfcAnimationView;
    private Button closeButton;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isWriteMode = false;
    private String fullNameToWrite, userIdToWrite, genderToWrite, stateToWrite, cityToWrite, phoneToWrite, bgToWrite, aadharToWrite;

    // Declare a MediaPlayer object
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("NFC_MODE")) {
            String mode = intent.getStringExtra("NFC_MODE");
            if ("write".equals(mode)) {
                isWriteMode = true;
                fullNameToWrite = intent.getStringExtra("FULL_NAME");
                userIdToWrite = intent.getStringExtra("USER_ID");
                genderToWrite = intent.getStringExtra("GENDER");
                stateToWrite = intent.getStringExtra("STATE");
                cityToWrite = intent.getStringExtra("CITY");
                phoneToWrite = intent.getStringExtra("PHONE");
                bgToWrite = intent.getStringExtra("BLOOD_GROUP");
                aadharToWrite = intent.getStringExtra("AADHAR");
                showNfcDialog("Tap your health card on mobile to update");
            } else if ("read".equals(mode)) {
                isWriteMode = false;
                showNfcDialog("Tap your health card on mobile to read");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not supported on this device.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC is turned off. Please enable it.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Intent nfcIntent = new Intent(this, NfcActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, nfcIntent, PendingIntent.FLAG_MUTABLE);
        IntentFilter[] intentFiltersArray = new IntentFilter[] {
                new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
                new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED),
                new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        };
        String[][] techLists = new String[][] { new String[] { android.nfc.tech.Ndef.class.getName() } };
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techLists);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
        // Release the MediaPlayer resources
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (nfcDialog != null && nfcDialog.isShowing()) {
            handleNfcIntent(intent);
        }
    }

    private void handleNfcIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action) ||
                NfcAdapter.ACTION_TECH_DISCOVERED.equals(action) ||
                NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {

            // Play the custom sound
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            mediaPlayer = MediaPlayer.create(this, R.raw.nfc_sound);
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag != null) {
                if (isWriteMode) {
                    try {
                        Ndef ndef = Ndef.get(tag);
                        if (ndef != null) {
                            ndef.connect();
                            if (ndef.isWritable()) {
                                String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                                String data =
                                        "Name: " + fullNameToWrite + "\n" +
                                                "UserId: " + userIdToWrite + "\n" +
                                                "Gender: " + genderToWrite + "\n" +
                                                "State: " + stateToWrite + "\n" +
                                                "City: " + cityToWrite + "\n" +
                                                "Phone: " + phoneToWrite + "\n" +
                                                "Blood Group: " + bgToWrite + "\n" +
                                                "Aadhar: " + aadharToWrite + "\n" +
                                                "Last Updated: " + timeStamp;
                                NdefRecord record = createTextRecord(data);
                                NdefMessage message = new NdefMessage(record);
                                ndef.writeNdefMessage(message);
                                updateDialog("Health card updated successfully ✅", null);
                            } else {
                                updateDialog("Error: Tag is not writable ❌", null);
                            }
                            ndef.close();
                        } else {
                            updateDialog("Error: Tag does not support NDEF ❌", null);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "NFC Write Error", e);
                        updateDialog("Error: " + e.getMessage() + " ❌", null);
                    }
                } else { // Read Mode
                    try {
                        Ndef ndef = Ndef.get(tag);
                        if (ndef != null) {
                            ndef.connect();
                            NdefMessage message = ndef.getCachedNdefMessage();
                            if (message != null) {
                                StringBuilder readText = new StringBuilder();
                                for (NdefRecord record : message.getRecords()) {
                                    if (record.getTnf() == NdefRecord.TNF_WELL_KNOWN &&
                                            Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)) {
                                        String text = readTextFromNdefRecord(record);
                                        readText.append(text).append("\n");
                                    }
                                }
                                updateDialog("Read data:", readText.toString());
                            } else {
                                updateDialog("Error: No NDEF message found ❌", null);
                            }
                            ndef.close();
                        } else {
                            updateDialog("Error: Tag does not support NDEF ❌", null);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "NFC Read Error", e);
                        updateDialog("Error: " + e.getMessage() + " ❌", null);
                    }
                }
            }
        }
    }

    private void updateDialog(String message, String content) {
        if (nfcDialog != null && nfcDialog.isShowing()) {
            if (nfcAnimationView != null) {
                nfcAnimationView.loop(false);
                nfcAnimationView.cancelAnimation();
            }

            LinearLayout mainLayout = (LinearLayout) ((CardView) nfcDialog.findViewById(R.id.cardView)).getChildAt(0);
            mainLayout.removeAllViews();

            TextView titleTextView = new TextView(this);
            titleTextView.setText(message);
            titleTextView.setTextSize(18);
            titleTextView.setTextColor(Color.BLACK);
            titleTextView.setGravity(Gravity.CENTER);
            mainLayout.addView(titleTextView);

            if (content != null) {
                String[] lines = content.split("\n");
                for (String line : lines) {
                    LinearLayout lineLayout = new LinearLayout(this);
                    lineLayout.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    lineParams.topMargin = 8;
                    lineLayout.setLayoutParams(lineParams);
                    lineLayout.setGravity(Gravity.START);

                    if (line.contains(":")) {
                        String[] parts = line.split(":", 2);
                        TextView labelView = new TextView(this);
                        labelView.setText(parts[0].trim() + ":");
                        labelView.setTextSize(16);
                        labelView.setTextColor(Color.parseColor("#007BFF"));
                        labelView.setGravity(Gravity.START);
                        lineLayout.addView(labelView);

                        TextView valueView = new TextView(this);
                        valueView.setText(parts[1].trim());
                        valueView.setTextSize(16);
                        valueView.setTextColor(Color.BLACK);
                        valueView.setGravity(Gravity.START);
                        lineLayout.addView(valueView);
                    } else {
                        TextView genericView = new TextView(this);
                        genericView.setText(line);
                        genericView.setTextSize(16);
                        genericView.setTextColor(Color.BLACK);
                        genericView.setGravity(Gravity.START);
                        lineLayout.addView(genericView);
                    }
                    mainLayout.addView(lineLayout);
                }
            }

            Button closeBtn = new Button(new ContextThemeWrapper(this, R.style.CustomButtonStyle));
            closeBtn.setText("Close");
            closeBtn.setOnClickListener(v -> finish());
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            buttonParams.topMargin = 20;
            buttonParams.gravity = Gravity.CENTER_HORIZONTAL;
            mainLayout.addView(closeBtn, buttonParams);
        }
    }

    private void showNfcDialog(String message) {
        if (nfcDialog != null && nfcDialog.isShowing()) {
            nfcDialog.dismiss();
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        nfcAnimationView = new LottieAnimationView(this);
        nfcAnimationView.setAnimation(R.raw.nfc_available_animation);
        nfcAnimationView.loop(true);
        nfcAnimationView.playAnimation();
        LinearLayout.LayoutParams animParams = new LinearLayout.LayoutParams(400, 400);
        layout.addView(nfcAnimationView, animParams);

        nfcMessageText = new TextView(this);
        nfcMessageText.setText(message);
        nfcMessageText.setTextColor(Color.BLACK);
        nfcMessageText.setTextSize(18);
        nfcMessageText.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams msgParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        msgParams.topMargin = 12;
        layout.addView(nfcMessageText, msgParams);

        closeButton = new Button(new ContextThemeWrapper(this, R.style.CustomButtonStyle));
        closeButton.setText("Close");
        closeButton.setOnClickListener(v -> finish());
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonParams.topMargin = 20;
        buttonParams.gravity = Gravity.CENTER_HORIZONTAL;
        layout.addView(closeButton, buttonParams);

        CardView cardView = new CardView(this);
        cardView.setId(R.id.cardView);
        cardView.setCardBackgroundColor(Color.WHITE);
        cardView.setRadius(40f);
        cardView.addView(layout);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(cardView);
        builder.setCancelable(false);
        nfcDialog = builder.create();
        nfcDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        nfcDialog.show();
    }

    private NdefRecord createTextRecord(String payload) {
        byte[] langBytes = "en".getBytes(StandardCharsets.US_ASCII);
        byte[] textBytes = payload.getBytes(StandardCharsets.UTF_8);
        int langLength = langBytes.length;
        int textLength = textBytes.length;
        byte[] payloadBytes = new byte[1 + langLength + textLength];
        payloadBytes[0] = (byte) langLength;
        System.arraycopy(langBytes, 0, payloadBytes, 1, langLength);
        System.arraycopy(textBytes, 0, payloadBytes, 1 + langLength, textLength);
        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payloadBytes);
    }

    private String readTextFromNdefRecord(NdefRecord record) {
        try {
            byte[] payload = record.getPayload();
            String encoding = ((payload[0] & 0x80) == 0) ? "UTF-8" : "UTF-16";
            int languageCodeLength = payload[0] & 0x3F;
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, encoding);
        } catch (Exception e) {
            return "";
        }
    }
}