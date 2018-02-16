package com.gaute.grrclientandroid.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServiceBackgroundGrrClientAndroid extends Service {
    private String TAG = this.getClass().getSimpleName();

    private File sharedFile;
    private FileWriter fileWriter;
    private boolean killed;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //forcefully restart this service again. System starts apk by reading AndroidManifest.xml file always
        //If the starter process of apk is already destroyed, then the below two lines of code doesn't work at all
        //Because MainActivity.class can be already destroyed, and @Nonnull check of Intent runs on the runtime
//        Intent intentMainActivity = new Intent(this, MainActivity.class);
//        startActivity(intentMainActivity);

        //If the starter process of this service is already destroyed in Android System memory, then the below two lines of code doesn't work at all
//        Intent intentServiceGrrClientAndroid = new Intent(this, ServiceBackgroundGrrClientAndroid.class);
//        startService(intentServiceGrrClientAndroid); //async call. Therefore, no blocking on UI thread
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

//        startForeground(1001, new Notification()); //not needed anymore, Grr_nanny_android restart this as necessary
        /* holds wake lock for long time and battery usage notification comes there */


        //shared file creation
        File filesDir = getApplicationContext().getExternalFilesDir(".");
        Log.i("external file ", filesDir.toString());
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

        sharedFile = new File(filesDir, "sharedFile.txt");
        try {
            sharedFile.createNewFile();
        } catch (IOException e) {
            System.out.println("can't create sharedFile!");
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                writeToFile(sharedFile);
            }
        };

        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(runnable, 0, 1000, TimeUnit.SECONDS);

        //kill the transparent MainActivity
        if (!killed) {
            sendBroadcast(new Intent("destroy_activity_grr_client_android"));
            Log.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
            killed = true;
        }

        return START_NOT_STICKY;
    }

    private void writeToFile(File file) {
        try {
            fileWriter = new FileWriter(file, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //update a file using its timestamp
        Long time = System.currentTimeMillis();
        try {
            fileWriter.write(time.toString());
            Log.i("filewrite ", time.toString());
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
