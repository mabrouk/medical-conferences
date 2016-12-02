package com.mabrouk.medicalconferences.persistance.sqlite;

import android.content.ContentValues;
import android.database.Cursor;

import com.mabrouk.medicalconferences.model.User;

import java.util.Date;

/**
 * Created by User on 12/2/2016.
 */

public class UserMapper {
    public static User userFromCursor(Cursor cursor) {
        return new User(cursor.getInt(cursor.getColumnIndex(UserTable.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_USER_EMAIL)),
                cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_FIRST_NAME)),
                cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_LAST_NAME)),
                cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_PASSWORD)),
                cursor.getInt(cursor.getColumnIndex(UserTable.COLUMN_ROLE)),
                cursor.getLong(cursor.getColumnIndex(UserTable.COLUMN_LAST_LOGIN_TIMESTAMP)));
    }

    public static ContentValues getUpdateLastLoginContentValues() {
        ContentValues values = new ContentValues();
        values.put(UserTable.COLUMN_LAST_LOGIN_TIMESTAMP, new Date().getTime());
        return values;
    }
    private UserMapper() {
        throw new IllegalStateException("Shouldn't instantiate instances of UserMapper, rather use static methods");
    }
}
