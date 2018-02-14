package com.gaute.grrclientandroid.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gaute.grrclientandroid.activity.MainActivity;

public class BroadcastReceiverStartOnBoot extends BroadcastReceiver {
    private String TAG = this.getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());

        //can't start the service via BroadcastReceiver. Service is started from UI components only.
        Intent intentMainActivity = new Intent(context, MainActivity.class);
        context.startActivity(intentMainActivity);

        //Note: After BroadcastReceiver returns from onReceive, it is subject to get destroyed by the system
        //Note: System starts apk by reading AndroidManifest.xml file always
    }
}
