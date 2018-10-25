package com.sxt.chat.activity;

import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.sxt.chat.R;
import com.sxt.chat.base.BaseActivity;

import java.io.File;

/**
 * Created by sxt on 2018/10/25.
 */
public class ThreeDActivity extends BaseActivity {

//    private GLSurfaceView glSurfaceView;
    private SurfaceView glSurfaceView;
    private boolean taking;
    private boolean successTakePhoto;
    private Camera mCamera;
    private View takePhoto;
    private ImageView imgCenter;
    private File file;
    private long delayMillis = 1500L;
    private final int MSG_AUTOFUCS = 100;
    private Camera.AutoFocusCallback autoFocusCallback;
    private String TAG = "TakePhoto";
    private SurfaceHolder surfaceHolder;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.v(TAG, "" + msg.what);
            switch (msg.what) {
                case MSG_AUTOFUCS:
                    if (mCamera != null && !taking) {
                        try {
                            mCamera.autoFocus(autoFocusCallback);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    };
    private SurfaceHolder.Callback callback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3d_layout);
        glSurfaceView = findViewById(R.id.gl_surface_view);
        surfaceHolder = glSurfaceView.getHolder();
//        glSurfaceView.setRenderer(new GLSurfaceView.Renderer() {
//            @Override
//            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//                if (null == mCamera) {
//                    mCamera = getCameraInstance();
//                }
//                try {
//                    mCamera.setPreviewDisplay(glSurfaceView.getHolder());
//                    mCamera.autoFocus(autoFocusCallback);
//                    mCamera.startPreview();
//                } catch (Exception e) {
//                    Log.d(TAG, "Error setting mCamera preview: " + e.getMessage());
//                }
//            }
//
//            @Override
//            public void onSurfaceChanged(GL10 gl, int width, int height) {
//                refreshCamera(); // 这一步是否多余？在以后复杂的使用场景下，此步骤是必须的。
//                int rotation = getDisplayOrientation(); //获取当前窗口方向
//                mCamera.setDisplayOrientation(rotation); //设定相机显示方向
//            }
//
//            @Override
//            public void onDrawFrame(GL10 gl) {
//
//            }
//        });
        callback = new SurfaceHolder.Callback() {
            public void surfaceCreated(SurfaceHolder holder) {
                if (null == mCamera) {
                    mCamera = getCameraInstance();
                }
                try {
                    if (mCamera != null) {
                        mCamera.setPreviewDisplay(holder);
                        mCamera.autoFocus(autoFocusCallback);
                        mCamera.startPreview();
                    } else {
                        Log.d(TAG, "Error setting mCamera preview: Camera is null");
                    }
                } catch (Exception e) {
                    Log.d(TAG, "Error setting mCamera preview: " + e.getMessage());
                }
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.i(TAG, "callback");
                refreshCamera(); // 这一步是否多余？在以后复杂的使用场景下，此步骤是必须的。
                int rotation = getDisplayOrientation(); //获取当前窗口方向
                mCamera.setDisplayOrientation(rotation); //设定相机显示方向
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                holder.removeCallback(this);
                mHandler.removeCallbacksAndMessages(null);
                if (mCamera != null) {
                    mCamera.setPreviewCallback(null);
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;
                }
            }
        };
        glSurfaceView.getHolder().addCallback(callback);
        autoFocusCallback = new Camera.AutoFocusCallback() {

            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (mHandler != null && !taking) {
                    mHandler.sendEmptyMessageDelayed(MSG_AUTOFUCS, delayMillis);
                }
            }
        };
    }

    // 暂停时执行的动作：把相机关闭，避免占用导致其他应用无法使用相机
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        mHandler.removeCallbacksAndMessages(null);
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    // 恢复apk时执行的动作
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        start();
    }

    private void start() {
        mHandler.sendEmptyMessage(MSG_AUTOFUCS);
        if (null == mCamera) {
            mCamera = getCameraInstance();
            surfaceHolder.addCallback(callback);
            if (mCamera == null) return;
        }
        mCamera.startPreview();
    }

    public Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
            if (c == null) {
                int cametacount = Camera.getNumberOfCameras();
                c = Camera.open(cametacount - 1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    // 获取当前窗口管理器显示方向
    private int getDisplayOrientation() {
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        Camera.CameraInfo camInfo =
                new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, camInfo);

        // 这里其实还是不太懂：为什么要获取camInfo的方向呢？相当于相机标定？？
        int result = (camInfo.orientation - degrees + 360) % 360;

        return result;
    }

    // 刷新相机
    private void refreshCamera() {
        if (surfaceHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
            Log.i(TAG, e.toString());
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
            mCamera.autoFocus(autoFocusCallback);
        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }
    }

    private void setCameraPictureSize() {
        try {
            WindowManager windowManager = getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            int screenWidth = display.getWidth();
            Camera.Parameters mParameters = mCamera.getParameters();
            Camera.Size size = null;
            Log.i(TAG, "screenWidth ==  " + screenWidth);
            for (int i = 0; i < mParameters.getSupportedPictureSizes().size(); i++) {
                if (Math.abs(screenWidth - mParameters.getSupportedPictureSizes().get(i).width) == 0) {
                    size = mParameters.getSupportedPictureSizes().get(i);
                    break;
                }
                Log.i(TAG, "width  ==  " + mParameters.getSupportedPictureSizes().get(i).width + "  height  ==  " + mParameters.getSupportedPictureSizes().get(i).height);
            }
            if (size == null) {
                size = mParameters.getSupportedPictureSizes().get(mParameters.getSupportedPictureSizes().size() / 2);
            }
            mParameters.setPictureSize(size.width, size.height);
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
