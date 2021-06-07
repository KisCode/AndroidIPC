package com.aidl.kiscode;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BookManagerService extends Service {
    private static final String TAG = "BookManagerService";
    private volatile boolean mIsDestory = false;

    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();
    private RemoteCallbackList<INewBookArriveListener> mListenerList = new RemoteCallbackList<>();

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
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mBookList.add(book);
        }

        @Override
        public void registerBookArriveListener(INewBookArriveListener listener) throws RemoteException {
            Log.i(TAG, "register " + listener.asBinder());
            mListenerList.register(listener);
        }

        @Override
        public void unRegisterBookArriveListener(INewBookArriveListener listener) throws RemoteException {
            Log.i(TAG, "unRegister  " + listener.asBinder());
            mListenerList.unregister(listener);
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        return START_STICKY;
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
        return mBookManger;
    }

    /***
     * 模拟耗时操作 10秒后自动结束
     */
    private void doWork() {
        new Thread(() -> {
            while (!mIsDestory) {
                try {
                    Thread.sleep(2000);
                    int id = 100 + mBookList.size();
                    onNewBookArrive(new Book(id, "new Book " + id, 0));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    private void onNewBookArrive(Book book) {
        int count = mListenerList.beginBroadcast();
        for (int i = 0; i < count; i++) {
            INewBookArriveListener listener = mListenerList.getBroadcastItem(i);
            try {
                Log.i(TAG, "onNewBookArrive " + listener.asBinder());
                listener.onArriveNewBook(book);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mListenerList.finishBroadcast();

    }
}