package com.sxt.chat.utils;

import android.app.Activity;
import android.content.Context;

import java.lang.ref.WeakReference;


/**
 * @Descripition
 * @Auther Tina
 * @CreateTime 2017/5/18
 * @Version
 * @Since
 */
public class ActivityManager {

    private static ActivityManager instance;
    private Context context;

    private ActivityManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized ActivityManager getInstance(Context context) {
        if (instance == null) {
            instance = new ActivityManager(context);

        }
        return instance;
    }

    private WeakReference<Activity> sCurrentActivityWeakRef;

    public Activity getCurrentActivity() {
        Activity currentActivity = null;
        if (sCurrentActivityWeakRef != null) {
            currentActivity = sCurrentActivityWeakRef.get();
        }
        return currentActivity;
    }

    public void setCurrentActivity(Activity activity) {
        sCurrentActivityWeakRef = new WeakReference<Activity>(activity);
    }
}
