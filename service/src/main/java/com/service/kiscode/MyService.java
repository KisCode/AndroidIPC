package com.service.kiscode;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicInteger;

/***
 * start service sample
 */
public class MyService extends Service {
    private static final String TAG = "MyService";
    private static final int TIME_DISTANCE = 1000;
    private TimeHandler mHander;
    private Runnable timeRunnable = new Runnable() {
        private AtomicInteger mNum = new AtomicInteger();

        @Override
        public void run() {
            mNum.addAndGet(1);
            Log.i(TAG, "------->" + mNum.get());

            mHander.postDelayed(this, TIME_DISTANCE);
        }
    };

    public MyService() {
        mHander = new TimeHandler(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        mHander.postDelayed(timeRunnable, TIME_DISTANCE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");

        mHander.removeCallbacksAndMessages(null);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    static class TimeHandler extends Handler {
        WeakReference<Context> reference;

        public TimeHandler(Context context) {
            this.reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            if (reference != null) {
                Context context = reference.get();
            }
        }
    }
}