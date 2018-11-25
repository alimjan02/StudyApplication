package com.sxt.chat.task;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.sxt.chat.R;
import com.sxt.chat.activity.MainActivity;

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
        Notification.Builder builder;
        Intent in = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, in, 0);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            CharSequence name = "畅玩";
            String description = "畅玩一下";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(String.valueOf(NOTIFY_ID), name, importance);
            mChannel.setSound(null, null);
            mChannel.enableVibration(false);
            mChannel.setDescription(description);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(mChannel);
            }
            builder = new Notification.Builder(this, String.valueOf(NOTIFY_ID));
        } else {
            builder = new Notification.Builder(this);
        }
        startForeground(NOTIFY_ID, builder.setSmallIcon(R.drawable.ic_ar_photo_main_blue_24dp)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getString(R.string.app_name))
                .setContentText("畅玩一下")
                .setContentIntent(pendingIntent)
                .build());

        return START_STICKY;
    }
}
