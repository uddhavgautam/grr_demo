package com.gaute.grrnannyandroid.service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

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
//        Util util = new Util();
//        util.schedulerWork(getApplicationContext());
    }

    /* onStartCommand is called by AlarmManager in repeat mode */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());


//        startForeground(1000, new Notification()); // not needed anymore, AlarmManager repeats this service
        /* holds wake lock for long time and battery drain
        notification comes there */

        //kill the transparent MainActivity
        if (!killed) {
            sendBroadcast(new Intent("destroy_activity_grr_nanny_android"));
            killed = true;
        }

        //shared file creation
//        File filesDir = getApplicationContext().getExternalFilesDir(".");
        //create a sharedFile

        //each app is separate virtual machine, and it has different user id.

        /* Soln1: we need shared user for two apps. However I strongly discourage doing this.
        The vast majority of apps shouldn't do it; it is only for special cases.
        Using this results in a lot of subtle differences in behavior (such as all of the apps sharing the same permissions)
        that most developers shouldn't be inflicting upon themselves.

    <manifest xmlns:android="http://schemas.android.com/apk/res/android"
    android:sharedUserLabel="@string/label_shared_user"
    android:sharedUserId="com.gaute.shareduserid"
    package="com.gaute.grrnannyandroid">


    <manifest xmlns:android="http://schemas.android.com/apk/res/android"
    android:sharedUserLabel="@string/label_shared_user"
    android:sharedUserId="com.gaute.shareduserid"
    package="com.gaute.grrclientandroid">

        Link: https://stackoverflow.com/questions/6354035/two-android-applications-with-the-same-user-id
        */

        sharedFile = new File("/storage/emulated/0/Android/data/com.gaute.grrclientandroid/files/sharedFile.txt");
        try {
            sharedFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("checkfe ", sharedFile.exists() + ""); //bug here
        if (sharedFile.exists()) {
//            try {
//                sharedFile.createNewFile();
//            } catch (IOException e) {
//                System.out.println("can't create sharedFile!");
//            }
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (needRestart(sharedFile)/* checks if GrrClientAndroid restart is needed based on shared file */) {
                        //restart Grr_client_android
                        Log.i("restart ", "restarting!");
                        restartGrrClientAndroid();
                    }
                }
            };

            Thread thread = new Thread(runnable);
            thread.start();

        } else {
            //no file
            Log.i("no file", Thread.currentThread().getStackTrace()[2].getMethodName());
            restartGrrClientAndroid();
        }

        return START_REDELIVER_INTENT;
    }

    private boolean needRestart(File file) {

        Log.i("restart ", "needed");
        //read the file
        String line = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            Long ourTime = new Long(System.currentTimeMillis());
            line = bufferedReader.readLine();
            String valueFromFile;
            Long longValueFromFile;

            if (line != null && line.length() > 0) {
                Log.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
                valueFromFile = line.split(" ")[0];
                longValueFromFile = Long.valueOf(valueFromFile);
                if (ourTime - longValueFromFile >= 20000 /* unresponsive time */) {
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
//                    String cmdline1 = "am force-stop com.gaute.grrclientandroid"; //force stop everything associated with <PACKAGE>
//                    Runtime.getRuntime().exec(cmdline1); //returns a process

                    try {
                        Thread.currentThread().sleep(10000 /* resurrection period */);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //problem1: can't use explicit intent to point MainActivity of Grr_client_android
                    //problem2: Verify There is an App to Receive the Intent
                    /* Caution: If you invoke an intent and there is no app available on the device that can handle the intent, your app will crash. */

                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.gauteclient");
                    PackageManager packageManager = getPackageManager();
                    List activities = packageManager.queryIntentActivities(intent,
                            PackageManager.MATCH_DEFAULT_ONLY);
                    boolean isIntentSafe = activities.size() > 0;

                    if (isIntentSafe) {
                        getApplicationContext().startActivity(intent);
                        Log.i("appavailable ", "yes");
                    } else {
                        Log.i("appavailable ", "no");
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();


    }
}
