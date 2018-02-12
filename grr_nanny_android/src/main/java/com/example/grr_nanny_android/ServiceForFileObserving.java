package com.example.grr_nanny_android;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

public class ServiceForFileObserving extends JobService {
    private String TAG = this.getClass().getSimpleName();

    public ServiceForFileObserving() {
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
        return false; /* not in separate thread */
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false/* no reschedule */;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
    }

}
