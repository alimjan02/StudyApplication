package com.sxt.chat.task;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

/**
 * Created by sxt on 2018/8/1.
 */

public class AlarmTaskManager {

    private static AlarmTaskManager alarmTaskManager;
    private static AlarmManager alarmManager;
    private static Context context;
    private long delay = 10 * 1000L;

    private AlarmTaskManager() {
    }

    public static synchronized AlarmTaskManager getInstance(Context ctx) {
        context = ctx;
        if (alarmTaskManager == null) {
            alarmTaskManager = new AlarmTaskManager();
        }
        return alarmTaskManager;
    }

    public AlarmTaskManager createAlarm() {
        if (alarmManager == null) {
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }
        long atTime = SystemClock.elapsedRealtime() + delay;
        Intent intent = new Intent(context, MainService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {//19
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, atTime, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, atTime, pendingIntent);
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, atTime, pendingIntent);
        }
        return this;
    }
}
