package com.example.grr_nanny_android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GrrNannyBootBroadcastReceiver extends BroadcastReceiver {
    private String TAG = this.getClass().getSimpleName();

   public void onReceive(Context context, Intent intent) {

        //start my JobScheduler, in each job it tracks whether shared file time has incremented or not?
        if(intent.getAction() == Intent.ACTION_BOOT_COMPLETED) {
            Util util = new Util();
            util.scheduleJob(context);
        }
    }
}
