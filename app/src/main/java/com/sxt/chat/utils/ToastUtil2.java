package com.sxt.chat.utils;


import android.content.Context;
import android.os.Build;
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

public class ToastUtil2 {

    private static TextView mTextView;

    private static Toast toast;

    @SuppressWarnings("deprecation")
    public static void showToast(Context context, String message) {
        //加载Toast布局
        View toastRoot = LayoutInflater.from(context).inflate(R.layout.toast2, null);
        //初始化布局控件
        mTextView = (TextView) toastRoot.findViewById(R.id.message);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
            toast = new Toast(context);
        } else {
            if (toast == null) {
                toast = new Toast(context);
            }
        }
        //为控件设置属性
        mTextView.setText(message);
        //获取屏幕高度
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        //Toast的Y坐标是屏幕高度的1/3，不会出现不适配的问题
        toast.setGravity(Gravity.TOP, 0, (int) (height * 0.5));
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(toastRoot);
        toast.show();
    }

    @SuppressWarnings("deprecation")
    public static void showToast(Context context, int message) {
        //加载Toast布局
        View toastRoot = LayoutInflater.from(context).inflate(R.layout.toast2, null);
        //初始化布局控件
        mTextView = (TextView) toastRoot.findViewById(R.id.message);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
            toast = new Toast(context);
        } else {
            if (toast == null) {
                toast = new Toast(context);
            }
        }
        //为控件设置属性
        mTextView.setText(context.getResources().getString(message));
        //获取屏幕高度
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        //Toast的Y坐标是屏幕高度的1/3，不会出现不适配的问题
        toast.setGravity(Gravity.TOP, 0, (int) (height * 0.66));
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(toastRoot);
        toast.show();
    }

}
