package com.example.ex1;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements MessageRecyclerUtils.MessageClickCallback
{
    ArrayList<String> Items;
    MessageRecyclerUtils.MessageAdapter adapter;
    EditText sendMessageInput;
    RecyclerView sentMessages;
    LinearLayoutManager layoutManager;
    Button sendButton;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadMainActivity(savedInstanceState);

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                sendButtonOnClick();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString("sendMessageInput", sendMessageInput.getText().toString());
        outState.putStringArrayList("Items", Items);
    }

    @Override
    public void longMessageClick(int position, boolean sure)
    {
        if (sure)
        {
            ArrayList<String> messagesCopy = new ArrayList<>(this.Items);
            messagesCopy.remove(position);
            this.Items = messagesCopy;
            this.adapter.submitList(this.Items);
            saveDataToSharedPreferences();
        }
    }

    protected void loadDataFromSharedPreferences()
    {
        if (Items == null)
        {
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            Gson gson = new Gson();
            String json = sharedPreferences.getString("messages", null);
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            Items = gson.fromJson(json, type);
            if (Items == null)
            {
                Items = new ArrayList<>();
            }
        }
    }

    protected void saveDataToSharedPreferences()
    {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(Items);
        editor.putString("messages", json);
        editor.apply();
    }

    protected void log()
    {
        Log.i("Messages array size", String.valueOf(Items.size()));
    }

    protected void getViews()
    {
        sendButton = findViewById(R.id.sendButton);
        sendMessageInput = findViewById(R.id.sendMessageEditText);
        sentMessages = findViewById(R.id.sentMessages);
    }

    protected void getSavedInstanceState(Bundle savedInstanceState)
    {
        if (savedInstanceState != null)
        {
            sendMessageInput.setText(savedInstanceState.getString("sendMessageInput"));
            Items = savedInstanceState.getStringArrayList("Items");
        }
    }

    protected void setMessagesRecyclerView()
    {
        adapter = new MessageRecyclerUtils.MessageAdapter();
        sentMessages.setAdapter(adapter);
        adapter.callback = this;
        adapter.submitList(Items);
        layoutManager = new LinearLayoutManager(this);
        sentMessages.setLayoutManager(layoutManager);
    }

    protected void sendButtonOnClick()
    {
        String newMessage = sendMessageInput.getText().toString();
        if (newMessage.length() == 0)
        {
            sendEmptyMessage();
        }
        else
        {
            sendMessage(newMessage);
        }
    }

    protected void sendEmptyMessage()
    {
        Toast toast = Toast.makeText(getApplicationContext(),
                "You can't send blank message!", Toast.LENGTH_SHORT);
        toast.show();
    }

    protected void sendMessage(String newMessage)
    {
        Items.add(newMessage);
        adapter.notifyItemInserted(adapter.getItemCount());
        sendMessageInput.setText("");
        saveDataToSharedPreferences();
    }

    protected void loadMainActivity(Bundle savedInstanceState)
    {
        loadDataFromSharedPreferences();
        log();
        getViews();
        getSavedInstanceState(savedInstanceState);
        setMessagesRecyclerView();
    }
}
