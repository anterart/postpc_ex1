package com.example.ex1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> Items = new ArrayList<>();
    MessageAdapter adapter;
    EditText sendMessageInput;
    RecyclerView sentMessages;
    LinearLayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button sendButton = findViewById(R.id.sendButton);
        sendMessageInput = findViewById(R.id.sendMessageEditText);
        sentMessages = findViewById(R.id.sentMessages);

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String newMessage = sendMessageInput.getText().toString();
                if (newMessage.length() == 0)
                {
                    Toast toast = Toast.makeText(getApplicationContext(), "LOL! BAD BOY!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else
                {
                    Items.add(newMessage);
                    adapter.notifyItemInserted(adapter.getItemCount());
                    sendMessageInput.setText("");
                }
            }
        });

        if (savedInstanceState != null)
        {
            sendMessageInput.setText(savedInstanceState.getString("sendMessageInput"));
            Items = savedInstanceState.getStringArrayList("Items");
        }
        adapter = new MessageAdapter(this, Items);
        layoutManager = new LinearLayoutManager(this);
        sentMessages.setLayoutManager(layoutManager);
        sentMessages.setAdapter(adapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {

        super.onSaveInstanceState(outState);
        outState.putString("sendMessageInput", sendMessageInput.getText().toString());
        outState.putStringArrayList("Items", Items);
    }
}
