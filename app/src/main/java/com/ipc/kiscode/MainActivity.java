package com.ipc.kiscode;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private IUserManager mIUserManager;
    private TextView tvContent;
    private IOnNewUserArrivedListener mOnNewUserArrivedListener = new IOnNewUserArrivedListener.Stub() {
        @Override
        public void onNewUserArrived(User user) throws RemoteException {
            Log.i(TAG, Thread.currentThread().getName() + "onNewUserArrived:" + user.toString());
            runOnUiThread(() -> {
                try {
                    List<User> userList = mIUserManager.getUserList();
                    Collections.reverse(userList);
                    tvContent.setText(userList.toString());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            });
        }
    };


    //进程死亡监听
    private final IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {

        @Override
        public void binderDied() {
            //remote进程死亡 运行在binder线程
            Log.i(TAG, "binderDied remote进程死亡 in " + Thread.currentThread().getName());
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIUserManager = IUserManager.Stub.asInterface(service);
            try {
                List<User> userList = mIUserManager.getUserList();
                Log.i(TAG, "currentThread:" + Thread.currentThread().getName());
                Log.i(TAG, "user list type:" + userList.getClass().getCanonicalName());
                Log.i(TAG, "user list:" + userList.toString());

                mIUserManager.addUser(new User(10001, "Client 10001"));
                Log.i(TAG, "user list:" + mIUserManager.getUserList().toString());

                mIUserManager.registerUserArrivedListener(mOnNewUserArrivedListener);

                //设置binder死亡回调，当remote进程被杀死后，触发binderDied()回调
                mIUserManager.asBinder().linkToDeath(deathRecipient, 0);
            } catch (Exception exception) {
                Log.e(TAG, exception.toString());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //当binder进程被杀死后同样会回调该方法，运行在UI线程
            Log.i(TAG, "onServiceDisconnected :" + name + " in " + Thread.currentThread().getName());
            startUserService();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initViews();
        startUserService();
    }

    private void startUserService() {
        Intent intent = new Intent(this, UserManagerService.class);
//        Intent intent = new Intent();
//        intent.setClassName("com.ipc.kiscode", "com.ipc.kiscode.UserManagerService");
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        unbindService(mConnection);
        try {
            mIUserManager.unregisterUserArrivedListener(mOnNewUserArrivedListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private void initViews() {
        tvContent = findViewById(R.id.tv_content);
    }
}