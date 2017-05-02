package com.jason.chat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Jason on 2/14/2017.
 */

public class ChatSearch extends Service {

    private static final String TAG = "ChatSearch";

    public ChatSearch() {

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
