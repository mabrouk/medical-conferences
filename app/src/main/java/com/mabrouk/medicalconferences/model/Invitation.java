package com.mabrouk.medicalconferences.model;

/**
 * Created by User on 12/2/2016.
 */

public class Invitation {
    public static final int STATE_NEW = 1;
    public static final int STATE_PENDING = 2;
    public static final int STATE_ACCEPTED = 3;
    public static final int STATE_REJECTED = 4;

    public static String getStateTextForAdmin(Invitation invitation) {
        switch (invitation.state) {
            case STATE_NEW:
            case STATE_PENDING:
                return "Pending";

            case STATE_ACCEPTED:
                return "Attending";
            case STATE_REJECTED:
                return "Rejected";
            default:
                throw new IllegalStateException("Invitation state is undefined");
        }
    }

    final int id;
    final int adminId;
    final int doctorId;
    final int conferenceId;
    int state;
    long updatedAtTimestamp;

    User admin;
    User doctor;
    Conference conference;

    public Invitation(int id, int adminId, int doctorId, int conferenceId, int state, long updatedAtTimestamp) {
        this.id = id;
        this.adminId = adminId;
        this.doctorId = doctorId;
        this.conferenceId = conferenceId;
        this.state = state;
        this.updatedAtTimestamp = updatedAtTimestamp;
    }

    public int getId() {
        return id;
    }

    public int getAdminId() {
        return adminId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public int getConferenceId() {
        return conferenceId;
    }

    public int getState() {
        return state;
    }

    public long getUpdatedAtTimestamp() {
        return updatedAtTimestamp;
    }

    public User getAdmin() {
        return admin;
    }

    public User getDoctor() {
        return doctor;
    }

    public Conference getConference() {
        return conference;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setUpdatedAtTimestamp(long updatedAtTimestamp) {
        this.updatedAtTimestamp = updatedAtTimestamp;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public void setDoctor(User doctor) {
        this.doctor = doctor;
    }

    public void setConference(Conference conference) {
        this.conference = conference;
    }

}
