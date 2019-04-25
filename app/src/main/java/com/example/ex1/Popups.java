package com.example.ex1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class Popups
{
    static public class DeleteMessageDialogFragment extends DialogFragment {
        public int adapterPosition;
        public MessageRecyclerUtils.MessageClickCallback callback;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Are you sure you want to delete this message?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (callback != null)
                            {
                                callback.longMessageClick(adapterPosition, true);
                            }
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (callback != null)
                            {
                                callback.longMessageClick(adapterPosition, false);
                            }
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }

        public void updateListener(MessageRecyclerUtils.MessageClickCallback callback)
        {
            this.callback = callback;
        }
    }
}
