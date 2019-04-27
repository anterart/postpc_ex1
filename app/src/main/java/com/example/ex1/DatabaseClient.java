package com.example.ex1;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileInputStream;
import java.io.IOException;

 public class DatabaseClient
{
    private static FirebaseFirestore firestoreClient;

    private static void createFirestoreInstance()
    {
        firestoreClient = FirebaseFirestore.getInstance();
    }

    public static FirebaseFirestore getFirestoreClient()
    {
        if (firestoreClient == null)
        {
            createFirestoreInstance();
        }
        return firestoreClient;
    }
}
