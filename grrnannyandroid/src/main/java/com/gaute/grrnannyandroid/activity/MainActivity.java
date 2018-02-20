package com.gaute.grrnannyandroid.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.gaute.grrnannyandroid.Util;

public class MainActivity extends AppCompatActivity {
    private String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startAlarmManager();
        finishAndRemoveTask(); //since the startAlarmManager ultimately calls async calls, I can immediately call finishAndRemoveTask
    }

    private void startAlarmManager() {
        Util util = new Util();
        util.scheduleWork(getApplicationContext());
    }
}
