package com.kiscode.binder;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private IBookManager mBookManager;
    private AtomicInteger mIndex = new AtomicInteger();
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected :" + name);
            mBookManager = IBookManager.BookManagerImpl.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected ,即将开始重新连接服务端");
            connectService();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectService();
    }

    private void connectService() {
        Intent intent = new Intent(this, BookManagerService.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }

    public void getBookList(View view) {
        try {
            List<Book> bookList = mBookManager.getBookList();
            Log.i(TAG, "getBookList:" + bookList.size() + "\t" + Arrays.toString(new List[]{bookList}));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void addBook(View view) {
        Random random = new Random();
        mIndex.addAndGet(1);

        double price = random.nextDouble() * 100;
        int id = mIndex.get();
        String name = "Book " + mIndex.get();
        Book book = new Book(id, name, price);
        try {
            mBookManager.addBook(book);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}