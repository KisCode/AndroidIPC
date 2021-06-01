package com.service.kiscode;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

public class MyBindService extends Service {
    private static final String TAG = "MyBindService";
    private static final int TIME_DISTANCE = 1000;

    public MyBindService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        doWork();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return null;
    }

    private void doWork() {
        AtomicInteger mNum = new AtomicInteger();
        while (true) {
            if (Calendar.getInstance().getTimeInMillis() % TIME_DISTANCE == 0) {
                mNum.addAndGet(1);
                Log.i(TAG, "------->>>" + mNum.get());
            }
        }
    }

}