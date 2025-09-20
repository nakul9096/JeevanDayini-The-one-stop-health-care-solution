package com.example.unityapp;

import android.content.Intent;

public interface NfcWriter {
    void onTagWrite(Intent intent);
}
