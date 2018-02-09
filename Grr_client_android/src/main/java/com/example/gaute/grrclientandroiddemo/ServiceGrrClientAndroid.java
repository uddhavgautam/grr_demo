package com.example.gaute.grrclientandroiddemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServiceGrrClientAndroid extends Service {
    private String TAG = "ServiceGrrClientAndroid";

    private Runnable runnable1;
    private File sharedFile;
    private FileWriter fileWriter;
    private BufferedReader bufferedReader;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);


        File filesDir = getApplicationContext().getExternalFilesDir("filess");
        //create a sharedFile
        sharedFile = new File(filesDir, "sharedFile.txt");
        try {
            sharedFile.createNewFile();
        } catch (IOException e) {
            System.out.println("can't create sharedFile!");
        }

        runnable1 = new Runnable() {
            @Override
            public void run() {
                writeToFile(sharedFile);
//                readFromFile(sharedFile);
            }
        };

        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(runnable1, 0, 5, TimeUnit.SECONDS);

        return START_NOT_STICKY;
    }

    private void writeToFile(File file) {
        try {
            fileWriter = new FileWriter(file, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //update a file using its timestamp
        Long time = System.currentTimeMillis();
        try {
            fileWriter.write(time.toString());
            System.out.println("uddhav " + time.toString());
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readFromFile(File file) {

        //read the file
        String line = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));

            while ((line = bufferedReader.readLine()) != null) {
//                System.out.println("uddhav :"+ line);
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

    @Override
    public IBinder onBind(Intent intent) {
        return null /* I don't need binder. This is general process */;
    }

}
