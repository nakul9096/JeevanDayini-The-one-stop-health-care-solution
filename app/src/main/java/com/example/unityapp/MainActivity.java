package com.example.unityapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String CHANNEL_ID = "alert1";
    private static final int Splash_timeout = 2500;
    TextView wel;
    private ValueEventListener loggedOutListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        wel = findViewById(R.id.welcometextview);
        Animation myanimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.animation2);
        wel.startAnimation(myanimation);
        Log.d(TAG, "onCreate called");
        FirebaseInstallations installations = FirebaseInstallations.getInstance();
        Task<String> task = installations.getId();
        task.addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    String installationId = task.getResult();
                    // Use the Installation ID here
                    Log.d("InstallationID", installationId);
                } else {
                    // Handle error
                    Log.e("InstallationID", "Error getting Installation ID", task.getException());
                }
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkAuthenticationAndRedirect();
            }
        }, Splash_timeout);
        createNotificationChannel();
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {

                            return;
                        }
                        String token = task.getResult().getToken();
                        Log.e("TOKEN", token);
                    }
                });
    }
    private void createNotificationChannel() {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        NotificationChannel channel = new NotificationChannel("12345", "Alerts", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);
    }

    private void checkAuthenticationAndRedirect() {
        Executor executor = Executors.newSingleThreadExecutor();
        CompletableFuture.supplyAsync(() -> {
            try {
                AppDatabase db = AppDatabase.getDatabase(this);
                LoginDataDao loginDataDao = db.loginDataDao();
                return loginDataDao.getLoginDataByUid(getCurrentUserId());
            } catch (Exception e) {
                Log.e(TAG, "Error during database operation: " + e.getMessage(), e);
                return null;
            }
        }, executor).thenAccept(this::handleLoginData);
    }
    private void handleLoginData(LoginData loginData) {
        if (loginData != null) {
            String userUid = loginData.getUserUid();
            String userType = loginData.getUserType();
            Log.d("test9", "User data found in database - UserUid: " + userUid + ", UserType: " + userType);
            if ("user".equals(userType)) {
                Intent intent = new Intent(MainActivity.this, Session.class);
                startActivity(intent);
            } else if ("admin".equals(userType)) {
                Intent intent = new Intent(MainActivity.this, Management.class);
                startActivity(intent);
            } else if ("Hospital".equals(userType)) {
                Intent intent = new Intent(MainActivity.this, HospDashboard.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MainActivity.this, HomeDashboard.class);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(MainActivity.this, HomeDashboard.class);
            startActivity(intent);
        }
        finish();
    }

    private String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return (currentUser != null) ? currentUser.getUid() : null;
    }
    @Override
    protected void onStop()
    {
        super.onStop();
    }
}
