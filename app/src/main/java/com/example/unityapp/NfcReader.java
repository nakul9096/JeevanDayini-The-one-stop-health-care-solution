package com.example.unityapp;

import android.nfc.Tag;

public interface NfcReader {
    void onTagDiscovered(Tag tag);
}
