package kiscode.aidl.download;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

public class DownloadService extends Service {
    private static final String TAG = "DownloadService";
    private static final String KEY_DOWNLOAD_URL = "key_download_url";

    private RemoteCallbackList<IDownloadCallback> remoteCallbackList = new RemoteCallbackList<>();

    private IDownloadManager.Stub mBinder = new IDownloadManager.Stub() {

        @Override
        public void startDownload(String url) throws RemoteException {
            Log.i(TAG, "startDownload:" + url);
//            remoteCallbackList.register(callback);
            onStart(url);

            doDownload(url);
        }

        @Override
        public void cancelDownload(String url) throws RemoteException {
            Log.i(TAG, "cancleDownload:" + url);
        }

        @Override
        public void cancelAll() throws RemoteException {
            Log.i(TAG, "cancelAll");
        }

        @Override
        public void addDownloadCallback(IDownloadCallback callback) throws RemoteException {
            remoteCallbackList.register(callback);
        }

        @Override
        public void removeDownloadCallback(IDownloadCallback callback) throws RemoteException {
            remoteCallbackList.register(callback);
        }
    };

    public DownloadService() {
    }

    private void doDownload(String url) {
        //模拟下载操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= 100; i++) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    onProgress(url, i);
                }
                onComplete(url);
            }
        }).start();
    }

    private synchronized void onStart(String url) {
        try {
            remoteCallbackList.beginBroadcast();
            int count = remoteCallbackList.getRegisteredCallbackCount();
            for (int i = 0; i < count; i++) {
                IDownloadCallback broadcastItem = remoteCallbackList.getBroadcastItem(i);
                broadcastItem.onStart(url);
            }
            remoteCallbackList.finishBroadcast();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private synchronized void onProgress(String url, int progress) {
        try {
            remoteCallbackList.beginBroadcast();
            int count = remoteCallbackList.getRegisteredCallbackCount();
            for (int i = 0; i < count; i++) {
                IDownloadCallback broadcastItem = remoteCallbackList.getBroadcastItem(i);
                broadcastItem.onProgress(url, progress);
            }
            remoteCallbackList.finishBroadcast();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private synchronized void onComplete(String url) {
        try {
            remoteCallbackList.beginBroadcast();
            int count = remoteCallbackList.getRegisteredCallbackCount();
            Log.i(TAG, count + "\tonComplete:" + url);
            for (int i = 0; i < count; i++) {
                IDownloadCallback broadcastItem = remoteCallbackList.getBroadcastItem(i);
                broadcastItem.onComplete(url);
            }
            remoteCallbackList.finishBroadcast();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}