package com.sxt.chat.explayer;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.exoplayer2.SimpleExoPlayer;

public class ExoPlayerOnTouchListener implements View.OnTouchListener {

    private float downX;
    private float downY;
    private float moveX;
    private float moveY;
    private float upX;
    private float upY;
    private float width;
    private float height;
    private long newPosition;
    private boolean isPosition;
    private boolean isVolume;
    private boolean isAppha;
    private SimpleExoPlayer player;
    private final String TAG = "onTouch";
    private OnTouchInfoListener onTouchInfoListener;

    public ExoPlayerOnTouchListener(Context context, SimpleExoPlayer player) {
        this.player = player;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        width = dm.widthPixels;
        height = dm.heightPixels;
        Log.e(TAG, "Width : " + width + " Height : " + height);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (onTouchInfoListener != null) onTouchInfoListener.onTouch();
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
                if (isPosition || !isVolume && !isAppha && Math.abs(dx) >= Math.abs(dy) && Math.abs(dx) > 100) {//横向滑动
                    isPosition = true;
                    float rateWidth = dx / width;

                    newPosition = (long) (rateWidth * player.getDuration());
                    long currentPosition = player.getCurrentPosition() + newPosition;
                    currentPosition = currentPosition <= 0 ? 0 : currentPosition >= player.getDuration() ? player.getDuration() : currentPosition;
                    int progress = (int) (((float) currentPosition) / ((float) player.getDuration()) * 100);

                    if (onTouchInfoListener != null) {
                        onTouchInfoListener.onProgressChanged(currentPosition, player.getDuration(), progress);
                    }

                } /*else {//竖向滑动
                    if (isVolume || !isPosition && !isAppha && moveX < width / 2 && Math.abs(dy) > 100) {//左侧 竖向 调节音量
                        isVolume = true;
                        player.setVolume(dy / height * 100 * 255);
                    } else {//右侧 竖向 调节亮度
                        isVolume = true;
                        float rateAlpha = 1 / height;
//                        WindowManager.LayoutParams attributes = getWindow().getAttributes();
//                        attributes.alpha = 1 - rateAlpha * dy;
//                        getWindow().setAttributes(attributes);
                    }
                }*/
                break;
            case MotionEvent.ACTION_UP:
                if (isPosition && onTouchInfoListener != null) {
                    long targetPosition = player.getCurrentPosition() + newPosition;
                    onTouchInfoListener.onTouchUp(targetPosition >= player.getDuration() ? player.getDuration() : targetPosition);
                }
                downX = 0;
                downY = 0;
                moveX = 0;
                moveY = 0;
                isAppha = false;
                isVolume = false;
                isPosition = false;
                newPosition = 0;
                break;
        }
        return false;
    }


    public ExoPlayerOnTouchListener setOnTouchInfoListener(OnTouchInfoListener onTouchInfoListener) {
        this.onTouchInfoListener = onTouchInfoListener;
        return this;
    }
}