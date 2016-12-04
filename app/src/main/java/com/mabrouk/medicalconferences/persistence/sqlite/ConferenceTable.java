package com.mabrouk.medicalconferences.persistence.sqlite;

/**
 * Created by User on 12/2/2016.
 */

public class ConferenceTable {
    public static final String TABLE_NAME = "Conference";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_ADMIN_ID = "admin_id";
    public static final String COLUMN_CANCELLED = "cancelled";
    public static final String COLUMN_UPDATED_AT = "updated_at";

    public static final String CREATE_STATEMENT = "CREATE TABLE " + TABLE_NAME + "( "
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME + " TEXT, "
            + COLUMN_ADMIN_ID + " INTEGER, " + COLUMN_DATE + " INTEGER, " + COLUMN_CANCELLED
            + " INTEGER, " + COLUMN_UPDATED_AT + " INTEGER, "
            + "FOREIGN KEY ( " + COLUMN_ADMIN_ID + " ) REFERENCES " + UserTable.TABLE_NAME
            + " ( " + UserTable.COLUMN_ID + " )" + " );";
}
