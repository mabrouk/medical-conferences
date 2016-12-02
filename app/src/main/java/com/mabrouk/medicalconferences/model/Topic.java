package com.mabrouk.medicalconferences.model;

/**
 * Created by User on 12/2/2016.
 */

public class Topic {
    final int id;
    final String description;
    final long createdAtTimestamp;
    final int creatorId;
    final int conferenceId;

    User creator;
    Conference conference;

    public Topic(int id, String description, long createdAtTimestamp, int creatorId, int conferenceId) {
        this.id = id;
        this.description = description;
        this.createdAtTimestamp = createdAtTimestamp;
        this.creatorId = creatorId;
        this.conferenceId = conferenceId;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public long getCreatedAtTimestamp() {
        return createdAtTimestamp;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public int getConferenceId() {
        return conferenceId;
    }

    public User getCreator() {
        return creator;
    }

    public Conference getConference() {
        return conference;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public void setConference(Conference conference) {
        this.conference = conference;
    }
}
