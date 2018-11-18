package com.sxt.chat.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.sxt.chat.R;

/**
 * Created by sxt on 2018/11/15.
 */
public class LocationPoint extends View {

    private float startX, startY, endX, endY;
    private Paint basePaint, paintOut, paintIn, paintCenter;
    private ValueAnimator valueAnimator;
    private float mAnimatorValue;
    private float[] radius;
    private float pieR;

    public LocationPoint(Context context) {
        this(context, null);
    }

    public LocationPoint(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LocationPoint(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            this.startX = (float) this.getPaddingLeft();
            this.endX = (float) (this.getMeasuredWidth() - this.getPaddingRight());
            this.startY = (float) (this.getMeasuredHeight() - this.getPaddingBottom());
            this.endY = (float) this.getPaddingTop();
            this.radius = new float[2];
            float R1 = this.startY - this.endY;
            float R2 = this.endX - -this.startX;
            this.radius[0] = this.startX + R2 / 2.0F;
            this.radius[1] = this.endY + R1 / 2.0F;
            this.pieR = Math.min(R1, R2) / 2;
            invalidate();
        }
    }

    private void init(Context context) {
        basePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        basePaint.setColor(ContextCompat.getColor(context, R.color.blue_sharder));

        paintOut = new Paint(basePaint);
        paintCenter = new Paint(basePaint);
        paintCenter.setColor(ContextCompat.getColor(context, R.color.blue_sharder));
        paintIn = new Paint(basePaint);
        paintIn.setColor(ContextCompat.getColor(context, R.color.green_sharder));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (radius == null || radius.length < 2) return;
        canvas.drawCircle(radius[0], radius[1], pieR, paintOut);
        if (valueAnimator != null) {
            canvas.drawCircle(radius[0], radius[1], pieR * mAnimatorValue, paintIn);
        } else {
            start(pieR);
        }
        canvas.drawCircle(radius[0], radius[1], 10, paintCenter);
    }

    public LocationPoint start(float radio) {
        this.pieR = radio;
//        post(new Runnable() {
//            @Override
//            public void run() {
        if (valueAnimator == null) {
            valueAnimator = ValueAnimator.ofFloat(0, 1).setDuration(2000);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mAnimatorValue = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            valueAnimator.setRepeatMode(ValueAnimator.REVERSE);//反向重复执行,可以避免抖动
            valueAnimator.start();
        }
//            }
//        });
        return this;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }
}