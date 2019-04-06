package com.example.ex1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button sendButton = findViewById(R.id.sendButton);
        final EditText sendMessageEditText = findViewById(R.id.sendMessageEditText);
        final TextView sentMessagesTextView = findViewById(R.id.sentMessagesTextView);

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String newMessage = sendMessageEditText.getText() + "\n";
                String allMessages = sentMessagesTextView.getText() + newMessage;
                sentMessagesTextView.setText(allMessages);
                sendMessageEditText.setText("");
            }
        });

        if (savedInstanceState != null)
        {
            sendMessageEditText.setText(savedInstanceState.getString("currentMessage"));
            sentMessagesTextView.setText(savedInstanceState.getString("sentMessages"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {

        super.onSaveInstanceState(outState);
        final EditText sendMessageEditText = findViewById(R.id.sendMessageEditText);
        final TextView sentMessagesTextView = findViewById(R.id.sentMessagesTextView);
        outState.putString("sentMessages", sentMessagesTextView.getText().toString());
        outState.putString("currentMessage", sendMessageEditText.getText().toString());
    }
}
