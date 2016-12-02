package com.mabrouk.medicalconferences.persistance.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mabrouk.medicalconferences.model.User;

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
}
