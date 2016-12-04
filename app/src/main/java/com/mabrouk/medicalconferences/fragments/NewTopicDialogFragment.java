package com.mabrouk.medicalconferences.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.Toast;

import com.mabrouk.medicalconferences.R;

public class NewTopicDialogFragment extends DialogFragment {
    private OnTopicAddedListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DialogInterface.OnClickListener actionListener = (dialog, which) -> {
            if(which == Dialog.BUTTON_NEGATIVE) {
                return;
            }

            EditText input = (EditText) ((AlertDialog) dialog).findViewById(R.id.topic_input);
            String topic = input.getText().toString();
            if(topic.isEmpty())
                Toast.makeText(getContext(), "Topic is empty, nothing added", Toast.LENGTH_SHORT).show();
            else
                listener.onTopicAdded(topic);
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Suggest new topic");
        builder.setView(R.layout.dialog_new_topic);
        builder.setNegativeButton("Cancel", actionListener);
        builder.setPositiveButton("Suggest", actionListener);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTopicAddedListener) {
            listener = (OnTopicAddedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTopicAddedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnTopicAddedListener {

        void onTopicAdded(String topic);
    }
}
