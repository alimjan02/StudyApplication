package com.sxt.chat.adapter.config;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

/**
 * Created by izhaohu on 2018/1/5.
 */

public class NoScrollLinearLayoutManager extends LinearLayoutManager {
    private boolean enable;

    public NoScrollLinearLayoutManager(Context context) {
        super(context);
    }

    public NoScrollLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public NoScrollLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public NoScrollLinearLayoutManager setScrollEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    @Override
    public boolean canScrollVertically() {
        return enable && super.canScrollVertically();
    }
}
