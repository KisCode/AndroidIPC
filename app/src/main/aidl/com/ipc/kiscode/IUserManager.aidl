// IUserManager.aidl
package com.ipc.kiscode;

// Declare any non-default types here with import statements
import com.ipc.kiscode.User;

interface IUserManager {
    List<User> getUserList();

    void addUser(in User user);
}