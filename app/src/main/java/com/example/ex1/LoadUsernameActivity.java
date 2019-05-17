package com.example.ex1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.lang.ref.WeakReference;

public class LoadUsernameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_username);
        AsyncUsernameLoad asyncUsernameLoad = new AsyncUsernameLoad();
        asyncUsernameLoad.setParameters(getApplicationContext(), this);
        asyncUsernameLoad.execute();
    }

    public void loadMainActivity(String username){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("username", username);
        finish();
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    public void loadConfigureNameActivity(){
        Intent intent = new Intent(this, ConfigureNameActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);
        overridePendingTransition(0,0);
    }
}

class AsyncUsernameLoad extends AsyncTask<Void, Void, Void>
{
    private String username;
    private WeakReference<Context> context;
    private WeakReference<LoadUsernameActivity> loadUsernameActivityWeakReference;
    public void setParameters(Context context, LoadUsernameActivity loadUsernameActivity){
        this.context = new WeakReference<>(context);
        this.loadUsernameActivityWeakReference = new WeakReference<>(loadUsernameActivity);
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
                        loadUsernameActivityWeakReference.get().loadMainActivity(username);
                    }
                    else{
                        loadUsernameActivityWeakReference.get().loadConfigureNameActivity();
                    }
                }
            });
        }
        else{
            loadUsernameActivityWeakReference.get().loadMainActivity(username);
        }
        return null;
    }
}
