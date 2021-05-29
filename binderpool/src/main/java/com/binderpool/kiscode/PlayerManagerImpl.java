package com.binderpool.kiscode;


import android.os.RemoteException;
import android.util.Log;

import com.binderpool.kiscode.constants.Config;

import java.util.List;

/****
 * Description:
 * Author:  keno
 * CreateDate: 2021/5/29 21:29
 */

public class PlayerManagerImpl extends IPlayerManager.Stub {
    @Override
    public void play(Song song) throws RemoteException {
        Log.i(Config.LOG_TAG, "PlayerManagerImpl play " + song);
    }

    @Override
    public void pause() throws RemoteException {
        Log.i(Config.LOG_TAG, "PlayerManagerImpl pause");

    }

    @Override
    public void stop() throws RemoteException {
        Log.i(Config.LOG_TAG, "PlayerManagerImpl stop");
    }
}
