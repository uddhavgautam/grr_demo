package com.gaute.grrnannyandroid.activity;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.gaute.grrnannyandroid.Util;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private String TAG = this.getClass().getSimpleName();
    private MainActivityKiller mainActivityKiller;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        setContentView(R.layout.activity_main); /* note, I can't write this line with Transparent theme */
        Log.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
//
//        Intent intentServiceGrrClientAndroid = new Intent(this, ServiceHeartBeatChecker.class);
//        startService(intentServiceGrrClientAndroid);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mainActivityKiller = new MainActivityKiller();
        registerReceiver(mainActivityKiller, new IntentFilter("destroy_activity_grr_nanny_android"));

        //start AlarmManager
        Util util = new Util();
        util.schedulerWork(getApplicationContext());
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mainActivityKiller);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /* MainActivityKiller kills transparent MainActivity so that user don't need to manually swipe out transparent activity */
    public class MainActivityKiller extends BroadcastReceiver {
        private String TAG = this.getClass().getSimpleName();

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.equals(intent.getAction(), "destroy_activity_grr_nanny_android")) {
                Log.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
                MainActivity.this.finishAndRemoveTask(); /* Another hacked */
            }
        }
    }
}
