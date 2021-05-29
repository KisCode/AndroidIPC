// IDownloadManager.aidl
package com.binderpool.kiscode;

// Declare any non-default types here with import statements

//文件下载接口
interface IDownloadManager {

    void download(String url);

    void stopAll();
}