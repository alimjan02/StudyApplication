package com.sxt.chat.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.sxt.chat.R;
import com.sxt.chat.utils.Prefs;

/**
 * Created by sxt on 2019/3/8.
 */
public class FloatButton {

    private final String TAG = this.getClass().getName();

    private Activity activity;
    private ImageView itemFloatView;
    private final FrameLayout dectorView;
    private OnClickListener onClickListener;
    private float bottomBarHeight, statusBarHeight;//底部Tab高度,状态栏高度
    private int widthPixels, heightPixels;//手机屏幕的宽高
    private int itemFloatViewWidth, itemFloatViewHeight;//浮动按钮的宽高
    private Prefs prefs;

    public FloatButton(Activity activity) {
        if (activity == null) throw new IllegalArgumentException("上下文参数异常");
        this.activity = activity;
        dectorView = (FrameLayout) activity.getWindow().getDecorView();
        init();
    }

    private void init() {
        initFloatButton();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initFloatButton() {
        widthPixels = activity.getResources().getDisplayMetrics().widthPixels;
        heightPixels = activity.getResources().getDisplayMetrics().heightPixels;
        bottomBarHeight = activity.getResources().getDimension(R.dimen.bottom_app_bar_height);
        //获取状态栏的高度
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
        }
        prefs = Prefs.getInstance(activity);
        itemFloatView = new ImageView(activity);
        itemFloatView.setClickable(true);
        itemFloatView.setFocusable(true);
        itemFloatView.setVisibility(View.VISIBLE);
        itemFloatView.setImageResource(R.mipmap.ic_launcher_round);
        dectorView.post(new Runnable() {
            @Override
            public void run() {
                int size = (int) activity.getResources().getDimension(R.dimen.dp_50);
                dectorView.addView(itemFloatView, new FrameLayout.LayoutParams(size, size));
                itemFloatView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        itemFloatView.removeOnLayoutChangeListener(this);
                        itemFloatViewWidth = itemFloatView.getWidth();
                        itemFloatViewHeight = itemFloatView.getHeight();
                        //从本地取出上次的坐标 设置进去 ; 默认坐标为右下角
                        float x = prefs.getFloat(Prefs.KEY_FLOAT_X, widthPixels - itemFloatViewWidth);
//                      float y = prefs.getFloat(Prefs.KEY_FLOAT_Y, heightPixels - itemFloatViewHeight - bottomBarHeight);
                        float y = prefs.getFloat(Prefs.KEY_FLOAT_Y, heightPixels / 3 * 2);
                        itemFloatView.setX(x);
                        itemFloatView.setY(y);
                        Log.e(TAG, String.format("恢复 x = %s , y = %s", x, y));
                        Log.e(TAG, String.format("测量Float宽高 width = %s , height = %s", itemFloatViewWidth, itemFloatViewHeight));
                    }
                });
            }
        });

        itemFloatView.setOnTouchListener(new View.OnTouchListener() {

            private float downX, downY, moveX, moveY;
            private long downMillis, upMillis;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        downY = event.getY();
                        downMillis = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        moveX = event.getX();
                        moveY = event.getY();
                        float distanceX = moveX - downX;
                        float distanceY = moveY - downY;
                        distanceX += itemFloatView.getX();
                        distanceY += itemFloatView.getY();

                        if (distanceX <= 0) distanceX = 0;//屏幕左侧边界
                        if (distanceX + itemFloatViewWidth > widthPixels) {
                            distanceX = widthPixels - itemFloatViewWidth;//屏幕右侧边界
                        }
                        if (distanceY < statusBarHeight) {//状态栏边界
                            distanceY = statusBarHeight;
                        }
                        if (distanceY + itemFloatViewHeight > heightPixels - bottomBarHeight) {//底部Tab栏边界
                            distanceY = heightPixels - bottomBarHeight - itemFloatViewHeight;
                        }

                        itemFloatView.setX(distanceX);
                        itemFloatView.setY(distanceY);

                        moveX = distanceX;
                        moveY = distanceY;

                        break;
                    case MotionEvent.ACTION_UP:
                        upMillis = System.currentTimeMillis();
                        if (upMillis - downMillis <= 100) {//触摸时长小于100默认为点击事件
                            if (onClickListener != null) onClickListener.onClick(itemFloatView);
                        }
                        //在这儿存储最后的坐标
                        prefs.putFloat(Prefs.KEY_FLOAT_X, moveX);
                        prefs.putFloat(Prefs.KEY_FLOAT_Y, moveY);
                        Log.e(TAG, String.format("更新 x = %s , y = %s", moveX, moveY));
                        downX = 0;
                        downY = 0;
                        moveX = 0;
                        moveY = 0;
                        break;
                }
                itemFloatView.performClick();
                return false;
            }
        });
    }

    public FloatButton setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    public void setVisibility(int visibility) {
        if (itemFloatView != null) {
            itemFloatView.setVisibility(visibility);
        }
    }

    public ImageView getItemFloatView() {
        return itemFloatView;
    }

    public interface OnClickListener {
        void onClick(View view);
    }

    public void onDestroy() {
        if (itemFloatView != null && dectorView != null) {
            dectorView.removeView(itemFloatView);
        }
    }
}
