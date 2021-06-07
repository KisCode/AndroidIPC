package kiscode.aidl.download;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String URL_YOUTUBE = "https://www.youtube.com/index/sxmasdfadfadf";
    private static final String URL_GOOGLE = "https://www.google.com";
    private static final String[] URL_ARRAY = new String[]{
            "https://www.youtube.com",
            "https://io.com",
            "http://www.json.cn",
            "https://outlook.live.com/mail/inbox",
            "https://microsoft.com",
            "https://windows.com",
            "https://gettoby.com",
            "https://www.baidu.com",
            "https://alexametrics.com",
            "https://adsblock.org",
            "https://china.cn",
            "https://china.online.cn",
            "https://github.com",
            "https://firefox.com",
            "https://pipe.aria.microsoft.com",
            "https://china.online.net",
            "https://microsofttranslator.com",
    };
    private ProgressBar progressBar;
    private DownloadFragment downloadFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);
        downloadFragment = DownloadFragment.initFragment(this, getSupportFragmentManager());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (downloadFragment != null && downloadFragment.isAdded()) {
            downloadFragment.onDestroy();
        }
    }

    public void startDownload(View view) {
        DownloadFragment downloadFragment = DownloadFragment.initFragment(this, getSupportFragmentManager());
        downloadFragment.startDownload(URL_YOUTUBE, new DownloadCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onProgress(long progress) {
                progressBar.setProgress((int) progress);
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onError(int httpCode, String msg) {

            }
        });
    }

    public void cancleDownload(View view) {
        DownloadFragment downloadFragment = DownloadFragment.initFragment(this, getSupportFragmentManager());
        downloadFragment.cancelDownload(URL_YOUTUBE);
    }

    public void startDownload2(View view) {
        DownloadFragment downloadFragment = DownloadFragment.initFragment(this, getSupportFragmentManager());

    }

    public void cancleDownload2(View view) {
        DownloadFragment downloadFragment = DownloadFragment.initFragment(this, getSupportFragmentManager());
        downloadFragment.cancelDownload(URL_GOOGLE);
    }

    public void startDownloadAll(View view) {
        DownloadFragment downloadFragment = DownloadFragment.initFragment(this, getSupportFragmentManager());
        for (String url : URL_ARRAY) {
            downloadFragment.startDownload(url, new DownloadCallback() {
                @Override
                public void onStart() {
                    Log.i(TAG, "onStart " + url);
                }

                @Override
                public void onProgress(long progress) {
                    Log.i(TAG, "onProgress: " + progress + "\t" + url);
                }

                @Override
                public void onComplete() {
                    Log.i(TAG, "onComplete " + url);
                }

                @Override
                public void onError(int httpCode, String msg) {
                    Log.e(TAG, "onError " + url + "\t error:" + msg);
                }
            });
        }

    }
}