package com.mabrouk.medicalconferences.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class CancelChangesDialogFragment extends DialogFragment {
    private static final String ARG_MESSAGE = "message";
    private OnCancelChangesListener listener;

    public static CancelChangesDialogFragment createInstance(String message) {
        CancelChangesDialogFragment fragment = new CancelChangesDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onCancelChangesConfirmed(which == DialogInterface.BUTTON_POSITIVE);
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Warning");
        builder.setMessage(getArguments().getString(ARG_MESSAGE));
        builder.setNegativeButton("No", onClickListener);
        builder.setPositiveButton("Yes", onClickListener);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCancelChangesListener) {
            listener = (OnCancelChangesListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCancelChangesListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnCancelChangesListener {
        void onCancelChangesConfirmed(boolean confirmed);
    }
}
