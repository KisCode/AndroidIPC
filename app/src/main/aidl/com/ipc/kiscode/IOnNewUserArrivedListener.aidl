// IOnNewUserArrivedListener.aidl
package com.ipc.kiscode;

// Declare any non-default types here with import statements
import com.ipc.kiscode.User;
interface IOnNewUserArrivedListener {
    void onNewUserArrived(in User user);
}