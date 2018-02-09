package com.example.grr_nanny_android;

import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

public class GrrNannyBroadcastReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {

        //start my JobScheduler, in each job it tracks whether shared file time has incremented or not?
        if(intent.getAction() == Intent.ACTION_BOOT_COMPLETED) {
            Util.scheduleJob(context);
        }
    }
}
