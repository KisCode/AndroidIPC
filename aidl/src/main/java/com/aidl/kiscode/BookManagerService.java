package com.aidl.kiscode;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BookManagerService extends Service {
    private static final String TAG = "BookManagerService";

    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();

    private IBookManager.Stub mBookManger = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            Log.i(TAG, "getBookList in " + Thread.currentThread().getName());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            Log.i(TAG, "addBook in " + Thread.currentThread().getName());
            mBookList.add(book);
        }
    };

    public BookManagerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate " + Thread.currentThread().getName());

        doWork();
    }

    /***
     * 模拟耗时操作 10秒后自动结束
     */
    private void doWork() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                stopSelf();
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return mBookManger;
    }
}