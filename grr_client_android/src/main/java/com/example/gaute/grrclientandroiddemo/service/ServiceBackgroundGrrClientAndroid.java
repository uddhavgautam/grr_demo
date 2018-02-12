package com.example.gaute.grrclientandroiddemo.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ServiceBackgroundGrrClientAndroid extends Service {
    private String TAG = this.getClass().getSimpleName();

    private File sharedFile;
    private FileWriter fileWriter;
    private BufferedReader bufferedReader;
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
        //Because MainActivity.class can be already destoryed, and @Nonnull check of Intent runs on the runtime
//        Intent intentMainActivity = new Intent(this, MainActivity.class);
//        startActivity(intentMainActivity);

        //If the starter process of this service is already destroyed in Android System memory, then the below two lines of code doesn't work at all
        Intent intentServiceGrrClientAndroid = new Intent(this, ServiceBackgroundGrrClientAndroid.class);
        startService(intentServiceGrrClientAndroid); //async call. Therefore, no blocking on UI thread

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1000, new Notification());
        super.onStartCommand(intent, flags, startId);

        //kill the transparent MainActivity
        if (!killed) {
            sendBroadcast(new Intent("destroy_activity"));
            Log.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
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
                writeToFile(sharedFile);
//                readFromFile(sharedFile);
            }
        };

//        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
//        exec.scheduleAtFixedRate(runnable, 0, 100, TimeUnit.SECONDS);

        //the backuped service after destroy doesn't run. Now it is subject to GC
        return START_REDELIVER_INTENT;
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
            System.out.println("uddhav " + time.toString());
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

    @Override
    public IBinder onBind(Intent intent) {
        return null /* I don't need binder. This is general process */;
    }



}
