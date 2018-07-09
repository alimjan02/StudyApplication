package com.sxt.chat.utils;

import android.app.Activity;

import com.sxt.chat.activity.LoginActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @Descripition
 * @Auther Tina
 * @CreateTime 2017/5/19
 * @Version
 * @Since
 */
public class ActivityCollector {

    public static Activity current;
    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public static void finishAllExceptLogin() {
        for (Activity activity : activities) {
            if (!activity.isFinishing() && (!(activity instanceof LoginActivity)/* && !(activity instanceof MainActivity)*/)) {
                activity.finish();
            }
        }
    }

}
