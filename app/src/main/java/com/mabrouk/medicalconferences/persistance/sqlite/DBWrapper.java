package com.mabrouk.medicalconferences.persistance.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mabrouk.medicalconferences.MedicalConferencesApplication;
import com.mabrouk.medicalconferences.model.Conference;
import com.mabrouk.medicalconferences.model.Invitation;
import com.mabrouk.medicalconferences.model.Topic;
import com.mabrouk.medicalconferences.model.User;
import com.mabrouk.medicalconferences.persistance.preferences.UserPreferences;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by User on 12/2/2016.
 */

public class DBWrapper {
    private static DBWrapper instance;

    public static DBWrapper getInstance() {
        return instance;
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new DBWrapper(context);
        }
    }

    private DBOpenHelper dbHelper;

    private DBWrapper(Context context) {
        dbHelper = new DBOpenHelper(context, null);
    }

    public User getUser(String userName, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(UserTable.TABLE_NAME, null,
                String.format("%s = ? AND %s = ?", UserTable.COLUMN_USER_EMAIL, UserTable.COLUMN_PASSWORD),
                new String[]{userName, password}, null, null, null);
        if (cursor.getCount() == 0)
            return null;
        cursor.moveToFirst();
        return UserMapper.userFromCursor(cursor);
    }

    public User getUserById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(UserTable.TABLE_NAME, null,
                String.format("%s = ?", UserTable.COLUMN_ID),
                new String[]{String.valueOf(id)}, null, null, null);

        cursor.moveToFirst();
        return UserMapper.userFromCursor(cursor);
    }

    public List<User> getNotInvitedDoctors(int confId) {
        String query = String.format("SELECT * FROM %s WHERE %s = ? AND %s NOT IN (SELECT %s FROM %s WHERE %s = ? );",
                UserTable.TABLE_NAME, UserTable.COLUMN_ROLE, UserTable.COLUMN_ID, InvitationTable.COLUMN_DOCTOR_ID,
                InvitationTable.TABLE_NAME, InvitationTable.COLUMN_CONFERENCE_ID);
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(query, new String[]{String.valueOf(User.ROLE_DOCTOR),
                String.valueOf(confId)});

        List<User> doctors = new ArrayList<>(cursor.getCount());
        while (cursor.moveToNext()) {
            doctors.add(UserMapper.userFromCursor(cursor));
        }
        return doctors;
    }

    public void updateLoggedInUser(User user) {
        ContentValues values = UserMapper.getUpdateLastLoginContentValues();
        dbHelper.getWritableDatabase()
                .update(UserTable.TABLE_NAME, values, UserTable.COLUMN_ID + " = ?",
                        new String[]{String.valueOf(user.getId())});
    }

    public List<Conference> getUpcomingConferencesForAdmin(int adminId) {
        try {
            String query = String.format("%s = ? AND %s >= ? AND %s = ?",
                    ConferenceTable.COLUMN_ADMIN_ID, ConferenceTable.COLUMN_DATE, ConferenceTable.COLUMN_CANCELLED);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            Cursor cursor = db.query(ConferenceTable.TABLE_NAME, null,
                    query, new String[]{String.valueOf(adminId), String.valueOf(getTodayTimeMillis()), "0"},
                    null, null, ConferenceTable.COLUMN_DATE + " DESC");

            List<Conference> conferences = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext()) {
                conferences.add(ConferenceMapper.conferenceFromCursor(cursor));
            }
            return conferences;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteConference(Conference conference) {
        int currentUserId = new UserPreferences(MedicalConferencesApplication.getInstance()).getUserId();
        if (currentUserId != conference.getAdminId()) {
            throw new IllegalArgumentException("Current user cannot delete this conference");
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(ConferenceTable.TABLE_NAME,
                ConferenceTable.COLUMN_ID + " = ?",
                new String[]{String.valueOf(conference.getId())}) != 0;
    }

    public int insertConference(Conference conference) {
        return insert(ConferenceMapper.valuesForConference(conference), ConferenceTable.TABLE_NAME);
    }

    public int updateConference(Conference conference) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.update(ConferenceTable.TABLE_NAME, ConferenceMapper.valuesForConference(conference),
                ConferenceTable.COLUMN_ID + " = ? ", new String[]{String.valueOf(conference.getId())});
        return conference.getId();
    }

    public Conference getConferenceById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConferenceTable.TABLE_NAME, null, ConferenceTable.COLUMN_ID + " = ? ",
                new String[]{String.valueOf(id)}, null, null, null);
        cursor.moveToFirst();
        return ConferenceMapper.conferenceFromCursor(cursor);
    }

    public List<Invitation> getInvitationsForConference(int conferenceId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        final String invTable = InvitationTable.TABLE_NAME;
        final String userTable = UserTable.TABLE_NAME;
        Cursor cursor = db.rawQuery(String.format("SELECT I.%s, %s, %s, %s, %s, D.%s, D.%s FROM %s AS I JOIN %s AS D" +
                        " ON I.%s = D.%s WHERE %s = ? ORDER BY I.%s DESC", InvitationTable.COLUMN_ID,
                InvitationTable.COLUMN_ADMIN_ID, InvitationTable.COLUMN_CONFERENCE_ID, InvitationTable.COLUMN_STATE,
                InvitationTable.COLUMN_DOCTOR_ID, UserTable.COLUMN_FIRST_NAME, UserTable.COLUMN_LAST_NAME,
                invTable, userTable, InvitationTable.COLUMN_DOCTOR_ID, UserTable.COLUMN_ID,
                InvitationTable.COLUMN_CONFERENCE_ID, InvitationTable.COLUMN_UPDATED_AT),
                new String[]{String.valueOf(conferenceId)});

        List<Invitation> invitations = new ArrayList<>(cursor.getCount());
        while (cursor.moveToNext()) {
            invitations.add(InvitationMapper.getInvitationWithDoctorFromCursor(cursor));
        }
        return invitations;
    }

    public List<Topic> getConferenceTopics(int conferenceId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = String.format("SELECT T.%s, %s, %s, %s, %s, %s, %s FROM %s AS T JOIN %s AS D ON T.%s = D.%s" +
                        " WHERE %s = ? ORDER BY %s DESC", TopicTable.COLUMN_ID, TopicTable.COLUMN_DESCRIPTION,
                TopicTable.COLUMN_CREATED_AT, TopicTable.COLUMN_CREATOR_ID, TopicTable.COLUMN_CONFERENCE_ID,
                UserTable.COLUMN_FIRST_NAME, UserTable.COLUMN_LAST_NAME, TopicTable.TABLE_NAME, UserTable.TABLE_NAME,
                TopicTable.COLUMN_CREATOR_ID, UserTable.COLUMN_ID, TopicTable.COLUMN_CONFERENCE_ID,
                TopicTable.COLUMN_CREATED_AT);

        Cursor cursor = db.rawQuery(query,
                new String[]{String.valueOf(conferenceId)});

        List<Topic> topics = new ArrayList<>(cursor.getCount());
        while (cursor.moveToNext())
            topics.add(TopicMapper.topicFromCursor(cursor));
        return topics;
    }

    public int insertTopic(Topic topic) {
        return insert(TopicMapper.valuesForTopic(topic), TopicTable.TABLE_NAME);
    }

    public int insertInvitation(Invitation invitation) {
        return insert(InvitationMapper.valuesForInvitation(invitation), InvitationTable.TABLE_NAME);
    }

    int insert(ContentValues values, String table) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return (int) db.insert(table, null, values);
    }

    public List<Invitation> getPendingInvitations(int doctorId) {
        String query = String.format("SELECT I.%s, I.%s, I.%s, I.%s, I.%s, I.%s FROM %s AS I JOIN %s AS C ON I.%s = C.%s" +
                        " WHERE I.%s = ? AND I.%s = ? AND C.%s > ?", InvitationTable.COLUMN_ID, InvitationTable.COLUMN_ADMIN_ID,
                InvitationTable.COLUMN_DOCTOR_ID, InvitationTable.COLUMN_CONFERENCE_ID, InvitationTable.COLUMN_STATE,
                InvitationTable.COLUMN_UPDATED_AT, InvitationTable.TABLE_NAME, ConferenceTable.TABLE_NAME,
                InvitationTable.COLUMN_CONFERENCE_ID, ConferenceTable.COLUMN_ID, InvitationTable.COLUMN_DOCTOR_ID,
                InvitationTable.COLUMN_STATE, ConferenceTable.COLUMN_DATE);

        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(query, new String[]{String.valueOf(doctorId),
                String.valueOf(Invitation.STATE_PENDING), String.valueOf(getTodayTimeMillis())});
        List<Invitation> result = new ArrayList<>(cursor.getCount());
        while (cursor.moveToNext())
            result.add(InvitationMapper.getInvitationFromCursor(cursor));
        return result;
    }

    public List<Conference> getUpcomingConferencesForDoctor(int doctorId) {
        String query = String.format("SELECT C.%s, C.%s, C.%s, C.%s, C.%s, I.%s, I.%s FROM %s AS C JOIN %s AS I " +
                        "ON C.%s = I.%s WHERE %s = ? AND %s = ? AND %s > ? ORDER BY C.%s",
                ConferenceTable.COLUMN_ID, ConferenceTable.COLUMN_ADMIN_ID, ConferenceTable.COLUMN_NAME,
                ConferenceTable.COLUMN_DATE, ConferenceTable.COLUMN_CANCELLED, InvitationTable.COLUMN_STATE,
                InvitationTable.COLUMN_UPDATED_AT, ConferenceTable.TABLE_NAME, InvitationTable.TABLE_NAME,
                ConferenceTable.COLUMN_ID, InvitationTable.COLUMN_CONFERENCE_ID, InvitationTable.COLUMN_DOCTOR_ID,
                ConferenceTable.COLUMN_CANCELLED, ConferenceTable.COLUMN_DATE, ConferenceTable.COLUMN_DATE);
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(query,
                new String[]{String.valueOf(doctorId), "0", String.valueOf(getTodayTimeMillis())});
        List<Conference> conferences = new ArrayList<>();
        while (cursor.moveToNext())
            conferences.add(ConferenceMapper.conferenceWithInvitationFromCursor(cursor, doctorId));
        return conferences;

    }

    public boolean updateDoctorInvitation(int doctorId, int conferenceId, int state) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        return db.update(InvitationTable.TABLE_NAME, InvitationMapper.updateValues(state),
                String.format("%s = ? AND %s = ?", InvitationTable.COLUMN_DOCTOR_ID, InvitationTable.COLUMN_CONFERENCE_ID),
                new String[]{String.valueOf(doctorId), String.valueOf(conferenceId)}) == 1;

    }

    long getTodayTimeMillis() {
        Calendar today = Calendar.getInstance();
        today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE), 0, 0);
        return today.getTimeInMillis();
    }
}
