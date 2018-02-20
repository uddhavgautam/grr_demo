package com.gaute.grrnannyandroid.broadcastreceiver;

import android.content.Context;
import android.content.Intent;

import com.gaute.grrnannyandroid.activity.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;

/**
 * Created by gaute on 2/19/18.
 */

@RunWith(MockitoJUnitRunner.class)
public class BroadcastReceiverStartOnBootTest {

    @Mock
    private Context context;

    @Mock
    private MainActivity mainActivity;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void onReceive() /* has Intent and Context as parameter. Mock (Inject) both */ throws Exception {
        //Check order: checks if Intent is defined first and then context is used to start service from that defined intent
        //Intent needs Activity. Mock (inject) Activity

        Intent intentMainActivity = new Intent(context, mainActivity.getClass());
        assertNotNull(intentMainActivity);
        context.startActivity(intentMainActivity);

    }

}