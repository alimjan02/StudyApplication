/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sxt.chat.vr.video360;

import android.Manifest.permission;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.google.vr.ndk.base.DaydreamApi;
import com.sxt.chat.R;
import com.sxt.chat.base.BaseActivity;
import com.sxt.chat.vr.video360.rendering.Mesh;

import java.util.ArrayList;

/**
 * Basic Activity to hold {@link MonoscopicView} and render a 360 video in 2D.
 * <p>
 * Most of this Activity's code is related to Android & VR permission handling. The real work is in
 * MonoscopicView.
 * <p>
 * The default intent for this Activity will load a 360 placeholder panorama. For more options on
 * how to load other media using a custom Intent, see {@link MediaLoader}.
 */
public class VideoActivity extends BaseActivity {
    private static final String TAG = "VideoActivity";
    private static final int READ_EXTERNAL_STORAGE_PERMISSION_ID = 1;
    private final int REQUEST_CODE_GALLERY = 100;
    private MonoscopicView videoView;
    private VideoUiView videoUi;

    /**
     * Checks that the appropriate permissions have been granted. Otherwise, the sample will wait
     * for the user to grant the permission.
     *
     * @param savedInstanceState unused in this sample but it could be used to track video position
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_activity);

        // Configure the MonoscopicView which will render the video and UI.
        videoView = (MonoscopicView) findViewById(R.id.video_view);
        videoUi = (VideoUiView) findViewById(R.id.video_ui_view);
        videoView.initialize(videoUi);
        videoUi.setVrIconClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Convert the Intent used to launch the 2D Activity into one that can launch the VR
                        // Activity. This flow preserves the extras and data in the Intent.
                        DaydreamApi api = DaydreamApi.create(VideoActivity.this);
                        if (api != null) {
                            // Launch the VR Activity with the proper intent.
                            Intent intent = DaydreamApi.createVrIntent(
                                    new ComponentName(VideoActivity.this, VrVideoActivity.class));
                            intent.setData(getIntent().getData());
                            intent.putExtra(
                                    MediaLoader.MEDIA_FORMAT_KEY,
                                    getIntent().getIntExtra(MediaLoader.MEDIA_FORMAT_KEY, Mesh.MEDIA_MONOSCOPIC));
                            api.launchInVr(intent);
                            api.close();
                        } else {
                            // Fall back for devices that don't have Google VR Services. This flow should only
                            // be used for older Cardboard devices.
                            Intent intent =
                                    new Intent(getIntent()).setClass(VideoActivity.this, VrVideoActivity.class);
                            intent.removeCategory(Intent.CATEGORY_LAUNCHER);
                            intent.setFlags(0);  // Clear any flags from the previous intent.
                            startActivity(intent);
                        }

                        // See VrVideoActivity's launch2dActivity() for more info about why this finish() call
                        // may be required.
                        finish();
                    }
                });

        // Boilerplate for checking runtime permissions in Android.
        View button = findViewById(R.id.select);
        button.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean b = checkPermission(READ_EXTERNAL_STORAGE_PERMISSION_ID, permission.READ_EXTERNAL_STORAGE, new String[]{permission.READ_EXTERNAL_STORAGE});
                        if (b) {
                            initializeActivity();
                        }
                    }
                });
        button.callOnClick();
    }

    @Override
    public void onPermissionsAllowed(int requestCode, String[] permissions, int[] grantResults) {
        super.onPermissionsAllowed(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeActivity();
            }
        }
    }

    /**
     * Normal apps don't need this. However, since we use adb to interact with this sample, we
     * want any new adb Intents to be routed to the existing Activity rather than launching a new
     * Activity.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        // Save the new Intent which may contain a new Uri. Then tear down & recreate this Activity to
        // load that Uri.
        setIntent(intent);
        recreate();
    }

    /**
     * Initializes the Activity only if the permission has been granted.
     */
    private void initializeActivity() {
        ViewGroup root = (ViewGroup) findViewById(R.id.activity_root);
        for (int i = 0; i < root.getChildCount(); ++i) {
            root.getChildAt(i).setVisibility(View.VISIBLE);
        }
        startGalleryApp();
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


        //4.3以上的action，该action会将文件副本导入我们的应用，即我们看到的是副本
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //4.4以上的action，该action不会将文件副本导入我们的应用，即我们看到的是源文件
        //只显示照片供选择，可以多选
        //        intent.setType("image/*");
        //只显示视频供选择，可以多选
        //        intent.setType("video/*");
        //同时显示照片和视频供选择，此时Intent.EXTRA_ALLOW_MULTIPLE不能为true,即不支持多个文件
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        ArrayList<String> mimes = new ArrayList<>();
        mimes.add("image/*");
        mimes.add("video/*");
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
                    videoView.loadMedia(data);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.onResume();
    }

    @Override
    protected void onPause() {
        // MonoscopicView is a GLSurfaceView so it needs to pause & resume rendering. It's also
        // important to pause MonoscopicView's sensors & the video player.
        videoView.onPause();
        super.onPause();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            initWindowStyle();
        }
    }

    @Override
    protected void onDestroy() {
        videoView.destroy();
        super.onDestroy();
    }
}
