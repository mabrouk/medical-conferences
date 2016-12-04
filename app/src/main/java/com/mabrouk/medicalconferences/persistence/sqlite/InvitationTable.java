package com.mabrouk.medicalconferences.persistence.sqlite;

/**
 * Created by User on 12/2/2016.
 */

public class InvitationTable {
    public static final String TABLE_NAME = "Invitation";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ADMIN_ID = "admin_id";
    public static final String COLUMN_DOCTOR_ID = "doctor_id";
    public static final String COLUMN_CONFERENCE_ID = "conference_id";
    public static final String COLUMN_STATE = "state";
    public static final String COLUMN_UPDATED_AT = "updated_at";

    public static final String CREATE_STATEMENT = "CREATE TABLE " + TABLE_NAME + " ( " + COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_DOCTOR_ID + " INTEGER, "
            + COLUMN_CONFERENCE_ID + " INTEGER, " + COLUMN_ADMIN_ID + " INTEGER, " + COLUMN_STATE
            + " INTEGER, " + COLUMN_UPDATED_AT + " INTEGER, "
            + "FOREIGN KEY ( " + COLUMN_DOCTOR_ID + " ) REFERENCES " + UserTable.TABLE_NAME
            + " ( " + UserTable.COLUMN_ID + " ), "
            + "FOREIGN KEY ( " + COLUMN_ADMIN_ID + " ) REFERENCES " + UserTable.TABLE_NAME
            + " ( " + UserTable.COLUMN_ID + " ), "
            + "FOREIGN KEY ( " + COLUMN_CONFERENCE_ID + " ) REFERENCES " + ConferenceTable.TABLE_NAME
            + " ( " + ConferenceTable.COLUMN_ID + " )" + ");";
}
