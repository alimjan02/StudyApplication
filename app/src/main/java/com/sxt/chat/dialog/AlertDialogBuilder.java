package com.sxt.chat.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxt.chat.R;


public class AlertDialogBuilder {
    private Activity mActivity = null;
    private LayoutInflater mInflater;
    private Dialog mDialog;
    private View rootView;
    private TextView dialogMsgText;
    private TextView leftButton;
    private TextView rightButton;
    private RelativeLayout leftButtonLayout;
    private RelativeLayout rightButtonLayout;
    private TextView titleTV;
    private ImageView img;
    private ImageView close;
    private View line;
    private TextView bottomBtn;

    public AlertDialogBuilder(Activity activity) {
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
        init();
    }

    private void init() {
        rootView = mInflater.inflate(R.layout.dialog_alert, null);
        titleTV = (TextView) rootView.findViewById(R.id.title);
        img = (ImageView) rootView.findViewById(R.id.img);
        close = (ImageView) rootView.findViewById(R.id.close);
        bottomBtn = (TextView) rootView.findViewById(R.id.bottom_btn);
        dialogMsgText = (TextView) rootView.findViewById(R.id.message);
        line = rootView.findViewById(R.id.line);
        leftButton = (TextView) rootView.findViewById(R.id.left_button);
        rightButton = (TextView) rootView.findViewById(R.id.right_button);
        leftButtonLayout = (RelativeLayout) rootView.findViewById(R.id.left_button_layout);
        rightButtonLayout = (RelativeLayout) rootView.findViewById(R.id.right_button_layout);
        mDialog = new Dialog(mActivity, R.style.PopContextMenu);
    }

    public AlertDialogBuilder setTitle(int resId) {
        titleTV.setText(resId);
        titleTV.setVisibility(View.VISIBLE);
        return this;
    }

    public AlertDialogBuilder setTitle(int resId, boolean bold) {
        titleTV.setText(resId);
        titleTV.getPaint().setFakeBoldText(bold);
        titleTV.setVisibility(View.VISIBLE);
        return this;
    }

    public AlertDialogBuilder setTitle(String text) {
        titleTV.setText(text);
        titleTV.setVisibility(View.VISIBLE);
        return this;
    }

    public AlertDialogBuilder setTitle(String text, boolean bold) {
        titleTV.setText(text);
        titleTV.setVisibility(View.VISIBLE);
        titleTV.getPaint().setFakeBoldText(bold);
        return this;
    }

    public AlertDialogBuilder setTitleDrawableLeft(int drawableLeftResId) {
        titleTV.setVisibility(View.VISIBLE);
        Drawable left = ContextCompat.getDrawable(mActivity, drawableLeftResId);
        titleTV.setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
        return this;
    }

    public AlertDialogBuilder setMessage(int resId) {
        dialogMsgText.setText(resId);
        dialogMsgText.setVisibility(View.VISIBLE);
        return this;
    }

    public AlertDialogBuilder setMessage(CharSequence msg) {
        dialogMsgText.setText(msg);
        dialogMsgText.setVisibility(View.VISIBLE);
        return this;
    }

    public AlertDialogBuilder setMessageSize(int unit, int size) {
        dialogMsgText.setTextSize(unit, size);
        dialogMsgText.setVisibility(View.VISIBLE);
        return this;
    }

    public AlertDialogBuilder setMessage(String msg) {
        dialogMsgText.setVisibility(View.VISIBLE);
        dialogMsgText.setText(msg);
        return this;
    }

    public AlertDialogBuilder setMessageColor(int colorResId) {
        dialogMsgText.setVisibility(View.VISIBLE);
        dialogMsgText.setTextColor(ContextCompat.getColor(mActivity, colorResId));
        return this;
    }

    public AlertDialogBuilder setView(View view) {
        rootView = view;
        return this;
    }

    public AlertDialogBuilder setLeftButtonResBg(int resId) {
        leftButtonLayout.setVisibility(View.VISIBLE);
        leftButton.setBackgroundResource(resId);
        return this;
    }

    public AlertDialogBuilder setLeftButtonColor(int colorResId) {
        leftButtonLayout.setVisibility(View.VISIBLE);
        leftButton.setTextColor(ContextCompat.getColor(mActivity, colorResId));
        return this;
    }

    public AlertDialogBuilder setRightButtonResBg(int resId) {
        rightButtonLayout.setVisibility(View.VISIBLE);
        rightButton.setBackgroundResource(resId);
        return this;
    }

    public AlertDialogBuilder setRightButtonColor(int colorResId) {
        rightButtonLayout.setVisibility(View.VISIBLE);
        rightButton.setTextColor(ContextCompat.getColor(mActivity, colorResId));
        return this;
    }

    public AlertDialogBuilder setRightButton(int textId, final OnClickListener listener) {
        rightButtonLayout.setVisibility(View.VISIBLE);
        rightButton.setText(textId);
        if (listener != null) {
            rightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(mDialog, 0);
                }
            });
        } else {
            rightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
        return this;
    }

    public AlertDialogBuilder setRightButton(String text, final OnClickListener listener) {
        rightButtonLayout.setVisibility(View.VISIBLE);
        rightButton.setText(text);
        if (listener != null) {
            rightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(mDialog, 0);
                }
            });
        } else {
            rightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
        return this;
    }

    public AlertDialogBuilder setLeftButton(int textId, final OnClickListener listener) {
        leftButtonLayout.setVisibility(View.VISIBLE);
        leftButton.setText(textId);
        if (listener != null) {
            leftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(mDialog, 1);
                }
            });
        } else {
            leftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
        return this;
    }

    public AlertDialogBuilder setLeftButton(String text, final OnClickListener listener) {
        leftButtonLayout.setVisibility(View.VISIBLE);
        leftButton.setText(text);
        if (listener != null) {
            leftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(mDialog, 1);
                }
            });
        } else {
            leftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
        return this;
    }

    public AlertDialogBuilder setBottomButton(String text, final OnClickListener listener) {
        bottomBtn.setVisibility(View.VISIBLE);
        bottomBtn.setText(text);
        if (listener != null) {
            bottomBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(mDialog, 1);
                }
            });
        } else {
            bottomBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
        return this;
    }

    public AlertDialogBuilder setCancelable(boolean cancelable) {
        if (mDialog != null) {
            mDialog.setCancelable(cancelable);
        }
        return this;
    }

    public AlertDialogBuilder setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        if (mDialog != null) {
            mDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        }
        return this;
    }

    public void setButtonText(String leftText, String rightText) {
        if (leftText != null) {
            leftButton.setText(leftText);
        }
        if (rightText != null) {
            rightButton.setText(rightText);
        }
    }

    public AlertDialogBuilder setTopImageRes(int resImg) {
        if (img != null) {
            img.setVisibility(View.VISIBLE);
            img.setImageResource(resImg);
        }
        return this;
    }

    public AlertDialogBuilder setBottomImageRes(int resImg) {
        if (close != null) {
            close.setVisibility(View.VISIBLE);
            close.setImageResource(resImg);
        }
        return this;
    }

    public AlertDialogBuilder setCloseCliklistener(View.OnClickListener onClickListener) {
        if (close != null && onClickListener != null) {
            close.setOnClickListener(onClickListener);
        }
        return this;
    }

    public AlertDialogBuilder setOnDismissListener(
            final OnDismissListener listener) {
        if (mDialog != null) {
            mDialog.setOnDismissListener(listener);
        }
        return this;
    }

    @SuppressWarnings("deprecation")
    public void show() {
        try {
            mDialog.setContentView(rootView);
            mDialog.show();
            Window window = mDialog.getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            WindowManager wm = mActivity.getWindowManager();
            Display display = wm.getDefaultDisplay();
            layoutParams.width = (int) (display.getWidth() * 0.875);
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    public boolean isShowing() {
        return mDialog.isShowing();
    }

    public AlertDialogBuilder setShowLine(boolean showLine) {
        line.setVisibility(showLine ? View.VISIBLE : View.GONE);
        return this;
    }
}
