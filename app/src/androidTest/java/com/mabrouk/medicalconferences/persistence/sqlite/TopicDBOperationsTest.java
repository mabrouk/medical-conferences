package com.mabrouk.medicalconferences.persistence.sqlite;

/**
 * Created by VPN on 12/4/2016.
 */
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.mabrouk.medicalconferences.model.Conference;
import com.mabrouk.medicalconferences.model.Topic;
import com.mabrouk.medicalconferences.model.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class TopicDBOperationsTest {
    long today = new Date().getTime();
    long day = 1000 * 60 * 60 * 24;
    Context appContext;
    Conference conference;
    User doctor;

    @Before
    public void init() {
        appContext = InstrumentationRegistry.getTargetContext();

        DBWrapper.initForTesting(appContext);
        Conference temp = new Conference(0, "Conf 1", 1, today, false, today);
        int id = DBWrapper.getInstance().insertConference(temp);
        conference = new Conference(id, "Conf 1", 1, today, false, today);

        doctor = DBWrapper.getInstance().getUserById(3);
    }

    @Test
    public void testInsertTopic() {
        int id = insertTopic("topic", today);
        assertEquals(1, id);
    }

    int insertTopic(String title, long timestamp) {
        Topic topic = new Topic(0, title, timestamp, doctor.getId(), conference.getId());
        return DBWrapper.getInstance().insertTopic(topic);
    }

    @Test
    public void testQueryConferenceTopics() {
        insertTopic("t1", today);
        insertTopic("t2", today + day);
        insertTopic("t3", today + 2 * day);
        insertTopic("t4", today + 3 * day);

        List<Topic> topics = DBWrapper.getInstance().getConferenceTopics(conference.getId());
        assertEquals(4, topics.size());
        assertTrue(topics.get(0).getDescription().equals("t4"));
        assertTrue(topics.get(3).getDescription().equals("t1"));
    }
}
