package com.example.grr_nanny_android;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JobServiceHeartBeatChecker extends JobService {
    private String TAG = this.getClass().getSimpleName();


    private BufferedReader bufferedReader;
    private boolean status;
    private File sharedFile;

    private boolean killed;


    public JobServiceHeartBeatChecker() {
        Log.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    @Override
    public boolean onStartJob(JobParameters params) {
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
            status = sharedFile.createNewFile();
        } catch (IOException e) {
            System.out.println("can't create sharedFile!");
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (status) {
                    if (needRestart(sharedFile)/* checks if GrrClientAndroid restart is needed based on shared file */) {
                        //restart Grr_client_android
                        restartGrrClientAndroid();
                    }
                }
            }
        };

        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(runnable, 0, 10, TimeUnit.SECONDS);

        return true /* I need separate thread for onStartJob */;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false /* no reschedule */;
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

    }
}
