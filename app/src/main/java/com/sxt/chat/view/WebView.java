package com.sxt.chat.view;

import android.content.Context;
import android.util.AttributeSet;

public class WebView extends android.webkit.WebView {

    private OnScrollChangedListener onScrollChangedListener;

    public WebView(Context context) {
        super(context);
    }

    public WebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        super.onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY);
        if (onScrollChangedListener != null) {
            onScrollChangedListener.onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY);
        }
    }

    public void setOnScrollChangedListener(OnScrollChangedListener onScrollChangedListener) {
        this.onScrollChangedListener = onScrollChangedListener;
    }

    public interface OnScrollChangedListener {
        void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY);
    }
}
