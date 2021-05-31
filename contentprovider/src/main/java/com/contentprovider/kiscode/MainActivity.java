package com.contentprovider.kiscode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private final String authorities = "content://com.contentprovider.kiscode.provider";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Uri bookUri = BookContentProvider.BOOK_URI;
        ContentResolver contentResolver = getContentResolver();


        ContentValues book = new ContentValues();
        book.put("_id", 0);
        book.put("name", "Java8 in Code");

        ContentValues book1 = new ContentValues();
        book1.put("_id", 1);
        book1.put("name", "Effective C++");
        ContentValues book2 = new ContentValues();
        book2.put("_id", 2);
        book2.put("name", "Effective Java");
        ContentValues book3 = new ContentValues();
        book3.put("_id", 3);
        book3.put("name", "Effective CSS");

        contentResolver.insert(bookUri, book);
        contentResolver.insert(bookUri, book1);
        contentResolver.insert(bookUri, book2);
        contentResolver.insert(bookUri, book3);

        Cursor cursor = contentResolver.query(bookUri, null, null, null, null);
        while (cursor.moveToNext()) {
            Log.i(TAG, "_id=" + cursor.getInt(0) + ",name=" + cursor.getString(1));
        }
        cursor.close();


        Log.i(TAG, "------------------------Delete------------------------");
        contentResolver.delete(bookUri, "name like ? ", new String[]{"%Java8%"});


        Log.i(TAG, "------------------------Updadate------------------------");
        book2.put("name", "Effective Java(第二版)");
        book3.put("name", "Effective C#");

        //更新数据库
        contentResolver.update(bookUri, book3, "_id = ?", new String[]{book3.getAsString("_id ")});
        contentResolver.update(bookUri, book2, "name like ? ", new String[]{"%Java%"});
//        contentResolver.update(bookUri, book4, "name like ? ", new String[]{"%C++%"});
        cursor = contentResolver.query(bookUri, null, null, null, null);
        while (cursor.moveToNext()) {
            Log.i(TAG, "_id=" + cursor.getInt(0) + ",name=" + cursor.getString(1));
        }
        cursor.close();


        Log.i(TAG, "------------------------Insert User------------------------");
        Uri userUri = BookContentProvider.USER_URI;
        ContentValues user1 = new ContentValues();
        user1.put("_id", 0);
        user1.put("name", "Admin");
        user1.put("age", 25);
        contentResolver.insert(userUri, user1);
        cursor = contentResolver.query(userUri, null, null, null, null);
        while (cursor.moveToNext()) {
            Log.i(TAG, "_id=" + cursor.getInt(0)
                    + ",name=" + cursor.getString(1)
                    + ",age=" + cursor.getInt(2)
            );
        }
        cursor.close();

    }
}