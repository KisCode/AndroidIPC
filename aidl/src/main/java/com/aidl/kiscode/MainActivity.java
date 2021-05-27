package com.aidl.kiscode;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private IBookManager mBookManager;
    private TextView tvContent;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBookManager = IBookManager.Stub.asInterface(service);
            try {
                mBookManager.addBook(new Book(1, "Android", 99.9));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        Intent intent = new Intent(this, BookManagerService.class);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void initViews() {
        Button btnQuery = findViewById(R.id.btn_query);
        Button btnAdd = findViewById(R.id.btn_add);
        tvContent = findViewById(R.id.tv_content);

        btnQuery.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                addNewBook();
                break;
            case R.id.btn_query:
                queryBookList();
                break;
        }
    }

    private void addNewBook() {
        Random random = new Random();
        int id = random.nextInt();
        double price = random.nextDouble() * 100;
        Book book = new Book(id, "NewBook" + id, price);
        try {
            Log.i(TAG, "addBook in " + Thread.currentThread().getName());
            mBookManager.addBook(book);
            Toast.makeText(this, "Add  " + book.getName(), Toast.LENGTH_SHORT).show();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void queryBookList() {
        try {
            Log.i(TAG, "getBookList in " + Thread.currentThread().getName());
            List<Book> bookList = mBookManager.getBookList();
            tvContent.setText("book size:" + bookList.size() + "\n" + bookList.toString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}