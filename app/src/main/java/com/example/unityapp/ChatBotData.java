package com.example.unityapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatBotData extends AppCompatActivity {

    private EditText questionEditText;
    private EditText answerEditText;
    private Button submitButton;
    private DatabaseReference chatDataReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot_data);

        questionEditText = findViewById(R.id.questionEditText);
        answerEditText = findViewById(R.id.answerEditText);
        submitButton = findViewById(R.id.submitButton);

        chatDataReference = FirebaseDatabase.getInstance().getReference().child("chatdata");

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitData();
            }
        });
    }

    private void submitData() {
        String question = questionEditText.getText().toString().trim();
        String answer = answerEditText.getText().toString().trim();

        if (question.isEmpty() || answer.isEmpty()) {
            Helper.showToast(ChatBotData.this,"Please enter both question and answer");
            return;
        }

        DatabaseReference newChatDataRef = chatDataReference.child(question);
        newChatDataRef.setValue(answer, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@NonNull DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null) {
                    Helper.showToast(ChatBotData.this,"Data stored successfully");
                    questionEditText.setText("");
                    answerEditText.setText("");
                } else {
                    Helper.showToast( ChatBotData.this,"Failed to store data");
                }
            }
        });
    }
}
