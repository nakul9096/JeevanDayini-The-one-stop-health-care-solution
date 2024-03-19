package com.example.unityapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Login extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    TextView RegisterNow;
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private Intent intent1, intent2;
    private SignInButton btnGoogleSignIn;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.etUsername);
        editTextPassword = findViewById(R.id.etPassword);
        buttonLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressbar);
        btnGoogleSignIn = findViewById(R.id.signInButton);
        RegisterNow = findViewById(R.id.tvForgotPassword);
        RegisterNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
            }
        });
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
        btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void loginUser() {
        if (!isNetworkConnected()) {
            showToast("No internet connection. Please check your network settings.");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            showToast("Please enter both email and password");
            progressBar.setVisibility(View.GONE);
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            String userUid = mAuth.getCurrentUser().getUid();
                            FirebaseUser users = mAuth.getCurrentUser();
                            if (users != null) {
                                String userEmail = users.getEmail();
                                Log.d("LoginActivity", "User email: " + userEmail);
                                String deviceModel = Build.MODEL;
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                String signInTime = sdf.format(new Date());
                                String lastSeenTime = sdf.format(new Date());
                                Context context;
                                String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                                String sanitizedEmail = email.replace("@","").replace(".","");
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(sanitizedEmail).child("login_info").child(deviceModel);
                                userRef.child("deviceModel").setValue(deviceModel);
                                userRef.child("signInTime").setValue(signInTime);
                                userRef.child("lastSeenTime").setValue(lastSeenTime);
                                userRef.child("androidId").setValue(androidId);
                                DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference("Admin");
                                adminRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot adminSnapshot) {
                                        Log.d("LoginActivity", "DataSnapshot (Admin): " + adminSnapshot);

                                        if (adminSnapshot.exists()) {
                                            for (DataSnapshot adminData : adminSnapshot.getChildren()) {
                                                String adminType = adminData.child("adminType").getValue(String.class);
                                                if ("Admin".equals(adminType) || "FC Admin".equals(adminType)) {
                                                    saveUserToDatabase("admin", userUid);
                                                    updateFCMToken("Admin", email);
                                                    updateFCMTokenBroadcastRef(adminType, email);
                                                    Intent intent = new Intent(getApplicationContext(), Management.class);
                                                    startActivity(intent);
                                                    finish();
                                                    showToast("Admin login successful");
                                                } else {
                                                    saveUserToDatabase("Hospital", userUid);
                                                    updateFCMToken("Admin", email);
                                                    updateFCMTokenBroadcastRef(adminType, email);
                                                    Intent intent = new Intent(getApplicationContext(), HospDashboard.class);
                                                    startActivity(intent);
                                                    finish();
                                                    showToast("Login successful");
                                                }
                                            }
                                        } else {
                                            checkNormalUser(userUid, email);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.e("LoginActivity", "Database error (Admin): " + databaseError.getMessage());
                                    }
                                });
                            }
                        } else {
                            showToast("Authentication failed. Please check your credentials.");
                            Log.d("LoginActivity", "Authentication failed: " + task.getException().getMessage());
                        }
                    }
                });
    }
    private void showToast(String message) {
        Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
    }

    private void saveUserToDatabase(String userType, String userUid) {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userUid", userUid);
        editor.apply();
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Perform database operations on a background thread
            Log.d("LoginActivity", "uid: " + userUid + "type: " + userType);
            AppDatabase db = AppDatabase.getDatabase(this);
            LoginDataDao loginDataDao = db.loginDataDao();
            LoginData loginData = new LoginData(userUid, userType);
            loginDataDao.insertLoginData(loginData);
        });
    }

    private void updateFCMToken(String userType, String email) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String fcmToken = task.getResult();
                        String sanitizedUserUid = email.replace("@", "").replace(".", "");
                        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference(userType)
                                .child(sanitizedUserUid)
                                .child("fcmToken");
                        userReference.setValue(fcmToken);
                        Log.d("LoginActivity", "FCM Token updated: " + fcmToken);
                        if ("Admin".equals(userType) || "FC Admin".equals(userType)) {
                            DatabaseReference adminReference = FirebaseDatabase.getInstance().getReference("Admin")
                                    .child(sanitizedUserUid)
                                    .child("fcmToken");
                            adminReference.setValue(fcmToken);
                        }
                    } else {
                        Log.e("LoginActivity", "Failed to get FCM Token: " + task.getException());
                    }
                });
    }

    private void updateFCMTokenBroadcastRef(String userType, String email) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String fcmToken = task.getResult();
                        String sanitizedUserUid = email.replace("@", "").replace(".", "");
                        DatabaseReference broadcastRef = FirebaseDatabase.getInstance().getReference("broadcast");

                        if (userType.equals("user")) {
                            DatabaseReference userReference = broadcastRef
                                    .child("user")
                                    .child(sanitizedUserUid)
                                    .child("fcmToken");
                            userReference.setValue(fcmToken);
                            Log.d("LoginActivity", "FCM Token updated for " + userType + ": " + fcmToken);
                        }
                        if ("Admin".equals(userType) || "FC Admin".equals(userType) || "Hospital".equals(userType) || "Clinic".equals(userType) || "Blood banks".equals(userType)) {
                            DatabaseReference adminReference = broadcastRef
                                    .child(userType)
                                    .child(sanitizedUserUid)
                                    .child("fcmToken");
                            adminReference.setValue(fcmToken);
                            Log.d("LoginActivity", "FCM Token updated for " + userType + " under adminType: " + fcmToken);
                        }
                    } else {
                        Log.e("LoginActivity", "Failed to get FCM Token: " + task.getException());
                    }
                });
    }

    private void checkNormalUser(String userUid, String email) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                if (userSnapshot.exists()) {
                    saveUserToDatabase("user", userUid);
                    updateFCMToken("users", email);
                    updateFCMTokenBroadcastRef("user", email);
                    Intent intent = new Intent(getApplicationContext(), Session.class);
                    startActivity(intent);
                    finish();
                    showToast("Login successful");
                } else {
                    showToast("User not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("LoginActivity", "Database error (Users): " + databaseError.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w("LoginActivity", "Google sign in failed", e);
                showToast("Google sign in failed");
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(Login.this, "signInWithCredential:success", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("LoginActivity", "signInWithCredential:failure", task.getException());
                            showToast("Authentication failed");
                        }
                    }
                });
    }
    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}