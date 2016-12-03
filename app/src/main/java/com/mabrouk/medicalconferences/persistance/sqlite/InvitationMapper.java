package com.mabrouk.medicalconferences.persistance.sqlite;

import android.content.ContentValues;
import android.database.Cursor;

import com.mabrouk.medicalconferences.model.Conference;
import com.mabrouk.medicalconferences.model.Invitation;
import com.mabrouk.medicalconferences.model.User;

import java.util.Date;

/**
 * Created by User on 12/3/2016.
 */

public class InvitationMapper {
    public static ContentValues valuesForInvitation(Invitation invitation) {
        ContentValues values = new ContentValues();
        values.put(InvitationTable.COLUMN_ADMIN_ID, invitation.getAdminId());
        values.put(InvitationTable.COLUMN_CONFERENCE_ID, invitation.getConferenceId());
        values.put(InvitationTable.COLUMN_STATE, invitation.getState());
        values.put(InvitationTable.COLUMN_DOCTOR_ID, invitation.getDoctorId());
        values.put(InvitationTable.COLUMN_UPDATED_AT, invitation.getUpdatedAtTimestamp());

        return values;
    }

    public static ContentValues updateValues(int state) {
        ContentValues values = new ContentValues();
        values.put(InvitationTable.COLUMN_STATE, state);
        values.put(InvitationTable.COLUMN_UPDATED_AT, new Date().getTime());
        return values;
    }

    public static Invitation getInvitationWithDoctorFromCursor(Cursor cursor) {
        Invitation invitation = getInvitationFromCursor(cursor);
        invitation.setDoctor(new User(invitation.getDoctorId(), "",
                cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_FIRST_NAME)),
                cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_LAST_NAME)),
                "", User.ROLE_DOCTOR, 0));
        return invitation;
    }

    public static Invitation getInvitationFromCursor(Cursor cursor) {
        long timestamp = 0;
        if(cursor.getColumnIndex(InvitationTable.COLUMN_UPDATED_AT) != -1)
            timestamp = cursor.getLong(cursor.getColumnIndex(InvitationTable.COLUMN_UPDATED_AT));

        return new Invitation(cursor.getInt(cursor.getColumnIndex(InvitationTable.COLUMN_ID)),
                cursor.getInt(cursor.getColumnIndex(InvitationTable.COLUMN_ADMIN_ID)),
                cursor.getInt(cursor.getColumnIndex(InvitationTable.COLUMN_DOCTOR_ID)),
                cursor.getInt(cursor.getColumnIndex(InvitationTable.COLUMN_CONFERENCE_ID)),
                cursor.getInt(cursor.getColumnIndex(InvitationTable.COLUMN_STATE)),
                timestamp);
    }

    private InvitationMapper() {
        throw new IllegalStateException("Shouldn't instantiate instances of a Mapper, rather use static methods");
    }
}
