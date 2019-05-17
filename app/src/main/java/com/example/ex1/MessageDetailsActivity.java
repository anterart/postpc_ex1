package com.example.ex1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MessageDetailsActivity extends AppCompatActivity {


    String messageText;
    String messageDeviceId;
    String messageTimestamp;
    int messageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);
        Intent intentCreatedMe = getIntent();
        messageId = intentCreatedMe.getIntExtra("message_id", -1);
        messageText = intentCreatedMe.getStringExtra("message_text");
        messageDeviceId = intentCreatedMe.getStringExtra("message_device_id");
        messageTimestamp = intentCreatedMe.getStringExtra("message_timestamp");
        TextView messageTextTextView = findViewById(R.id.messageText);
        TextView messageTimestampTextView = findViewById(R.id.messageTimestamp);
        TextView messageDeviceIdTextView = findViewById(R.id.messageDeviceID);
        Button deleteButton = findViewById(R.id.deleteMessage);
        messageText = "message content: " + messageText;
        messageDeviceId = "message sent from device: " + messageDeviceId;
        messageTimestamp = "message sent at: " + messageTimestamp;
        messageTextTextView.setText(messageText);
        messageDeviceIdTextView.setText(messageDeviceId);
        messageTimestampTextView.setText(messageTimestamp);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentBack = new Intent(MessageDetailsActivity.this, MainActivity.class);
                intentBack.putExtra("message_id", messageId);
                intentBack.putExtra("message_text", messageText);
                intentBack.putExtra("message_device_id", messageDeviceId);
                intentBack.putExtra("message_timestamp", messageTimestamp);
                setResult(RESULT_OK, intentBack);
                finish();
            }
        });
    }
}
