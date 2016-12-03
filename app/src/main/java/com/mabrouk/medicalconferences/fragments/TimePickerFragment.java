package com.mabrouk.medicalconferences.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.mabrouk.medicalconferences.R;

public class TimePickerFragment extends DialogFragment {
    private static final String ARG_HOUR = "hour";
    private static final String ARG_MINUTE = "minute";

    private OnTimeSelectedListener listener;

    public static TimePickerFragment newInstance(int hour, int minute) {
        TimePickerFragment fragment = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_HOUR, hour);
        args.putInt(ARG_MINUTE, minute);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                listener.onTimeSelected(hourOfDay, minute);
            }
        };
        TimePickerDialog dialog = new TimePickerDialog(getActivity(), onTimeSetListener,
                args.getInt(ARG_HOUR), args.getInt(ARG_MINUTE), true);
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTimeSelectedListener) {
            listener = (OnTimeSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnTimeSelectedListener {
        void onTimeSelected(int hour, int minute);
    }
}
