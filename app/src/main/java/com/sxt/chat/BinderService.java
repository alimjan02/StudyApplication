package com.sxt.chat;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by sxt on 2018/7/30.
 */
public class BinderService extends Service {

    final String TAG = this.getClass().getName();

    @Override
    public IBinder onBind(Intent intent) {
        return new ServiceBinder();
    }

    public class ServiceBinder extends Binder {

        public ServiceBinder() {
            Log.i(TAG, "ServiceBinder --> Created");
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "BinderService --> onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "BinderService --> onDestroy");
    }
}
