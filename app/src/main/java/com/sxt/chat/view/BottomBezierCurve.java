package com.sxt.chat.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.sxt.chat.R;

/**
 * Created by sxt on 2018/6/5.
 * 绘制贝塞尔来绘制波浪形
 */
public class BottomBezierCurve extends View {

    private int currentHeight;
    private Path path;
    private Paint paint;

    public BottomBezierCurve(Context context) {
        this(context, null, 0);
    }

    public BottomBezierCurve(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomBezierCurve(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        path = new Path();
        paint = new Paint();
        paint.setColor(ContextCompat.getColor(getContext(), R.color.main_red_dark));
        paint.setAntiAlias(true);

        setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public void setWaveColor(@ColorInt int color) {
        paint.setColor(color);
    }

    public void setCurrentHeight(int currentHeight) {
        this.currentHeight = currentHeight;
        invalidate();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float rectBottom = getHeight() / 5f * 4;
        @SuppressLint("DrawAllocation") int[] colors = new int[]{ContextCompat.getColor(getContext(), R.color.main_blue), ContextCompat.getColor(getContext(), R.color.main_blue_press)};
        paint.setShader(new LinearGradient(0, 0, 0,
                currentHeight + getHeight() > rectBottom ? currentHeight + getHeight() : rectBottom,
                colors,
                null,
                Shader.TileMode.CLAMP));

        canvas.drawRect(getPaddingLeft(), getPaddingTop(), getWidth(), rectBottom, paint);
        //重置画笔
        path.reset();
        //绘制贝塞尔曲线
        path.moveTo(0, rectBottom);
        path.quadTo(getWidth() / 2, currentHeight + getHeight() > rectBottom ? currentHeight + getHeight() : rectBottom, getWidth(), rectBottom);
        canvas.drawPath(path, paint);
    }
}
