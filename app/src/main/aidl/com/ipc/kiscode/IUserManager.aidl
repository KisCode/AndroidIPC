// IUserManager.aidl
package com.ipc.kiscode;

// Declare any non-default types here with import statements
import com.ipc.kiscode.User;
import com.ipc.kiscode.IOnNewUserArrivedListener;

interface IUserManager {
    List<User> getUserList();

    void addUser(in User user);

    void registerUserArrivedListener(IOnNewUserArrivedListener listener);

    void unregisterUserArrivedListener(IOnNewUserArrivedListener listener);
}