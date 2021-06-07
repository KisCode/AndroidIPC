package kiscode.aidl.download;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

public class DownloadService extends Service {
    private static final String TAG = "DownloadService";
    private RemoteCallbackList<IDownloadListener> remoteCallbackList = new RemoteCallbackList<>();
    private IDownloadManager.Stub mBinder = new IDownloadManager.Stub() {

        @Override
        public void startDownload(String url) throws RemoteException {
            Log.i(TAG, "startDownload:" + url);
            onStart(url);
            DownloadTask.THREAD_POOL_EXECUTOR.execute(new DownloadRunnable(url));
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
        public void registerDownloadListener(IDownloadListener listener) throws RemoteException {
            Log.i(TAG, "addDownloadCallback " + listener.asBinder());
            remoteCallbackList.register(listener);
        }

        @Override
        public void unregisterDownloadListener(IDownloadListener listener) throws RemoteException {
            Log.i(TAG, "unregisterDownloadListener " + listener.asBinder());
            remoteCallbackList.unregister(listener);
        }
    };

    public DownloadService() {
    }

    private synchronized void onStart(String url) {
        try {
            remoteCallbackList.beginBroadcast();
            int count = remoteCallbackList.getRegisteredCallbackCount();
            for (int i = 0; i < count; i++) {
                IDownloadListener broadcastItem = remoteCallbackList.getBroadcastItem(i);
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
                IDownloadListener broadcastItem = remoteCallbackList.getBroadcastItem(i);
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
                IDownloadListener broadcastItem = remoteCallbackList.getBroadcastItem(i);
                broadcastItem.onComplete(url);
            }
            remoteCallbackList.finishBroadcast();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private synchronized void onError(String url, int httpCode, String msg) {
        try {
            remoteCallbackList.beginBroadcast();
            int count = remoteCallbackList.getRegisteredCallbackCount();
            Log.i(TAG, count + "\tonError:" + url);
            for (int i = 0; i < count; i++) {
                IDownloadListener broadcastItem = remoteCallbackList.getBroadcastItem(i);
                broadcastItem.onError(url, httpCode, msg);
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

    private class DownloadRunnable implements Runnable {
        private String url;

        public DownloadRunnable(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            if (url.endsWith(".cn") || url.endsWith(".net")) {
                onError(url, 500, "下载错误，服务器内部异常！");
                return;
            }
            //模拟下载操作
            for (int i = 0; i <= 100; i++) {
                try {
                    long sleepTime = url.length() * 2;
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                onProgress(url, i);
            }
            Log.i(TAG, "THREAD IN " + Thread.currentThread().getName());
            onComplete(url);
        }
    }
}