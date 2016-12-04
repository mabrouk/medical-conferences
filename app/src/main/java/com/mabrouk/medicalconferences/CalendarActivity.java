package com.mabrouk.medicalconferences;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.imanoweb.calendarview.CalendarListener;
import com.imanoweb.calendarview.CustomCalendarView;
import com.imanoweb.calendarview.DayDecorator;
import com.imanoweb.calendarview.DayView;
import com.mabrouk.medicalconferences.model.Conference;
import com.mabrouk.medicalconferences.persistence.preferences.UserPreferences;
import com.mabrouk.medicalconferences.persistence.sqlite.DBWrapper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CalendarActivity extends AppCompatActivity {

    public static void startInstance(Activity activity) {
        Intent intent = new Intent(activity, CalendarActivity.class);
        activity.startActivity(intent);
    }

    CustomCalendarView calendarView;
    List<Long> conferenceDates;
    List<Conference> conferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        calendarView = (CustomCalendarView) findViewById(R.id.calendar_view);

        setTitle("Calendar");

        int userId = new UserPreferences(this).getUserId();
        Observable.just(userId)
                .map(DBWrapper.getInstance()::getAllConferencesForDoctor)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::gotConferences, this::gotError);
    }

    void gotError(Throwable e) {
        e.printStackTrace();
    }

    void gotConferences(List<Conference> conferences) {
        this.conferences = conferences;
        conferenceDates = new ArrayList<>(conferences.size());
        for(Conference conference : conferences) {
           conferenceDates.add(dayTimestampRemovingHourAndMinute(conference.getDateTimestamp()));
        }

        calendarView.setCalendarListener(new CalendarListener() {
            @Override
            public void onDateSelected(Date date) {
                long timestamp = dayTimestampRemovingHourAndMinute(date.getTime());
                int index = conferenceDates.indexOf(timestamp);
                refreshCalender(date);
                if(index != -1) {
                    ConferenceDetailsActivity.startInstance(CalendarActivity.this, conferences.get(index));
                }
            }

            @Override
            public void onMonthChanged(Date date) {}
        });

        List<DayDecorator> decorators = new ArrayList<>();
        decorators.add(new ColorDecorator());
        calendarView.setDecorators(decorators);

        calendarView.setShowOverflowDate(false);

        calendarView.refreshCalendar(Calendar.getInstance());
    }

    void refreshCalender(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendarView.refreshCalendar(calendar);
    }

    long dayTimestampRemovingHourAndMinute(long timestamp) {
        int factor = 1000 * 60 * 60 * 24;
        return timestamp / factor;
    }

    private class ColorDecorator implements DayDecorator {
        @Override
        public void decorate(DayView dayView) {
            long timestamp = dayTimestampRemovingHourAndMinute(dayView.getDate().getTime());
            if (conferenceDates.contains(timestamp)) {
                int color = getResources().getColor(R.color.colorAccent);
                dayView.setBackgroundColor(color);
            }
        }
    }
}
