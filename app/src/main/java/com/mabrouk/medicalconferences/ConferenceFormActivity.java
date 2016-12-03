package com.mabrouk.medicalconferences;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mabrouk.medicalconferences.Util.DateUtils;
import com.mabrouk.medicalconferences.fragments.CancelChangesDialogFragment;
import com.mabrouk.medicalconferences.fragments.DatePickerFragment;
import com.mabrouk.medicalconferences.fragments.TimePickerFragment;
import com.mabrouk.medicalconferences.model.Conference;
import com.mabrouk.medicalconferences.persistance.preferences.UserPreferences;
import com.mabrouk.medicalconferences.persistance.sqlite.DBWrapper;
import com.mabrouk.medicalconferences.persistance.sqlite.UserTable;

import java.util.Calendar;
import java.util.Date;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.internal.operators.BufferUntilSubscriber;
import rx.schedulers.Schedulers;

public class ConferenceFormActivity extends AppCompatActivity implements DatePickerFragment.OnDateSelectedListener,
        TimePickerFragment.OnTimeSelectedListener, CancelChangesDialogFragment.OnCancelChangesListener{
    public static final String EXTRA_CONFERENCE = "conference";

    public static void startInstance(Activity activity, int requestCode) {
        startInstance(activity, null, requestCode);
    }

    public static void startInstance(Activity activity, Conference conference, int requestCode) {
        Intent intent = new Intent(activity, ConferenceFormActivity.class);
        intent.putExtra(EXTRA_CONFERENCE, conference);
        activity.startActivityForResult(intent, requestCode);
    }

    EditText nameInput;
    Button timeButton;
    Button dateButton;
    Button submitButton;

    Conference conference;
    boolean changeExists;
    int year, month, day, hour, minute;

    boolean inTransaction = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conference_form);
        conference = getIntent().getParcelableExtra(EXTRA_CONFERENCE);
        if(conference == null) {
            conference = new Conference(0, "", 0, new Date().getTime(), false, new Date().getTime());
            setTitle(R.string.edit_conference);
        }else{
            setTitle(R.string.new_conference);
        }

        nameInput = (EditText) findViewById(R.id.name_input);
        nameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!conference.getName().equals(s))
                    changeExists = true;
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(v -> submit());

        dateButton = (Button) findViewById(R.id.date_button);
        dateButton.setOnClickListener(view -> {
            DatePickerFragment fragment = DatePickerFragment.createInstance(day, month, year);
            fragment.show(getSupportFragmentManager(), "datePicker");
        });
        timeButton = (Button) findViewById(R.id.time_button);

        timeButton.setOnClickListener(v -> {
            TimePickerFragment fragment = TimePickerFragment.newInstance(hour, minute);
            fragment.show(getSupportFragmentManager(), "timePicker");
        });

        populateWithConference(conference);
    }

    void populateWithConference(Conference conference) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(conference.getDateTimestamp());
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DATE);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        nameInput.setText(conference.getName());
        dateButton.setText(DateUtils.getDate(new Date(conference.getDateTimestamp())));
        timeButton.setText(DateUtils.getTime(new Date(conference.getDateTimestamp())));
    }

    void submit() {
        if(changeExists) {
            if(nameInput.getText().length() == 0) {
                nameInput.setError("Conference name can't be empry");
            }else{
                doSubmit();
            }
        }else {
            Toast.makeText(this, "No changes made", Toast.LENGTH_SHORT).show();
            cancel();
        }
    }

    void doSubmit() {
        if(inTransaction)
            return;
        inTransaction = true;
        DBWrapper dbWrapper = DBWrapper.getInstance();
        Observable.just(createConference())
                .map(c -> c.getId() == 0 ? dbWrapper.insertConference(c)
                        : dbWrapper.updateConference(c))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onDataSaved, e -> {
                    e.printStackTrace();
                    inTransaction = false;
                });
    }

    Conference createConference() {
        UserPreferences userPreferences = new UserPreferences(this);
        int adminId = userPreferences.getUserId();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);
        return new Conference(conference.getId(), nameInput.getText().toString(), adminId, calendar.getTimeInMillis()
                , false, new Date().getTime());
    }

    void onDataSaved(int id) {
        inTransaction = false;
        Intent intent = new Intent();
        intent.putExtra(EXTRA_CONFERENCE, id);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if(changeExists) {
            CancelChangesDialogFragment fragment = new CancelChangesDialogFragment();
            fragment.show(getSupportFragmentManager(), "cancelChanges");
        } else {
            setResult(RESULT_CANCELED);
            super.onBackPressed();
        }
    }

    @Override
    public void onDateSelected(int year, int month, int day) {
        changeExists = true;
        this.year = year;
        this.month = month;
        this.day = day;
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        long timestamp = calendar.getTimeInMillis();
        dateButton.setText(DateUtils.getDate(new Date(timestamp)));
    }

    @Override
    public void onTimeSelected(int hour, int minute) {
        changeExists = true;
        this.hour = hour;
        this.minute = minute;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        long timestamp = calendar.getTimeInMillis();
        timeButton.setText(DateUtils.getTime(new Date(timestamp)));
    }

    @Override
    public void onCancelChangesConfirmed(boolean confirmed) {
        if(confirmed) {
            cancel();
        }
    }

    void cancel() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
