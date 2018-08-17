package com.sxt.chat.task;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.sxt.chat.App;

/**
 * Created by sxt on 2018/8/1.
 */

public class MainService extends Service {

    private final String TAG = this.getClass().getName();
    public static final int NOTIFY_ID = 100;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "服务创建成功");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        Notification notification;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            CharSequence name = "MyStreamingApplication";
            String description = "radio";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(String.valueOf(NOTIFY_ID), name, importance);
            mChannel.setSound(null, null);
            mChannel.enableVibration(false);
            mChannel.setDescription(description);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(mChannel);
            }
            notification = new Notification.Builder(this, String.valueOf(NOTIFY_ID)).build();
        } else {
            notification = new Notification();
        }

        startForeground(NOTIFY_ID, notification);
        return START_STICKY;
    }
}
