package com.example.ex1;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MessageRecyclerUtils
{
    interface MessageClickCallback
    {
        void longMessageClickPositive(Message longClickedMessage);

        void longMessageClickNegative(Message longClickedMessage);
    }

    static class MessageAdapter extends ListAdapter<Message, MessageHolder>
    {

        public MessageAdapter()
        {
            super(new MessageCallback());
        }

        public MessageClickCallback callback;

        @NonNull
        @Override
        public MessageHolder onCreateViewHolder(@NonNull final ViewGroup parent, int itemType)
        {
            final Context context = parent.getContext();
            View itemView = LayoutInflater.from(context)
                    .inflate(R.layout.message, parent, false);
            final MessageHolder holder = new MessageHolder(itemView);
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v)
                {
//                    Popups.DeleteMessageDialogFragment deleteMessagePopup = new
//                            Popups.DeleteMessageDialogFragment();
//                    deleteMessagePopup.setRetainInstance(true);
//                    deleteMessagePopup.longClickedMessage = getItem(holder.getAdapterPosition());
//                    deleteMessagePopup.callback = callback;
//                    FragmentManager fm = ((AppCompatActivity)context).getSupportFragmentManager();
//                    deleteMessagePopup.show(fm, "deleteMessagePopup");

                    Intent intent = new Intent((MainActivity)callback, MessageDetailsActivity.class);
                    Message message = getItem(holder.getAdapterPosition());
                    intent.putExtra("message_id", message.get_id());
                    intent.putExtra("message_text", message.get_message());
                    intent.putExtra("message_device_id", message.get_deviceID());
                    intent.putExtra("message_timestamp", message.get_local_timestamp());
                    ((MainActivity) callback).startActivityForResult(intent, 777);
                    return true;
                }
            });
            {

            }
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MessageHolder messageHolder, int position)
        {
            Message message = getItem(position);
            messageHolder.text.setText(message.get_message());
        }
    }

    static class MessageHolder extends RecyclerView.ViewHolder
    {
        public final TextView text;
        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.message);
        }
    }

    static class MessageCallback extends DiffUtil.ItemCallback<Message>
    {
        @Override
        public boolean areItemsTheSame(@NonNull Message m1, @NonNull Message m2) {
            return m1.get_id() == m2.get_id();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Message m1, @NonNull Message m2) {
            return m1.get_message().equals(m2.get_message());
        }
    }


}
