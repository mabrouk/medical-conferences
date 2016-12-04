package com.mabrouk.medicalconferences.persistence.sqlite;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.mabrouk.medicalconferences.model.Conference;
import com.mabrouk.medicalconferences.model.User;
import com.mabrouk.medicalconferences.persistence.preferences.UserPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.util.Date;

/**
 * Created by VPN on 12/4/2016.
 */
@RunWith(AndroidJUnit4.class)
public class ConferenceDBOperationsTest {
    long today = new Date().getTime();
    Context appContext;
    @Before
    public void init() {
        appContext = InstrumentationRegistry.getTargetContext();

        DBWrapper.initForTesting(appContext);
    }

    @Test
    public void testInsert() {
        Conference conference = new Conference(0, "Conf 1", 1, today, false, today);
        long id = DBWrapper.getInstance().insertConference(conference);
        assertEquals(1, id);
    }

    @Test
    public void testConferencesForAdmin() {
        Conference conference = new Conference(0, "Conf 1", 1, today, false, today);
        DBWrapper.getInstance().insertConference(conference);
        conference = new Conference(0, "Conf 2", 1, today, false, today);
        DBWrapper.getInstance().insertConference(conference);
        conference = new Conference(0, "Conf 3", 2, today, false, today);
        DBWrapper.getInstance().insertConference(conference);

        int size = DBWrapper.getInstance().getUpcomingConferencesForAdmin(1).size();
        assertEquals(2, size);
    }

    @Test
    public void testCancel() {
        //make sure we're the right admin
        new UserPreferences(appContext).login(1, User.ROLE_ADMIN);

        Conference conference = new Conference(0, "Conf 1", 1, today, false, today);
        DBWrapper.getInstance().insertConference(conference);
        conference = new Conference(0, "Conf 2", 1, today, false, today);
        int id = DBWrapper.getInstance().insertConference(conference);
        conference = new Conference(id, "Conf 2", 1, today, false, today);

        boolean b = DBWrapper.getInstance().cancelConference(conference);
        assertTrue(b);

        Conference canceled = DBWrapper.getInstance().getConferenceById(conference.getId());
        assertTrue(canceled.isCancelled());

        int size = DBWrapper.getInstance().getUpcomingConferencesForAdmin(1).size();
        assertEquals(1, size);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCancelWithWrongAdmin() {
        Conference conference = new Conference(0, "Conf 1", 1, today, false, today);
        DBWrapper.getInstance().insertConference(conference);

        //make sure the loggedin admin isn't responsible for the conference
        new UserPreferences(appContext).login(2, User.ROLE_ADMIN);

        //throws Illegal argument exception
        DBWrapper.getInstance().cancelConference(conference);
    }

    @Test
    public void testEdit() {
        Conference conference = new Conference(0, "Conf 1", 1, today, false, today);
        long id = DBWrapper.getInstance().insertConference(conference);
        Conference updated = new Conference((int) id, "updated", 1, today, false, new Date().getTime());

        int affected = DBWrapper.getInstance().updateConference(updated);
        assertEquals(1, affected);

        conference = DBWrapper.getInstance().getConferenceById(updated.getId());
        assertTrue(conference.getName().equals(updated.getName()));
    }

    //TODO test confrences for dr
}
