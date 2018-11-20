/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sxt.chat.activity;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import com.sxt.chat.R;
import com.sxt.chat.base.HeaderActivity;
import com.sxt.chat.utils.NotificationHelper;

/**
 * Display main screen for sample. Displays controls for sending test notifications.
 */
public class NotifycationActivity extends HeaderActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int NOTI_PRIMARY1 = 1100;
    private static final int NOTI_PRIMARY2 = 1101;
    private static final int NOTI_SECONDARY1 = 1200;
    private static final int NOTI_SECONDARY2 = 1201;
    private TextView titlePrimary;
    private TextView titleSecondary;

    private NotificationHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifycation);
        setTitle("通知演示");
        titlePrimary = findViewById(R.id.main_primary_title);
        findViewById(R.id.main_primary_send1).setOnClickListener(this);
        findViewById(R.id.main_primary_send2).setOnClickListener(this);
        findViewById(R.id.main_primary_config).setOnClickListener(this);

        titleSecondary = (TextView) findViewById(R.id.main_secondary_title);
        findViewById(R.id.main_secondary_send1).setOnClickListener(this);
        findViewById(R.id.main_secondary_send2).setOnClickListener(this);
        findViewById(R.id.main_secondary_config).setOnClickListener(this);
        findViewById(R.id.btnA).setOnClickListener(this);

        helper = new NotificationHelper(this);
    }

    /**
     * 发送通知
     *
     * @param id    创建的通知的ID
     * @param title 通知的Title
     */
    public void sendNotification(int id, String title) {
        Notification.Builder nb = null;
        switch (id) {
            case NOTI_PRIMARY1:
                nb = helper.getNotification1(title, getString(R.string.primary1_body));
                break;

            case NOTI_PRIMARY2:
                nb = helper.getNotification1(title, getString(R.string.primary2_body));
                break;

            case NOTI_SECONDARY1:
                nb = helper.getNotification2(title, getString(R.string.secondary1_body));
                break;

            case NOTI_SECONDARY2:
//                nb = helper.getNotification2(title, getString(R.string.secondary2_body));
                helper.notify(id, helper.getCustomNotification());
                break;
        }
        if (nb != null) {
            helper.notify(id, nb);
        }
    }

    /**
     * 跳转到当前app系统通知设置界面
     */
    public void goToNotificationSettings() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Intent i = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            i.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            startActivity(i);
        }
    }

    /**
     * 跳转到当前app系统通知设置界面 (具体的某一条通道channel)
     *
     * @param channel 通道名称
     */
    public void goToNotificationSettings(String channel) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Intent i = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
            i.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            i.putExtra(Settings.EXTRA_CHANNEL_ID, channel);
            startActivity(i);
        }
    }

    private String getTitlePrimaryText() {
        if (titlePrimary != null) {
            return titlePrimary.getText().toString();
        }
        return "";
    }

    private String getTitleSecondaryText() {
        if (titlePrimary != null) {
            return titleSecondary.getText().toString();
        }
        return "";
    }

    int ID;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_primary_send1:
                sendNotification(NOTI_PRIMARY1, getTitlePrimaryText());
                break;
            case R.id.main_primary_send2:
                sendNotification(NOTI_PRIMARY2, getTitlePrimaryText());
                break;
            case R.id.main_primary_config:
                goToNotificationSettings(NotificationHelper.PRIMARY_CHANNEL);
                break;

            case R.id.main_secondary_send1:
                sendNotification(NOTI_SECONDARY1, getTitleSecondaryText());
                break;
            case R.id.main_secondary_send2:
                sendNotification(1080, getTitleSecondaryText());
                break;
            case R.id.main_secondary_config:
                goToNotificationSettings(NotificationHelper.SECONDARY_CHANNEL);
                break;
            case R.id.btnA:
                goToNotificationSettings();
                break;
            default:
                break;
        }
    }
}
