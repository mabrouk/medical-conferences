package com.mabrouk.medicalconferences;

import android.animation.ObjectAnimator;
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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.mabrouk.medicalconferences.Util.DateUtils;
import com.mabrouk.medicalconferences.fragments.InvitedDoctorsFragment;
import com.mabrouk.medicalconferences.fragments.TopicsFragment;
import com.mabrouk.medicalconferences.model.Conference;
import com.mabrouk.medicalconferences.persistance.sqlite.DBWrapper;

import java.util.Arrays;
import java.util.Date;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ConferenceDetailsActivity extends AppCompatActivity {
    private static final String EXTRA_CONFERENCE = "conference";
    private static final int REQUEST_INVITE = 1;

    public static void startInstance(Activity activity, Conference conference) {
        Intent intent = new Intent(activity, ConferenceDetailsActivity.class);
        intent.putExtra(EXTRA_CONFERENCE, conference);
        activity.startActivity(intent);
    }

    TextView date;
    TextView time;
    TextView admin;
    ViewPager viewPager;
    FloatingActionButton addFab;
    PagerAdapter adapter;

    Conference conference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conference_details);
        conference = getIntent().getParcelableExtra(EXTRA_CONFERENCE);
        setTitle(conference.getName());
        date = (TextView) findViewById(R.id.date);
        time = (TextView) findViewById(R.id.time);
        admin = (TextView) findViewById(R.id.admin);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        addFab = (FloatingActionButton) findViewById(R.id.add_button);
        addFab.setOnClickListener(v ->
                InviteDoctorsActivity.startInstance(this, conference.getId(), REQUEST_INVITE));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                addFab.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        date.setText("Date: " + DateUtils.getDate(new Date(conference.getDateTimestamp())));
        time.setText("Time: " + DateUtils.getTime(new Date(conference.getDateTimestamp())));
        adapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        Observable.just(conference.getAdminId())
                .map(DBWrapper.getInstance()::getUserById)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> admin.setText("Admin: " + user.getFirstName() + " " + user.getLastName()),
                        e -> e.printStackTrace());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_INVITE && resultCode == RESULT_OK) {
            addNewInvitations(data);
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    void addNewInvitations(Intent data) {
        int[] newInvitations = data.getIntArrayExtra(InviteDoctorsActivity.EXTRA_NEW_INVITATIONS);
        adapter.invitedDoctorsFragment.addNewInvitations(newInvitations);
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
            return position == 0 ? "Invitations" : "Topics";
        }
    }
}
