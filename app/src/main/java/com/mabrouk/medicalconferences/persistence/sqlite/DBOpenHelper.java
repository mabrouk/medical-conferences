package com.mabrouk.medicalconferences.persistence.sqlite;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by User on 12/2/2016.
 */

public class DBOpenHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserTable.CREATE_STATEMENT);
        db.execSQL(ConferenceTable.CREATE_STATEMENT);
        db.execSQL(TopicTable.CREATE_STATEMENT);
        db.execSQL(InvitationTable.CREATE_STATEMENT);
        DBInitialData.populateDB(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
