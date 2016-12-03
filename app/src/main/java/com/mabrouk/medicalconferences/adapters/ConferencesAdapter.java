package com.mabrouk.medicalconferences.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mabrouk.medicalconferences.AdminHomeActivity;
import com.mabrouk.medicalconferences.R;
import com.mabrouk.medicalconferences.Util.DateUtils;
import com.mabrouk.medicalconferences.model.Conference;
import com.mabrouk.medicalconferences.persistance.sqlite.DBWrapper;

import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by User on 12/2/2016.
 */

public class ConferencesAdapter extends RecyclerView.Adapter<ConferencesAdapter.ConferenceViewHolder> {
    List<Conference> conferences;
    AdminHomeActivity activity;
    public ConferencesAdapter(AdminHomeActivity activity, List<Conference> conferences) {
        this.conferences = conferences;
        this.activity = activity;
    }

    public void addConference(Conference conference) {
        int position = appropriateIndex(conference);
        conferences.add(position, conference);
        notifyItemRangeInserted(position, 1);
    }

    int appropriateIndex(Conference conference) {
        int size = conferences.size();
        for(int i = 0; i < size; i++) {
            if(conferences.get(i).getDateTimestamp() < conference.getDateTimestamp()) {
                return i;
            }
        }
        return size;
    }

    public void updateConference(Conference conference) {
        int oldPosition = -1;
        int size = conferences.size();
        for(int i = 0; i < size; i++) {
            if(conferences.get(i).getId() == conference.getId()) {
                oldPosition = i;
                break;
            }
        }
        int newPosition = appropriateIndex(conference);
        if(newPosition == oldPosition) {
            conferences.set(oldPosition, conference);
            notifyItemChanged(oldPosition);
        }else{
            notifyItemChanged(oldPosition);
            conferences.remove(oldPosition);
            conferences.add(newPosition, conference);
            notifyItemMoved(oldPosition, newPosition);
        }
    }

    @Override
    public ConferenceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.row_conference, parent, false);

        return new ConferenceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ConferenceViewHolder holder, int position) {
        Conference conference = conferences.get(position);
        holder.name.setText(conference.getName());
        Date date = new Date(conference.getDateTimestamp());
        holder.dateTime.setText(String.format("Date: %s, at: %s", DateUtils.getDate(date), DateUtils.getTime(date)));
        holder.deleteButton.setOnClickListener(v -> deleteConference(conference, position));
        holder.editButton.setOnClickListener(v -> activity.editConference(conference));
    }

    void deleteConference(Conference conference, int position) {
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
        conferences.remove(position);
        notifyItemRemoved(position);
    }

    void deleteError(Throwable e) {
        e.printStackTrace();
    }

    @Override
    public int getItemCount() {
        return conferences.size();
    }

    static class ConferenceViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView dateTime;
        View deleteButton;
        View editButton;

        public ConferenceViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.conference_name);
            dateTime = (TextView) itemView.findViewById(R.id.date_time);
            deleteButton = itemView.findViewById(R.id.delete_button);
            editButton = itemView.findViewById(R.id.edit_button);
        }
    }

}
