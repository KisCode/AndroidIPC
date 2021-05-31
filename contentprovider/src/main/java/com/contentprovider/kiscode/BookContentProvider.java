package com.contentprovider.kiscode;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/***
 * 独立进程的ContentProvider
 */
public class BookContentProvider extends ContentProvider {
    private static final String TAG = "BookContentProvider";

    //Provider的唯一标识：authorites
    private static final String AUTHORITY = "com.contentprovider.kiscode.provider";

    public static final Uri BOOK_URI = Uri.parse("content://" + AUTHORITY + "/book");
    public static final Uri USER_URI = Uri.parse("content://" + AUTHORITY + "/user");

    private static final int CODE_BOOK_URI = 0;
    private static final int CODE_USER_URI = 1;


    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    static {
        //初始化 添加bookUri和userUir统一管理
        mUriMatcher.addURI(AUTHORITY, "book", CODE_BOOK_URI);
        mUriMatcher.addURI(AUTHORITY, "user", CODE_USER_URI);
    }

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public BookContentProvider() {
    }

    @Override
    public boolean onCreate() {
        Log.i(TAG, "onCreate, thread in" + Thread.currentThread().getName());
        mContext = getContext();
        mDatabase = new DBHelper(mContext).getWritableDatabase();
        return false;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //如与项目运行在同一进程，则该方法运行在主线程
        //如运行在独立进程，则该方法运行在binder子线程
        Log.i(TAG, "update, thread in" + Thread.currentThread().getName());

        String tableName = getTableName(uri);
        int count = mDatabase.delete(tableName, selection, selectionArgs);

        if (count > 0) {
            mContext.getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public String getType(Uri uri) {

        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.i(TAG, "insert, thread in" + Thread.currentThread().getName());
        String tableName = getTableName(uri);
        mDatabase.insert(tableName, null, values);

        //通知观察者进行刷新
        mContext.getContentResolver().notifyChange(uri, null);
        return uri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.i(TAG, "query, thread in" + Thread.currentThread().getName());

        String tableName = getTableName(uri);
        return mDatabase.query(tableName, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        Log.i(TAG, "update, thread in" + Thread.currentThread().getName());
        String tableName = getTableName(uri);
        int count = mDatabase.update(tableName, values, selection, selectionArgs);

        if (count > 0) {
            mContext.getContentResolver().notifyChange(uri, null);
        }
        return count;
    }


    /***
     * 根据uri 获取对应的数据库表名
     * @param uri uri
     * @return 数据库表名
     */
    public String getTableName(Uri uri) {
        String tableName = null;
        switch (mUriMatcher.match(uri)) {
            case CODE_BOOK_URI:
                tableName = DBHelper.BOOK_TABLE_NAME;
                break;
            case CODE_USER_URI:
                tableName = DBHelper.USER_TABLE_NAME;
                break;
            default:
                break;
        }
        return tableName;

    }

}