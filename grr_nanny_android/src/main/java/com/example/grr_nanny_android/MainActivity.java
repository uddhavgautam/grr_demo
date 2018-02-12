package com.example.grr_nanny_android;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private String TAG = this.getClass().getSimpleName();

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main); /* note, I can't write this line with Transparent theme */
        Log.i(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());

        //start my JobScheduler, in each job it tracks whether shared file time has incremented or not?
        Util util = new Util();
        util.scheduleJob(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
