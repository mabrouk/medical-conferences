package com.mabrouk.medicalconferences;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mabrouk.medicalconferences.util.DateUtils;
import com.mabrouk.medicalconferences.fragments.InvitedDoctorsFragment;
import com.mabrouk.medicalconferences.fragments.NewTopicDialogFragment;
import com.mabrouk.medicalconferences.fragments.TopicsFragment;
import com.mabrouk.medicalconferences.model.Conference;
import com.mabrouk.medicalconferences.model.Invitation;
import com.mabrouk.medicalconferences.model.Topic;
import com.mabrouk.medicalconferences.persistence.preferences.UserPreferences;
import com.mabrouk.medicalconferences.persistence.sqlite.DBWrapper;

import java.util.Date;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ConferenceDetailsActivity extends AppCompatActivity implements NewTopicDialogFragment.OnTopicAddedListener {
    private static final String EXTRA_CONFERENCE = "conference";
    private static final String EXTRA_MODE = "mode";
    private static final String EXTRA_STATE = "state";

    private static final int REQUEST_INVITE = 1;
    private static final int MODE_ADMIN = 0;
    private static final int MODE_DOCTOR = 1;


    public static void startInstance(Activity activity, Conference conference) {
        Intent intent = new Intent(activity, ConferenceDetailsActivity.class);
        intent.putExtra(EXTRA_CONFERENCE, conference);
        int mode = activity instanceof AdminHomeActivity ? MODE_ADMIN : MODE_DOCTOR;
        intent.putExtra(EXTRA_MODE, mode);
        if (mode == MODE_DOCTOR)
            intent.putExtra(EXTRA_STATE, conference.getInvitation().getState());
        activity.startActivity(intent);
    }

    TextView date;
    TextView time;
    TextView extraInfo;
    ViewPager viewPager;
    FloatingActionButton addFab;
    PagerAdapter adapter;

    int mode;
    Conference conference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conference_details);
        conference = getIntent().getParcelableExtra(EXTRA_CONFERENCE);
        mode = getIntent().getIntExtra(EXTRA_MODE, 0);

        setTitle(conference.getName());
        date = (TextView) findViewById(R.id.date);
        time = (TextView) findViewById(R.id.time);
        extraInfo = (TextView) findViewById(R.id.extra_info);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        addFab = (FloatingActionButton) findViewById(R.id.add_button);

        addFab.setVisibility(mode == MODE_DOCTOR ? View.GONE : View.VISIBLE);
        addFab.setOnClickListener(this::fabClicked);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                addFab.setVisibility(position == mode ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        date.setText("Date: " + DateUtils.getDate(new Date(conference.getDateTimestamp())));
        time.setText("Time: " + DateUtils.getTime(new Date(conference.getDateTimestamp())));
        adapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        if (mode == MODE_ADMIN) {
            getAdmin();
        } else {
            int state = getIntent().getIntExtra(EXTRA_STATE, 0);
            extraInfo.setText("State: " + Invitation.getStateText(state));
        }
    }

    void fabClicked(View fab) {
        if (mode == MODE_ADMIN) {
            InviteDoctorsActivity.startInstance(this, conference.getId(), REQUEST_INVITE);
        } else {
            NewTopicDialogFragment dialogFragment = new NewTopicDialogFragment();
            dialogFragment.show(getSupportFragmentManager(), "new_topic");
        }
    }

    void getAdmin() {
        Observable.just(conference.getAdminId())
                .map(DBWrapper.getInstance()::getUserById)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> extraInfo.setText("Admin: " + user.getFirstName() + " " + user.getLastName()),
                        this::gotError);
    }

    void gotError(Throwable e) {
        e.printStackTrace();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_INVITE && resultCode == RESULT_OK) {
            addNewInvitations(data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    void addNewInvitations(Intent data) {
        int[] newInvitations = data.getIntArrayExtra(InviteDoctorsActivity.EXTRA_NEW_INVITATIONS);
        adapter.invitedDoctorsFragment.addNewInvitations(newInvitations);
    }

    @Override
    public void onTopicAdded(String topic) {
        Observable.just(topic)
                .map(this::createTopic)
                .map(DBWrapper.getInstance()::insertTopic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(id -> adapter.topicsFragment.topicAdded(id), this::gotError);
    }

    Topic createTopic(String topic) {
        int doctorId = new UserPreferences(this).getUserId();
        return new Topic(0, topic, new Date().getTime(), doctorId, conference.getId());
    }

    class PagerAdapter extends FragmentPagerAdapter {
        InvitedDoctorsFragment invitedDoctorsFragment;
        TopicsFragment topicsFragment;

        public PagerAdapter(FragmentManager fm) {
            super(fm);
            invitedDoctorsFragment = InvitedDoctorsFragment.newInstance(conference.getId());
            topicsFragment = TopicsFragment.newInstance(conference.getId());
        }

        @Override
        public Fragment getItem(int position) {
            return position == 0 ? invitedDoctorsFragment : topicsFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return position == 0 ? "Invited" : "Topics";
        }
    }
}
