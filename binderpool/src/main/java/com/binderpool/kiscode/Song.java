package com.binderpool.kiscode;


import android.os.Parcel;
import android.os.Parcelable;

/****
 * Description: 歌曲
 * Author:  keno
 * CreateDate: 2021/5/29 21:07
 */
public class Song implements Parcelable {

    /***
     * 歌曲序号
     */
    private int index;

    /***
     * 歌名
     */
    private String name;

    /***
     * 歌手
     */
    private String singer;

    public Song(int index, String name, String singer) {
        this.index = index;
        this.name = name;
        this.singer = singer;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public static Creator<Song> getCREATOR() {
        return CREATOR;
    }

    protected Song(Parcel in) {
        index = in.readInt();
        name = in.readString();
        singer = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(index);
        dest.writeString(name);
        dest.writeString(singer);
    }

    @Override
    public String toString() {
        return "Song{" +
                "index=" + index +
                ", name='" + name + '\'' +
                ", singer='" + singer + '\'' +
                '}';
    }
}
