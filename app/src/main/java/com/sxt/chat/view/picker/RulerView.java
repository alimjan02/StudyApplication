package com.sxt.chat.view.picker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import com.sxt.chat.R;
import com.sxt.chat.utils.Px2DpUtil;


/**
 * Created by xt.sun on 2018/02/11
 */
public class RulerView extends View {
    private int mMinVelocity;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mWidth;
    private int mHeight;
    private float mBasePadding;

    private float mSelectorValue = 0.0f;
    private float mMaxValue = 100.0f;
    private float mMinValue = 0.0f;
    private float mPerValue = 1;

    private float mLineSpaceWidth = 5;
    private float mLineWidth = 1;
    private float mLineMaxHeight = 42;
    private float mLineMidHeight = 30;
    private float mLineMinHeight = 17;
    private int mLineColor = 1;

    private boolean isVertical = false;

    private float mTextMarginTop = 8;
    private float mTextSize = 14;
    private int mTextColor = 1;

    private float mTextHeight;

    private Paint mTextPaint;
    private Paint mLinePaint;
    private Paint indicatorPaint;
    private Path indicatorPath;

    private int mTotalLine;
    private int mMaxOffset;
    private float mOffset;
    private int mLastX, mLastY, mMove;
    private OnValueChangeListener mListener;


    public RulerView(Context context) {
        this(context, null);
    }

    public RulerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    protected void init(Context context, AttributeSet attrs) {
        mScroller = new Scroller(context);

        final TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.RulerViewLast);

        mLineSpaceWidth = typedArray.getDimension(R.styleable.RulerViewLast_lineSpaceWidth, dp2px(context, mLineSpaceWidth));
        mLineWidth = typedArray.getDimension(R.styleable.RulerViewLast_lineWidth, dp2px(context, mLineWidth));
        mLineMaxHeight = typedArray.getDimension(R.styleable.RulerViewLast_lineMaxHeight, dp2px(context, mLineMaxHeight));
        mLineMidHeight = typedArray.getDimension(R.styleable.RulerViewLast_lineMidHeight, dp2px(context, mLineMidHeight));
        mLineMinHeight = typedArray.getDimension(R.styleable.RulerViewLast_lineMinHeight, dp2px(context, mLineMinHeight));
        mLineColor = typedArray.getColor(R.styleable.RulerViewLast_lineColor, mLineColor);

        mTextSize = typedArray.getDimension(R.styleable.RulerViewLast_textSize, dp2px(context, mTextSize));
        mTextColor = typedArray.getColor(R.styleable.RulerViewLast_textColor, mTextColor);
        mTextMarginTop = typedArray.getDimension(R.styleable.RulerViewLast_textMarginTop, dp2px(context, mTextMarginTop));

        mSelectorValue = typedArray.getFloat(R.styleable.RulerViewLast_selectorValue, 0.0f);
        mMinValue = typedArray.getFloat(R.styleable.RulerViewLast_minValue, 0.0f);
        mMaxValue = typedArray.getFloat(R.styleable.RulerViewLast_maxValue, 100.0f);
        mPerValue = typedArray.getFloat(R.styleable.RulerViewLast_perValue, 0.1f);

        isVertical = typedArray.getBoolean(R.styleable.RulerViewLast_isVertical, false);
        mMinVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextHeight = getFontHeight(mTextPaint);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStrokeWidth(mLineWidth);
        mLinePaint.setColor(mLineColor);

        indicatorPath = new Path();
        indicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        indicatorPaint.setStrokeWidth(Px2DpUtil.dip2px(getContext(), 2));
        indicatorPaint.setColor(ContextCompat.getColor(getContext(), R.color.main_blue));
        indicatorPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        setValue(mSelectorValue, mMinValue, mMaxValue, mPerValue);

        typedArray.recycle();
    }

    private int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private float getFontHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }

    public RulerView setTextColor(int color) {
        mTextPaint.setColor(color);
        invalidate();
        return this;
    }

    public RulerView setTextSize(float textSize) {
        mTextPaint.setTextSize(textSize);
        invalidate();
        return this;
    }

    public RulerView setTextMarginTop(float marginTop) {
        mTextMarginTop = marginTop;
        invalidate();
        return this;
    }

    public RulerView setLineColor(int color) {
        mLinePaint.setColor(color);
        invalidate();
        return this;
    }

    public RulerView setLineWidth(float width) {
        mLineWidth = width;
        invalidate();
        return this;
    }

    public RulerView setLineSpaceWidth(float width) {
        mLineSpaceWidth = width;
        invalidate();
        return this;
    }

    public RulerView setLineMinHeight(float height) {
        mLineMinHeight = height;
        invalidate();
        return this;
    }

    public RulerView setLineMidHeight(float height) {
        mLineMidHeight = height;
        invalidate();
        return this;
    }

    public RulerView setLineMaxHeight(float height) {
        mLineMaxHeight = height;
        invalidate();
        return this;
    }

    public float getSelectorValue() {
        return this.mSelectorValue;
    }

    public void setValue(float selectorValue, float minValue, float maxValue, float per) {
        this.mSelectorValue = selectorValue;
        this.mMaxValue = maxValue;
        this.mMinValue = minValue;
        this.mPerValue = (int) (per * 10.0f);
        this.mTotalLine = ((int) ((mMaxValue * 10 - mMinValue * 10) / mPerValue)) + 1;
        mMaxOffset = (int) (-(mTotalLine - 1) * mLineSpaceWidth);

        mOffset = (mMinValue - mSelectorValue) / mPerValue * mLineSpaceWidth * 10;
        invalidate();
        setVisibility(VISIBLE);
    }

    public void setOnValueChangeListener(OnValueChangeListener listener) {
        mListener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            mWidth = w;
            mHeight = h;
            mBasePadding = Px2DpUtil.dip2px(getContext(), 24);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isVertical) {
            drawHorizontol(canvas);
        } else {
            drawVertical(canvas);
        }
    }

    private void drawVertical(Canvas canvas) {
        float top = 0, width;
        String value;
        int srcPointY = mHeight / 2;
        drawBg(canvas);
        for (int i = 0; i < mTotalLine; i++) {
            top = srcPointY + mOffset + i * mLineSpaceWidth;

            if (top < 0 || top > mHeight) {
                continue;
            }

            if (i % 10 == 0) {
                width = mLineMaxHeight;
            } else if (i % 5 == 0) {
                width = mLineMidHeight;
            } else {
                width = mLineMinHeight;
            }

            canvas.drawLine(mBasePadding, top, width + mBasePadding, top, mLinePaint);

            if (i % 10 == 0) {
                Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
                float height = fontMetrics.bottom - fontMetrics.top;//计算行高

                value = String.valueOf((int) (mMinValue + i * mPerValue / 10));
                canvas.drawText(value, mTextPaint.measureText(value) / 2 + mLineMaxHeight + mBasePadding + mTextMarginTop,
                        top + height / 3, mTextPaint);
            }
        }
        drawIndicator(canvas);
    }

    private void drawIndicator(Canvas canvas) {
        //画边框
        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(getContext(), R.color.dividing_line));
        paint.setStrokeWidth(Px2DpUtil.dip2px(getContext(), 1));
        paint.setStyle(Paint.Style.STROKE);
        if (isVertical) {
            canvas.drawRect(mBasePadding, getPaddingTop(), getMeasuredWidth() - mBasePadding, getMeasuredHeight() - getPaddingBottom(), paint);
        } else {
            canvas.drawRect(getPaddingLeft(), getPaddingTop() + mBasePadding, getMeasuredWidth() - getPaddingRight(), getMeasuredHeight() - getPaddingBottom(), paint);
        }
        //画指示器
        notifyValueChange();
        if (isVertical) {
            float centerY = mHeight / 2;
            indicatorPath.reset();
            indicatorPath.moveTo(mBasePadding, centerY);
            indicatorPath.lineTo(mBasePadding / 2, centerY - mBasePadding / 3);
            indicatorPath.lineTo(mBasePadding / 2, centerY + mBasePadding / 3);
            indicatorPath.close();
            canvas.drawPath(indicatorPath, indicatorPaint);
            canvas.drawLine(mBasePadding, centerY, mLineMaxHeight + mBasePadding, centerY, indicatorPaint);
        } else {
            float centerX = mWidth / 2;
            float centerY = getPaddingTop() + mBasePadding;
            indicatorPath.reset();
            indicatorPath.moveTo(centerX, centerY);
            indicatorPath.lineTo(centerX - mBasePadding / 3.5f, centerY - mBasePadding / 3f);
            indicatorPath.lineTo(centerX + mBasePadding / 3.5f, centerY - mBasePadding / 3f);
            indicatorPath.close();
            canvas.drawPath(indicatorPath, indicatorPaint);
            canvas.drawLine(centerX, centerY, centerX, centerY + mLineMaxHeight, indicatorPaint);
        }
    }

    private void drawBg(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.main_body));
        if (isVertical) {
            canvas.drawRect(mBasePadding, getPaddingTop(), getMeasuredWidth() - mBasePadding, getMeasuredHeight() - getPaddingBottom(), paint);
        } else {
            canvas.drawRect(getPaddingLeft(), getPaddingTop() + mBasePadding, getMeasuredWidth() - getPaddingRight(), getMeasuredHeight() - getPaddingBottom(), paint);
        }
    }

    private void drawHorizontol(Canvas canvas) {
        float left, height;
        String value;
        drawBg(canvas);
        int srcPointX = mWidth / 2;
        float startY = getPaddingTop() + mBasePadding;
        for (int i = 0; i < mTotalLine; i++) {
            left = srcPointX + mOffset + i * mLineSpaceWidth;

            if (left < 0 || left > mWidth) {
                continue;
            }

            if (i % 10 == 0) {
                height = mLineMaxHeight;
            } else if (i % 5 == 0) {
                height = mLineMidHeight;
            } else {
                height = mLineMinHeight;
            }

            canvas.drawLine(left, startY, left, startY + height, mLinePaint);

            if (i % 10 == 0) {
                Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
                float paintHeight = fontMetrics.bottom - fontMetrics.top;//计算行高

                value = String.valueOf((int) (mMinValue + i * mPerValue / 10));
                canvas.drawText(value, left + paintHeight / 2 - mTextPaint.measureText(value) / 2,
                        height + startY + mTextMarginTop + mTextHeight, mTextPaint);
            }
        }
        drawIndicator(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int xPosition = (int) event.getX();
        int yPosition = (int) event.getY();

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mScroller.forceFinished(true);
                if (!isVertical) {
                    mLastX = xPosition;
                } else {
                    mLastY = yPosition;
                }
                mMove = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isVertical) {
                    mMove = (mLastX - xPosition);
                } else {
                    mMove = (mLastY - yPosition);
                }
                changeMoveAndValue();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                countMoveEnd();
                countVelocityTracker();
                return false;
            default:
                break;
        }

        if (!isVertical) {
            mLastX = xPosition;
        } else {
            mLastY = yPosition;
        }
        return true;
    }

    private void countVelocityTracker() {
        mVelocityTracker.computeCurrentVelocity(1000);
        if (!isVertical) {
            float xVelocity = mVelocityTracker.getXVelocity();
            if (Math.abs(xVelocity) > mMinVelocity) {
                mScroller.fling(0, 0, (int) xVelocity, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
            }
        } else {
            float yVelocity = mVelocityTracker.getYVelocity();
            if (Math.abs(yVelocity) > mMinVelocity) {
                mScroller.fling(0, 0, 0, (int) yVelocity, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            }
        }
    }

    private void countMoveEnd() {
        mOffset -= mMove;
        if (mOffset <= mMaxOffset) {
            mOffset = mMaxOffset;
        } else if (mOffset >= 0) {
            mOffset = 0;
        }

        mLastY = 0;
        mLastX = 0;
        mMove = 0;

        mSelectorValue = mMinValue + Math.round(Math.abs(mOffset) * 1.0f / mLineSpaceWidth) * mPerValue / 10.0f;
        mOffset = (mMinValue - mSelectorValue) * 10.0f / mPerValue * mLineSpaceWidth;
        notifyValueChange();
        postInvalidate();
    }

    private void changeMoveAndValue() {
        mOffset -= mMove;
        if (mOffset <= mMaxOffset) {
            mOffset = mMaxOffset;
            mMove = 0;
            mScroller.forceFinished(true);
        } else if (mOffset >= 0) {
            mOffset = 0;
            mMove = 0;
            mScroller.forceFinished(true);
        }
        mSelectorValue = mMinValue + Math.round(Math.abs(mOffset) * 1.0f / mLineSpaceWidth) * mPerValue / 10.0f;
        notifyValueChange();
        postInvalidate();
    }

    private void notifyValueChange() {
        if (null != mListener) {
            mListener.onValueChange(mSelectorValue);
        }
    }

    public interface OnValueChangeListener {
        void onValueChange(float value);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            if (!isVertical) {
                if (mScroller.getCurrX() == mScroller.getFinalX()) {
                    countMoveEnd();
                } else {
                    int xPosition = mScroller.getCurrX();
                    mMove = (mLastX - xPosition);
                    changeMoveAndValue();
                    mLastX = xPosition;
                }
            } else {
                if (mScroller.getCurrY() == mScroller.getFinalY()) {
                    countMoveEnd();
                } else {
                    int yPosition = mScroller.getCurrY();
                    mMove = (mLastY - yPosition);
                    changeMoveAndValue();
                    mLastY = yPosition;
                }
            }
        }
    }
}
