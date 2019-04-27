package com.example.ex1;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
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
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements MessageRecyclerUtils.MessageClickCallback
{
    State state;
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
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v)
            {
                sendButtonOnClick();
            }
        });
    }

    protected void onResume() {
        super.onResume();
        Popups.DeleteMessageDialogFragment mDialogFragment =
                (Popups.DeleteMessageDialogFragment)getSupportFragmentManager()
                        .findFragmentByTag("deleteMessagePopup");
        if(mDialogFragment  != null){
            mDialogFragment.callback = this;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString("sendMessageInput", sendMessageInput.getText().toString());
        Gson gson = new Gson();
        String jsonState = gson.toJson(state);
        outState.putString("state", jsonState);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void longMessageClickPositive(Message longClickedMessage)
    {
        state.removeMessageById(longClickedMessage.get_id());
        List<Message> messages = state.get_all_messages();
        this.adapter.submitList(messages);
    }

    @Override
    public void longMessageClickNegative(Message longClickedMessage)
    {

    }

    protected boolean loadDataFromSharedPreferences()
    {
        if (state == null)
        {
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            Gson gson = new Gson();
            String jsonState = sharedPreferences.getString("state", null);
            Type type = new TypeToken<State>() {}.getType();
            state = gson.fromJson(jsonState, type);
            if (state == null)
            {
                state = State.getInstance();
            }
            return true;
        }
        return false;
    }

    protected void saveDataToSharedPreferences()
    {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(state);
        editor.putString("state", json);
        editor.apply();
    }

    protected void log()
    {
        Log.i("Messages array size", String.valueOf(state.get_num_of_messages()));
    }

    protected void getViews()
    {
        sendButton = findViewById(R.id.sendButton);
        sendMessageInput = findViewById(R.id.sendMessageEditText);
        sentMessages = findViewById(R.id.sentMessages);
    }

    protected boolean getSavedInstanceState(Bundle savedInstanceState)
    {
        if (savedInstanceState != null)
        {
            sendMessageInput.setText(savedInstanceState.getString("sendMessageInput"));
            String jsonState = savedInstanceState.getString("state");
            Gson gson = new Gson();
            Type type = new TypeToken<State>() {}.getType();
            state = gson.fromJson(jsonState, type);
            return true;
        }
        return false;
    }

    protected void setMessagesRecyclerView()
    {
        adapter = new MessageRecyclerUtils.MessageAdapter();
        sentMessages.setAdapter(adapter);
        adapter.callback = this;
        adapter.submitList(state.get_all_messages());
        layoutManager = new LinearLayoutManager(this);
        sentMessages.setLayoutManager(layoutManager);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void sendButtonOnClick()
    {
        String newMessageText = sendMessageInput.getText().toString();
        if (newMessageText.length() == 0)
        {
            sendEmptyMessage();
        }
        else
        {
            sendMessage(newMessageText);
        }
    }

    protected void sendEmptyMessage()
    {
        Toast toast = Toast.makeText(getApplicationContext(),
                "You can't send blank message!", Toast.LENGTH_SHORT);
        toast.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void sendMessage(String newMessage)
    {
        state.add_message(newMessage);
        adapter.notifyItemInserted(adapter.getItemCount());
        sendMessageInput.setText("");
        saveDataToSharedPreferences();
    }

    protected void loadMainActivity(Bundle savedInstanceState)
    {
        getViews();
        if (!getSavedInstanceState(savedInstanceState))
        {
            loadDataFromSharedPreferences();
            state.loadMessagesListFromRemoteDatabase(this);
        }
        else
        {
            log();
            setMessagesRecyclerView();
        }

    }
}
