package com.sxt.chat.base;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

/**
 * Created by sxt on 2018/10/22.
 * BottomSheetFragment 拥有自己的生命周期 , dismiss后fragment销毁
 */
public class BaseBottomSheetFragment extends BottomSheetDialogFragment {

    protected Context context;
    protected View contentView;
    protected String TAG = this.getClass().getName();
    private BottomSheetDialog bottomSheetDialog;
    private BottomSheetBehavior<View> bottomSheetBehavior;

    public BaseBottomSheetFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        Log.i(TAG, "onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.i(TAG, "onCreateDialog");
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        contentView = getDisplayView();
        bottomSheetDialog.setContentView(contentView);
        View parent = (View) contentView.getParent();
        parent.setBackgroundColor(Color.TRANSPARENT);
        ViewGroup.LayoutParams layoutParams = parent.getLayoutParams();
        layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.95);
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        parent.setLayoutParams(layoutParams);
        Window window = bottomSheetDialog.getWindow();
        if (window != null) {
            View view = window.findViewById(android.support.design.R.id.design_bottom_sheet);
            bottomSheetBehavior = BottomSheetBehavior.from(view);
            bottomSheetBehavior.setPeekHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 360, getResources().getDisplayMetrics()));
            bottomSheetBehavior.setHideable(true);
        }
        return bottomSheetDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected View getDisplayView() {
        return null;
    }

    public BaseBottomSheetFragment show(FragmentManager manager) {
        super.show(manager, "dialog");
        return this;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach");
    }
}
