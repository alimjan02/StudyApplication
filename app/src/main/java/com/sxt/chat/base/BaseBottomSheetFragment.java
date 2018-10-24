package com.sxt.chat.base;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.sxt.chat.App;

/**
 * Created by sxt on 2018/10/22.
 * BottomSheetFragment 拥有自己的生命周期 , dismiss后fragment销毁
 */
public class BaseBottomSheetFragment extends BottomSheetDialogFragment {

    protected Context context;
    protected View contentView;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private OnBottomSheetDialogCreateListener onBottomSheetDialogCreateListener;

    protected String TAG = this.getClass().getName();
    private final String CONTENT_VIEW_IS_EMPTY = "contentView is null, Please invoke this method after onCreateDialog()";
    private int PEEK_HEIGHT_DEFAULT = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, App.getCtx().getResources().getDisplayMetrics());

    public BaseBottomSheetFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        Log.i(TAG, "onAttach");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.i(TAG, "onCreateDialog");
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        contentView = getDisplayView();
        bottomSheetDialog.setContentView(contentView);
//        Window window = bottomSheetDialog.getWindow();
//        if (window != null) {
//            View view = window.findViewById(android.support.design.R.id.design_bottom_sheet);
//            bottomSheetBehavior = BottomSheetBehavior.from(view);
//        }
        bottomSheetBehavior = BottomSheetBehavior.from((View) contentView.getParent());
        onBottomSheetDialogCreate(bottomSheetDialog, contentView);
        return bottomSheetDialog;
    }

    private void onBottomSheetDialogCreate(BottomSheetDialog bottomSheetDialog, View contentView) {
        if (onBottomSheetDialogCreateListener != null) {
            onBottomSheetDialogCreateListener.onBottomSheetDialogCreate(this, bottomSheetDialog, contentView);
        } else {
            defaultSettings(bottomSheetDialog, contentView);
        }
    }

    /**
     * 如果Child不设置OnBottomSheetDialogCreateListener监听 , 将默认为Child 设置parentView背景色为透明 && 折叠时高度为200dp && 向下滑动时可隐藏
     *
     * @param bottomSheetDialog 当前对话框
     * @param contentView       当前对话框通过 dialog.setContentView() 添加进来的View
     */
    public void defaultSettings(BottomSheetDialog bottomSheetDialog, View contentView) {
        View parent = (View) contentView.getParent();
        parent.setBackgroundColor(Color.TRANSPARENT);
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setPeekHeight(PEEK_HEIGHT_DEFAULT);
            bottomSheetBehavior.setHideable(true);
        }
    }

    public BottomSheetBehavior<View> getBottomSheetBehavior() {
        if (bottomSheetBehavior == null) {
            throw new NullPointerException(CONTENT_VIEW_IS_EMPTY);
        }
        return bottomSheetBehavior;
    }

    public boolean isBottomSheetExpanded() {
        return bottomSheetBehavior == null || bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED;
    }

    public BaseBottomSheetFragment setBottomSheetState(int bottomSheetState) {
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setState(bottomSheetState);
        } else {
            throw new NullPointerException(CONTENT_VIEW_IS_EMPTY);
        }
        return this;
    }

    public BaseBottomSheetFragment setBottomSheetBackgroundColor(int colorResId) {
        if (contentView == null) {
            throw new NullPointerException(CONTENT_VIEW_IS_EMPTY);
        }
        View parent = (View) contentView.getParent();
        parent.setBackgroundColor(ContextCompat.getColor(context, colorResId));
        return this;
    }

    public BaseBottomSheetFragment setBottomSheetBackgroundResource(int resId) {
        if (contentView == null) {
            throw new NullPointerException(CONTENT_VIEW_IS_EMPTY);
        }
        View parent = (View) contentView.getParent();
        parent.setBackgroundResource(resId);
        return this;
    }

    public BaseBottomSheetFragment setBottomSheetBackground(Drawable drawable) {
        if (contentView == null) {
            throw new NullPointerException(CONTENT_VIEW_IS_EMPTY);
        }
        View parent = (View) contentView.getParent();
        parent.setBackground(drawable);
        return this;
    }

    protected View getDisplayView() {
        return null;
    }

    public BaseBottomSheetFragment show(FragmentManager manager) {
        super.show(manager, "dialog");
        return this;
    }

    public BaseBottomSheetFragment setOnBottomSheetDialogCreateListener(OnBottomSheetDialogCreateListener onBottomSheetDialogCreateListener) {
        this.onBottomSheetDialogCreateListener = onBottomSheetDialogCreateListener;
        return this;
    }

    public interface OnBottomSheetDialogCreateListener {
        void onBottomSheetDialogCreate(BaseBottomSheetFragment bottomSheetFragment, BottomSheetDialog bottomSheetDialog, View contentView);
    }
}
