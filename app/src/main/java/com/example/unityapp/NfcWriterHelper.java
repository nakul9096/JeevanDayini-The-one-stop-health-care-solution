package com.example.unityapp;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.Locale;

public class NfcWriterHelper {
    private final Activity activity;
    private final NfcAdapter nfcAdapter;
    private final String userData;

    public NfcWriterHelper(Activity activity, String userData) {
        this.activity = activity;
        this.userData = userData;
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(activity);
    }

    // Enable foreground dispatch (blocks other apps from handling the NFC event)
    public void enableForegroundDispatch() {
        if (nfcAdapter == null) return;
        Intent intent = new Intent(activity, activity.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        IntentFilter[] filters = new IntentFilter[]{};
        String[][] techList = new String[][]{};
        nfcAdapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    public void disableForegroundDispatch() {
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(activity);
        }
    }

    // Handle NFC intent
    public void handleNfcIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            boolean success = writeToTag(tag, userData);
            if (success) {
                Toast.makeText(activity, "✅ Health card updated successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "❌ Failed to update NFC card.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean writeToTag(Tag tag, String data) {
        try {
            NdefRecord textRecord = createTextRecord(data);
            NdefMessage message = new NdefMessage(new NdefRecord[]{textRecord});

            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) return false;
                ndef.writeNdefMessage(message);
                ndef.close();
                return true;
            } else {
                // Try to format the tag if it’s empty
                NdefFormatable formatable = NdefFormatable.get(tag);
                if (formatable != null) {
                    formatable.connect();
                    formatable.format(message);
                    formatable.close();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Create NDEF Text record
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
