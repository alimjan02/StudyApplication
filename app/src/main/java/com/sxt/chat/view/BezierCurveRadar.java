package com.sxt.chat.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.LinearInterpolator;

import com.sxt.chat.R;

import java.util.ArrayList;
import java.util.List;

public class BezierCurveRadar extends View {

    private float basePadding = 30;
    private float startX, endX, startY, endY;
    private float[] center = new float[2];
    private float minR, maxR;
    private Paint basePaint;
    private long duration = 2000;
    private boolean isStop = true;
    private List<ValueAnimator> valueAnimators = new ArrayList<>();
    private int startColor, endColor;

    public BezierCurveRadar(Context context) {
        this(context, null);
    }

    public BezierCurveRadar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BezierCurveRadar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        final TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.BezierCurveRadar);

        int backgroundCOlor = typedArray.getColor(R.styleable.BezierCurveRadar_backgroundColor, ContextCompat.getColor(context, R.color.day_night_normal_color));
        int defaultStartColor = ContextCompat.getColor(getContext(), R.color.blue_shader);
        int defaultEndColor = ContextCompat.getColor(getContext(), R.color.day_night_normal_color);
        startColor = typedArray.getColor(R.styleable.BezierCurveRadar_startColor, defaultStartColor);
        endColor = typedArray.getColor(R.styleable.BezierCurveRadar_endColor, defaultEndColor);
        typedArray.recycle();

        basePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        basePaint.setColor(backgroundCOlor);
        basePaint.setStrokeCap(Paint.Cap.ROUND);
        basePaint.setStrokeWidth(8);
        basePaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    private void startAnimator(float startValue, float endValue) {
        if (isStop) return;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(startValue, endValue).setDuration(duration);
        valueAnimator.addUpdateListener(animation -> {
            if (isStop) {
                valueAnimator.cancel();
            }
            invalidate();
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                valueAnimators.remove(valueAnimator);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                valueAnimators.remove(valueAnimator);
            }
        });
        if (!valueAnimators.contains(valueAnimator)) {
            valueAnimators.add(valueAnimator);
        }
        valueAnimator.start();
        postDelayed(() -> startAnimator(minR, maxR), duration / 3);
    }

    private void stopAllAnimator() {
        valueAnimators.clear();
        isStop = true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            startX = left + getPaddingLeft() + basePadding;
            endX = right - getPaddingRight() - basePadding;
            startY = getMeasuredHeight() - getPaddingBottom() - basePadding;
            endY = getPaddingTop() + basePadding;

            float maxX = endX - startX;
            float maxY = startY - endY;
            center[0] = startX + maxX / 2;
            center[1] = endY + maxY / 2;
            minR = basePadding;
            maxR = Math.min(maxX, maxY) / 2;//最大半径取宽高的最小值
            startAnimator(minR, maxR);
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint(basePaint);
        canvas.drawRoundRect(new RectF(startX, endY, endX, startY), 16, 16, paint);

        if (isStop) return;//如果当前为暂停状态，就停止绘制

        for (ValueAnimator animator : valueAnimators) {
            Paint paint1 = new Paint(basePaint);
            float animatedValue = (float) animator.getAnimatedValue();
            paint1.setColor(computeGradientColor(startColor, endColor, animatedValue / maxR));
            canvas.drawCircle(center[0], center[1], animatedValue, paint1);
        }
    }

//    @SuppressLint("ClickableViewAccessibility")
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            startAnimator(this, (int) event.getX(), (int) event.getY());
//        }
//        return super.onTouchEvent(event);
//    }

    public void startAnimator(View target, int centerX, int centerY) {
        double sqrt = Math.sqrt(target.getWidth() * target.getWidth() + target.getHeight() * target.getHeight());

        Animator animator = createRevealAnimator(target, centerX, centerY, 0, (int) (sqrt / 2));
        animator.setDuration(500);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    public static Animator createRevealAnimator(View view, int centerX, int centerY,
                                                float startRadius, float endRadius) {
        AnimatorSet set = new AnimatorSet();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius);
            set.play(revealAnimator);
        }
        return set;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAllAnimator();
    }

    public void setStop(boolean stop) {
        isStop = stop;
        if (!isStop) {
            startAnimator(minR, maxR);
        }
    }

    public boolean isStop() {
        return isStop;
    }

    /**
     * 计算渐变后的颜色
     *
     * @param startColor 开始颜色
     * @param endColor   结束颜色
     * @param rate       渐变率（0,1）
     * @return 渐变后的颜色，当rate=0时，返回startColor，当rate=1时返回endColor
     */
    public static int computeGradientColor(int startColor, int endColor, float rate) {
        if (rate < 0) {
            rate = 0;
        }
        if (rate > 1) {
            rate = 1;
        }

        int alpha = Color.alpha(endColor) - Color.alpha(startColor);
        int red = Color.red(endColor) - Color.red(startColor);
        int green = Color.green(endColor) - Color.green(startColor);
        int blue = Color.blue(endColor) - Color.blue(startColor);

        return Color.argb(
                Math.round(Color.alpha(startColor) + alpha * rate),
                Math.round(Color.red(startColor) + red * rate),
                Math.round(Color.green(startColor) + green * rate),
                Math.round(Color.blue(startColor) + blue * rate));
    }
}
