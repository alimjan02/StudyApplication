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

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.sxt.chat.App;
import com.sxt.chat.R;
import com.sxt.chat.base.HeaderActivity;
import com.sxt.chat.utils.NotificationHelper;
import com.sxt.chat.utils.glide.GlideRoundTransformer;

/**
 * Display main screen for sample. Displays controls for sending test notifications.
 */
public class NotifycationActivity extends HeaderActivity implements View.OnClickListener {

    private Uri uri;
    private EditText editText;
    private NotificationHelper helper;
    private static final int NOTI_1 = 10086;
    private static final int NOTI_2 = 10088;
    private static final int NOTI_3 = 10087;
    private int NOTI_ID_1 = NOTI_1;
    private int NOTI_ID_2 = NOTI_2;
    private int NOTI_ID_3 = NOTI_3;
    private final int REQUEST_CODE_GALLERY = 100;
    private final String URL0 = "http://bmob-cdn-18541.b0.upaiyun.com/2018/10/14/53754835404593e68021286e340d9d6e.jpg";
    private final String URL1 = "http://bmob-cdn-18541.b0.upaiyun.com/2018/10/14/06e128cf40717a69805ee61fc9201842.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifycation);
        setTitle("通知演示");
        editText = findViewById(R.id.editeText);
        findViewById(R.id.send1).setOnClickListener(this);
        findViewById(R.id.send2).setOnClickListener(this);
        findViewById(R.id.send3).setOnClickListener(this);
        findViewById(R.id.config).setOnClickListener(this);
        findViewById(R.id.btnA).setOnClickListener(this);

        helper = new NotificationHelper(this);
    }

    /**
     * 发送通知
     *
     * @param id 创建的通知的ID
     */
    public void sendNotification(int id, final String body) {
        final NotificationCompat.Action action = new NotificationCompat.Action(R.drawable.ic_ar_photo_main_blue_24dp, String.valueOf("点我哦~"), getPendingIntent(this, MainActivity.class));
        switch (id) {
            case NOTI_1:
                helper.notify(NOTI_ID_1--, helper.buildNotificationText("文本通知 Title", body, getPendingIntent(this, MainActivity.class), action, action, action));
                break;

            case NOTI_2:
                Glide.with(this)
                        .load(uri != null ? uri : URL1)
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap source, GlideAnimation<? super Bitmap> glideAnimation) {
//                                float radius = Resources.getSystem().getDisplayMetrics().density * 8;
//                                Bitmap bitmap = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
//                                Canvas canvas = new Canvas(bitmap);
//                                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//                                paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
//                                RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
//                                canvas.drawRoundRect(rectF, radius, radius, paint);
//
//                                ImageView img = findViewById(R.id.img);
//                                img.setImageBitmap(bitmap);
                                helper.notify(NOTI_ID_2++, helper.buildNotificationImage("图片通知 Title", body,
                                        source,
                                        getPendingIntent(App.getCtx(), MainActivity.class)));
                            }
                        });

                break;

            case NOTI_3:
                helper.notify(NOTI_ID_3, helper.buildCustomNotificationDefault("自定义通知  Title", body, getPendingIntent(this, MainActivity.class)));
                break;
        }
    }

    private PendingIntent getPendingIntent(Context context, Class<?> cls) {
        Intent in = new Intent(context, cls);
        return PendingIntent.getActivity(this, 0, in, 0);
    }

    /**
     * 跳转到当前app系统通知设置界面
     */
    public void goToNotificationSettings() {
        Intent intent = new Intent();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        } else {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
        }
        startActivity(intent);
    }

    /**
     * 跳转到当前app系统通知设置界面 (具体的某一条通道channel)
     *
     * @param channel 通道名称
     */
    public void goToNotificationSettings(String channel) {
        Intent intent = new Intent();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel);
        } else {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
        }
        startActivity(intent);
    }

    private String getTitleText() {
        if (editText != null) {
            return editText.getText().toString();
        }
        return "";
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send1:
                sendNotification(NOTI_1, getTitleText());
                break;
            case R.id.send2:
                sendNotification(NOTI_2, getTitleText());
                break;
            case R.id.send3:
                sendNotification(NOTI_3, getTitleText());
                break;
            case R.id.config:
                goToNotificationSettings(NotificationHelper.DEFAULT_CHANNEL);
                break;
            case R.id.btnA:
                goToNotificationSettings();
                break;
            default:
                break;
        }
    }

    public void startGallery(View view) {
        boolean b = checkPermission(REQUEST_CODE_GALLERY, Manifest.permission_group.STORAGE, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
        if (b) {
            startGalleryApp();
        }
    }

    private void startGalleryApp() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        }

        intent = Intent.createChooser(intent, "选择图片");
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_GALLERY) {
                if (data != null && data.getData() != null) {
                    this.uri = data.getData();
                    Glide.with(this)
                            .load(data.getData())
                            .transform(new CenterCrop(this), new GlideRoundTransformer(this, 8))
                            .into((ImageView) findViewById(R.id.img));
                }
            } else {
                Toast("所选图片有误,请选择其他图片");
            }
        }
    }

    @Override
    public void onPermissionsaAlowed(int requestCode, String[] permissions, int[] grantResults) {
        super.onPermissionsaAlowed(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_GALLERY:
                startGalleryApp();
                break;
        }
    }

    @Override
    public void onPermissionsRefusedNever(int requestCode, String[] permissions,
                                          int[] grantResults) {
        super.onPermissionsRefusedNever(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_GALLERY:
                goToAppSettingsPage();
                Toast(R.string.allow_WRITE_EXTERNAL_STORAGE);
                break;
        }
    }
}
