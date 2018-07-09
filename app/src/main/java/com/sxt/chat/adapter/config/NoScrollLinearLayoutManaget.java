package com.sxt.chat.adapter.config;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

/**
 * Created by 11837 on 2018/5/19.
 */

public class NoScrollLinearLayoutManaget extends LinearLayoutManager {
    private boolean isCanScrollVertically;

    public NoScrollLinearLayoutManaget(Context context) {
        super(context);
    }

    public NoScrollLinearLayoutManaget(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public NoScrollLinearLayoutManaget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public NoScrollLinearLayoutManaget setCanScrollVertically(boolean isCanScrollVertically) {
        this.isCanScrollVertically = isCanScrollVertically;
        return this;
    }

    @Override
    public boolean canScrollVertically() {
        return super.canScrollVertically() && isCanScrollVertically;
    }
}
