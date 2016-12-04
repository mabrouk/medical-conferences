package com.mabrouk.medicalconferences;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mabrouk.medicalconferences.adapters.DoctorsListAdapter;
import com.mabrouk.medicalconferences.fragments.CancelChangesDialogFragment;
import com.mabrouk.medicalconferences.model.Invitation;
import com.mabrouk.medicalconferences.model.User;
import com.mabrouk.medicalconferences.persistence.preferences.UserPreferences;
import com.mabrouk.medicalconferences.persistence.sqlite.DBWrapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class InviteDoctorsActivity extends AppCompatActivity implements CancelChangesDialogFragment.OnCancelChangesListener{
    private static final String EXTRA_CONFERENCE = "conference";
    public static final String EXTRA_NEW_INVITATIONS = "invitations";

    public static void startInstance(Activity activity, int conferenceId, int requestCode) {
        Intent intent = new Intent(activity, InviteDoctorsActivity.class);
        intent.putExtra(EXTRA_CONFERENCE, conferenceId);
        activity.startActivityForResult(intent, requestCode);
    }

    int confId;

    TextView emptyTextView;
    ListView listView;
    Button inviteButton;
    DoctorsListAdapter adapter;
    ArrayList<Integer> newInvitations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_doctors);
        setTitle("Invite Doctors");
        emptyTextView = (TextView) findViewById(R.id.empty_text);
        listView = (ListView) findViewById(R.id.doctors_list);
        inviteButton = (Button) findViewById(R.id.add_button);
        confId = getIntent().getIntExtra(EXTRA_CONFERENCE, 0);

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        inviteButton.setOnClickListener(view -> invite());
        getDoctors();
    }

    void getDoctors() {
        Observable.just(confId)
                .map(DBWrapper.getInstance()::getNotInvitedDoctors)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::gotDoctors, e -> e.printStackTrace());
    }

    void gotDoctors(List<User> doctors) {
        if(doctors.isEmpty()) {
            inviteButton.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        }else{
            inviteButton.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
        }
        adapter = new DoctorsListAdapter(doctors);
        listView.setAdapter(adapter);
    }

    void invite() {
        List<User> selectedList = adapter.getSelectedDoctors();
        if(selectedList.isEmpty()) {
            Toast.makeText(this, "You haven't selected any doctors", Toast.LENGTH_SHORT).show();
        }else{
            invite(selectedList);
        }
    }

    void invite(List<User> doctors) {
        newInvitations = new ArrayList<>(doctors.size());
        Observable.from(doctors)
                .map(this::createInvitationForDoctor)
                .map(DBWrapper.getInstance()::insertInvitation)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::invitationSent, this::invitationError, this::invitationSendComplete);
    }

    Invitation createInvitationForDoctor(User doc) {
        int adminId = new UserPreferences(this).getUserId();
        return new Invitation(0, adminId, doc.getId(), confId, Invitation.STATE_PENDING, new Date().getTime());
    }

    void invitationSent(int id) {
        newInvitations.add(id);
    }

    void invitationError(Throwable e) {
        e.printStackTrace();
    }

    void invitationSendComplete() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_NEW_INVITATIONS, newInvitations.toArray());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        List<User> selectedList = adapter.getSelectedDoctors();
        if(selectedList.isEmpty())
            super.onBackPressed();
        else {
            CancelChangesDialogFragment fragment =
                    CancelChangesDialogFragment.createInstance("You've selected some doctors. Are you sure you want to exit");
            fragment.show(getSupportFragmentManager(), "cancelChanges");
        }
    }

    @Override
    public void onCancelChangesConfirmed(boolean confirmed) {
        if(confirmed) {
            setResult(RESULT_CANCELED);
            finish();
        }
    }
}
