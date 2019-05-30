package com.sxt.chat.activity;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;
import com.steelkiwi.cropiwa.util.UriUtil;
import com.sxt.chat.R;
import com.sxt.chat.base.BaseActivity;

import java.util.ArrayList;

/**
 * Created by sxt on 2018/10/25.
 */
public class VR360Activity extends BaseActivity {
    private final int REQUEST_CODE_GALLERY = 100;
    private VrPanoramaView vrPanoramaView;
    private ImageView touchFlagImg;
    private boolean flag = false;
    private View titleLayout;
    private boolean titleFlag = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vr_360_layout);
        vrPanoramaView = findViewById(R.id.vrPanoramaView);
        titleLayout = findViewById(R.id.titleLayout);
        touchFlagImg = findViewById(R.id.touch_only);
        vrPanoramaView.setStereoModeButtonEnabled(false);//隐藏眼镜按钮
        vrPanoramaView.setInfoButtonEnabled(false);//隐藏关于按钮
        vrPanoramaView.setFullscreenButtonEnabled(false);//隐藏全屏按钮
        vrPanoramaView.setTransitionViewEnabled(true);

        vrPanoramaView.setTouchTrackingEnabled(true);//设置手触摸
        vrPanoramaView.setFlingingEnabled(true);//设置跟随手指快速滑动
//        vrPanoramaView.setPureTouchTracking(true);//是否 只是手动触摸跟踪 而不使用陀螺仪 google translate 真TM牛逼
        vrPanoramaView.setEventListener(new VrPanoramaEventListener() {
            @Override
            public void onLoadSuccess() {
                super.onLoadSuccess();
                touchFlagImg.setImageResource(R.drawable.ic_sync_disabled_white_24dp);
                vrPanoramaView.setPureTouchTracking(true);//是否 只是手动触摸跟踪 而不使用陀螺仪 google translate 真TM牛逼
                touchFlagImg.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadError(String errorMessage) {
                super.onLoadError(errorMessage);
            }

            @Override
            public void onClick() {
                super.onClick();
                titleFlag = !titleFlag;
                if (titleFlag) {
                    translationYAnimatorForTitleLayout(titleLayout, -titleLayout.getHeight(), 0, 0, 1);
                } else {
                    translationYAnimatorForTitleLayout(titleLayout, 0, -titleLayout.getHeight(), 1, 0);
                }
            }
        });

        touchFlagImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vrPanoramaView.setPureTouchTracking(flag);
                flag = !flag;
                touchFlagImg.setImageResource(flag ? R.drawable.ic_sync_white_24dp : R.drawable.ic_sync_disabled_white_24dp);
            }
        });
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGallery();
            }
        });
        vrPanoramaView.post(new Runnable() {
            @Override
            public void run() {
                try {
                    VrPanoramaView.Options options = new VrPanoramaView.Options();
                    options.inputType = VrPanoramaView.Options.TYPE_MONO;
                    Bitmap bitmap = BitmapFactory.decodeStream(getAssets().open("panoramas/vr_placeholder.jpg"));
                    vrPanoramaView.loadImageFromBitmap(bitmap, options);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast("加载失败,请重试~");
                }
            }
        });
    }

    public void startGallery() {
        boolean b = checkPermission(REQUEST_CODE_GALLERY, Manifest.permission_group.STORAGE, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
        if (b) {
            startGalleryApp();
        }
    }

    private void startGalleryApp() {
//        Intent intent = new Intent();
////        intent.setType("image/*");
//        intent.setType("video/*");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//        } else {
//            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
//        }

//        intent = Intent.createChooser(intent, "选择图片");


//        4.3以上的action，该action会将文件副本导入我们的应用，即我们看到的是副本
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        4.4以上的action，该action不会将文件副本导入我们的应用，即我们看到的是源文件
//        只显示照片供选择，可以多选
//                intent.setType("image/*");
//        只显示视频供选择，可以多选
//                intent.setType("video/*");
//        同时显示照片和视频供选择，此时Intent.EXTRA_ALLOW_MULTIPLE不能为true,即不支持多个文件
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        ArrayList<String> mimes = new ArrayList<>();
        mimes.add("image/*");
//        mimes.add("video/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimes);
        //４．３以上的设备才支持Intent.EXTRA_ALLOW_MULTIPLE，是否可以一次选择多个文件
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        //返回的文件是否必须存在于设备上，而不是需要从远程服务下载的,用于解决用户选中的是云端文件时的问题
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, false);
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_GALLERY) {
                if (data != null && data.getData() != null) {
                    try {
                        VrPanoramaView.Options options = new VrPanoramaView.Options();
                        options.inputType = VrPanoramaView.Options.TYPE_MONO;
                        Bitmap bitmap = BitmapFactory.decodeFile(UriUtil.uri2Path(this, data.getData()));
                        vrPanoramaView.loadImageFromBitmap(bitmap, options);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast("加载失败,请重试~");
                    }
                }
            }
        }
    }

    /**
     * title栏Y轴执行位移动画
     */
    private void translationYAnimatorForTitleLayout(View targetView, int startTranslationY, int endTranslationY, int startAlpha, int endAlpha) {
        ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(targetView, "translationY", startTranslationY, endTranslationY).setDuration(200);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(targetView, "alpha", startAlpha, endAlpha);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.playTogether(translationAnimator, alphaAnimator);
        animatorSet.start();
    }

    @Override
    public void onPermissionsAllowed(int requestCode, String[] permissions, int[] grantResults) {
        super.onPermissionsAllowed(requestCode, permissions, grantResults);
        if (REQUEST_CODE_GALLERY == requestCode) {
            startGalleryApp();
        }
    }

    @Override
    public void onPermissionsRefusedNever(int requestCode, String[] permissions, int[] grantResults) {
        super.onPermissionsRefusedNever(requestCode, permissions, grantResults);
        if (REQUEST_CODE_GALLERY == requestCode) {
            Toast(R.string.allow_READ_EXTERNAL_STORAGE);
            goToAppSettingsPage();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            initWindowStyle();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        vrPanoramaView.resumeRendering();
    }

    @Override
    protected void onPause() {
        super.onPause();
        vrPanoramaView.pauseRendering();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vrPanoramaView.shutdown();
    }
}
