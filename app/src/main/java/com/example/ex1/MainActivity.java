package com.example.ex1;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements MessageRecyclerUtils.MessageClickCallback, State.DatabaseStateRefreshable {
    State state;
    boolean waitingForDatabase = false;
    MessageRecyclerUtils.MessageAdapter adapter;
    EditText sendMessageInput;
    RecyclerView sentMessages;
    LinearLayoutManager layoutManager;
    Button sendButton;
    TextView username;
    String deviceID;
    private int PHONE_STATE_PERMISSION = 7;
    Bundle savedInstanceState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadMainActivity(savedInstanceState);
    }

    protected void onResume() {
        super.onResume();
        Popups.DeleteMessageDialogFragment mDialogFragment =
                (Popups.DeleteMessageDialogFragment) getSupportFragmentManager()
                        .findFragmentByTag("deleteMessagePopup");
        if (mDialogFragment != null) {
            mDialogFragment.callback = this;
        }
        State.callback = this;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("sendMessageInput", sendMessageInput.getText().toString());
        outState.putBoolean("waitingForDatabase", waitingForDatabase);
        Gson gson = new Gson();
        String jsonState = gson.toJson(state);
        outState.putString("state", jsonState);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void longMessageClickPositive(Message longClickedMessage) {
        state.removeMessageById(longClickedMessage.get_id());
        List<Message> messages = state.get_all_messages();
        this.adapter.submitList(messages);
    }

    @Override
    public void longMessageClickNegative(Message longClickedMessage) {

    }

    private class AsyncDataLoad extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            Gson gson = new Gson();
            String jsonState = sharedPreferences.getString("state", null);
            Type type = new TypeToken<State>() {
            }.getType();
            state = gson.fromJson(jsonState, type);
            if (state != null) {
                state.loadMessagesListFromRemoteDatabase();
            }
            return null;
        }
    }

    protected void loadData() {
        if (state == null) {
            new AsyncDataLoad().execute();
            if (state == null) {
                state = State.getInstance();
                State.callback = MainActivity.this;
            }
        }
    }

    protected void saveDataToSharedPreferences() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(state);
        editor.putString("state", json);
        editor.apply();
    }

    public void log() {
        Log.i("Messages array size", String.valueOf(state.get_num_of_messages()));
    }

    protected void getViews() {
        sendButton = findViewById(R.id.sendButton);
        sendMessageInput = findViewById(R.id.sendMessageEditText);
        sentMessages = findViewById(R.id.sentMessages);
        username = findViewById(R.id.username);
    }

    protected boolean getSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            waitingForDatabase = savedInstanceState.getBoolean("waitingForDatabase");
            sendMessageInput.setText(savedInstanceState.getString("sendMessageInput"));
            if (!waitingForDatabase) {
                String jsonState = savedInstanceState.getString("state");
                Gson gson = new Gson();
                Type type = new TypeToken<State>() {
                }.getType();
                state = gson.fromJson(jsonState, type);
                return true;
            }
        }
        return false;
    }

    public void setMessagesRecyclerView() {
        adapter = new MessageRecyclerUtils.MessageAdapter();
        sentMessages.setAdapter(adapter);
        adapter.callback = this;
        adapter.submitList(state.get_all_messages());
        layoutManager = new LinearLayoutManager(this);
        sentMessages.setLayoutManager(layoutManager);
    }

    @Override
    public void setWaitingForDataBaseStatus(boolean status) {
        waitingForDatabase = status;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setState(State state) {
        this.state = state;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void sendButtonOnClick() {
        String newMessageText = sendMessageInput.getText().toString();
        if (newMessageText.length() == 0) {
            sendEmptyMessage();
        } else {
            sendMessage(newMessageText);
        }
    }

    protected void sendEmptyMessage() {
        Toast toast = Toast.makeText(getApplicationContext(),
                "You can't send blank message!", Toast.LENGTH_SHORT);
        toast.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void sendMessage(String newMessage) {
        state.add_message(newMessage, deviceID);
        adapter.notifyItemInserted(adapter.getItemCount());
        sendMessageInput.setText("");
        saveDataToSharedPreferences();
    }

    private void loadUserName() {
        Intent intentCreatedMe = getIntent();
        String usernameText = intentCreatedMe.getStringExtra("username");
        if (usernameText != null) {
            usernameText = "Hello " + usernameText + "!";
            username.setText(usernameText);
        }
    }

    protected void loadMainActivity(Bundle savedInstanceState) {
        State.callback = this;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
                new AlertDialog.Builder(this)
                        .setTitle("Permission needed")
                        .setMessage("This permission is needed by Reem Halamish to this and that!")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, PHONE_STATE_PERMISSION);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, PHONE_STATE_PERMISSION);
            }
        } else {
            deviceID = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
            continueLoadingMainActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PHONE_STATE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                }
                deviceID = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                continueLoadingMainActivity();
            }
        }
    }

    private void continueLoadingMainActivity(){
        getViews();
        loadUserName();
        if (!getSavedInstanceState(savedInstanceState))
        {
            if(!waitingForDatabase)
            {
                loadData();
            }
        }
        setMessagesRecyclerView();
        sendButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {
                sendButtonOnClick();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 777){
            if (resultCode == RESULT_OK){
                int messageId = data.getIntExtra("message_id", -1);
                String messageText = data.getStringExtra("message_text");
                String messageDeviceId = data.getStringExtra("message_device_id");
                String messageTimestamp = data.getStringExtra("message_timestamp");
                Message message = new Message(messageId, messageText, messageDeviceId, messageTimestamp);
                longMessageClickPositive(message);
            }
        }
    }
}
