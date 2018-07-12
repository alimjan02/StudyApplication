package com.sxt.chat.wifi;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxt.chat.R;

/**
 * @Descripition
 * @Auther Tina
 * @CreateTime 2017/5/22
 * @Version
 * @Since
 */
public class DialogBuilder {

    private Activity mActivity = null;
    private LayoutInflater mInflater;
    private Dialog mDialog;
    private View rootView;
    private TextView tvMsg;
    private Button btnConfirm;
    private ImageView btnCancel;
    private TextView tvTitle;
    public ImageView ivLotteryBg;
    public ImageView ivLotteryImgIcon;


    public DialogBuilder(Activity activity) {
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
        init();
    }

    public DialogBuilder(Activity activity, int ResStyle) {
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
        init(ResStyle);
    }

    private void init(int resStyle) {
        rootView = mInflater.inflate(R.layout.item_common_dialog_layout, null);
        ivLotteryBg = (ImageView) rootView.findViewById(R.id.lottery_img_bg);
        ivLotteryImgIcon = (ImageView) rootView.findViewById(R.id.lottery_img_icon);
        tvTitle = (TextView) rootView.findViewById(R.id.lottery_tv_title);
        tvMsg = (TextView) rootView.findViewById(R.id.lottery_tv_msg);
        btnConfirm = (Button) rootView.findViewById(R.id.lottery_btn_confirm);
        btnCancel = (ImageView) rootView.findViewById(R.id.lottery_btn_cancel);
        mDialog = new Dialog(mActivity, resStyle);
    }

    private void init() {
        rootView = mInflater.inflate(R.layout.item_common_dialog_layout, null);
        ivLotteryBg = (ImageView) rootView.findViewById(R.id.lottery_img_bg);
        ivLotteryImgIcon = (ImageView) rootView.findViewById(R.id.lottery_img_icon);
        tvTitle = (TextView) rootView.findViewById(R.id.lottery_tv_title);
        tvMsg = (TextView) rootView.findViewById(R.id.lottery_tv_msg);
        btnConfirm = (Button) rootView.findViewById(R.id.lottery_btn_confirm);
        btnCancel = (ImageView) rootView.findViewById(R.id.lottery_btn_cancel);
        mDialog = new Dialog(mActivity, R.style.Theme_Design_BottomSheetDialog);
    }

    public DialogBuilder setTitle(int resId) {
        tvTitle.setText(resId);
        return this;
    }

    public DialogBuilder setTitle(String text) {
        tvTitle.setText(text);
        return this;
    }

    public DialogBuilder setMessage(int resId) {
        tvMsg.setText(resId);
        return this;
    }

    public DialogBuilder setMessage(CharSequence msg) {
        tvMsg.setText(msg);
        return this;
    }

    public DialogBuilder setTextSize(int unit, int titleTextSize, int messageTextSize, int confirmBtnTextSize) {
        tvTitle.setTextSize(unit, titleTextSize);
        tvMsg.setTextSize(unit, titleTextSize);
        btnConfirm.setTextSize(unit, titleTextSize);
        return this;
    }

    public DialogBuilder setLotteryBg(int imgResId) {
        return this;
    }

    public DialogBuilder setConfirmButton(int bgResId, int textId, final View.OnClickListener listener) {
        if (bgResId != -1) {
            btnConfirm.setBackgroundResource(bgResId);
        }
        btnConfirm.setText(textId);
        if (listener != null) {
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(v);
                }
            });
        } else {
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
        return this;
    }

    public DialogBuilder setCancelButton(int bgResId, final View.OnClickListener listener) {
        btnCancel.setImageResource(bgResId);
        if (listener != null) {
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(v);
                }
            });
        } else {
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }

        return this;
    }


    public DialogBuilder setCancelable(boolean cancelable) {
        if (mDialog != null) {
            mDialog.setCancelable(cancelable);
        }
        return this;
    }

    public DialogBuilder setCancelableOutSide(boolean cancelable) {
        if (mDialog != null) {
            mDialog.setCanceledOnTouchOutside(cancelable);
        }
        return this;
    }


    public ImageView getLotteryIcon() {
        if (ivLotteryImgIcon != null) {
            ivLotteryImgIcon.setVisibility(View.VISIBLE);
        }
        return ivLotteryImgIcon;
    }

    public DialogBuilder setOnDismissListener(
            final DialogInterface.OnDismissListener listener) {
        if (mDialog != null) {
            mDialog.setOnDismissListener(listener);
        }
        return this;
    }


    int dip2px(float dipValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    @SuppressWarnings("deprecation")
    public void show(double width, int gravity) {
        try {
            if (rootView != null) mDialog.setContentView(rootView);
            mDialog.show();
            Window window = mDialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams layoutParams = window.getAttributes();
                WindowManager wm = mActivity.getWindowManager();
                Display display = wm.getDefaultDisplay();
                layoutParams.width = (int) (display.getWidth() * width);
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                layoutParams.gravity = gravity;
                window.setAttributes(layoutParams);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DialogBuilder setContentView(View view) {
        if (mDialog != null && view != null) {
            mDialog.setContentView(view);
        }
        return this;
    }

    public DialogBuilder replaceView(View view) {
        this.rootView = view;
        return this;
    }

    public void dismiss() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    public boolean isShowing() {
        return mDialog.isShowing();
    }

}
