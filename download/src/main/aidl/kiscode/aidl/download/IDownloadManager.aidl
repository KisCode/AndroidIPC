// IDownloadManager.aidl
package kiscode.aidl.download;

// Declare any non-default types here with import statements
import kiscode.aidl.download.IDownloadListener;

interface IDownloadManager {

    //开始下载
    oneway void startDownload(String url);
    //停止下载
    oneway void cancelDownload(String url);
    //取消全部下载
    oneway void cancelAll();
    //注册下载回调
    void registerDownloadListener(IDownloadListener listener);
    //取消瞎子啊回调
    void unregisterDownloadListener(IDownloadListener listener);
}