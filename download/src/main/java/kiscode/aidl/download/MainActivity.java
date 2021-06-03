package kiscode.aidl.download;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final String URL_YOUTUBE = "https://www.youtube.com";
    private static final String URL_GOOGLE = "https://www.google.com";
    private static final String[] URL_ARRAY = new String[]{
            "https://www.youtube.com",
            "https://www.youtube.com",
            "https://microsoft.com",
            "https://windows.com",
            "https://gettoby.com",
            "https://alexametrics.com",
            "https://adsblock.org",
            "https://github.com",
            "https://firefox.com",
            "https://pipe.aria.microsoft.com",
            "https://microsofttranslator.com",
    };


    private DownloadFragment downloadFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        downloadFragment = DownloadFragment.initFragment(this, getSupportFragmentManager());
    }

    public void startDownload(View view) {
        downloadFragment.startDownload(URL_YOUTUBE, new DownloadCallback() {
            @Override
            public void onStart(String url) throws RemoteException {
                Log.i(TAG, "onStart:" + url);
            }

            @Override
            public void onProgress(String url, int progress) throws RemoteException {
                Log.i("onProgress", "onProgress:" + url + "\t --->" + progress);
            }

            @Override
            public void onComplete(String url) throws RemoteException {
                Log.i(TAG, "onComplete:" + url);
            }
        });
    }

    public void cancleDownload(View view) {
        DownloadFragment downloadFragment = DownloadFragment.initFragment(this, getSupportFragmentManager());
        downloadFragment.cancleDownload(URL_YOUTUBE);
    }

    public void startDownload2(View view) {
        DownloadFragment downloadFragment = DownloadFragment.initFragment(this, getSupportFragmentManager());
        downloadFragment.startDownload(URL_GOOGLE, new DownloadCallback() {
            @Override
            public void onStart(String url) throws RemoteException {
                Log.i(TAG, "onStart:" + url);
            }

            @Override
            public void onProgress(String url, int progress) throws RemoteException {
                Log.e("onProgress2", "onProgress:" + url + "\t --->" + progress);
            }

            @Override
            public void onComplete(String url) throws RemoteException {
                Log.i(TAG, "onComplete:" + url);
            }
        });
    }

    public void cancleDownload2(View view) {
        DownloadFragment downloadFragment = DownloadFragment.initFragment(this, getSupportFragmentManager());
        downloadFragment.cancleDownload(URL_GOOGLE);
    }

    public void startDownloadAll(View view) {
        DownloadFragment downloadFragment = DownloadFragment.initFragment(this, getSupportFragmentManager());
        for (String url : URL_ARRAY) {
            downloadFragment.startDownload(url, new DownloadCallback() {
                @Override
                public void onStart(String url) throws RemoteException {
                    Log.i(TAG, "onStart:" + url);
                }

                @Override
                public void onProgress(String url, int progress) throws RemoteException {
                    Log.e("onProgress2", "onProgress:" + url + "\t --->" + progress);
                }

                @Override
                public void onComplete(String url) throws RemoteException {
                    Log.i(TAG, "onComplete:" + url);
                }
            });
        }

    }
}