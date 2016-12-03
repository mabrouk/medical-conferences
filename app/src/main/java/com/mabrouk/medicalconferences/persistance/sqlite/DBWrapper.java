package com.mabrouk.medicalconferences.persistance.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mabrouk.medicalconferences.MedicalConferencesApplication;
import com.mabrouk.medicalconferences.model.Conference;
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
        if(instance == null) {
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
        if(cursor.getCount() == 0)
            return null;
        cursor.moveToFirst();
        return UserMapper.userFromCursor(cursor);
    }

    public void updateLoggedInUser(User user) {
        ContentValues values = UserMapper.getUpdateLastLoginContentValues();
        try {
            dbHelper.getWritableDatabase()
                    .update(UserTable.TABLE_NAME, values, UserTable.COLUMN_ID + " = ?",
                            new String[]{ String.valueOf(user.getId()) });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Conference> getUpcomingConferencesForAdmin(int adminId) {
        try {
            String query = String.format("%s = ? AND %s >= ? AND %s = ?",
                    ConferenceTable.COLUMN_ADMIN_ID, ConferenceTable.COLUMN_DATE, ConferenceTable.COLUMN_CANCELLED);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Calendar today = Calendar.getInstance();
            today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE), 0, 0);

            Cursor cursor = db.query(ConferenceTable.TABLE_NAME, null,
                    query, new String[] {String.valueOf(adminId), String.valueOf(today.getTimeInMillis()), "0"},
                    null, null, ConferenceTable.COLUMN_DATE + " DESC");

            List<Conference> conferences = new ArrayList<>(cursor.getCount());
            while(cursor.moveToNext()) {
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
        if(currentUserId != conference.getAdminId()) {
            throw new IllegalArgumentException("Current user cannot delete this conference");
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(ConferenceTable.TABLE_NAME,
                ConferenceTable.COLUMN_ID + " = ?",
                new String[] {String.valueOf(conference.getId())}) != 0;
    }

    public int insertConference(Conference conference) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return (int) db.insert(ConferenceTable.TABLE_NAME, null, ConferenceMapper.valuesForConference(conference));
    }

    public int updateConference(Conference conference) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.update(ConferenceTable.TABLE_NAME, ConferenceMapper.valuesForConference(conference),
                ConferenceTable.COLUMN_ID + " = ? ", new String[] {String.valueOf(conference.getId())});
        return conference.getId();
    }

    public Conference getConferenceById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ConferenceTable.TABLE_NAME, null, ConferenceTable.COLUMN_ID + " = ? ",
                new String[] {String.valueOf(id)}, null, null, null);
        cursor.moveToFirst();
        return ConferenceMapper.conferenceFromCursor(cursor);
    }
}
