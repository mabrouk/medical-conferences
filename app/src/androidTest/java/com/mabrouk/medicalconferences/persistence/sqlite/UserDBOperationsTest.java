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

import static org.junit.Assert.*;
import java.util.Date;
import java.util.List;

/**
 * Created by VPN on 12/4/2016.
 */

@RunWith(AndroidJUnit4.class)
public class UserDBOperationsTest {
    long today = new Date().getTime();
    long day = 1000 * 60 * 60 * 24;
    Context appContext;
    @Before
    public void init() {
        appContext = InstrumentationRegistry.getTargetContext();

        DBWrapper.initForTesting(appContext);
    }

    @Test
    public void testLoginSuccess() {
        User user = DBWrapper.getInstance().getUser("dr1@mabrouk.com", "123456");
        assertNotNull(user);
    }

    @Test
    public void testLoginFail() {
        User user = DBWrapper.getInstance().getUser("dr6@mabourk.com", "123456");
        assertNull(user);
    }

    @Test
    public void testGetPendingInvitations() {
        User doctor = DBWrapper.getInstance().getUserById(3);

        int expectedFound1 = insertConferenceAndInvitationToIt(doctor.getId(), today + day, false);
        int expectedNotFound1 = insertConferenceAndInvitationToIt(doctor.getId(), today - day, false);  //shouldn't exist for it ended
        int expectedFound2 = insertConferenceAndInvitationToIt(doctor.getId(), today + 2 * day, false);
        int expectedNotFound2 = insertConferenceAndInvitationToIt(doctor.getId(), today + 3 * day, true);  //shouldn't exist cancel

        List<Invitation> pending = DBWrapper.getInstance().getPendingInvitations(doctor.getId());
        assertEquals(2, pending.size());
        assertTrue(contains(pending, expectedFound1));
        assertTrue(contains(pending, expectedFound2));
        assertFalse(contains(pending, expectedNotFound1));
        assertFalse(contains(pending, expectedNotFound2));
    }

    boolean contains(List<Invitation> invitations, int id) {
        for(Invitation invitation : invitations)
            if(invitation.getId() == id)
                return true;
        return false;
    }

    int insertConferenceAndInvitationToIt(int doctorId, long timestamp, boolean canceled) {
        Conference conference = new Conference(0, "Conf", 1, timestamp, canceled, timestamp);
        int confId = DBWrapper.getInstance().insertConference(conference);

        Invitation invitation = new Invitation(0, 1, doctorId, confId, Invitation.STATE_PENDING, today - day);
        int id = DBWrapper.getInstance().insertInvitation(invitation);
        return id;
    }
}
