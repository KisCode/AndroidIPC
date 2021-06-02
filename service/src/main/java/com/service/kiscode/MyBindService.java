package com.service.kiscode;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.atomic.AtomicInteger;

public class MyBindService extends Service {
    private static final String TAG = "MyBindService";

    /**
     * 标志位页面是否结束，当 mIsDestory = true 结束当前正在进行的线程工作线程，避免内存泄露
     */
    private volatile boolean mIsDestory = false;

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
        Log.i(TAG, "onDestroy");
        mIsDestory = true;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    private void doWork() {
        new Thread(() -> {
            AtomicInteger mNum = new AtomicInteger();
//            while (!mIsDestory) {
            while (!mIsDestory) {
                mNum.addAndGet(1);
                Log.i(TAG, "------->>>" + mNum.get());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}