package com.mabrouk.medicalconferences.persistence.sqlite;

/**
 * Created by User on 12/2/2016.
 */

public class UserTable {
    public static final String TABLE_NAME = "User";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_ROLE = "role";
    public static final String COLUMN_LAST_LOGIN_TIMESTAMP = "last_login";

    public static final String CREATE_STATEMENT = "CREATE TABLE " + TABLE_NAME + "( "
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_USER_EMAIL + " TEXT UNIQUE, " + COLUMN_PASSWORD
            + " TEXT, " + COLUMN_FIRST_NAME + " TEXT, " + COLUMN_LAST_NAME + " TEXT, "
            + COLUMN_ROLE + " INTEGER, " + COLUMN_LAST_LOGIN_TIMESTAMP + " BIGINT " + ");";
}
