package com.example.ex1;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class State
{
    private List<Message> messages;
    private int next_message_id;
    private static State state;
    private Firestore firestoreClient;

    private State()
    {
        messages = new ArrayList<>();
        next_message_id = 0;
        firestoreClient = DatabaseClient.getFirestoreClient();
    }

    public static State getInstance()
    {
        if (state == null)
        {
            state = new State();
        }
        return state;
    }

    public int get_num_of_messages()
    {
        return messages.size();
    }

    private void addMessageToRemoteDatabase(Message message)
    {
        String messageId = String.valueOf(message.get_id());
        DocumentReference docRef = firestoreClient.collection("messages").document();
        docRef.set(message);
    }

    private void deleteMessageFromRemoteDataBae(final int message_id)
    {
        String messageId = String.valueOf(message_id);
        firestoreClient.collection("messages").document(messageId).delete();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void add_message(String messageText)
    {
        Message message = new Message(next_message_id, messageText);
        messages.add(message);
        addMessageToRemoteDatabase(message);
        next_message_id++;
    }

    public List<Message> get_all_messages()
    {
        return messages;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void removeMessageById(final int message_id)
    {
        messages = messages.stream().filter(message -> message.get_id() != message_id).
                collect(Collectors.toList());
        deleteMessageFromRemoteDataBae(message_id);
    }

    public void loadMessagesListFromRemoteDatabase()
    {
        try {
            ApiFuture<QuerySnapshot> future = firestoreClient.collection("messages").get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            List<Message> remoteMessages = new ArrayList<>();
            for (QueryDocumentSnapshot document: documents)
            {
                remoteMessages.add(document.toObject(Message.class));
            }
            messages = new ArrayList<>(remoteMessages);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
