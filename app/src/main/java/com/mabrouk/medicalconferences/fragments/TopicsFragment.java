package com.mabrouk.medicalconferences.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mabrouk.medicalconferences.R;
import com.mabrouk.medicalconferences.adapters.TopicsAdapter;
import com.mabrouk.medicalconferences.model.Topic;
import com.mabrouk.medicalconferences.persistence.sqlite.DBWrapper;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TopicsFragment extends Fragment {
    private static final String ARG_CONFERENCE_ID = "conf_id";

    private int confId;

    RecyclerView recyclerView;
    TextView emptyTextView;
    TopicsAdapter adapter;

    public static TopicsFragment newInstance(int confId) {
        TopicsFragment fragment = new TopicsFragment();
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
        View root =  inflater.inflate(R.layout.fragment_topics, container, false);
        emptyTextView = (TextView) root.findViewById(R.id.empty_text);
        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        getTopics();
        return root;
    }

    void getTopics() {
        Observable.just(confId)
                .map(DBWrapper.getInstance()::getConferenceTopics)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::gotTopics, this::gotError);
    }

    void gotTopics(List<Topic> topics) {
        if(topics.isEmpty()) {
            emptyTextView.setVisibility(View.VISIBLE);
        }else{
            emptyTextView.setVisibility(View.GONE);
        }
        adapter = new TopicsAdapter(topics);
        recyclerView.setAdapter(adapter);
    }

    void gotError(Throwable e) {
        e.printStackTrace();
    }

    public void topicAdded(int topicId) {
        Observable.just(topicId)
                .map(DBWrapper.getInstance()::getTopicById)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topic -> adapter.addTopic(topic),
                        e -> e.printStackTrace());
    }
}
