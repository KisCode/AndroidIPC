package com.messenger.kiscode;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.messenger.kiscode.conts.MsgConstans;

public class RemoteService extends Service {
    private static final String TAG = "RemoteService";
    private static final int HEART_DISTANCE = 3000;

    public RemoteService() {
    }

    private MessengerHandler mMessengerHandler = new MessengerHandler();

    private Messenger mMessenger = new Messenger(mMessengerHandler);

    private Runnable mHeartRunnable = new Runnable() {
        @Override
        public void run() {
            mMessengerHandler.postDelayed(this, HEART_DISTANCE);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        mMessengerHandler.postDelayed(mHeartRunnable, HEART_DISTANCE);
    }

    private static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MsgConstans.MSG_FROM_CLIENT:
                    String receiveMsg = msg.getData().getString(MsgConstans.KEY_MSG_CONTENT);
                    Log.i(TAG, "receive msg from client: " + receiveMsg);

                    //回复客户端消息
                    Messenger clientMessenger = msg.replyTo;
                    Message replyMsg = Message.obtain(null, MsgConstans.MSG_FROM_SERVER);
                    Bundle bundle = new Bundle();
                    bundle.putString(MsgConstans.KEY_MSG_CONTENT, "已收到你的消息!");
                    replyMsg.setData(bundle);
                    try {
                        clientMessenger.send(replyMsg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);

            }
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}