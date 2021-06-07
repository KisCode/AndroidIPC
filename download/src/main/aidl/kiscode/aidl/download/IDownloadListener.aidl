// IDownloadListener.aidl
package kiscode.aidl.download;

// Declare any non-default types here with import statements

interface IDownloadListener {
    void onStart(String url);
    void onProgress(String url,long progress);
    void onComplete(String url);
    void onError(String url,int httpCode,String msg);
}