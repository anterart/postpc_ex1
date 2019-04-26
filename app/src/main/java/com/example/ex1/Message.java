package com.example.ex1;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.time.LocalDateTime;

public class Message
{
    private final int id;
    private final String message;
    private final LocalDateTime local_timestamp;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Message(int id, String message)
    {
        this.id = id;
        this.message = message;
        this.local_timestamp = LocalDateTime.now();
    }

    public int get_id()
    {
        return this.id;
    }

    public String get_message()
    {
        return this.message;
    }

    public LocalDateTime get_local_timestamp()
    {
        return this.local_timestamp;
    }
}
