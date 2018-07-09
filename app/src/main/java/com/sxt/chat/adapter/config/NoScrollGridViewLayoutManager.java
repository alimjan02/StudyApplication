package com.sxt.chat.adapter.config;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;

/**
 * Created by izhaohu on 2018/1/5.
 */

public class NoScrollGridViewLayoutManager extends GridLayoutManager {
    private boolean enable;

    public NoScrollGridViewLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public NoScrollGridViewLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public NoScrollGridViewLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    public NoScrollGridViewLayoutManager setScrollEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    @Override
    public boolean canScrollVertically() {
        return enable && super.canScrollVertically();
    }
}
