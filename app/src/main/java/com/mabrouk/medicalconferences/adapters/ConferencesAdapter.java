package com.mabrouk.medicalconferences.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mabrouk.medicalconferences.AdminHomeActivity;
import com.mabrouk.medicalconferences.ConferenceDetailsActivity;
import com.mabrouk.medicalconferences.DoctorHomeActivity;
import com.mabrouk.medicalconferences.R;
import com.mabrouk.medicalconferences.util.DateUtils;
import com.mabrouk.medicalconferences.model.Conference;
import com.mabrouk.medicalconferences.model.Invitation;

import java.util.Date;
import java.util.List;

/**
 * Created by User on 12/2/2016.
 */

public class ConferencesAdapter extends RecyclerView.Adapter<ConferencesAdapter.ConferenceViewHolder> {
    static int conferenceIndexBasedOnDate(Conference conference, List<Conference> conferences) {
        int size = conferences.size();
        for(int i = 0; i < size; i++) {
            if(conferences.get(i).getDateTimestamp() <= conference.getDateTimestamp()) {
                return i-1;
            }
        }
        return size;
    }

    List<Conference> conferences;
    Presenter presenter;
    public ConferencesAdapter(Activity activity, List<Conference> conferences) {
        this.conferences = conferences;
        if(activity instanceof AdminHomeActivity)
            presenter = new AdminPresenter((AdminHomeActivity) activity);
        else
            presenter = new DoctorHomePresenter((DoctorHomeActivity) activity);
    }

    public void addConference(Conference conference) {
        int position = conferenceIndexBasedOnDate(conference, conferences);
        conferences.add(position, conference);
        notifyItemRangeInserted(position, 1);
    }

    public void updateConference(Conference conference) {
        int oldPosition = indexOfConference(conference);

        int newPosition = conferenceIndexBasedOnDate(conference, conferences);

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

    int indexOfConference(Conference conference) {
        int size = conferences.size();
        for(int i = 0; i < size; i++) {
            if(conferences.get(i).getId() == conference.getId()) {
                return i;
            }
        }
        return -1;
    }

    public void conferenceStateChanged(Conference conference) {
        int index = indexOfConference(conference);
        if(index != -1) {
            conferences.set(index, conference);
            notifyItemChanged(index);
        }
    }

    @Override
    public ConferenceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_conference, parent, false);

        ConferenceViewHolder vh = new ConferenceViewHolder(itemView);
        presenter.initViewHolderView(vh);
        return vh;
    }

    @Override
    public void onBindViewHolder(ConferenceViewHolder holder, int position) {
        presenter.populateData(holder, conferences.get(position), position);
    }

    public void itemDeleted(int position) {
        conferences.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return conferences.size();
    }

    static class ConferenceViewHolder extends RecyclerView.ViewHolder {
        TextView headline;
        TextView subhead1;
        TextView subhead2;
        Button action1Button;
        Button action2Button;

        public ConferenceViewHolder(View itemView) {
            super(itemView);
            headline = (TextView) itemView.findViewById(R.id.headline);
            subhead2 = (TextView) itemView.findViewById(R.id.subheadline2);
            subhead1 = (TextView) itemView.findViewById(R.id.subheadline1);
            action1Button = (Button) itemView.findViewById(R.id.action1_button);
            action2Button = (Button) itemView.findViewById(R.id.action2_button);
        }
    }

    interface Presenter {
        void initViewHolderView(ConferenceViewHolder viewHolder);
        void populateData(ConferenceViewHolder viewHolder, Conference data, int position);
    }

    static class AdminPresenter implements Presenter {
        AdminHomeActivity activity;
        public AdminPresenter(AdminHomeActivity activity) {
            this.activity = activity;
        }
        @Override
        public void initViewHolderView(ConferenceViewHolder viewHolder) {
            viewHolder.subhead2.setVisibility(View.GONE);
            viewHolder.action1Button.setText("Delete");
            viewHolder.action2Button.setText("Edit");
        }

        @Override
        public void populateData(ConferenceViewHolder holder, Conference conference, int position) {
            holder.headline.setText(conference.getName());
            Date date = new Date(conference.getDateTimestamp());
            holder.subhead1.setText(String.format("Date: %s, at: %s", DateUtils.getDate(date), DateUtils.getTime(date)));
            holder.action1Button.setOnClickListener(v -> activity.cancelConference(conference, position));
            holder.action2Button.setOnClickListener(v -> activity.editConference(conference));
            holder.itemView.setOnClickListener(v -> ConferenceDetailsActivity.startInstance(activity, conference));
        }
    }

    static class DoctorHomePresenter implements Presenter {
        DoctorHomeActivity activity;

        public DoctorHomePresenter(DoctorHomeActivity activity) {
            this.activity = activity;
        }

        @Override
        public void initViewHolderView(ConferenceViewHolder viewHolder) {
            viewHolder.action1Button.setText("Accept");
            viewHolder.action2Button.setText("Reject");
        }

        @Override
        public void populateData(ConferenceViewHolder holder, Conference data, int position) {
            holder.headline.setText(data.getName());
            Date date = new Date(data.getDateTimestamp());
            holder.subhead1.setText(String.format("Date: %s, at: %s",
                    DateUtils.getDate(date), DateUtils.getTime(date)));
            Invitation invitation = data.getInvitation();
            holder.subhead2.setText("State: " + Invitation.getStateText(invitation));

            int state = invitation.getState();
            holder.action1Button.setVisibility(state == Invitation.STATE_ACCEPTED ? View.GONE : View.VISIBLE);
            holder.action2Button.setVisibility(state == Invitation.STATE_REJECTED ? View.GONE : View.VISIBLE);
            holder.action1Button.setOnClickListener(view -> activity.acceptConferenceInvitation(data));
            holder.action2Button.setOnClickListener(view -> activity.rejectConferenceInvitation(data));
            holder.itemView.setOnClickListener(v -> ConferenceDetailsActivity.startInstance(activity, data));
            boolean isNewInvitation = Invitation.isNewInvitation(invitation, activity.getDoctorLastLogin());
            holder.itemView.setBackgroundColor(isNewInvitation ? activity.getResources().getColor(R.color.colorAccent) : 0x00);
        }
    }
}
