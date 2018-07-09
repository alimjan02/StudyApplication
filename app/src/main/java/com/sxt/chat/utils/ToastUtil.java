package com.sxt.chat.utils;

import android.content.Context;
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

    @SuppressWarnings("deprecation")
    public static void showToast(Context context, String message) {
        //加载Toast布局
        View toastRoot = LayoutInflater.from(context).inflate(R.layout.toast, null);
        //初始化布局控件
        mTextView = (TextView) toastRoot.findViewById(R.id.message);
        if (toastStart == null) {
            //为控件设置属性
            mTextView.setText(message);
            //Toast的初始化
            toastStart = new Toast(context);
        } else {
            //为控件设置属性
            mTextView.setText(message);
        }
        //获取屏幕高度
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        //Toast的Y坐标是屏幕高度的1/3，不会出现不适配的问题
        toastStart.setGravity(Gravity.TOP, 0, (int) (height * 0.66));
        toastStart.setDuration(Toast.LENGTH_LONG);
        toastStart.setView(toastRoot);
        toastStart.show();
    }

    @SuppressWarnings("deprecation")
    public static void showToast(Context context, int message) {
        //加载Toast布局
        View toastRoot = LayoutInflater.from(context).inflate(R.layout.toast, null);
        //初始化布局控件
        mTextView = (TextView) toastRoot.findViewById(R.id.message);
        if (toastStart == null) {
            //为控件设置属性
            mTextView.setText(context.getResources().getString(message));
            //Toast的初始化
            toastStart = new Toast(context);
        } else {
            //为控件设置属性
            mTextView.setText(context.getResources().getString(message));
        }
        //获取屏幕高度
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        //Toast的Y坐标是屏幕高度的1/3，不会出现不适配的问题
        toastStart.setGravity(Gravity.TOP, 0, (int) (height * 0.66));
        toastStart.setDuration(Toast.LENGTH_LONG);
        toastStart.setView(toastRoot);
        toastStart.show();
    }

}
