package com.mabrouk.medicalconferences;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mabrouk.medicalconferences.adapters.ConferencesAdapter;
import com.mabrouk.medicalconferences.model.Conference;
import com.mabrouk.medicalconferences.model.User;
import com.mabrouk.medicalconferences.persistance.preferences.UserPreferences;
import com.mabrouk.medicalconferences.persistance.sqlite.DBWrapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AdminHomeActivity extends AppCompatActivity {
    private static final String EXTRA_ADMIN_ID = "admin_id";
    private static final int REQUEST_CREATE_CONFERENCE = 1;
    private static final int REQUEST_UPDATE_CONFERENCE = 2;

    public static void startInstance(Activity activity, int adminId) {
        Intent intent = new Intent(activity, AdminHomeActivity.class);
        intent.putExtra(EXTRA_ADMIN_ID, adminId);
        activity.startActivity(intent);
    }

    ProgressBar progressBar;
    RecyclerView recyclerView;
    FloatingActionButton addButton;
    TextView emptyTextView;
    int adminId;
    ConferencesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        addButton = (FloatingActionButton) findViewById(R.id.add_conference_button);
        emptyTextView = (TextView) findViewById(R.id.empty_text);

        adminId = getIntent().getIntExtra(EXTRA_ADMIN_ID, 0);
        addButton.setOnClickListener(view ->
                ConferenceFormActivity.startInstance(this, REQUEST_CREATE_CONFERENCE));

        setTitle(R.string.home);
        getConferencesList();
    }

    void getConferencesList() {
        progressBar.setVisibility(View.VISIBLE);
        emptyTextView.setVisibility(View.GONE);

        DBWrapper wrapper = DBWrapper.getInstance();
        Observable.just(adminId)
                .map(wrapper::getUpcomingConferencesForAdmin)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::gotConferencesList, this::gotError);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.logout) {
            new UserPreferences(this).logOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void gotConferencesList(List<Conference> conferences) {
        progressBar.setVisibility(View.GONE);
        if(conferences.isEmpty())
            emptyTextView.setVisibility(View.VISIBLE);
        adapter = new ConferencesAdapter(this, conferences);
        recyclerView.setAdapter(adapter);
    }

    void gotError(Throwable e) {
        e.printStackTrace();
    }

    public void editConference(Conference conference) {
        ConferenceFormActivity.startInstance(this, conference, REQUEST_UPDATE_CONFERENCE);
    }

    public void deleteConference(Conference conference, int position) {
        DBWrapper wrapper = DBWrapper.getInstance();
        Observable.just(conference)
                .map(wrapper::deleteConference)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> {
                    if(success)
                        itemDeleted(position);
                    else
                        deleteError(new Exception("Couldn't delete"));
                }, this::deleteError);
    }

    void itemDeleted(int position) {
        adapter.itemDeleted(position);
    }

    void deleteError(Throwable e) {
        e.printStackTrace();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if((requestCode == 1 || requestCode == 2) && resultCode == RESULT_OK) {
            int confId = data.getIntExtra(ConferenceFormActivity.EXTRA_CONFERENCE, 0);
            Observable.just(confId)
                    .map(DBWrapper.getInstance()::getConferenceById)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(conf -> {
                        if(requestCode == 1)
                            adapter.addConference(conf);
                        else
                            adapter.updateConference(conf);
                    }, e -> e.printStackTrace());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
