package kiscode.aidl.download;

public interface DownloadCallback{
    void onStart();
    void onProgress(long progress);
    void onComplete();
    void onError(int httpCode,String msg);
}