package com.gaute.grrnannyandroid.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.gaute.grrnannyandroid.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ServiceHeartBeatChecker extends Service {
    private String TAG = this.getClass().getSimpleName();


    private BufferedReader bufferedReader;
    private File sharedFile;

    private boolean killed;


    public ServiceHeartBeatChecker() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //start AlarmManager
        Util util = new Util();
        util.schedulerWork(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1000, new Notification());
        /* holds wake lock for long time and battery drain
        notification comes there */
        super.onStartCommand(intent, flags, startId);

        //kill the transparent MainActivity
        if (!killed) {
            sendBroadcast(new Intent("destroy_activity_grr_nanny_android"));
            killed = true;
        }

        File filesDir = getApplicationContext().getExternalFilesDir("filess");
        //create a sharedFile
        sharedFile = new File(filesDir, "sharedFile.txt");
        try {
            sharedFile.createNewFile();
        } catch (IOException e) {
            System.out.println("can't create sharedFile!");
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (needRestart(sharedFile)/* checks if GrrClientAndroid restart is needed based on shared file */) {
                    //restart Grr_client_android
                    restartGrrClientAndroid();
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();

        return START_REDELIVER_INTENT;
    }

    private boolean needRestart(File file) {

        //read the file
        String line = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));

            if ((line = bufferedReader.readLine()) != null) {
                if (System.currentTimeMillis() - Long.parseLong(line.split(".")[0]) > 25000 /* unresponsive time */) {
                    /* if unresponsive, restart Grr_client_android */
                    return true;
                }
            } else /* if nothing written in the file */ {
                //restart Grr_client_android
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException ignored) {
                ignored.printStackTrace();
            }
        }
        return false;
    }

    private void restartGrrClientAndroid() {

        //using monkey tool
        //adb shell monkey -p com.gaute.grrnannyandroid -c android.intent.category.LAUNCHER 1
        //adb shell monkey -p com.gaute.grrclientandroid -c android.intent.category.LAUNCHER 1

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Log.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());

                    //am means activity manager
                    //can get "Activity class doesn't exists" error if the grr_client_android process is totally destroyed by GC */

            /* Note: This below 3 lines command works from Computer. Not from the android phone.
            String[] cmdline = { "sh", "-c", "adb shell monkey -p com.gaute.grrclientandroid -c android.intent.category.LAUNCHER 1" };
            String cmdline = "adb shell monkey -p com.gaute.grrclientandroid -c android.intent.category.LAUNCHER 1";
            Runtime.getRuntime().exec(cmdline); */

            /*Working 1: cmdline = "monkey -p com.gaute.grrclientandroid -c android.intent.category.LAUNCHER 1"
            Working 2: cmdline = "am start -n com.gaute.grrclientandroid/com.gaute.grrclientandroid.activity.MainActivity"
            Working 3: cmdline = "am start -n com.gaute.grrclientandroid/.activity.MainActivity"
            working 4: cmdline = "am start -c android.intent.category.LAUNCHER -a android.intent.action.MAIN -n com.gaute.grrclientandroid/com.gaute.grrclientandroid.activity.MainActivity"
            */
                    String cmdline1 = "am stop -n com.gaute.grrclientandroid/.activity.MainActivity";
                    Runtime.getRuntime().exec(cmdline1); //returns a process

                    try {
                        Thread.currentThread().sleep(10000 /* resurrection period */);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    String cmdline = "am start -n com.gaute.grrclientandroid/.activity.MainActivity";
                    Runtime.getRuntime().exec(cmdline); //returns another process

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();


    }
}
