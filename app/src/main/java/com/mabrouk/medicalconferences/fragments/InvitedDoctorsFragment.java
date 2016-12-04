package com.mabrouk.medicalconferences.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mabrouk.medicalconferences.R;
import com.mabrouk.medicalconferences.adapters.InvitationDoctorAdapter;
import com.mabrouk.medicalconferences.model.Invitation;
import com.mabrouk.medicalconferences.persistence.sqlite.DBWrapper;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class InvitedDoctorsFragment extends Fragment {
    private static final String ARG_CONFERENCE_ID = "conf_id";

    private int confId;
    RecyclerView recyclerView;
    TextView emptyTextView;
    InvitationDoctorAdapter adapter;

    public static InvitedDoctorsFragment newInstance(int confId) {
        InvitedDoctorsFragment fragment = new InvitedDoctorsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CONFERENCE_ID, confId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            confId = getArguments().getInt(ARG_CONFERENCE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_invited_doctors, container, false);
        emptyTextView = (TextView) root.findViewById(R.id.empty_text);
        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        getInvitations();
        return root;
    }

    void getInvitations() {
        Observable.just(confId)
                .map(DBWrapper.getInstance()::getInvitationsForConference)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::gotInvitations, this::gotError);
    }

    void gotInvitations(List<Invitation> invitedDoctors) {
        if(invitedDoctors.size() == 0) {
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            emptyTextView.setVisibility(View.GONE);
        }
        adapter = new InvitationDoctorAdapter(invitedDoctors);
        recyclerView.setAdapter(adapter);
    }

    void gotError(Throwable e) {
        e.printStackTrace();
    }

    public void addNewInvitations(int[] newInvitations) {
        getInvitations();
    }
}
