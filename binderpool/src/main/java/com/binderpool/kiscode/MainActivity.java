package com.binderpool.kiscode;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    private void initViews() {
        findViewById(R.id.btn_download).setOnClickListener(this);
        findViewById(R.id.btn_play).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_download:

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        download();
                    }
                }).start();
                break;
            case R.id.btn_play:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        playSong();
                    }
                }).start();
                break;
        }
    }

    private void download() {
        BinderPool binderPool = BinderPool.getInstance(this);
        IBinder binder = binderPool.queryBinder(BinderPool.BINDER_DOWNLOAD);

        try {
            IDownloadManager downloadManager = IDownloadManager.Stub.asInterface(binder);
            downloadManager.download("https://fanyi.baidu.com/");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void playSong() {
        BinderPool binderPool = BinderPool.getInstance(this);
        IBinder binder = binderPool.queryBinder(BinderPool.BINDER_PLAY);

        try {
            IPlayerManager playerManager = IPlayerManager.Stub.asInterface(binder);
            Song song = new Song(1, "Love me", "KSK");
            playerManager.play(song);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}