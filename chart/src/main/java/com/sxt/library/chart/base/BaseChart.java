package com.sxt.library.chart.base;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by sxt on 2017/12/30.
 */

public class BaseChart extends View {

    public final String TAG = this.getClass().getName();

    public BaseChart(Context context) {
        this(context, null);
    }

    public BaseChart(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void start() {
    }


    /**
     * 检测制定View是否被遮住显示不全
     *
     * @return
     */
    public boolean isCover(View view) {
        Rect rect = new Rect();
        if (view.getGlobalVisibleRect(rect)) {
            if (rect.width() >= view.getMeasuredWidth() && rect.height() >= view.getMeasuredHeight() * 0.8) {
                return true;
            }
        }
        return false;
    }
}
