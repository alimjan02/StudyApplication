package com.sxt.chat.task;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.sxt.chat.App;
import com.sxt.chat.R;
import com.sxt.chat.activity.MainActivity;

/**
 * Created by sxt on 2018/8/1.
 */

public class TaskService extends Service {
    private final String TAG = this.getClass().getName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification.Builder notificationBuilder = new Notification.Builder(App.getCtx())
                .setContentTitle(getString(R.string.app_name))
                .setContentText("提升为前台进程")
                .setSmallIcon(R.mipmap.app_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.app_icon))
                .setContentIntent(PendingIntent.getActivity(App.getCtx(), 0, new Intent(App.getCtx(), MainActivity.class), 0));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setChannelId(TAG);
        }
        startForeground(1, notificationBuilder.build());
        Log.i(TAG, "onStartCommand");
        AlarmTaskManager.getInstance(App.getCtx()).createAlarm();
        return super.onStartCommand(intent, flags, startId);
    }
}
