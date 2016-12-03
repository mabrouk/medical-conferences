package com.mabrouk.medicalconferences.persistance.sqlite;

import android.content.ContentValues;
import android.database.Cursor;

import com.mabrouk.medicalconferences.model.Conference;

/**
 * Created by User on 12/2/2016.
 */

public class ConferenceMapper {
    public static Conference conferenceFromCursor(Cursor cursor) {
        return new Conference(cursor.getInt(cursor.getColumnIndex(ConferenceTable.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(ConferenceTable.COLUMN_NAME)),
                cursor.getInt(cursor.getColumnIndex(ConferenceTable.COLUMN_ADMIN_ID)),
                cursor.getLong(cursor.getColumnIndex(ConferenceTable.COLUMN_DATE)),
                cursor.getInt(cursor.getColumnIndex(ConferenceTable.COLUMN_CANCELLED)) == 1,
                cursor.getLong(cursor.getColumnIndex(ConferenceTable.COLUMN_UPDATED_AT)));
    }

    public static ContentValues valuesForConference(Conference conference) {
        ContentValues values = new ContentValues();
        values.put(ConferenceTable.COLUMN_ADMIN_ID, conference.getAdminId());
        values.put(ConferenceTable.COLUMN_NAME, conference.getName());
        values.put(ConferenceTable.COLUMN_DATE, conference.getDateTimestamp());
        values.put(ConferenceTable.COLUMN_UPDATED_AT, conference.getUpdatedAtTimestamp());
        values.put(ConferenceTable.COLUMN_CANCELLED, conference.isCancelled() ? 1 : 0);
        return values;
    }

    private ConferenceMapper() {
        throw new IllegalStateException("Shouldn't instantiate instances of a Mapper, rather use static methods");
    }
}
