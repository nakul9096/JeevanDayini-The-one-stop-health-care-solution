package com.example

/

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FirebaseManager {
    private static final String DATABASE_URL = "https://your-firebase-project-id.firebaseio.com";
    private DatabaseReference database;

    public FirebaseManager() {
        initializeFirebase();
        database = FirebaseDatabase.getInstance().getReference();
    }

    private void initializeFirebase() {
        try {
            FileInputStream serviceAccount = new FileInputStream("path/to/your/firebase-credentials.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
                    .setDatabaseUrl(DATABASE_URL)
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            e.print
