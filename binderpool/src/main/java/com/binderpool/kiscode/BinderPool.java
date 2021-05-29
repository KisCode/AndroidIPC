package com.binderpool.kiscode;


import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.concurrent.CountDownLatch;

/****
 * Description:
 * Author:  keno
 * CreateDate: 2021/5/29 21:43
 */

public class BinderPool {
    private static final String TAG = "BinderPool";
    public static final int BINDER_PLAY = 997;
    public static final int BINDER_DOWNLOAD = 612;

    private IBinderPool mBinderpool;
    private Context mContext;
    private static volatile BinderPool mInstance;
    private CountDownLatch mConnectBinderPoolCountDownLatch;

    public BinderPool(Context context) {
        this.mContext = context.getApplicationContext();
        connectService();
    }

    public static void init(Context context) {
        getInstance(context);
    }

    public static BinderPool getInstance(Context context) {
        if (mInstance == null) {
            synchronized (BinderPool.class) {
                if (mInstance == null) {
                    mInstance = new BinderPool(context);
                }
            }
        }
        return mInstance;
    }

    /***
     * 启动BinderPoolService
     */
    private synchronized void connectService() {
        mConnectBinderPoolCountDownLatch = new CountDownLatch(1);

        Log.i(TAG, "connectService bindService");
        Intent intent = new Intent(mContext, BinderPoolService.class);
        mContext.bindService(intent, mBinderServiceConnection, Context.BIND_AUTO_CREATE);

        try {
            mConnectBinderPoolCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private IBinder.DeathRecipient mBinderPoolDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.w(TAG, "binder died...");
            mBinderpool.asBinder().unlinkToDeath(mBinderPoolDeathRecipient, 0);
            mBinderpool = null;
            connectService();
        }
    };


    private ServiceConnection mBinderServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinderpool = IBinderPool.Stub.asInterface(service);
            Log.i(TAG, "onServiceConnected mBinderPool is " + mBinderpool);

            try {
                mBinderpool.asBinder().linkToDeath(mBinderPoolDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            mConnectBinderPoolCountDownLatch.countDown(); //计数器值减一
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.w(TAG, "onServiceDisconnected");
        }
    };

    public IBinder queryBinder(int binderCode) {
        IBinder binder = null;
        try {
            if (mBinderpool != null) {
                binder = mBinderpool.queryBinder(binderCode);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return binder;
    }

    public static class BinderPoolImpl extends IBinderPool.Stub {
        @Override
        public IBinder queryBinder(int binderCode) throws RemoteException {
            IBinder binder = null;
            switch (binderCode) {
                case BINDER_DOWNLOAD:
                    binder = new DownloadManagerImpl();
                    break;
                case BINDER_PLAY:
                    binder = new PlayerManagerImpl();
                    break;

            }
            return binder;
        }
    }
}
