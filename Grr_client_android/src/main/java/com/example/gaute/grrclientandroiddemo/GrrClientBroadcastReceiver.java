package com.example.gaute.grrclientandroiddemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/* This BroadcastReceiver for IPC between Grr_client_android and Grr_nanny_android */
/* This BroadcastReceiver is also responsible for starting ServiceGrrClientAndroid at system boot */
public class GrrClientBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentServiceGrrClientAndroid = new Intent(context, ServiceGrrClientAndroid.class);

        //start the service at boot
        if(intent.getAction() == Intent.ACTION_BOOT_COMPLETED) {
            context.startService(intentServiceGrrClientAndroid); //async call. Therefore, no blocking on UI thread
        }

        //restart the service if it known to be unresponsive by Grr_nanny_android
        if(intent.getAction() == "com.grr_nanny_android.restart") {
            context.stopService(intentServiceGrrClientAndroid);
            context.startService(intentServiceGrrClientAndroid);
        }
    }
}
