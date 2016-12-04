package com.mabrouk.medicalconferences.adapters;

import com.mabrouk.medicalconferences.model.Conference;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by VPN on 12/4/2016.
 */

public class ConferencesAdapterTest {
    List<Conference> conferenceList;
    long today = new Date().getTime();
    long day = 1000 * 60 * 60 * 24;

    @Before
    public void setup() {
        conferenceList = new ArrayList<>();

        //today +3 mock
        Conference mockConf = mock(Conference.class);
        when(mockConf.getDateTimestamp()).thenReturn(today + 3 * day);
        conferenceList.add(mockConf);

        //today +2 mock
        mockConf = mock(Conference.class);
        when(mockConf.getDateTimestamp()).thenReturn(today + 2 * day);
        conferenceList.add(mockConf);

        //today +1 mock
        mockConf = mock(Conference.class);
        when(mockConf.getDateTimestamp()).thenReturn(today + day);
        conferenceList.add(mockConf);

        //today mock
        mockConf = mock(Conference.class);
        when(mockConf.getDateTimestamp()).thenReturn(today);
        conferenceList.add(mockConf);
    }

    @Test
    public void testNewConfOutStart() {
        Conference mocked = mock(Conference.class);
        when(mocked.getDateTimestamp()).thenReturn(today + 4 * day);

        int index = ConferencesAdapter.conferenceIndexBasedOnDate(mocked, conferenceList);
        assertThat(index).isEqualTo(0);
    }


    @Test
    public void testNewConfOutEnd() {
        Conference mocked = mock(Conference.class);
        when(mocked.getDateTimestamp()).thenReturn(today - day);

        int index = ConferencesAdapter.conferenceIndexBasedOnDate(mocked, conferenceList);
        assertThat(index).isEqualTo(conferenceList.size());
    }

    @Test
    public void testNewConfInside() {
        Conference mocked = mock(Conference.class);
        when(mocked.getDateTimestamp()).thenReturn(today + 2 * day);

        int index = ConferencesAdapter.conferenceIndexBasedOnDate(mocked, conferenceList);
        assertThat(index).isEqualTo(1);
    }
}
