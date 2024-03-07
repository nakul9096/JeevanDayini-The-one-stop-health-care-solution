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
                // Update message
                if (isConversationInProgress) {
                    // Display a toast message indicating that one conversation is allowed at a time
                    Toast.makeText(Assistant.this, "One conversation at a time, please", Toast.LENGTH_SHORT).show();
                    return;
                }
                updateMessage("Account related information", true);
                isConversationInProgress = true;
                // Handle received message
                if (usersDatabaseRef != null) {
                    // Search for the corresponding value in chatdata
                    DatabaseReference chatDataRef = FirebaseDatabase.getInstance().getReference().child("chatdata").child("account");
                    chatDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String accountInfo = dataSnapshot.getValue(String.class);
                            // Update received message with the retrieved account information
                            updateReceivedMessage(accountInfo);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle database error
                        }
                    });
                }
            }
        });

        findViewById(R.id.tagAccount1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update message

                if (isConversationInProgress) {
                    // Display a toast message indicating that one conversation is allowed at a time
                    Toast.makeText(Assistant.this, "One conversation at a time, please", Toast.LENGTH_SHORT).show();
                    return;
                }
                updateMessage("Payment related information", true);
                isConversationInProgress = true;
                // Handle received message
                if (usersDatabaseRef != null) {
                    // Search for the corresponding value in chatdata
                    DatabaseReference chatDataRef = FirebaseDatabase.getInstance().getReference().child("chatdata").child("payment");
                    chatDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String paymentInfo = dataSnapshot.getValue(String.class);
                            updateReceivedMessage(paymentInfo);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle database error
                        }
                    });
                }
            }
        });
        findViewById(R.id.tagAccount2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update message

                if (isConversationInProgress) {
                    // Display a toast message indicating that one conversation is allowed at a time
                    Toast.makeText(Assistant.this, "One conversation at a time, please", Toast.LENGTH_SHORT).show();
                    return;
                }
                updateMessage("Service related Info", true);
                isConversationInProgress = true;
                // Handle received message
                if (usersDatabaseRef != null) {
                    // Search for the corresponding value in chatdata
                    DatabaseReference chatDataRef = FirebaseDatabase.getInstance().getReference().child("chatdata").child("service");
                    chatDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String serviceInfo = dataSnapshot.getValue(String.class);
                            // Update received message with the retrieved account information
                            updateReceivedMessage(serviceInfo);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle database error
                        }
                    });
                }
            }
        });
        String userEmail = intent.getStringExtra("userEmail");
        if (userEmail != null) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
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
                        // Handle database error
                    }
                });
            }
        } else {
            // Handle the case where userEmail is null
            Toast.makeText(this, "User email is null", Toast.LENGTH_SHORT).show();
            finish(); // You may want to close the Assistant activity in this case
        }
    }

    private void updateGreeting(String userName) {
        String greeting = "Hello, " + userName;
        greetingTextView.setText(greeting);
    }

    private void updateMessage(String message, boolean isSent) {
        // Create a new TextView to display the message
        TextView newMessageTextView = new TextView(this);
        newMessageTextView.setText(message);
        newMessageTextView.setTextColor(Color.WHITE);
        // Set padding to make the background somewhat bigger
        int paddingInDp = 8;  // You can adjust the padding as needed
        float scale = getResources().getDisplayMetrics().density;
        int paddingInPixels = (int) (paddingInDp * scale + 0.5f);
        newMessageTextView.setPadding(paddingInPixels, paddingInPixels, paddingInPixels, paddingInPixels);

        // Set margin to add space between messages
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int marginInDp = 8;  // You can adjust the margin as needed
        int marginInPixels = (int) (marginInDp * scale + 0.5f);
        layoutParams.setMargins(marginInPixels, marginInPixels, marginInPixels, marginInPixels);
        newMessageTextView.setLayoutParams(layoutParams);

        // Add the new TextView to the messagesLinearLayout
        LinearLayout messagesLinearLayout = findViewById(R.id.messagesLinearLayout);
        messagesLinearLayout.addView(newMessageTextView);

        // Optionally, you can set a background dynamically here based on the tag clicked
        newMessageTextView.setBackgroundResource(R.drawable.msg_bg);
        if (isSent) {
            newMessageTextView.setBackgroundResource(R.drawable.message_item);
        } else {
            newMessageTextView.setBackgroundResource(R.drawable.msg_bg);
        }
    }

    private void updateReceivedMessage(String message) {
        // Create a new TextView to display the received message
        TextView newMessageTextView = new TextView(this);
        newMessageTextView.setText(message);
        newMessageTextView.setTextColor(Color.BLACK);

        // Set padding to make the background somewhat bigger
        int paddingInDp = 8;  // You can adjust the padding as needed
        float scale = getResources().getDisplayMetrics().density;
        int paddingInPixels = (int) (paddingInDp * scale + 0.5f);
        newMessageTextView.setPadding(paddingInPixels, paddingInPixels, paddingInPixels, paddingInPixels);

        // Set margin to add space between messages
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
            // Assuming you have a variable to store the selected option/input
            int selectedOption = 1;

            // Check if the selected option/input is 1
            if (selectedOption == 1) {
                // Show the refund field from the database
                showRefundField();
            }
        }
        if (isConversationInProgress && message.equalsIgnoreCase("Payment related information")) {
            // Assuming you have a variable to store the selected option/input
            int selectedOption = 1;

            // Check if the selected option/input is 1
            if (selectedOption == 1) {
                // Show the refund field from the database
                showRefundField();
            }
            if (selectedOption == 2) {
                // Show the refund field from the database
                showPlanField();
            }
            if (selectedOption == 3) {
                // Show the refund field from the database
                showPaymenttcField();
            }
            if (selectedOption == 4) {
                // Show the refund field from the database
                showOtherField();
            }
        }
        if (isConversationInProgress && message.equalsIgnoreCase("Service related information")) {
            // Assuming you have a variable to store the selected option/input
            int selectedOption = 1;

            // Check if the selected option/input is 1
            if (selectedOption == 1) {
                // Show the refund field from the database
                showRefundField();
            }
        }
    }

    private void showRefundField() {
        // Assuming you want to set a specific message for the refund information
        String refundInformation = "Your refund information is here";

        // Create a new TextView to display the refund information
        TextView refundMessageTextView = new TextView(this);
        refundMessageTextView.setText("Refund Information: " + refundInformation);
        refundMessageTextView.setTextColor(Color.BLACK);

        // Set padding to make the background somewhat bigger
        int paddingInDp = 8;  // You can adjust the padding as needed
        float scale = getResources().getDisplayMetrics().density;
        int paddingInPixels = (int) (paddingInDp * scale + 0.5f);
        refundMessageTextView.setPadding(paddingInPixels, paddingInPixels, paddingInPixels, paddingInPixels);

        // Set margin to add space between messages
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int marginInDp = 8;
        int marginInPixels = (int) (marginInDp * scale + 0.5f);
        layoutParams.setMargins(marginInPixels, marginInPixels, marginInPixels, marginInPixels);
        refundMessageTextView.setLayoutParams(layoutParams);

        // Add the refund message TextView to the messagesLinearLayout
        LinearLayout messagesLinearLayout = findViewById(R.id.messagesLinearLayout);
        messagesLinearLayout.addView(refundMessageTextView);

        // Optionally, you can set a background dynamically here based on the tag clicked
        refundMessageTextView.setBackgroundResource(R.drawable.msg_rcvd_bg);

        // Make the input and send button visible
        messageEditText.setVisibility(View.VISIBLE);
        sendButton.setVisibility(View.VISIBLE);
    }


    private void showPlanField() {
        // Here you can add code to retrieve the refund information from the database
        // and display it in your UI. For example, you can update a TextView with the refund information.
        // TextView refundTextView = findViewById(R.id.refundTextView);
        // refundTextView.setText("Refund Information: ...");
    }

    private void showPaymenttcField() {
        // Here you can add code to retrieve the refund information from the database
        // and display it in your UI. For example, you can update a TextView with the refund information.
        // TextView refundTextView = findViewById(R.id.refundTextView);
        // refundTextView.setText("Refund Information: ...");
    }

    private void showOtherField() {
        // Here you can add code to retrieve the refund information from the database
        // and display it in your UI. For example, you can update a TextView with the refund information.
        // TextView refundTextView = findViewById(R.id.refundTextView);
        // refundTextView.setText("Refund Information: ...");
    }
}
