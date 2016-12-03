package com.mabrouk.medicalconferences.persistance.sqlite;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.mabrouk.medicalconferences.model.Conference;
import com.mabrouk.medicalconferences.model.User;

import java.util.Date;

/**
 * Created by User on 12/2/2016.
 */

public class DBInitialData {
    public static void populateDB(SQLiteDatabase db) {
        createUsers(db);
        createConfs(db);
    }

    public static void createUsers(SQLiteDatabase sqLiteDatabase) {
        insertUser(sqLiteDatabase,
                new User(0, "admin1@mabrouk.com", "John", "Doe", "123456", User.ROLE_ADMIN, new Date().getTime()));
        insertUser(sqLiteDatabase,
                new User(0, "admin2@mabrouk.com", "Jane", "Doe", "123456", User.ROLE_ADMIN, new Date().getTime()));

        insertUser(sqLiteDatabase,
                new User(0, "dr1@mabrouk.com", "Tom", "Doe", "123456", User.ROLE_DOCTOR, new Date().getTime()));
        insertUser(sqLiteDatabase,
                new User(0, "dr2@mabrouk.com", "Kim", "Doe", "123456", User.ROLE_DOCTOR, new Date().getTime()));
        insertUser(sqLiteDatabase,
                new User(0, "dr3@mabrouk.com", "Ali", "Doe", "123456", User.ROLE_DOCTOR, new Date().getTime()));
        insertUser(sqLiteDatabase,
                new User(0, "dr4@mabrouk.com", "Anna", "Doe", "123456", User.ROLE_DOCTOR, new Date().getTime()));
        insertUser(sqLiteDatabase,
                new User(0, "dr5@mabrouk.com", "Sara", "Doe", "123456", User.ROLE_DOCTOR, new Date().getTime()));

    }
    
    public static void createConfs(SQLiteDatabase db) {
        int adminId = 1;
        long day = 24 * 60 * 60 * 1000;
        insertConference(db, ConferenceMapper.valuesForConference(new Conference(1, "Conf 1", adminId, new Date().getTime() + day, false, new Date().getTime())));
        insertConference(db, ConferenceMapper.valuesForConference(new Conference(2, "Conf 2", adminId, new Date().getTime() + day, false, new Date().getTime())));
        insertConference(db, ConferenceMapper.valuesForConference(new Conference(3, "Conf 3", adminId, new Date().getTime(), false, new Date().getTime())));
        insertConference(db, ConferenceMapper.valuesForConference(new Conference(4, "Conf 4", adminId, new Date().getTime()+ day, false, new Date().getTime())));
        insertConference(db, ConferenceMapper.valuesForConference(new Conference(5, "Conf 5", adminId, new Date().getTime(), false, new Date().getTime())));
        insertConference(db, ConferenceMapper.valuesForConference(new Conference(6, "Conf 6", adminId, new Date().getTime()+ day, false, new Date().getTime())));
        insertConference(db, ConferenceMapper.valuesForConference(new Conference(7, "Conf 7", adminId, new Date().getTime()+ day, true, new Date().getTime())));
        insertConference(db, ConferenceMapper.valuesForConference(new Conference(8, "Conf 8", adminId, new Date().getTime()+ day, false, new Date().getTime())));
        insertConference(db, ConferenceMapper.valuesForConference(new Conference(9, "Conf 9", adminId, new Date().getTime()+ day, false, new Date().getTime())));
        insertConference(db, ConferenceMapper.valuesForConference(new Conference(10, "Conf 10", adminId, new Date().getTime()+ day, false, new Date().getTime())));
    }
    
    private static void insertConference(SQLiteDatabase db, ContentValues values) {
        db.insert(ConferenceTable.TABLE_NAME, null, values);
    }

    private static void insertUser(SQLiteDatabase sqLiteDatabase, User user) {
        ContentValues values = new ContentValues();
        values.put(UserTable.COLUMN_FIRST_NAME, user.getFirstName());
        values.put(UserTable.COLUMN_LAST_NAME, user.getLastName());
        values.put(UserTable.COLUMN_PASSWORD, user.getPassword());
        values.put(UserTable.COLUMN_USER_EMAIL, user.getEmail());
        values.put(UserTable.COLUMN_ROLE, user.getRole());
        values.put(UserTable.COLUMN_LAST_LOGIN_TIMESTAMP, user.getLastLoginTimestamp());

        sqLiteDatabase.insert(UserTable.TABLE_NAME, null, values);
    }
}
