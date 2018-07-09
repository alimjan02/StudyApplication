package com.sxt.chat.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.sxt.chat.R;

public class LoadingDialog2 {
    private Activity mActivity = null;
    private LayoutInflater mInflater;
    private Dialog mDialog;
    private View rootView;
    private ImageView loading;
    private AnimationDrawable animation;

    public LoadingDialog2(Activity activity) {
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
        init();
    }

    private void init() {
        rootView = mInflater.inflate(R.layout.item_loading, null);
        mDialog = new Dialog(mActivity, R.style.PopContextMenu);
        loading = (ImageView) rootView.findViewById(R.id.loading);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                animation.stop();
                animation.setVisible(false, false);
            }
        });

        loading.setBackgroundResource(R.drawable.loading_gif);
        animation = (AnimationDrawable) loading.getBackground();
    }

    public LoadingDialog2 setCancelable(boolean cancelable) {
        if (mDialog != null) {
            mDialog.setCancelable(cancelable);
        }
        return this;
    }

    @SuppressWarnings("deprecation")
    public void show() {
        try {
            mDialog.setContentView(rootView);
            mDialog.show();
            animation.setVisible(true, true);
            animation.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismiss() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            animation.stop();
        }
    }

    public boolean isShowing() {
        return mDialog.isShowing();
    }
}
