package com.example.ex1;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message implements Comparable<Message>
{
    private int id;
    private String message;
    private String local_timestamp;

    public Message(){}

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Message(int id, String message)
    {
        this.id = id;
        this.message = message;
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.local_timestamp = now.format(formatter);
    }

    public Message(int id, String message, String local_timestamp)
    {
        this.id = id;
        this.message = message;
        this.local_timestamp = local_timestamp;
    }

    public int get_id()
    {
        return this.id;
    }

    public String get_message()
    {
        return this.message;
    }

    public String get_local_timestamp()
    {
        return this.local_timestamp;
    }

    @Override
    public int compareTo(Message o)
    {
        return Integer.compare(this.id, o.id);
    }
}
