package com.mabrouk.medicalconferences.persistance.sqlite;

import android.content.ContentValues;
import android.database.Cursor;

import com.mabrouk.medicalconferences.model.Invitation;
import com.mabrouk.medicalconferences.model.User;

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

    public static Invitation getInvitationWithDoctorFromCursor(Cursor cursor) {
        Invitation invitation = new Invitation(cursor.getInt(cursor.getColumnIndex(InvitationTable.COLUMN_ID)),
                cursor.getInt(cursor.getColumnIndex(InvitationTable.COLUMN_ADMIN_ID)),
                cursor.getInt(cursor.getColumnIndex(InvitationTable.COLUMN_CONFERENCE_ID)),
                cursor.getInt(cursor.getColumnIndex(InvitationTable.COLUMN_DOCTOR_ID)),
                cursor.getInt(cursor.getColumnIndex(InvitationTable.COLUMN_STATE)),
                0);
        invitation.setDoctor(new User(invitation.getDoctorId(), "",
                cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_FIRST_NAME)),
                cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_LAST_NAME)),
                "", User.ROLE_DOCTOR, 0));
        return invitation;
    }

    private InvitationMapper() {
        throw new IllegalStateException("Shouldn't instantiate instances of a Mapper, rather use static methods");
    }
}
