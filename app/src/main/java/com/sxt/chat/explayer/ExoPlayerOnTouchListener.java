package com.sxt.chat.explayer;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

public class ExoPlayerOnTouchListener implements View.OnTouchListener {

    private String TAG = "Video";
    private long newPosition;
    private float width, height;
    private int currentVolume, maxVolume;
    private float moveX, moveY, downX, downY;
    private boolean isPosition, isVolume, isAppha;
    private int touchSlop;

    private SimpleExoPlayer player;
    private AudioManager audiomanager;
    private Activity activity;
    private OnTouchInfoListener onTouchInfoListener;


    public ExoPlayerOnTouchListener(Activity activity, final PlayerView exoPlayerView, final SimpleExoPlayer player) {
        this.player = player;
        this.activity = activity;
        exoPlayerView.post(new Runnable() {
            @Override
            public void run() {
                width = exoPlayerView.getWidth();
                height = exoPlayerView.getHeight();
                Log.e(TAG, "width " + width + " height " + height);
            }
        });
        audiomanager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        if (audiomanager != null) {
            maxVolume = audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 获取系统最大音量
            currentVolume = audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC); // 获取当前值
        }
        touchSlop = ViewConfiguration.get(activity).getScaledTouchSlop();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = motionEvent.getX();
                downY = motionEvent.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = motionEvent.getX();
                moveY = motionEvent.getY();
                float dx = moveX - downX;
                float dy = moveY - downY;

                //直播没有进度滑动 所以屏蔽进度手势
                if (/*!isPosition &&*/ !isAppha && !isVolume && (Math.abs(dx) > touchSlop || Math.abs(dy) > touchSlop)) {
                    if (Math.abs(dx) >= Math.abs(dy)) {
                        isPosition = true;
                    } else {
                        if (moveX < width / 2) {
                            isAppha = true;
                        } else {
                            isVolume = true;
                        }
                    }
                }
                if (isPosition) {
                    float rateWidth = dx / width;
                    newPosition = (long) (rateWidth * player.getDuration());
                    long currentPosition = player.getCurrentPosition() + newPosition;
                    currentPosition = currentPosition <= 0 ? 0 : currentPosition >= player.getDuration() ? player.getDuration() : currentPosition;
                    int progress = (int) (((float) currentPosition) / ((float) player.getDuration()) * 100);

                    if (onTouchInfoListener != null) {
                        onTouchInfoListener.onProgressChanged(currentPosition, player.getDuration(), progress);
                    }
                } else if (isAppha) {
                    float alphaRate = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL / height * 2;//单位距离亮度值 (*2是因为加速增减亮度)
                    WindowManager.LayoutParams lpa = activity.getWindow().getAttributes();
                    lpa.screenBrightness -= alphaRate * dy;

                    if (lpa.screenBrightness > 1.0f) {
                        lpa.screenBrightness = 1.0f;
                    } else if (lpa.screenBrightness < 0.01f) {
                        lpa.screenBrightness = 0.01f;
                    }
                    activity.getWindow().setAttributes(lpa);
                    if (onTouchInfoListener != null) {
                        onTouchInfoListener.onAlphaChanged(lpa.screenBrightness);
                    }
                } else if (isVolume) {
                    float volumeRate = (float) maxVolume / height;
                    currentVolume = audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    if (dy > 0) {
                        currentVolume -= dy * volumeRate;
                    } else {
                        currentVolume += dy * volumeRate;
                    }
                    if (currentVolume >= maxVolume) {
                        currentVolume = maxVolume;
                    } else if (currentVolume <= 0) {
                        currentVolume = 0;
                    }
                    audiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
                    if (onTouchInfoListener != null) {
                        onTouchInfoListener.onVolumeChanged(currentVolume, maxVolume);
                    }
                }

                downX = moveX;
                downY = moveY;

                break;
            case MotionEvent.ACTION_UP:
                if (onTouchInfoListener != null) {
                    if (isPosition) {
                        long targetPosition = player.getCurrentPosition() + newPosition;
                        onTouchInfoListener.onProgressTouchUp(targetPosition >= player.getDuration() ? player.getDuration() : targetPosition);
                    }
                    if (isAppha) {
                        onTouchInfoListener.onAlphaTouchUp();
                    }
                    if (isVolume) {
                        onTouchInfoListener.onVolumeTouchUp();
                    }
                }
                downX = 0;
                downY = 0;
                moveX = 0;
                moveY = 0;
                newPosition = 0;
                isAppha = false;
                isVolume = false;
                isPosition = false;
                break;
        }
        return false;
    }

    public void updateVideoUIParmeras(final View surfaceView) {
        surfaceView.post(new Runnable() {
            @Override
            public void run() {
                width = surfaceView.getMeasuredWidth();
                height = surfaceView.getMeasuredHeight();
                Log.e(TAG, "update : width " + width + " height " + height);

            }
        });
    }

    public ExoPlayerOnTouchListener setOnTouchInfoListener(OnTouchInfoListener onTouchInfoListener) {
        this.onTouchInfoListener = onTouchInfoListener;
        return this;
    }
}