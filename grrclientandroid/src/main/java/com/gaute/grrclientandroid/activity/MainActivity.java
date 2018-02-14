package com.gaute.grrclientandroid.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.gaute.grrclientandroid.service.ServiceBackgroundGrrClientAndroid;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private String TAG = this.getClass().getSimpleName();
    private MainActivityKiller mainActivityKiller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivityKiller = new MainActivityKiller();
        registerReceiver(mainActivityKiller, new IntentFilter("destroy_activity_grr_client_android"));


//        setContentView(R.layout.activity_main); /* note, I can't write this line with Transparent theme */

//        Log.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());



        /*Note: For heavy duty services, OS alerts to force-stop */
        /*Problem1: our service must not be heavy duty */

    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intentServiceGrrClientAndroid = new Intent(this, ServiceBackgroundGrrClientAndroid.class);
        startService(intentServiceGrrClientAndroid); //async call. Therefore, no blocking on UI thread
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mainActivityKiller);
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
            if (Objects.equals(intent.getAction(), "destroy_activity_grr_client_android")) {
                Log.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
                MainActivity.this.finishAndRemoveTask(); /* Another hacked */
                try {
                    this.finalize();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }
    }

}
