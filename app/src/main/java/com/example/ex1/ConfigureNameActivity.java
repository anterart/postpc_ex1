package com.example.ex1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class ConfigureNameActivity extends AppCompatActivity {

    Button skip;
    Button sendMyName;
    EditText fillMyName;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bundle = savedInstanceState;
        AsyncUsernameLoad asyncUsernameLoad = new AsyncUsernameLoad();
        asyncUsernameLoad.setParameters(getApplicationContext(), this);
        asyncUsernameLoad.execute();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_name);
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
                sendUsernameToDB();
                loadMainActivity(fillMyName.getText().toString());
            }
        });
    }

    private void sendUsernameToDB(){
        Map<String, Object> userName = new HashMap<>();
        userName.put("username", fillMyName.getText().toString());
        DatabaseClient.getFirestoreClient().collection("defaults").document("user").set(userName);
    }

    public void loadMainActivity(String username){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("username", username);
        finish();
        startActivity(intent);
    }



}

class AsyncUsernameLoad extends AsyncTask<Void, Void, Void>
{
    private String username;
    private WeakReference<Context> context;
    private WeakReference<ConfigureNameActivity> configureNameActivity;
    public void setParameters(Context context, ConfigureNameActivity configureNameActivity){
        this.context = new WeakReference<>(context);
        this.configureNameActivity = new WeakReference<>(configureNameActivity);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        SharedPreferences sp =
                PreferenceManager.getDefaultSharedPreferences(context.get());
        username = sp.getString("username", null);
        if(username == null)
        {
            DocumentReference usernameRef = DatabaseClient.getFirestoreClient()
                    .collection("defaults").document("user");
            usernameRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.get("username") != null){
                        username = documentSnapshot.getString("username");
                        configureNameActivity.get().loadMainActivity(username);
                    }
                    else{
                        configureNameActivity.get().loadConfigureNameActivity();
                    }
                }
            });
        }
        else{
            configureNameActivity.get().loadMainActivity(username);
        }
        return null;
    }
}
