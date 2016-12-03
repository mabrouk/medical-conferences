package com.mabrouk.medicalconferences.persistance.sqlite;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by User on 12/2/2016.
 */

public class DBOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "med.db";
    private static final int DB_VERSION = 1;

    public DBOpenHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DB_NAME, factory, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(UserTable.CREATE_STATEMENT);
            db.execSQL(ConferenceTable.CREATE_STATEMENT);
            db.execSQL(TopicTable.CREATE_STATEMENT);
            db.execSQL(InvitationTable.CREATE_STATEMENT);
            DBInitialData.populateDB(db);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
