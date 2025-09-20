package com.example.unityapp;

import android.app.Dialog;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class NfcReaderDialogFragment extends DialogFragment implements NfcHandler {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(requireContext()); // waiting dialog
    }

    @Override
    public void onNfcIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag == null) {
            Toast.makeText(getContext(), "No NFC tag", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                Toast.makeText(getContext(), "Not NDEF supported", Toast.LENGTH_SHORT).show();
                return;
            }
            ndef.connect();
            NdefMessage msg = ndef.getNdefMessage();
            ndef.close();
            if (msg != null) {
                for (NdefRecord rec : msg.getRecords()) {
                    if (rec.getTnf() == NdefRecord.TNF_WELL_KNOWN &&
                            Arrays.equals(rec.getType(), NdefRecord.RTD_TEXT)) {
                        String text = readText(rec);
                        Toast.makeText(getContext(), "Read: " + text, Toast.LENGTH_LONG).show();
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Read failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String readText(NdefRecord record) throws UnsupportedEncodingException {
        byte[] payload = record.getPayload();
        String encoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
        int langCodeLen = payload[0] & 63;
        return new String(payload, langCodeLen + 1,
                payload.length - langCodeLen - 1, encoding);
    }
}
