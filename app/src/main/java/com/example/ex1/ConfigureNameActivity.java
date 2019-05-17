package com.example.ex1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

public class ConfigureNameActivity extends AppCompatActivity {

    Button skip;
    Button sendMyName;
    EditText fillMyName;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_name);
        bundle = savedInstanceState;
        loadConfigureNameActivity();
    }

    public void loadConfigureNameActivity(){
        getViews();
        getSaveInstanceState(bundle);
        setListeners();
    }

    private void getViews(){
        skip = findViewById(R.id.skip);
        sendMyName = findViewById(R.id.sendMyName);
        fillMyName = findViewById(R.id.fillMyName);
    }

    private void getSaveInstanceState(Bundle bundle){
        if(bundle != null){
            fillMyName.setText(bundle.getString("fillMyName"));
        }
    }

    private void setListeners(){
        sendMyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUsernameToSP();
                sendUsernameToDB();
                loadMainActivity(fillMyName.getText().toString());
            }
        });
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMainActivity(null);
            }
        });
    }

    private void sendUsernameToDB(){
        Map<String, Object> userName = new HashMap<>();
        userName.put("username", fillMyName.getText().toString());
        DatabaseClient.getFirestoreClient().collection("defaults").document("user").set(userName);
    }

    private void saveUsernameToSP(){
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", fillMyName.getText().toString());
        editor.apply();
    }

    public void loadMainActivity(String username){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("username", username);
        finish();
        startActivity(intent);
    }



}


