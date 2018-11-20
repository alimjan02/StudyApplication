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

package com.sxt.chat.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.sxt.chat.R;

/**
 * Helper class to manage notification channels, and create notifications.
 */
public class NotificationHelper extends ContextWrapper {
    private Context context;
    private NotificationManager manager;
    public static final String PRIMARY_CHANNEL = "default";
    public static final String SECONDARY_CHANNEL = "second";
    public static final String CUSTOM_NOTIFY_CHANNEL = "CUSTOM_NOTIFY_CHANNEL";
    public static final String GROUP_KEY_WORK_CHAT = "com.sxt.chat";

    /**
     * Registers notification channels, which can be used later by individual notifications.
     */
    public NotificationHelper(Context ctx) {
        super(ctx);
        context = ctx;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel chan1 = new NotificationChannel(PRIMARY_CHANNEL,
                    "通知1未分组的Channel", NotificationManager.IMPORTANCE_HIGH);
            chan1.setLightColor(Color.GREEN);
            Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notify_message);
            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();

            chan1.setSound(sound, att);
            chan1.setShowBadge(true);//设置桌面上的app启动图标是否显示未读圆点
            chan1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            getManager().createNotificationChannel(chan1);

            NotificationChannel chan2 = new NotificationChannel(SECONDARY_CHANNEL,
                    "通知2已分组的Channel", NotificationManager.IMPORTANCE_HIGH);
            chan2.setLightColor(Color.RED);
            chan2.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(chan2);

            NotificationChannel chanCustom = new NotificationChannel(CUSTOM_NOTIFY_CHANNEL,
                    "Custom Layout Channel", NotificationManager.IMPORTANCE_HIGH);
            chanCustom.setLightColor(Color.BLUE);
            chanCustom.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(chanCustom);
        }
    }

    /**
     * 创建通道1的通知
     */
    public Notification.Builder getNotification1(String title, String body) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new Notification.Builder(getApplicationContext(), PRIMARY_CHANNEL)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setLargeIcon(getLargeIcon())
//                    .setBadgeIconType(Notification.BADGE_ICON_LARGE)
                    .setSmallIcon(R.drawable.ic_ar_photo_main_blue_24dp)
                    .setAutoCancel(true);
        }
        return null;
    }

    /**
     * 创建通道2的通知
     */
    public Notification.Builder getNotification2(String title, String body) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new Notification.Builder(getApplicationContext(), SECONDARY_CHANNEL)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setLargeIcon(getLargeIcon())
                    .setSmallIcon(getSmallIcon())
                    .setAutoCancel(true);
        }
        return null;
    }

    /**
     * 创建自定义通知
     */
    public NotificationCompat.Builder getCustomNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 获取自定义通知的布局
            RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_small);
            RemoteViews notificationLayoutExpanded = new RemoteViews(getPackageName(), R.layout.notification_large);
            //将布局设置到通知上
            return new NotificationCompat.Builder(context, CUSTOM_NOTIFY_CHANNEL)
                    .setSmallIcon(getSmallIcon())
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(notificationLayout)
                    .setCustomBigContentView(notificationLayoutExpanded);
        }
        return null;
    }

    /**
     * 发送通知
     *
     * @param id           当前通知的ID
     * @param notification The notification object
     */
    public void notify(int id, Notification.Builder notification) {
        getManager().notify(id, notification.build());
    }

    public void notify(int id, NotificationCompat.Builder notification) {
        getManager().notify(id, notification.build());
    }

    /**
     * 通知栏显示大图标
     */
    private Bitmap getLargeIcon() {
        return BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_round);
    }

    /**
     * 通知栏显示的小图标
     */
    private int getSmallIcon() {
        return R.drawable.ic_vector_notifications_24dp;
    }

    /**
     * 获取 notification manager.
     */
    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }
}
