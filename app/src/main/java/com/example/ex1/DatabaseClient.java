package com.example.ex1;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;
import java.io.IOException;

 public class DatabaseClient
{
    private static Firestore firestoreClient;

    private static void createFirestoreInstance()
    {
        FileInputStream serviceAccount = null;
        try {
            serviceAccount = new FileInputStream
                    ("C:\\Users\\my-da\\Desktop\\uni\\4\\PostPC\\targilim\\postpc_ex1\\self-chat-7f2cc-firebase-adminsdk-3lyem-3c7994ebcf.json");
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://self-chat-7f2cc.firebaseio.com")
                    .build();

            FirebaseApp.initializeApp(options);
            FirebaseAuth defaultAuth = FirebaseAuth.getInstance();
            FirebaseDatabase defaultDatabase = FirebaseDatabase.getInstance();
            firestoreClient = FirestoreClient.getFirestore();

        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public static Firestore getFirestoreClient()
    {
        if (firestoreClient == null)
        {
            createFirestoreInstance();
        }
        return firestoreClient;
    }
}
