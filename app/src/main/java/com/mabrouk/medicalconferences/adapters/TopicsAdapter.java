package com.mabrouk.medicalconferences.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mabrouk.medicalconferences.MedicalConferencesApplication;
import com.mabrouk.medicalconferences.R;
import com.mabrouk.medicalconferences.model.Topic;
import com.mabrouk.medicalconferences.model.User;
import com.mabrouk.medicalconferences.persistance.preferences.UserPreferences;

import java.util.List;

/**
 * Created by User on 12/3/2016.
 */

public class TopicsAdapter extends RecyclerView.Adapter<TopicsAdapter.TopicsViewHolder>{
    List<Topic> topicList;
    int userId;
    public TopicsAdapter(List<Topic> topics) {
        this.topicList = topics;
        userId = new UserPreferences(MedicalConferencesApplication.getInstance()).getUserId();
    }

    @Override
    public TopicsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_topic, parent, false);
        return new TopicsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TopicsViewHolder holder, int position) {
        Topic topic = topicList.get(position);
        String doctorName = topic.getCreatorId() == userId ? "You" : topic.getCreator().getFirstName() +
            topic.getCreator().getLastName();
        holder.doctorName.setText("Suggested by: " + doctorName);
        holder.name.setText(topic.getDescription());
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    public void addTopic(Topic topic) {
        topicList.add(0, topic);
        notifyItemRangeInserted(0, 1);
    }

    static class TopicsViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView doctorName;
        public TopicsViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.topic_name);
            doctorName = (TextView) itemView.findViewById(R.id.doctor_name);
        }
    }
}
