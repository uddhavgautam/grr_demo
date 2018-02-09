package com.example.grr_nanny_android;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.JobIntentService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class HeartBeatCheckerJIS extends JobIntentService {
    private File sharedFile;
    private BufferedReader bufferedReader;


    public HeartBeatCheckerJIS() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        //intent is ACTION_BOOT_COMPLETED

        Intent service = new Intent(getApplicationContext(), HeartBeatCheckerJIS.class);
        getApplicationContext().startService(service); /* starts if not already started */


        File filesDir = getApplicationContext().getExternalFilesDir("filess");
        //create a sharedFile
        sharedFile = new File(filesDir, "sharedFile.txt");
        try {
            sharedFile.createNewFile();
        } catch (IOException e) {
            System.out.println("can't create sharedFile!");
        }


        readFromFile(sharedFile);

        Util.scheduleJob(getApplicationContext()); // reschedule the job



    }

    private void readFromFile(File file) {

        //read the file
        String line = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));

            while ((line = bufferedReader.readLine()) != null) {
                if(System.currentTimeMillis() -  Long.parseLong(line.split(".")[0]) > 20000) {
                    //restart Grr_client_android
                    sendBroadcast(new Intent("com.grr_nanny_android.restart"));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
            }
        }
    }
}
