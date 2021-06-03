// IDownloadCallback.aidl
package kiscode.aidl.download;

// Declare any non-default types here with import statements

interface IDownloadCallback {
    void onStart(String url);
    void onProgress(String url,int progress);
    void onComplete(String url);
}