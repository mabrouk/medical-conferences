package com.mabrouk.medicalconferences;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mabrouk.medicalconferences.adapters.ConferencesAdapter;
import com.mabrouk.medicalconferences.model.Conference;
import com.mabrouk.medicalconferences.model.Invitation;
import com.mabrouk.medicalconferences.model.User;
import com.mabrouk.medicalconferences.persistence.preferences.UserPreferences;
import com.mabrouk.medicalconferences.persistence.sqlite.DBWrapper;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DoctorHomeActivity extends AppCompatActivity {
    static final String EXTRA_USER = "user";
    public static void startInstance(Activity activity, User user) {
        Intent intent = new Intent(activity, DoctorHomeActivity.class);
        intent.putExtra(EXTRA_USER, user);
        activity.startActivity(intent);
    }

    User doctor;
    ConferencesAdapter adapter;

    TextView emptyTextView;
    RecyclerView recyclerView;
    View notificationsLayout;
    TextView notificationsText;

    List<Invitation> pendingInvitations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        doctor = getIntent().getParcelableExtra(EXTRA_USER);

        setContentView(R.layout.activity_doctor_home);
        emptyTextView = (TextView) findViewById(R.id.empty_text);
        notificationsText = (TextView) findViewById(R.id.notification_text);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        notificationsLayout = findViewById(R.id.notification_layout);

        getPendingInvitations();
        getUpcomingConferences();
    }

    void getPendingInvitations() {
        Observable.just(doctor.getId())
                .map(DBWrapper.getInstance()::getPendingInvitations)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::gotPendingInvitations, e -> e.printStackTrace());
    }

    void getUpcomingConferences() {
        Observable.just(doctor.getId())
                .map(DBWrapper.getInstance()::getUpcomingConferencesForDoctor)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::gotUpcomingConferences, e -> e.printStackTrace());
    }

    void gotPendingInvitations(List<Invitation> invitations) {
        this.pendingInvitations = invitations;
        if(invitations.isEmpty()) {
            notificationsLayout.setVisibility(View.GONE);
        }else{
            notificationsLayout.setVisibility(View.VISIBLE);
            int size = invitations.size();
            int newInvitationsCount = 0;
            for(int i = 0; i < size; i++) {
                if(Invitation.isNewInvitation(invitations.get(i), doctor.getLastLoginTimestamp()))
                    newInvitationsCount++;
            }
            if(newInvitationsCount > 0) {
                notificationsText.setText(notificationMessage(size, newInvitationsCount));
            } else {
                notificationsText.setText(String.format("You have %d pending invitations", invitations.size()));
            }
        }
    }

    SpannableString notificationMessage(int totalInvitations, int newInvitations) {
        String msg = String.format("You have %d pending invitations, %d %s new",
                totalInvitations, newInvitations, newInvitations == 1 ? "is" : "are");
        int start = msg.indexOf(",") + 1;
        SpannableString spannableString = new SpannableString(msg);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)),
                start, msg.length(), 0);
        return spannableString;
    }

    void gotUpcomingConferences(List<Conference> conferences) {
        if(conferences.isEmpty()) {
            emptyTextView.setVisibility(View.VISIBLE);
        }else {
            adapter = new ConferencesAdapter(this, conferences);
            recyclerView.setAdapter(adapter);
            emptyTextView.setVisibility(View.GONE);
        }
    }

    public void acceptConferenceInvitation(Conference conference) {
        updateInvitationState(conference, Invitation.STATE_ACCEPTED);
    }

    public void rejectConferenceInvitation(Conference conference) {
        updateInvitationState(conference, Invitation.STATE_REJECTED);
    }

    void updateInvitationState(Conference conference, int state){
        DBWrapper dbWrapper = DBWrapper.getInstance();
        Observable.just(true)
                .map(b -> dbWrapper.updateDoctorInvitation(doctor.getId(), conference.getId(), state))
                .doOnNext(b -> conference.getInvitation().setState(state))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(b -> {
                    adapter.conferenceStateChanged(conference);
                    removeNotification(conference);
                });
    }

    void removeNotification(Conference conference) {
        List<Invitation> invitations = new ArrayList<>(pendingInvitations.size());
        for(Invitation invitation : pendingInvitations) {
            if(invitation.getConferenceId() == conference.getId())
                continue;;
            invitations.add(invitation);
        }
        gotPendingInvitations(invitations);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_doctor_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.logout) {
            new UserPreferences(this).logOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }else if(item.getItemId() == R.id.calendar) {
            Toast.makeText(this, "Not implemented yet", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public Long getDoctorLastLogin() {
        return doctor.getLastLoginTimestamp();
    }
}
