package com.gaute.grrnannyandroid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.gaute.grrnannyandroid.service.ServiceHeartBeatChecker;

/**
 * Created by gaute on 2/9/18.
 */

public class Util {
    private String TAG = this.getClass().getSimpleName();

    private int JOB_ID = 1000;
    private long Interval = 10000; // 10 seconds

    public Util() {
    }

    public void schedulerWork(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intentServiceGrrClientAndroid = new Intent(context, ServiceHeartBeatChecker.class);

        PendingIntent pi = PendingIntent.getService(context, 0, intentServiceGrrClientAndroid, 0);

        assert alarmManager != null;
        alarmManager.setRepeating(AlarmManager.RTC, 100, 10000, pi);

    }
}