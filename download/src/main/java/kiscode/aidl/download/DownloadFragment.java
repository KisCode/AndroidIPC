package kiscode.aidl.download;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/***
 * DownloadFragment
 */
public class DownloadFragment extends Fragment {
    private static final String TAG = "DownloadFragment";
    private static final String TAG_FRARMENT = "tag_frarment";

    private Map<String, DownloadCallback> mListener = new HashMap<>();
    private ServiceConnection mServiceConnection;
    private IDownloadManager mDownloadManager;

    private IDownloadCallback.Stub mDownloadCallBack = new IDownloadCallback.Stub() {
        @Override
        public void onStart(String url) throws RemoteException {
            Log.i(TAG, "onStart:\t" + url);
            mListener.get(url).onStart(url);
        }

        @Override
        public void onProgress(String url, int progress) throws RemoteException {
            Log.i(TAG, "onProgress:\t" + url);
            mListener.get(url).onProgress(url, progress);
        }

        @Override
        public void onComplete(String url) throws RemoteException {
            Log.i(TAG, "onComplete:\t" + url);
            mListener.get(url).onComplete(url);
            mListener.remove(url);
        }
    };


    public static DownloadFragment initFragment(Context context, FragmentManager fragmentManager) {
        DownloadFragment downloadFragment = (DownloadFragment) fragmentManager.findFragmentByTag(TAG_FRARMENT);
        if (downloadFragment == null) {
            downloadFragment = new DownloadFragment();
            fragmentManager.beginTransaction().add(downloadFragment, TAG_FRARMENT).commitAllowingStateLoss();
        }
        return downloadFragment;

    }

    private void connect() {
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i(TAG, "onServiceConnected");
                mDownloadManager = IDownloadManager.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i(TAG, "onServiceDisconnected:" + name);
            }
        };

        Intent intent = new Intent(getContext(), DownloadService.class);
        getActivity().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        Objects.requireNonNull(getActivity()).unbindService(mServiceConnection);
    }

    public void startDownload(String url, DownloadCallback downloadCallback) {
        mListener.put(url, downloadCallback);

        try {
            mDownloadManager.startDownload(url);
            mDownloadManager.addDownloadCallback(mDownloadCallBack);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void cancleDownload(String url) {
/*        try {
            mDownloadManager.cancleDownload(url);
        } catch (RemoteException e) {
            e.printStackTrace();
        }*/
    }
}