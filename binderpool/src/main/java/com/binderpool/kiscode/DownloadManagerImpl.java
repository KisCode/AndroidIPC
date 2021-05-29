package com.binderpool.kiscode;


import android.os.RemoteException;
import android.util.Log;

import com.binderpool.kiscode.constants.Config;

/****
 * Description:
 * Author:  keno
 * CreateDate: 2021/5/29 21:11
 */

public class DownloadManagerImpl extends IDownloadManager.Stub {
    @Override
    public void download(String url) throws RemoteException {
        Log.i(Config.LOG_TAG, "DownloadManagerImp download,url is " + url);
    }

    @Override
    public void stopAll() throws RemoteException {
        Log.i(Config.LOG_TAG, "DownloadManagerImp stopAll");
    }
}
