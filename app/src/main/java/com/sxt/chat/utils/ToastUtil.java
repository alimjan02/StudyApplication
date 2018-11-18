package com.sxt.chat.utils;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationManagerCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.sxt.chat.R;

/**
 * Created by izhaohu on 2017/8/23.
 */

public class ToastUtil {

    private static TextView mTextView;
    private static Toast toastStart;
    private static TextView snackbarTextView;
    private static TextView snackbarActionView;

    public static void showToast(Activity activity, String message) {
        if (!isNotifyEnable(activity)) {
            showSnackBar(activity, message);
            return;
        }
        //加载Toast布局
        View toastRoot = LayoutInflater.from(activity).inflate(R.layout.toast, null);
        //初始化布局控件
        mTextView = (TextView) toastRoot.findViewById(R.id.message);
        if (toastStart == null) {
            //为控件设置属性
            mTextView.setText(message);
            //Toast的初始化
            toastStart = new Toast(activity);
        } else {
            //为控件设置属性
            mTextView.setText(message);
        }
        //获取屏幕高度
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        //Toast的Y坐标是屏幕高度的1/3，不会出现不适配的问题
        toastStart.setGravity(Gravity.TOP, 0, (int) (height * 0.66));
        toastStart.setDuration(Toast.LENGTH_LONG);
        toastStart.setView(toastRoot);
        toastStart.show();
    }

    @SuppressWarnings("deprecation")
    public static void showToast(Activity activity, int message) {
        if (!isNotifyEnable(activity)) {
            showSnackBar(activity, activity.getString(message));
            return;
        }
        //加载Toast布局
        View toastRoot = LayoutInflater.from(activity).inflate(R.layout.toast, null);
        //初始化布局控件
        mTextView = (TextView) toastRoot.findViewById(R.id.message);
        if (toastStart == null) {
            //为控件设置属性
            mTextView.setText(activity.getResources().getString(message));
            //Toast的初始化
            toastStart = new Toast(activity);
        } else {
            //为控件设置属性
            mTextView.setText(activity.getResources().getString(message));
        }
        //获取屏幕高度
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        //Toast的Y坐标是屏幕高度的1/3，不会出现不适配的问题
        toastStart.setGravity(Gravity.TOP, 0, (int) (height * 0.66));
        toastStart.setDuration(Toast.LENGTH_LONG);
        toastStart.setView(toastRoot);
        toastStart.show();
    }

    public static void showSnackBar(Activity activity, String message) {
        final Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT);
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onShown(Snackbar sb) {
                super.onShown(sb);
            }

            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
            }
        })
                .setAction("关闭", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
        snackbarTextView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        snackbarActionView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_action);
        snackbarTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        snackbarActionView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        snackbarTextView.setText(message);
        snackbar.show();
    }

    public static void showSnackBar(CoordinatorLayout coordinatorLayout, String message) {
        if (coordinatorLayout == null) {
            return;
        }
        final Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onShown(Snackbar sb) {
                super.onShown(sb);
            }

            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
            }
        })
                .setAction("关闭", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
        snackbarTextView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        snackbarActionView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_action);
        snackbarTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        snackbarActionView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        snackbarTextView.setText(message);
        snackbar.show();
    }

    private static boolean isNotifyEnable(Context context) {
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }

}
