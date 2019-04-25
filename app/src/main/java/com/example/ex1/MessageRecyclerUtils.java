package com.example.ex1;

import android.content.Context;
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
        void longMessageClick(int position, boolean sure);
    }

    static class MessageAdapter extends ListAdapter<String, MessageHolder>
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
                public boolean onLongClick(View v) {
                    Popups.DeleteMessageDialogFragment deleteMessagePopup = new
                            Popups.DeleteMessageDialogFragment();
                    deleteMessagePopup.adapterPosition = holder.getAdapterPosition();
                    deleteMessagePopup.callback = callback;

                    FragmentManager fm = ((AppCompatActivity)context).getSupportFragmentManager();
                    deleteMessagePopup.show(fm, "deleteMessagePopup");
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
            String t = getItem(position);
            messageHolder.text.setText(t);
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

    static class MessageCallback extends DiffUtil.ItemCallback<String>
    {
        @Override
        public boolean areItemsTheSame(@NonNull String s1, @NonNull String s2) {
            return s1.equals(s2);
        }

        @Override
        public boolean areContentsTheSame(@NonNull String s1, @NonNull String s2) {
            return s1.equals(s2);
        }
    }


}
