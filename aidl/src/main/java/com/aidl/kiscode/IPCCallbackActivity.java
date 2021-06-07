package com.aidl.kiscode;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/***
 * 进程健通讯 远程回调示例
 * 1. 点击注册按钮注册一个远程回调
 * 2. 点击取消注册 移除一个远程回调
 */
public class IPCCallbackActivity extends AppCompatActivity {
    private static final String TAG = "IPCCallbackActivity";
    private final AtomicInteger index = new AtomicInteger();
    private IBookManager mBookManager;

    //记录客户端 回调接口列表
    private CopyOnWriteArrayList<INewBookArriveListener.Stub> mListenerList = new CopyOnWriteArrayList<>();

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
        unbindService(mServiceConnection);
        clearAllRemoteCallBallBack();

        super.onDestroy();
    }

    /***
     * 移除全部注册回调
     */
    private void clearAllRemoteCallBallBack() {
        //移除全部注册
        if (!mListenerList.isEmpty()) {
            //移除全部
            for (INewBookArriveListener.Stub listener : mListenerList) {
                try {
                    mBookManager.unRegisterBookArriveListener(listener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mListenerList.clear();
        }
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
        IBinder iBinder = bookArriveListener.asBinder();
        Log.i(TAG, "registerNewBookArriveListener" + iBinder);

        try {
            index.addAndGet(1);
            mListenerList.add(bookArriveListener);
            mBookManager.registerBookArriveListener(bookArriveListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
}