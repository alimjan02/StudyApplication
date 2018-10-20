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
    private static TextView mTextView1;
    private static Toast toastStart;
    private static Toast toastStart1;
    private static View view;
    private static View view1;

    public static void showToast(Context context, String message) {
        if (toastStart == null) {
            toastStart = new Toast(context);
            toastStart.setDuration(Toast.LENGTH_LONG);
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            int height = 0;
            if (wm != null) {
                height = wm.getDefaultDisplay().getHeight();
            }
            toastStart.setGravity(Gravity.TOP, 0, (int) (height * 0.66));
        }
        view = LayoutInflater.from(context).inflate(R.layout.toast, null);
        mTextView = (TextView) view.findViewById(R.id.message);
        mTextView.setText(message);
        toastStart.setView(view);
        toastStart.show();
    }

    public static void showToast(Context context, int message) {

        if (toastStart1 == null || view1 == null) {
            toastStart1 = new Toast(context);
            toastStart.setDuration(Toast.LENGTH_LONG);
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            int height = 0;
            if (wm != null) {
                height = wm.getDefaultDisplay().getHeight();
            }
            toastStart.setGravity(Gravity.TOP, 0, (int) (height * 0.66));
        }
        view1 = LayoutInflater.from(context).inflate(R.layout.toast, null);
        mTextView1 = (TextView) view1.findViewById(R.id.message);
        mTextView1.setText(message);
        toastStart.setView(view1);
        toastStart.show();
    }

}
