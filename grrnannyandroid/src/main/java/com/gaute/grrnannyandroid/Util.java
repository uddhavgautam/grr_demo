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

    public Util() {
    }

    public void scheduleWork(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intentServiceGrrClientAndroid = new Intent(context, ServiceHeartBeatChecker.class);
        PendingIntent pi = PendingIntent.getService(context, 0, intentServiceGrrClientAndroid, 0);
        assert alarmManager != null; //catch exception on the class that calls it
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), Constants.HEARTBEAT_CHECK_INTERVAL, pi);
    }
}