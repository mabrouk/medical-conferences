package com.mabrouk.medicalconferences.persistence.sqlite;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.mabrouk.medicalconferences.model.Conference;
import com.mabrouk.medicalconferences.model.Invitation;
import com.mabrouk.medicalconferences.model.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class InvitationDBOperationTest {
    long today = new Date().getTime();
    long day = 1000 * 60 * 60 * 24;
    Context appContext;
    Conference conference;

    @Before
    public void init() {
        appContext = InstrumentationRegistry.getTargetContext();

        DBWrapper.initForTesting(appContext);
        Conference temp = new Conference(0, "Conf 1", 1, today, false, today);
        int id = DBWrapper.getInstance().insertConference(temp);
        conference = new Conference(id, "Conf 1", 1, today, false, today);
    }

    @Test
    public void testInsert() {
        int id = insertInvitation(3, today);
        assertEquals(1, id);
    }

    @Test
    public void testQueryConferenceInvitations() {
        insertInvitation(3, today);
        insertInvitation(5, today + day);
        insertInvitation(4, today + 2 * day);

        List<Invitation> invitations = DBWrapper.getInstance().getInvitationsForConference(conference.getId());
        assertEquals(3, invitations.size());
        assertEquals(4, invitations.get(0).getDoctorId());
    }

    @Test
    public void testQueryNotInvitedDoctors() {
        insertInvitation(4, today);
        insertInvitation(7, today + day);

        List<User> nonInvitedDoctors = DBWrapper.getInstance().getNotInvitedDoctors(conference.getId());
        assertEquals(3, nonInvitedDoctors.size());
        assertFalse(contains(nonInvitedDoctors, 7));
        assertFalse(contains(nonInvitedDoctors, 4));
    }

    boolean contains(List<User> users, int id) {
        for(User user : users)
            if(user.getId() == id)
                return true;
        return false;
    }

    int insertInvitation(int doctorId, long timestamp) {
        Invitation invitation = new Invitation(0, 1, doctorId, conference.getId(), Invitation.STATE_PENDING,timestamp);
        return DBWrapper.getInstance().insertInvitation(invitation);
    }


}
