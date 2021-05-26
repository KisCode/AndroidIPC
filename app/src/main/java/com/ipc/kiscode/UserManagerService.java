package com.ipc.kiscode;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/****
 * Description:
 * Author:  keno
 * CreateDate: 2021/5/26 22:57
 */

public class UserManagerService extends Service {
    private static final String TAG = "UserManagerService";

    private CopyOnWriteArrayList<User> mUserList = new CopyOnWriteArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        mUserList.add(new User(0, "Admin"));
    }

    private Binder mBinder = new IUserManager.Stub() {
        @Override
        public List<User> getUserList() throws RemoteException {
            return mUserList;
        }

        @Override
        public void addUser(User user) throws RemoteException {
            mUserList.add(user);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
