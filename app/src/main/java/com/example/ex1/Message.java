package com.example.ex1;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message implements Comparable<Message>
{
    private final int id;
    private final String message;
    private final String local_timestamp;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Message(int id, String message)
    {
        this.id = id;
        this.message = message;
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.local_timestamp = now.format(formatter);
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
