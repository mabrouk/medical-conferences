package com.mabrouk.medicalconferences.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Date;

/**
 * Created by User on 12/2/2016.
 */

public class DatePickerFragment extends DialogFragment {
    private static final String ARG_DAY = "day";
    private static final String ARG_YEAR = "year";
    private static final String ARG_MONTH = "month";

    public static DatePickerFragment createInstance(int day, int month, int year) {
        DatePickerFragment fragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DAY, day);
        args.putInt(ARG_MONTH, month);
        args.putInt(ARG_YEAR, year);
        fragment.setArguments(args);
        return fragment;
    }

    OnDateSelectedListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (OnDateSelectedListener) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                listener.onDateSelected(year, month, dayOfMonth);
            }
        };

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), onDateSetListener, args.getInt(ARG_YEAR),
                args.getInt(ARG_MONTH), args.getInt(ARG_DAY));
        dialog.getDatePicker().setMinDate(new Date().getTime());


        return dialog;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    public interface OnDateSelectedListener {
        void onDateSelected(int year, int month, int day);
    }
}
