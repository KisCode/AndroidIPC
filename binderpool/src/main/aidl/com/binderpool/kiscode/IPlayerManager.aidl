// IPlayerManager.aidl
package com.binderpool.kiscode;

// Declare any non-default types here with import statements
import com.binderpool.kiscode.Song;

// 声明播放接口
interface IPlayerManager {

    void play(in Song song);

    void pause();

    void stop();

}