package com.ipc.kiscode;


import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/****
 * Description: 用户管理Service,运行在独立进程 com.ipc.kiscode:remote
 * Author:  keno
 * CreateDate: 2021/5/26 22:57
 */
public class UserManagerService extends Service {
    private static final String TAG = "UserManagerService";

    private CopyOnWriteArrayList<User> mUserList = new CopyOnWriteArrayList<>();

    //跨进程远程回调方法，回调函数listener传值必须经过 序列化，再反序列化后 对象并非同一个，导致回调无法移除，必须使用RemoteCallbackList
//    private CopyOnWriteArrayList<IOnNewUserArrivedListener> mListeners = new CopyOnWriteArrayList<>();
    //跨进程远程回调使用 RemoteCallbackList
    private RemoteCallbackList<IOnNewUserArrivedListener> mListeners = new RemoteCallbackList<>();

    private Binder mBinder = new IUserManager.Stub() {

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Log.i(TAG,"onTransact");
            int check = checkCallingOrSelfPermission("com.ipc.kiscode.permission.ACCESS_USER_SERVICE");
            if (check == PackageManager.PERMISSION_DENIED) {
                //权限校验不通过 return false
                return false;
            }

            //对调用者包名进行校验 仅对包名为"com.ipc.kiscode"提供调用
            String[] packagesForUid = getPackageManager().getPackagesForUid(getCallingUid());
            String packageName = null;
            if (packagesForUid != null && packagesForUid.length > 0) {
                packageName = packagesForUid[0];
            }
            if (packageName == null || !packageName.equals("com.ipc.kiscode")) {
                return false;
            }
            return super.onTransact(code, data, reply, flags);
        }

        @Override
        public List<User> getUserList() throws RemoteException {
            Log.i(TAG, "getUserList in Thread:" + Thread.currentThread().getName());
            return mUserList;
        }

        @Override
        public void addUser(User user) throws RemoteException {
            Log.i(TAG, "addUser in Thread:" + Thread.currentThread().getName());
            mUserList.add(user);
        }

        @Override
        public void registerUserArrivedListener(IOnNewUserArrivedListener listener) throws RemoteException {
            mListeners.register(listener);
            Log.i(TAG, "registerUserArrivedListener ");
        }

        @Override
        public void unregisterUserArrivedListener(IOnNewUserArrivedListener listener) throws RemoteException {
            /*mListeners.remove(listener);
            Log.i(TAG, "unregisterUserArrivedListener " + mListeners.size());*/
            mListeners.unregister(listener);

            Log.i(TAG, "unregisterUserArrivedListener ");
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");

        mUserList.add(new User(0, "Admin"));

        new Thread(new UserRunnable()).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void onNewUserArrived(User user) throws RemoteException {
        mUserList.add(user);

        final int number = mListeners.beginBroadcast();
        for (int i = 0; i < number; i++) {
            IOnNewUserArrivedListener listener = mListeners.getBroadcastItem(i);
            listener.onNewUserArrived(user);
        }
        mListeners.finishBroadcast();
    }

    /***
     * 每隔三秒加入
     */
    private class UserRunnable implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int id = 1000 + mUserList.size();
                try {
                    onNewUserArrived(new User(id, "new User " + id));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
