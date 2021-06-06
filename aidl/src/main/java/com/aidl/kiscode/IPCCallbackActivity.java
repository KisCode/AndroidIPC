package com.aidl.kiscode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class IPCCallbackActivity extends AppCompatActivity {
    private static final String TAG = "IPCCallbackActivity";
    private IBookManager mBookManager;

    private final AtomicInteger index = new AtomicInteger();
    private List<INewBookArriveListener.Stub> mListenerList = new ArrayList<>();

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBookManager = IBookManager.Stub.asInterface(service);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipc_callback);

        //绑定service
        Intent intent = new Intent(this, BookManagerService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }

    public void unRegisterNewBookArriveListener(View view) {
        if (mListenerList.isEmpty()) {
            return;
        }
        //移除第一个
        try {
            mBookManager.unRegisterBookArriveListener(mListenerList.get(0));
            mListenerList.remove(0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void registerNewBookArriveListener(View view) {
        int i = index.get();
        INewBookArriveListener.Stub bookArriveListener = new INewBookArriveListener.Stub() {
            @Override
            public void onArriveNewBook(Book book) throws RemoteException {
                Log.i(TAG, "onArriveNewBook at index " + i);
            }
        };

        try {
            index.addAndGet(1);
            mListenerList.add(bookArriveListener);
            mBookManager.registerBookArriveListener(bookArriveListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
}