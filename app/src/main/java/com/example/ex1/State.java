package com.example.ex1;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class State
{
    private List<Message> messages;
    private int next_message_id;
    private static State current_state;

    private State()
    {
        messages = new ArrayList<>();
        next_message_id = 0;
    }

    public static State getInstance()
    {
        if (current_state == null)
        {
            current_state = new State();
        }
        return current_state;
    }

    public int get_num_of_messages()
    {
        return messages.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void add_message(String messageText)
    {
        Message message = new Message(next_message_id, messageText);
        messages.add(message);
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
    }
}
