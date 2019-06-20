package com.sxt.chat.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sxt.chat.R;
import com.sxt.chat.base.HeaderActivity;

import java.util.Collections;

public class ShortcutActivity extends HeaderActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortcut_layout);
        setTitle(R.string.shortcut);
        //动态创建快捷方式
        createDynamicShortcut();
        //创建固定的快捷方式
        createPinnedShortcut();
    }

    private void createPinnedShortcut() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);

            if (shortcutManager.isRequestPinShortcutSupported()) {
                // Assumes there's already a shortcut with the ID "my-shortcut".
                // The shortcut must be enabled.
                ShortcutInfo pinShortcutInfo =
                        new ShortcutInfo.Builder(this, "my-pinned—shortcut")
                                .setShortLabel("固定的快捷方式")
                                .setIcon(Icon.createWithResource(this, R.drawable.ic_ar_photo_main_blue_24dp))
                                .setIntent(new Intent(Intent.ACTION_VIEW,
                                        null, this, VR360Activity.class))
                                .build();

                //如果用户允许固定快捷方式。请注意，如果固定操作失败，则不会通知您的应用。
                //我们假设app已经实现了一个 createShortcutResultIntent（）的方法返回广播意图。
                Intent pinnedShortcutCallbackIntent =
                        shortcutManager.createShortcutResultIntent(pinShortcutInfo);

                //配置意图，以便应用程序的广播接收器回调成功的广播。
                PendingIntent successCallback = PendingIntent.getBroadcast(this, /* request code */ 0,
                        pinnedShortcutCallbackIntent, /* flags */ 0);

                shortcutManager.requestPinShortcut(pinShortcutInfo,
                        successCallback.getIntentSender());
            }
        }
    }

    private void createDynamicShortcut() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
            ShortcutInfo shortcut = new ShortcutInfo.Builder(this, "my-dynamic—shortcut")
                    .setShortLabel("动态创建的shortcut")
                    .setLongLabel("动态创建的shortcut-打开网页")
                    .setIcon(Icon.createWithResource(this, R.drawable.ic_ar_photo_main_blue_24dp))
                    .setIntent(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://blog.csdn.net/sxt_zls")))
                    .build();

            shortcutManager.setDynamicShortcuts(Collections.singletonList(shortcut));
        }
    }
}
