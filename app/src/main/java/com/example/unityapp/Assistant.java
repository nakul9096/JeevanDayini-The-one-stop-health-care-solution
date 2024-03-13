package com.example.unityapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Assistant extends AppCompatActivity {
    private TextView greetingTextView;
    private TextView helpPromptTextView;
    private LinearLayout tagsLayout;
    private EditText messageEditText;
    private TextView messageTextView;
    private RelativeLayout messageContainer;
    private Button sendButton;
    private FirebaseAuth mAuth;
    private boolean isConversationInProgress = false;
    private DatabaseReference usersDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistant);
        greetingTextView = findViewById(R.id.greetingTextView);
        helpPromptTextView = findViewById(R.id.helpPromptTextView);
        tagsLayout = findViewById(R.id.tagsLayout);
        messageContainer = findViewById(R.id.messageContainer);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        mAuth = FirebaseAuth.getInstance();
        messageEditText.setVisibility(View.GONE);
        sendButton.setVisibility(View.GONE);
        tagsLayout.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        findViewById(R.id.tagAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConversationInProgress) {
                    Helper.showToast(Assistant.this,"One conversation at a time, please");
                    return;
                }
                updateMessage("Account related information", true);
                isConversationInProgress = true;
                if (usersDatabaseRef != null) {
                    DatabaseReference chatDataRef = FirebaseDatabase.getInstance().getReference().child("chatdata").child("account");
                    chatDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String accountInfo = dataSnapshot.getValue(String.class);
                            updateReceivedMessage(accountInfo);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
        });

        findViewById(R.id.tagAccount1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isConversationInProgress) {
                    Helper.showToast(Assistant.this, "One conversation at a time, please");
                    return;
                }
                updateMessage("Payment related information", true);
                isConversationInProgress = true;
                if (usersDatabaseRef != null) {
                    DatabaseReference chatDataRef = FirebaseDatabase.getInstance().getReference().child("chatdata").child("payment");
                    chatDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String paymentInfo = dataSnapshot.getValue(String.class);
                            updateReceivedMessage(paymentInfo);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
        });
        findViewById(R.id.tagAccount2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isConversationInProgress) {
                    Helper.showToast(Assistant.this,"One conversation at a time, please");
                    return;
                }
                updateMessage("Service related Info", true);
                if (usersDatabaseRef != null) {
                    DatabaseReference chatDataRef = FirebaseDatabase.getInstance().getReference().child("chatdata").child("service");
                    chatDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String serviceInfo = dataSnapshot.getValue(String.class);
                            updateReceivedMessage(serviceInfo);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
        });
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userEmail = currentUser.getEmail();
        if (userEmail != null) {
            Log.d("Assistant", "User Email: " + userEmail);
            if (currentUser != null) {
                String encodedEmail = userEmail.replace("@", "").replace(".", "");
                Log.d("Assistant", "encoded Email: " + encodedEmail);
                usersDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(encodedEmail);
                usersDatabaseRef.child("fullName").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String userName = dataSnapshot.getValue(String.class);
                        updateGreeting(userName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        } else {
            Helper.showToast(Assistant.this,"User email is null");
            finish();
        }
    }

    private void updateGreeting(String userName) {
        String greeting = "Hello, " + userName;
        greetingTextView.setText(greeting);
    }

    private void updateMessage(String message, boolean isSent) {
        TextView newMessageTextView = new TextView(this);
        newMessageTextView.setText(message);
        newMessageTextView.setTextColor(Color.WHITE);
        int paddingInDp = 8;
        float scale = getResources().getDisplayMetrics().density;
        int paddingInPixels = (int) (paddingInDp * scale + 0.5f);
        newMessageTextView.setPadding(paddingInPixels, paddingInPixels, paddingInPixels, paddingInPixels);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int marginInDp = 8;
        int marginInPixels = (int) (marginInDp * scale + 0.5f);
        layoutParams.setMargins(marginInPixels, marginInPixels, marginInPixels, marginInPixels);
        newMessageTextView.setLayoutParams(layoutParams);

        LinearLayout messagesLinearLayout = findViewById(R.id.messagesLinearLayout);
        messagesLinearLayout.addView(newMessageTextView);

        newMessageTextView.setBackgroundResource(R.drawable.msg_bg);
        if (isSent) {
            newMessageTextView.setBackgroundResource(R.drawable.message_item);
        } else {
            newMessageTextView.setBackgroundResource(R.drawable.msg_bg);
        }
    }

    private void updateReceivedMessage(String message) {
        TextView newMessageTextView = new TextView(this);
        newMessageTextView.setText(message);
        newMessageTextView.setTextColor(Color.BLACK);

        int paddingInDp = 8;
        float scale = getResources().getDisplayMetrics().density;
        int paddingInPixels = (int) (paddingInDp * scale + 0.5f);
        newMessageTextView.setPadding(paddingInPixels, paddingInPixels, paddingInPixels, paddingInPixels);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int marginInDp = 8;
        int marginInPixels = (int) (marginInDp * scale + 0.5f);
        layoutParams.setMargins(marginInPixels, marginInPixels, marginInPixels, marginInPixels);
        newMessageTextView.setLayoutParams(layoutParams);
        LinearLayout messagesLinearLayout = findViewById(R.id.messagesLinearLayout);
        messagesLinearLayout.addView(newMessageTextView);
        newMessageTextView.setBackgroundResource(R.drawable.msg_rcvd_bg);
        if (isConversationInProgress && message.equalsIgnoreCase("Account related information")) {
            int selectedOption = 1;

            if (selectedOption == 1) {
                showRefundField();
            }
        }
        if (isConversationInProgress && message.equalsIgnoreCase("Payment related information")) {
            int selectedOption = 1;

            if (selectedOption == 1) {
                showRefundField();
            }
            if (selectedOption == 2) {
                showPlanField();
            }
            if (selectedOption == 3) {
                showPaymenttcField();
            }
            if (selectedOption == 4) {
                showOtherField();
            }
        }
        if (isConversationInProgress && message.equalsIgnoreCase("Service related information")) {
            int selectedOption = 1;
            if (selectedOption == 1) {
                showRefundField();
            }
        }
    }

    private void showRefundField() {
        String refundInformation = "Your refund information is here";

        TextView refundMessageTextView = new TextView(this);
        refundMessageTextView.setText("Refund Information: " + refundInformation);
        refundMessageTextView.setTextColor(Color.BLACK);

        int paddingInDp = 8;
        float scale = getResources().getDisplayMetrics().density;
        int paddingInPixels = (int) (paddingInDp * scale + 0.5f);
        refundMessageTextView.setPadding(paddingInPixels, paddingInPixels, paddingInPixels, paddingInPixels);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int marginInDp = 8;
        int marginInPixels = (int) (marginInDp * scale + 0.5f);
        layoutParams.setMargins(marginInPixels, marginInPixels, marginInPixels, marginInPixels);
        refundMessageTextView.setLayoutParams(layoutParams);

        LinearLayout messagesLinearLayout = findViewById(R.id.messagesLinearLayout);
        messagesLinearLayout.addView(refundMessageTextView);

        refundMessageTextView.setBackgroundResource(R.drawable.msg_rcvd_bg);

        messageEditText.setVisibility(View.VISIBLE);
        sendButton.setVisibility(View.VISIBLE);
    }


    private void showPlanField() {
    }

    private void showPaymenttcField() {
    }

    private void showOtherField() {
    }
}
