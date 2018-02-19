package com.gaute.grrnannyandroid.service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.gaute.grrnannyandroid.MyConstants;
import com.gaute.grrnannyandroid.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class ServiceHeartBeatChecker extends Service {
    private String TAG = this.getClass().getSimpleName();

    private BufferedReader bufferedReader /* reads the sharedFile.txt */;

    //default constructor
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
    }

    /* onStartCommand is called by AlarmManager in repeat mode */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
        File sharedFile = new File("/storage/emulated/0/Android/data/com.gaute.grrclientandroid/files", "sharedFile.txt");

        //creates new file if not already there
        try {
            sharedFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //use separate worker thread
        checkSharedFile(sharedFile);

        return START_NOT_STICKY;
    }

    private void checkSharedFile(final File file) {
        if (file.exists()/* checks whether file or directory exists */) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (needRestartBasedOnSharedFile(file)/* checks if GrrClientAndroid restart is needed based on shared file */) {
                        //restart Grr_client_android
                        Log.i(TAG, "grr-client restarting!");
                        restartGrrClientAndroid();
                    }
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();

        } else {
            //no file. Restart grr-client so that it creates and then writes in the sharedFile.txt.
            Log.i(TAG, " no-file ");
            restartGrrClientAndroid();
        }
    }

    private boolean needRestartBasedOnSharedFile(File file) {
        //read the file
        String line = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            Long ourTime = System.currentTimeMillis();
            line = bufferedReader.readLine();
            String valueFromFile;
            Long timeFromFile;

            if (line != null && line.length() > 0) {
                Log.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
                valueFromFile = line.split(" ")[0];
                timeFromFile = Long.valueOf(valueFromFile);
                if (ourTime - timeFromFile >= MyConstants.GRR_CLIENT_UNRESPONSIVE_TIME) {
                     /* if unresponsive, restart Grr_client_android */
                    return true;
                }
            } else /* if nothing written in the file */ {
                //restart Grr_client_android
                Log.i("nothing written", "true");
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void restartGrrClientAndroid() {
                try {
//                    Log.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
                    try {
                        Thread.currentThread().sleep(MyConstants.RESURRECTION_PERIOD);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //problem1: can't use explicit intent to point MainActivity of Grr_client_android
                    //problem2: Verify There is an App to Receive the Intent
                    /* Caution: If you invoke an intent and there is no app available on the device that can handle the intent, your app will crash. */

                    //Solution for problem2
                    Intent intent = new Intent();
                    intent.setAction(getString(R.string.startClientMainActivity));
                    PackageManager packageManager = getPackageManager();
                    List activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                    boolean isIntentSafe = activities.size() > 0;

                    if (isIntentSafe) {
                        Log.i("app ", "available");
                        getApplicationContext().startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
    }
}
