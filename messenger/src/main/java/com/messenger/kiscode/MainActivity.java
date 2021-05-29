package com.messenger.kiscode;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.messenger.kiscode.conts.MsgConstans;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Messenger mMessenger;

    private Messenger mGetReplyMessenger = new Messenger(new MsgHandler());


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMessenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        Intent intent = new Intent(this, RemoteService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onDestroy() {
        unbindService(mConnection);
        super.onDestroy();
    }

    private void initViews() {
        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsgToServer();
            }
        });
    }


    /***
     * 发送消息到服务端
     */
    private void sendMsgToServer() {
        Message msg = Message.obtain(null, MsgConstans.MSG_FROM_CLIENT);
        Bundle bundle = new Bundle();
        bundle.putString(MsgConstans.KEY_MSG_CONTENT, "hello,this is client");
        msg.setData(bundle);

        // 把服务端回复的Messenger传递给服务端
        msg.replyTo = mGetReplyMessenger;
        try {
            mMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    private static class MsgHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MsgConstans.MSG_FROM_SERVER:
                    String receiveMsg = msg.getData().getString(MsgConstans.KEY_MSG_CONTENT);
                    Log.i(TAG, "receive form server msg:" + receiveMsg);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}