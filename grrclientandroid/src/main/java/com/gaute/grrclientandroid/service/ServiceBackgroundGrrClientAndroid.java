package com.gaute.grrclientandroid.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ServiceBackgroundGrrClientAndroid extends Service {
    private String TAG = this.getClass().getSimpleName();
    private FileWriter fileWriter;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());

//        startForeground(1001, new Notification()); //not needed anymore, Grr_nanny_android restart this as necessary
        /* holds wake lock for long time and battery usage notification comes there */

        //shared file creation
        File filesDir = getApplicationContext().getExternalFilesDir(".");
        File sharedFile = new File(filesDir, "sharedFile.txt");
        try {
            sharedFile.createNewFile();
        } catch (IOException e) {
            System.out.println("can't create sharedFile!");
        }

        //write to sharedFile.txt using separate worker thread
        writeToFile(sharedFile);

        return START_NOT_STICKY;
    }

    private void writeToFile(File file) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    fileWriter = new FileWriter(file, false);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //update a file using its timestamp
                Long time = System.currentTimeMillis();
                try {
                    fileWriter.write(time.toString());
                    Log.i("file-write ", time.toString());
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
        };
        Thread thread = new Thread(runnable);
        thread.start();

        //stop now
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
