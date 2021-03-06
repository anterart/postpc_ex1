package com.example.ex1;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class State
{
    private  List<Message> messages;
    private  int next_message_id;
    private static State state;
    public static DatabaseStateRefreshable callback;

    private State()
    {
        messages = new ArrayList<>();
        next_message_id = 0;
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
        callback.setWaitingForDataBaseStatus(true);
        String messageId = String.valueOf(message.get_id());
        DatabaseClient.getFirestoreClient().collection("messages").document(messageId).set(message)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                callback.setWaitingForDataBaseStatus(false);
            }
        });
    }

    private void deleteMessageFromRemoteDataBae(final int message_id)
    {
        callback.setWaitingForDataBaseStatus(true);
        String messageId = String.valueOf(message_id);
        DatabaseClient.getFirestoreClient().collection("messages").document(messageId).delete()
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                callback.setWaitingForDataBaseStatus(false);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void add_message(String messageText, String deviceID)
    {
        Message message = new Message(next_message_id, messageText, deviceID);
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

    private class AsyncFireBaseRead extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {
            CollectionReference messagesRef = DatabaseClient.getFirestoreClient().collection("messages");
            messagesRef.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                        {
                            List<Message> remoteMessages = new ArrayList<>();
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                            {
                                remoteMessages.add(getMessage(documentSnapshot));
                            }
                            if (!messages.equals(remoteMessages))
                            {
                                messages = new ArrayList<>(remoteMessages);
                                if (messages.size() == 0)
                                {
                                    next_message_id = 0;
                                }
                                else
                                {
                                    next_message_id = Collections.max(messages).get_id() + 1;
                                }
                                if (callback.getState() == null)
                                {
                                    callback.setState(state);
                                }
                                callback.setMessagesRecyclerView();
                            }
                            callback.log();
                            callback.setWaitingForDataBaseStatus(false);
                        }
                    });
            return null;
        }
    }

    public void loadMessagesListFromRemoteDatabase()
    {
        callback.setWaitingForDataBaseStatus(true);
        new AsyncFireBaseRead().execute();
    }

    private Message getMessage(QueryDocumentSnapshot documentSnapshot)
    {
        int id = Objects.requireNonNull(documentSnapshot.getLong("_id")).intValue();
        String messageText = documentSnapshot.getString("_message");
        String local_timestamp = documentSnapshot.getString("_local_timestamp");
        String device_ID = documentSnapshot.getString("_deviceID");
        return new Message(id, messageText, device_ID, local_timestamp);
    }

    public interface DatabaseStateRefreshable
    {
        public void log();

        public void setMessagesRecyclerView();

        public void setWaitingForDataBaseStatus(boolean status);

        public State getState();

        public void setState(State state);
    }
}
