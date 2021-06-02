// IDownloadManager.aidl
package kiscode.aidl.download;

// Declare any non-default types here with import statements
import kiscode.aidl.download.IDownloadCallback;

interface IDownloadManager {
    oneway void startDownload(String url);
    oneway void cancleDownload(String url);
    oneway void cancleAll();

    void addDownloadCallback(IDownloadCallback callback);

  /*  void registerDownloadCallback(IDownloadCallback callback);
    void unRegisterDownloadCallback(IDownloadCallback callback);*/
}