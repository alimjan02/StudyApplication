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
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
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
import com.sxt.chat.dialog.AlertDialogBuilder;
import com.sxt.chat.utils.NotificationHelper;
import com.sxt.chat.utils.glide.GlideRoundTransformer;

import static com.sxt.chat.utils.NotificationHelper.DEFAULT_CHANNEL;

public class NotificationActivity extends HeaderActivity implements View.OnClickListener {

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
    private final String URL1 = "http://bmob-cdn-25616.b0.upaiyun.com/2019/05/11/18964015403dfc5e8012e7ce547aca05.webp";

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

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel hahahah = helper.getManager().getNotificationChannel(DEFAULT_CHANNEL);
            Log.e("NotificationChannel", String.format("importance %s , flag %s", hahahah.getImportance() == NotificationManager.IMPORTANCE_NONE, hahahah.getImportance()));

        }


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
                                loadImage();
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

    private void loadImage() {
        Glide.with(App.getCtx())
                .load(uri != null ? uri : URL1)
                .transform(new CenterCrop(App.getCtx()), new GlideRoundTransformer(App.getCtx(), 8))
                .into((ImageView) findViewById(R.id.img));
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
                goToNotificationSettings(DEFAULT_CHANNEL);
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
                    loadImage();
                }
            } else {
                Toast("所选图片有误,请选择其他图片");
            }
        }
    }

    @Override
    public void onPermissionsAllowed(int requestCode, String[] permissions, int[] grantResults) {
        super.onPermissionsAllowed(requestCode, permissions, grantResults);
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
                String appName = getString(R.string.app_name);
                String message = String.format(getString(R.string.permission_request_READ_EXTERNAL_STORAGE), appName);
                SpannableString span = new SpannableString(message);
                span.setSpan(new TextAppearanceSpan(this, R.style.text_color_2_15_style), 0, message.indexOf(appName), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                int start = message.indexOf(appName) + appName.length();
                span.setSpan(new TextAppearanceSpan(this, R.style.text_color_1_17_bold_style), message.indexOf(appName), start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                span.setSpan(new TextAppearanceSpan(this, R.style.text_color_2_15_style), start, message.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                showPermissionRefusedNeverDialog(span);
                break;
        }
    }

    /**
     * 权限被彻底禁止后 , 弹框提醒用户去开启
     */
    private void showPermissionRefusedNeverDialog(CharSequence message) {
        new AlertDialogBuilder(this)
                .setTitle(R.string.message_alert, true)
                .setMessage(message)
                .setLeftButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setRightButton(R.string.confirm, (dialog, which) -> {
                    dialog.dismiss();
                    goToAppSettingsPage();
                })
                .setShowLine(true)
                .setCanceledOnTouchOutside(false)
                .show();
    }

}
