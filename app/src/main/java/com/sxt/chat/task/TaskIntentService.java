package com.sxt.chat.task;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by sxt on 2018/8/1.
 */

public class TaskIntentService extends IntentService {

    private final String TAG = this.getClass().getName();

    public TaskIntentService() {
        super("TaskIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
