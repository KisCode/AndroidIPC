package com.ipc.kiscode;


import android.os.Parcel;
import android.os.Parcelable;

/****
 * Description:
 * Author:  keno
 * CreateDate: 2021/5/26 22:59
 */

public class User implements Parcelable {

    private int id;
    private String name;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    protected User(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }

    @Override
    public String toString() {
        return "\nUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
