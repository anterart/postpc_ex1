package com.example.ex1;

import com.google.firebase.firestore.FirebaseFirestore;


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
