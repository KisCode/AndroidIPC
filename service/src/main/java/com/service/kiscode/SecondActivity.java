package com.service.kiscode;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SecondActivity";
    private Button btnStartService;
    private Button btnBindService;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected " + name);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected " + name);
        }
    };

    public static void start(Context context) {
        Intent starter = new Intent(context, SecondActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start_service:
                startService(new Intent(this, MyService.class));
                break;
            case R.id.btn_bind_service:
                //绑定service
                bindService(new Intent(this, MyBindService.class), mServiceConnection, BIND_AUTO_CREATE);
                break;
        }
    }

    private void initView() {
        btnStartService = findViewById(R.id.btn_start_service);
        btnBindService = findViewById(R.id.btn_bind_service);
        btnStartService.setOnClickListener(this);
        btnBindService.setOnClickListener(this);
    }
}