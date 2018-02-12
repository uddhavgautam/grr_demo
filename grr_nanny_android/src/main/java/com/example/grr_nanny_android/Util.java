package com.example.grr_nanny_android;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by gaute on 2/9/18.
 */

public class Util {
    private String TAG = this.getClass().getSimpleName();

    private int JOB_ID = 1000;
    private long Interval = 10000; // 10 seconds

    public Util() {
    }

    /* this method deprecated from Android 26 */
    private static boolean isServiceRunning(Context context) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE /* For ActivityManager */);
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)){
                if("com.example.grr_nanny_android.ServiceForFileObserving".equals(service.service.getClassName())) {
                    return true;
                }
            }
            return false;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void scheduleJob(Context context) {

        ComponentName serviceComponent = new ComponentName(context, ServiceForFileObserving.class);
        JobInfo jobInfo;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jobInfo = new JobInfo.Builder(JOB_ID, serviceComponent)
                    /* setPeriodic(Interval) doesn't work with Nougat or higher */
                    .setMinimumLatency(Interval)
                    .setOverrideDeadline(1000)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build();
        } else {
            jobInfo = new JobInfo.Builder(JOB_ID, serviceComponent)
                    .setPeriodic(Interval)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build();
        }

        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        if(jobScheduler != null) {
            Log.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
            Log.i("Interval ", String.valueOf(jobInfo.getFlexMillis()));
            Log.i("Flex ", String.valueOf(jobInfo.getIntervalMillis()));
            Log.i(TAG, jobScheduler.getAllPendingJobs().toString());
            jobScheduler.schedule(jobInfo);
        }

    }

}