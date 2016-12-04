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

    private static void insertUser(SQLiteDatabase sqLiteDatabase, User user) {
        ContentValues values = UserMapper.valuesForUser(user);

        sqLiteDatabase.insert(UserTable.TABLE_NAME, null, values);
    }
}
