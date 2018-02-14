package com.gaute.grrnannyandroid.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gaute.grrnannyandroid.activity.MainActivity;

public class BroadcastReceiverStartOnBoot extends BroadcastReceiver {
    private String TAG = this.getClass().getSimpleName();

    public void onReceive(Context context, Intent intent) {

        //can't start the service via BroadcastReceiver. Service is started from UI components only.
        Intent intentMainActivity = new Intent(context, MainActivity.class);
        context.startActivity(intentMainActivity);

        //Note: After BroadcastReceiver returns from onReceive, it is subject to get destroyed by the system
        //Note: System starts apk by reading AndroidManifest.xml file always
    }
}
