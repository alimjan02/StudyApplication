package com.sxt.chat.base;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    private BottomSheetDialog bottomSheetDialog;

    public BaseBottomSheetFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        Log.i(TAG, "onAttach");
    }

    /**
     * 因为需要创建完Dialog以后才能创建bottomSheetBehavior
     * 所以设置bottomSheetBehavior的相关属性时，必须在onCreateDialog方法执行完以后调用
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.i(TAG, "onCreateDialog");
        contentView = getDisplayView();
        bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        bottomSheetDialog.setContentView(contentView);
        bottomSheetBehavior = BottomSheetBehavior.from((View) contentView.getParent());
//        Window window = bottomSheetDialog.getWindow();
//        if (window != null) {
//            View view = window.findViewById(android.support.design.R.id.design_bottom_sheet);
//            bottomSheetBehavior = BottomSheetBehavior.from(view);
//        }
        onCreateDialog(this, bottomSheetDialog, contentView);
        return bottomSheetDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initView();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * 此时Dialog已经创建完成了，可以设置一些相关属性了
     */
    private void onCreateDialog(BaseBottomSheetFragment baseBottomSheetFragment, BottomSheetDialog bottomSheetDialog, View contentView) {
        if (onBottomSheetDialogCreateListener != null) {
            onBottomSheetDialogCreateListener.onBottomSheetDialogCreate(baseBottomSheetFragment, bottomSheetDialog, contentView);
        } else {
            setBackgtoundColor(Color.TRANSPARENT);
            setCancelableOutside(true);
            setHideable(true);
        }
    }

    /**
     * 初始化View
     */
    protected void initView() {

    }

    public BaseBottomSheetFragment setPeekHeight(int peekHeight) {
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setPeekHeight(peekHeight);
        }
        return this;
    }

    public BaseBottomSheetFragment setHideable(boolean hideable) {
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setHideable(hideable);
        }
        return this;
    }

    public BaseBottomSheetFragment setCancelableOutside(boolean cancelableOutside) {
        if (bottomSheetDialog != null) {
            bottomSheetDialog.setCanceledOnTouchOutside(cancelableOutside);
        }
        return this;
    }

    public BaseBottomSheetFragment setBackgtoundColor(int backgroundColor) {
        if (contentView != null) {
            View parent = (View) contentView.getParent();
            parent.setBackgroundColor(backgroundColor);
        }
        return this;
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
        void onBottomSheetDialogCreate(BaseBottomSheetFragment baseBottomSheetFragment, BottomSheetDialog bottomSheetDialog, View contentView);
    }
}
