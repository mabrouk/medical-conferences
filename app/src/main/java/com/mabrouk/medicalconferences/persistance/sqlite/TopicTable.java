package com.mabrouk.medicalconferences.persistance.sqlite;

/**
 * Created by User on 12/2/2016.
 */

public class TopicTable {
    public static final String TABLE_NAME = "Topic";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CREATOR_ID = "creator_id";
    public static final String COLUMN_CONFERENCE_ID = "conference_id";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CREATED_AT = "created_at";

    public static final String CREATE_STATEMENT = "CREATE TABLE " + TABLE_NAME + " ( " + COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_CREATOR_ID + " INTEGER, "
            + COLUMN_CONFERENCE_ID + " INTEGER, " + COLUMN_DESCRIPTION + " TEXT, " + COLUMN_CREATED_AT
            + " INTEGER, "
            + "FOREIGN KEY ( " + COLUMN_CREATOR_ID + " ) REFERENCES " + UserTable.TABLE_NAME
            + " ( " + UserTable.COLUMN_ID + " ), "
            + "FOREIGN KEY ( " + COLUMN_CONFERENCE_ID + " ) REFERENCES " + ConferenceTable.TABLE_NAME
            + " ( " + ConferenceTable.COLUMN_ID + " )" + ");";
}
