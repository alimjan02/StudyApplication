//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sxt.chat.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;

import com.sxt.chat.R;
import com.sxt.library.chart.R.color;
import com.sxt.library.chart.R.styleable;
import com.sxt.library.chart.base.BaseChart;
import com.sxt.library.chart.bean.ChartBean;
import com.sxt.library.chart.utils.DateFormatUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChartLine extends BaseChart {
    private Paint basePaint;
    private Paint baseLabelPaint;
    private Paint xyPaint;
    private Paint touchPaint;
    private Paint curvePaint;
    private Paint coverPaint;
    private Path coverPath;
    private Paint fillPaint;
    private Path fillPath;
    private float basePadding = 30.0F;
    private float startX;
    private float endX;
    private float startY;
    private float endY;
    private boolean isFilled;
    private boolean isShowCoverLine;
    private boolean isShowXy;
    private boolean isShowHintLines;
    private boolean isPlayAnimator;
    private boolean isCanTouch;
    private Paint hintPaint;
    private int maxValueOfY = 100;
    private String[] labelStrs;
    private int[] labelColors;
    private Map<Integer, List<ChartBean>> curveDataLists;
    private Map<Integer, Integer> curvePaintColors;
    private Map<Integer, Integer> curveShaderColors;
    private List<Path> pathList;
    private long duration;
    private String unit;
    private int hintLinesNum = 6;
    private float curveXo;
    private int xNum = 4;
    private boolean isShowFloat = false;
    private float downX;
    private float downY;
    private float moveX;
    private float moveY;
    private float startX0;
    private float startY0;
    private boolean onTouch = false;
    @SuppressLint({"HandlerLeak"})
    private Handler handler = new Handler();
    private PathMeasure pathMeasureCover;
    private float curveLength;
    private boolean starting = false;
    private boolean isFirst = true;
    private float mAnimatorValue;
    private ValueAnimator valueAnimator;
    private Map<Integer, List<float[]>> pointLineList;

    public ChartLine(Context context) {
        super(context);
    }

    public ChartLine(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ChartLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(Context context, AttributeSet attrs) {
        super.init(context, attrs);
        this.initPaint();
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, styleable.BeizerCurveLine);
            if (typedArray != null) {
                try {
                    this.duration = (long) typedArray.getInt(styleable.BeizerCurveLine_line_duration, 1500);
                    this.isPlayAnimator = typedArray.getBoolean(styleable.BeizerCurveLine_line_isPlayAnimator, true);
                    this.isCanTouch = typedArray.getBoolean(styleable.BeizerCurveLine_line_isCanTouch, false);
                    this.isFilled = typedArray.getBoolean(styleable.BeizerCurveLine_line_isFilled, false);
                    this.isShowCoverLine = typedArray.getBoolean(styleable.BeizerCurveLine_line_isShowCoverLine, true);
                    this.isShowHintLines = typedArray.getBoolean(styleable.BeizerCurveLine_line_isShowHintLines, true);
                    this.isShowXy = typedArray.getBoolean(styleable.BeizerCurveLine_line_isShowXy, false);
                    this.isShowFloat = typedArray.getBoolean(styleable.BeizerCurveLine_line_isShowFloat, false);
                    this.unit = typedArray.getString(styleable.BeizerCurveLine_line_unit);
                    int line_xy_color = typedArray.getColor(styleable.BeizerCurveLine_line_xy_color, -7829368);
                    int line_hint_color = typedArray.getColor(styleable.BeizerCurveLine_line_hint_color, -7829368);
                    float line_hint_width = typedArray.getDimension(styleable.BeizerCurveLine_line_hint_width, 0.5F);
                    float line_cover_width = typedArray.getDimension(styleable.BeizerCurveLine_line_cover_width, 3.5F);
                    this.xyPaint.setColor(line_xy_color);
                    this.hintPaint.setColor(line_hint_color);
                    this.hintPaint.setStrokeWidth(line_hint_width);
                    this.coverPaint.setStrokeWidth(line_cover_width);
                } finally {
                    typedArray.recycle();
                }
            }
        }

    }

    private void initPaint() {
        this.basePaint = new Paint(1);
        this.basePaint.setColor(-7829368);
        this.basePaint.setStrokeWidth((float) this.dip2px(0.5F));
        this.basePaint.setTextSize((float) this.dip2px(10.0F));
        this.basePaint.setTextAlign(Align.LEFT);
        this.basePaint.setStrokeCap(Cap.ROUND);
        this.basePaint.setDither(true);
        this.baseLabelPaint = new Paint();
        this.baseLabelPaint.setColor(ContextCompat.getColor(this.getContext(), color.black));
        this.baseLabelPaint.setTextSize((float) this.dip2px(14.0F));
        this.baseLabelPaint.setTextAlign(Align.LEFT);
        Typeface font0 = Typeface.create(Typeface.SANS_SERIF, Typeface.DEFAULT_BOLD.getStyle());
        this.baseLabelPaint.setTypeface(font0);
        this.xyPaint = new Paint(this.basePaint);
        this.xyPaint.setColor(-7829368);
        this.xyPaint.setStrokeWidth((float) this.dip2px(1.0F));
        this.hintPaint = new Paint(this.basePaint);
        this.hintPaint.setStrokeWidth(0.5F);
        this.curvePaint = new Paint(this.basePaint);
        this.curvePaint.setStyle(Style.STROKE);
        this.curvePaint.setStrokeWidth((float) this.dip2px(4.0F));
        this.coverPaint = new Paint(this.basePaint);
        this.coverPaint.setStyle(Style.STROKE);
        this.coverPaint.setStrokeWidth((float) this.dip2px(4.0F));
        this.fillPaint = new Paint(this.basePaint);
        this.fillPaint.setStyle(Style.FILL);
        this.fillPaint.setStrokeWidth((float) this.dip2px(4.0F));
        this.coverPath = new Path();
        this.fillPath = new Path();
        this.touchPaint = new Paint(this.hintPaint);
        this.touchPaint.setStyle(Style.FILL);
        this.touchPaint.setColor(-16777216);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            this.startX = (float) this.getPaddingLeft() + basePadding;
            this.endX = (float) (this.getMeasuredWidth() - this.getPaddingRight()) - basePadding;
            this.startY = (float) (this.getMeasuredHeight() - this.getPaddingBottom()) - basePadding;
            this.endY = (float) this.getPaddingTop() + basePadding;
        }

    }

    protected void onDraw(Canvas canvas) {
        this.drawNoTouch(canvas);
        if (this.isCanTouch && this.onTouch && (double) this.mAnimatorValue == 1.0D) {
            this.drawOnTouch(canvas);
        }

        super.onDraw(canvas);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!this.isCanTouch) {
            return super.onTouchEvent(event);
        } else {
            switch (event.getAction()) {
                case 0:
                    if ((double) this.mAnimatorValue == 1.0D) {
                        this.onTouch = true;
                        this.downX = event.getX();
                        this.downY = event.getY();
                        this.moveX = this.downX;
                        this.moveY = this.downY;
                        this.startX0 = this.moveX;
                        this.startY0 = this.moveY;
                    }

                    Log.i("line", "Down");
                    break;
                case 1:
                    this.moveX = event.getX();
                    this.moveY = event.getY();
                    this.startX0 = this.moveX;
                    this.startY0 = this.moveY;
                    this.postDelayedInvalidate();
                    Log.i("line", "Up");
                    break;
                case 2:
                    if ((double) this.mAnimatorValue == 1.0D) {
                        this.moveX = event.getX();
                        this.moveY = event.getY();
                        if (this.moveX >= (float) this.getLeft() && this.moveX <= (float) this.getRight() && this.moveY >= (float) this.getTop() && this.moveY <= (float) this.getBottom()) {
                            this.getParent().requestDisallowInterceptTouchEvent(true);
                            this.invalidate();
                        } else {
                            this.postDelayedInvalidate();
                        }
                    }

                    Log.i("line", "Move");
            }

            return this.onTouch && (double) this.mAnimatorValue == 1.0D ? true : super.onTouchEvent(event);
        }
    }

    public void postDelayedInvalidate() {
        this.onTouch = false;
        this.getParent().requestDisallowInterceptTouchEvent(false);
        this.handler.postDelayed(new Runnable() {
            public void run() {
                ChartLine.this.invalidate();
            }
        }, 1000L);
    }

    private void drawNoTouch(Canvas canvas) {
        this.drawLabels(canvas);
        this.drawLines(canvas);
        if (this.curveDataLists != null && this.curveDataLists.size() != 0) {
            this.drawXY(canvas, (List) this.curveDataLists.get(Integer.valueOf(0)));

            for (int i = 0; i < this.curveDataLists.size(); ++i) {
                List<ChartBean> chartBeanList = (List) this.curveDataLists.get(Integer.valueOf(i));
                if (chartBeanList.size() <= 1) {
                    this.drawPoint(canvas);
                    canvas.save();
                } else {
                    this.drawCurveLines(canvas);
                    drawPoints(canvas);//画点
                }
            }
        }
    }

    private void drawPoints(Canvas canvas) {
        if (pointLineList == null || pointLineList.size() == 0) return;
        Paint paint = new Paint(xyPaint);
        paint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
        for (int i = 0; i < pointLineList.size(); i++) {
            float[] points = new float[pointLineList.get(i).size() * 2];
            int size = (int) (pointLineList.get(i).size() * mAnimatorValue);
            for (int j = 0; j < size; j++) {
                int newJ = j * 2;
                points[newJ] = pointLineList.get(i).get(j)[0];
                points[newJ + 1] = pointLineList.get(i).get(j)[1];
                if (j < pointLineList.get(i).size() - 1) {
                    points[newJ + 2] = pointLineList.get(i).get(j + 1)[0];
                    points[newJ + 3] = pointLineList.get(i).get(j + 1)[1];
                }
            }
            Log.e(TAG, "points : " + Arrays.toString(points));
            canvas.drawPoints(points, paint);
        }
    }

    @SuppressLint({"ResourceType"})
    private void drawOnTouch(Canvas canvas) {
        if (this.curveDataLists == null || this.curveDataLists.size() == 0) return;
        for (int i = 0; i < this.curveDataLists.size(); i++) {
            int index = (int) ((this.moveX - this.startX) / this.getDx());
            if (index >= this.curveDataLists.get(i).size()) {
                index = this.curveDataLists.get(i).size() - 1;
            }
            float y = this.curveDataLists.get(i).get(index).y;
            float maxValue = this.calculateMaxValueOfY();
            float minValue = this.calculateMinValueOfY();
            float dy = (this.startY - this.endY) / (maxValue - minValue);
            float x1 = this.curveXo + (float) index * this.getDx();
            float y1 = this.startY - (y - minValue) * dy;
            canvas.drawLine(x1, this.startY, x1, this.endY, this.touchPaint);
            canvas.drawLine(this.startX + 2.0F * this.basePadding, y1, this.endX, y1, this.touchPaint);
            Paint paint = new Paint(this.touchPaint);
            paint.setColor(-1);
            paint.setStrokeWidth((float) this.dip2px(9.5F));
            canvas.drawPoint(x1, y1, paint);
            paint.setColor(-16777216);
            paint.setStrokeWidth((float) this.dip2px(6.5F));
            canvas.drawPoint(x1, y1, paint);
            Paint p = new Paint(this.touchPaint);
            p.setColor(-16777216);
            p.setTextAlign(Align.CENTER);
            p.setTextSize((float) this.dip2px(15.0F));
            if (this.isShowFloat) {
                canvas.drawText(String.valueOf(y), x1, y1 - this.basePadding / 2.0F, p);
            } else {
                canvas.drawText(String.valueOf((int) y), x1, y1 - this.basePadding / 2.0F, p);
            }
        }
    }

    private void drawPoint(Canvas canvas) {
        float maxValue = this.calculateMaxValueOfY();
        float minValue = this.calculateMinValueOfY();
//        float dy0 = (this.startY - this.endY) / (float) this.hintLinesNum;
//        float dy = dy0 * (float) (this.hintLinesNum - 1) / maxValue;
        float dy = (this.startY - this.endY) / (maxValue - minValue);

        for (int i = 0; i < this.curveDataLists.size(); ++i) {
            for (int j = 0; j < this.curveDataLists.get(i).size(); ++j) {
                List<ChartBean> chartBeanList = this.curveDataLists.get(i);
                if (chartBeanList.size() <= 1) {
                    this.fillPaint.setColor(ContextCompat.getColor(this.getContext(), this.curvePaintColors.get(i)));
                    float yValue = this.startY - this.curveDataLists.get(i).get(j).y * dy;
                    canvas.drawPoint(this.curveXo, yValue, this.fillPaint);
                }
            }
        }

    }

    private void initPath() {
        float maxValue = this.calculateMaxValueOfY();
        float minValue = this.calculateMinValueOfY();
        this.curveXo = this.startX + this.basePadding * 2.5F;
        float dx = this.getDx();
        float dy = (this.startY - this.endY) / (maxValue - minValue);
//        float dy0 = (this.startY - this.endY) / (float) this.hintLinesNum;
//        float dy = dy0 * (float) (this.hintLinesNum - 1) / (maxValue <= 0.0F ? (float) (this.hintLinesNum - 1) : maxValue);
        this.pathList = new ArrayList();
        if (pointLineList != null) {
            pointLineList.clear();
        } else {
            pointLineList = new LinkedHashMap<>();
        }
        for (int j = 0; j < this.curveDataLists.size(); ++j) {
            Path curvePath = new Path();
            float targetEndX = this.curveXo + this.getDx() * (float) ((this.curveDataLists.get(j)).size() - 1);
            List<ChartBean> curveBeanList = this.curveDataLists.get(j);
            pointLineList.put(j, new ArrayList<float[]>());
            List<float[]> floats = pointLineList.get(j);
            for (int i = curveBeanList.size() - 1; i >= 0; --i) {
                if (i == curveBeanList.size() - 1) {
                    float yValue = this.startY - ((curveBeanList.get(i)).y - minValue) * dy;
                    curvePath.moveTo(targetEndX, yValue);
                    floats.add(new float[]{targetEndX, yValue});//记录点坐标
                } else {
                    float preX = this.curveXo + dx * (float) (i + 1);
                    float preY = this.startY - ((curveBeanList.get(i + 1)).y - minValue) * dy;
                    float currentX = this.curveXo + dx * (float) i;
                    float currentY = this.startY - ((curveBeanList.get(i)).y - minValue) * dy;
                    curvePath.lineTo(currentX, currentY);
                    //TODO 只有在曲线的情况下才计算贝塞尔曲线 额外添加标记判断是否为曲线 isBezierCurveLine
//                    curvePath.cubicTo((preX + currentX) / 2.0F, preY, (preX + currentX) / 2.0F, currentY, currentX, currentY);
                    //TODO 这里记录折线的坐标
                    floats.add(new float[]{currentX, currentY});//记录点坐标
                }
            }

            Collections.reverse(floats);//因为path中是倒序的 ,所以这里再次倒序来修复数据
            pointLineList.put(j, floats);
            this.pathList.add(curvePath);
        }
    }

    private float calculateMaxValueOfY() {
        if (this.curveDataLists != null && this.curveDataLists.size() > 0) {
            float max = this.curveDataLists.get(0).get(0).y;

            for (int j = 0; j < this.curveDataLists.size(); ++j) {
                for (int i = 0; i < this.curveDataLists.get(j).size(); ++i) {
                    float f = this.curveDataLists.get(j).get(i).y;
                    if (max < f) {
                        max = f;
                    }
                }
            }
            return max;
        } else {
            return 0.0F;
        }
    }

    private float calculateMinValueOfY() {
        if (this.curveDataLists != null && this.curveDataLists.size() > 0) {
            float min = this.curveDataLists.get(0).get(0).y;

            for (int j = 0; j < this.curveDataLists.size(); ++j) {
                for (int i = 0; i < this.curveDataLists.get(j).size(); ++i) {
                    float f = this.curveDataLists.get(j).get(i).y;
                    if (min > f) {
                        min = f;
                    }
                }
            }
            return min;
        } else {
            return 0.0F;
        }
    }

    private void drawCurveLines(Canvas canvas) {
        if (this.pathList != null && this.pathList.size() != 0) {
            Paint currentCoverPaint = null;
            Paint currentFillPaint = null;

            for (int i = 0; i < this.pathList.size(); ++i) {
                if (this.isShowCoverLine) {
                    currentCoverPaint = new Paint(this.coverPaint);
                    currentCoverPaint.setColor(ContextCompat.getColor(this.getContext(), this.curvePaintColors.get(i)));
                }

                if (this.isFilled) {
                    currentFillPaint = new Paint(this.fillPaint);
                    currentFillPaint.setColor(ContextCompat.getColor(this.getContext(), this.curvePaintColors.get(i)));
                    currentFillPaint.setShader(this.getShader(new int[]{ContextCompat.getColor(this.getContext(), this.curveShaderColors.get(i)), ContextCompat.getColor(this.getContext(), R.color.violet_shader)}));
                }

                if (this.isPlayAnimator) {
                    this.pathMeasureCover = new PathMeasure((Path) this.pathList.get(i), false);
                    this.curveLength = this.pathMeasureCover.getLength();
                    Path dst = new Path();
                    Path dst0 = new Path();
                    this.pathMeasureCover.getSegment(this.curveLength * (1.0F - this.mAnimatorValue), this.curveLength, dst, true);
                    if (this.isFilled) {
                        dst.lineTo(this.curveXo, this.startY);
                        dst.lineTo(this.getDx() * (float) ((this.curveDataLists.get(i).size() - 1) * this.mAnimatorValue + this.curveXo), this.startY);
                        dst.close();

                        assert currentFillPaint != null;

                        canvas.drawPath(dst, currentFillPaint);
                    }

                    if (this.isShowCoverLine) {
                        this.pathMeasureCover.getSegment(this.curveLength * (1.0F - this.mAnimatorValue), this.curveLength, dst0, true);

                        assert currentCoverPaint != null;

                        canvas.drawPath(dst0, currentCoverPaint);
                    }

                    if (!this.isFilled && !this.isShowCoverLine) {
                        canvas.drawPath(dst0, this.curvePaint);
                    }
                } else {
                    if (this.isFilled) {
                        this.fillPath.set((Path) this.pathList.get(i));
                        this.fillPath.lineTo(this.curveXo, this.startY);
                        this.fillPath.lineTo(this.curveXo + this.getDx() * (float) ((List) this.curveDataLists.get(Integer.valueOf(i))).size(), this.startY);
                        this.fillPath.close();

                        assert currentFillPaint != null;

                        canvas.drawPath(this.fillPath, currentFillPaint);
                    }

                    if (this.isShowCoverLine) {
                        this.coverPath.set((Path) this.pathList.get(i));

                        assert currentCoverPaint != null;

                        canvas.drawPath(this.coverPath, currentCoverPaint);
                    }

                    if (!this.isFilled && !this.isShowCoverLine) {
                        canvas.drawPath((Path) this.pathList.get(i), this.curvePaint);
                    }
                }
            }

        }
    }

    private void drawLabels(Canvas canvas) {
        if (this.labelStrs != null && this.labelStrs.length != 0) {
            if (this.labelColors != null && this.labelColors.length != 0) {
                float labelCenterY = this.endY - this.basePadding * 1.5F;
                Paint leftLabelPaint = new Paint(this.baseLabelPaint);
                leftLabelPaint.setTextSize(this.size2sp(15.0F, this.getContext()));
                leftLabelPaint.setTextAlign(Align.LEFT);
                Typeface font0 = Typeface.create(Typeface.MONOSPACE, Typeface.DEFAULT_BOLD.getStyle());
                leftLabelPaint.setTypeface(font0);
                canvas.drawText(this.labelStrs[0], this.startX + this.basePadding * 0.6F, labelCenterY, leftLabelPaint);
                float top0 = leftLabelPaint.getFontMetrics().top;
                float descent0 = leftLabelPaint.getFontMetrics().descent;
                Paint rectPaint = new Paint(this.basePaint);
                rectPaint.setStyle(Style.FILL);
                rectPaint.setColor(ContextCompat.getColor(this.getContext(), this.labelColors[0]));
                canvas.drawRect(this.startX, labelCenterY + top0 * 0.8F, this.startX + this.basePadding / 3.0F, labelCenterY + descent0 / 2.0F, rectPaint);
                if (this.labelStrs.length == 3 && this.labelColors.length == 3) {
                    float left = this.endX - this.basePadding * 8.0F;
                    float baseY = this.endY - this.basePadding;
                    float right = left + 4.5F * this.basePadding;
                    float DX = this.basePadding / 2.0F;
                    Paint paint = new Paint(this.basePaint);
                    paint.setTextSize((float) this.dip2px(10.0F));
                    paint.setColor(-16777216);
                    paint.setTextAlign(Align.LEFT);
                    canvas.drawText(this.labelStrs[1], left, baseY, paint);
                    float top1 = paint.getFontMetrics().top;
                    float descent1 = paint.getFontMetrics().descent;
                    canvas.drawText(this.labelStrs[2], right, baseY, paint);
                    float top2 = paint.getFontMetrics().top;
                    float descent2 = paint.getFontMetrics().descent;
                    rectPaint.setColor(ContextCompat.getColor(this.getContext(), this.labelColors[1]));
                    float top11 = top1 * 0.8F;
                    float descent11 = descent1 * 0.6F;
                    canvas.drawRect(left - DX + top11 - descent11, baseY + top11, left - DX, baseY + descent11, rectPaint);
                    float top22 = top2 * 0.8F;
                    rectPaint.setColor(ContextCompat.getColor(this.getContext(), this.labelColors[2]));
                    canvas.drawRect(right - DX + top11 - descent11, baseY + top22, right - DX, baseY + descent11, rectPaint);
                }
            }
        }
    }

    private void drawLines(Canvas canvas) {
        if (this.isShowHintLines) {
            float dy = (this.startY - this.endY) / (float) this.hintLinesNum;
            float x0 = this.startX + this.basePadding * 2.0F;

            for (int i = 0; i < this.hintLinesNum + 1; ++i) {
                if (i == this.hintLinesNum) {
                    canvas.drawLine(this.startX, this.startY - dy * (float) i, this.endX, this.startY - dy * (float) i, this.hintPaint);
                } else {
                    canvas.drawLine(x0, this.startY - dy * (float) i, this.endX, this.startY - dy * (float) i, this.hintPaint);
                }

                Paint unitPaint;
                if (i == this.hintLinesNum - 1) {
                    unitPaint = new Paint(this.xyPaint);
                    unitPaint.setTextAlign(Align.LEFT);
                    float baseY = this.startY - dy * (float) i - this.basePadding / 2.0F;
                    if (!TextUtils.isEmpty(this.unit)) {
                        canvas.drawText(this.unit, this.startX, baseY, unitPaint);
                    }
                    float maxValue = this.calculateMaxValueOfY();
                    String max = maxValue <= 0.0F ? "" : (this.isShowFloat ? String.valueOf(maxValue) : String.valueOf((int) maxValue));
                    canvas.drawText(max, this.startX, baseY + this.basePadding * 1.3F, unitPaint);
                }

                if (i == 0) {
                    unitPaint = new Paint(this.xyPaint);
                    unitPaint.setTextAlign(Align.LEFT);
                    canvas.drawText(String.valueOf(0), this.startX, this.startY, unitPaint);
                }
            }
        }

    }

    private void drawXY(Canvas canvas, List<ChartBean> xDatas) {
        if (this.isShowXy) {
            canvas.drawLine(this.startX, this.startY, this.endX, this.startY, this.xyPaint);
            canvas.drawLine(this.startX, this.startY, this.startX, this.endY, this.xyPaint);
        }

        float x0 = this.startX + this.basePadding;
        float dx = (this.endX - this.curveXo - this.basePadding) / (float) this.xNum;
        float y = this.startY + this.basePadding * 2.0F;
        this.xyPaint.setTextAlign(Align.LEFT);

        for (int i = 0; i < xDatas.size(); ++i) {
            canvas.drawText(((ChartBean) xDatas.get(i)).x, x0 + dx * (float) i, y, this.xyPaint);
        }

    }

    public ChartLine setMaxXNum(int xNum) {
        this.xNum = xNum;
        return this;
    }

    private float getDx() {
        return this.curveDataLists != null && this.curveDataLists.get(Integer.valueOf(0)) != null ? (this.endX - this.curveXo) / (float) this.xNum : 0.0F;
    }

    public ChartLine setLabels(String[] labelStrs, int[] labelColors) {
        this.labelStrs = labelStrs;
        this.labelColors = labelColors;
        return this;
    }

    public ChartLine setPlayAnimator(boolean isPlayAnimator) {
        this.isPlayAnimator = isPlayAnimator;
        return this;
    }

    public ChartLine setMaxValueOfY(int maxValueOfY) {
        this.maxValueOfY = maxValueOfY;
        return this;
    }

    public ChartLine setCoverLine(boolean isShowCoverLine) {
        this.isShowCoverLine = isShowCoverLine;
        return this;
    }

    public ChartLine setCoverLine(int coverLineColor) {
        if (this.coverPaint != null) {
            this.coverPaint.setColor(coverLineColor);
        }

        return this;
    }

    public ChartLine setCoverLineWidth(float widthDpValue) {
        if (this.coverPaint != null) {
            this.coverPaint.setStrokeWidth((float) this.dip2px(widthDpValue));
        }

        return this;
    }

    public ChartLine setFillState(boolean isFilled) {
        this.isFilled = isFilled;
        return this;
    }

    public ChartLine setXYShowState(boolean xyShowState) {
        this.isShowXy = xyShowState;
        return this;
    }

    public ChartLine setXYColor(int colorResId) {
        if (this.xyPaint != null) {
            this.xyPaint.setColor(ContextCompat.getColor(this.getContext(), colorResId));
        }

        return this;
    }

    public ChartLine setHintLineColor(int colorResId) {
        if (this.hintPaint != null) {
            this.hintPaint.setColor(ContextCompat.getColor(this.getContext(), colorResId));
        }

        return this;
    }

    public ChartLine setShowFloat(boolean isShowFloat) {
        this.isShowFloat = isShowFloat;
        return this;
    }

    public ChartLine setShowHintLines(boolean showHintLines) {
        this.isShowHintLines = showHintLines;
        return this;
    }

    public ChartLine setAnimDurationTime(long duration) {
        this.duration = duration;
        return this;
    }

    private ChartLine setHintLinesNum(int hintLinesNum) {
        if (hintLinesNum > -1) {
            this.hintLinesNum = hintLinesNum;
        }

        return this;
    }

    public ChartLine setUnit(String unit) {
        this.unit = unit;
        return this;
    }

    private LinearGradient getShader(int[] colors) {
        if (colors != null) {
            if (colors.length == 0) {
                colors = new int[]{-16711936, ContextCompat.getColor(this.getContext(), color.alpha_sharder)};
            }
        } else {
            colors = new int[]{-16711936, -16711936, ContextCompat.getColor(this.getContext(), color.alpha_sharder)};
        }

        return new LinearGradient(this.startX, this.endY, this.startX, this.startY, colors, (float[]) null, TileMode.CLAMP);
    }

    @NonNull
    public String parseDate(String currentDate) {
        if (currentDate != null && currentDate.length() > 0) {
            try {
                String currentStr = String.valueOf(DateFormatUtil.getSecondsFromDate(currentDate));
                String mm = DateFormatUtil.getDateFromSeconds(currentStr, "MM");
                String dd = DateFormatUtil.getDateFromSeconds(currentStr, "dd");
                if (mm.startsWith("0")) {
                    mm = mm.substring(1, mm.length());
                }

                if (dd.startsWith("0")) {
                    dd = dd.substring(1, dd.length());
                }

                return dd + "/" + mm;
            } catch (Exception var5) {
                var5.printStackTrace();
                return "";
            }
        } else {
            return "";
        }
    }

    public float parseFloat(String floatStr) {
        if (floatStr != null && floatStr.length() > 0) {
            try {
                if (floatStr.length() > 2 && floatStr.contains("/")) {
                    String[] split = floatStr.split("/");
                    return (new BigDecimal(split[0])).divide(new BigDecimal(split[1])).floatValue();
                } else {
                    return Float.parseFloat(floatStr) <= 0.0F ? 0.0F : Float.parseFloat(floatStr);
                }
            } catch (Exception var3) {
                var3.printStackTrace();
                return 0.0F;
            }
        } else {
            return 0.0F;
        }
    }

    private void startDraw(Map<Integer, List<ChartBean>> curveDataLists, Map<Integer, Integer> curvePaintColors, Map<Integer, Integer> curveShaderColors) {
        if (curveDataLists != null && curveDataLists.size() != 0 && curvePaintColors != null && curvePaintColors.size() != 0) {
            this.curveDataLists = curveDataLists;
            this.curvePaintColors = curvePaintColors;
            this.curveShaderColors = curveShaderColors;
            this.start();
        }
    }

    private void startDraw() {
        if (this.curveDataLists != null && this.curveDataLists.size() != 0 && this.curvePaintColors != null && this.curvePaintColors.size() != 0) {
            if (this.isFirst && !this.starting) {
                this.starting = true;
                this.initPath();
                if (this.isPlayAnimator) {
                    this.startAnimator();
                } else {
                    this.invalidate();
                }

            }
        }
    }

    public void start() {
        super.start();
        if (VERSION.SDK_INT >= 23) {
            if (this.isCover(this)) {
                this.startDraw();
            } else {
                this.post(new Runnable() {
                    public void run() {
                        if (ChartLine.this.isCover(ChartLine.this)) {
                            ChartLine.this.startDraw();
                        }

                    }
                });
            }
        } else {
            this.post(new Runnable() {
                public void run() {
                    ChartLine.this.startDraw();
                }
            });
        }

    }

    private void startAnimator() {
        this.valueAnimator = ValueAnimator.ofFloat(new float[]{0.0F, 1.0F}).setDuration(this.duration);
        this.valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                ChartLine.this.mAnimatorValue = ((Float) animation.getAnimatedValue()).floatValue();
                if (ChartLine.this.starting) {
                    ChartLine.this.invalidate();
                }

            }
        });
        this.valueAnimator.addListener(new AnimatorListener() {
            public void onAnimationStart(Animator animation) {
            }

            public void onAnimationEnd(Animator animation) {
                ChartLine.this.starting = false;
                ChartLine.this.isFirst = false;
            }

            public void onAnimationCancel(Animator animation) {
            }

            public void onAnimationRepeat(Animator animation) {
            }
        });
        this.valueAnimator.start();
    }

    protected void onDetachedFromWindow() {
        if (this.handler != null) {
            this.handler.removeCallbacksAndMessages((Object) null);
        }

        if (this.valueAnimator != null && this.valueAnimator.isRunning()) {
            this.valueAnimator.cancel();
        }

        super.onDetachedFromWindow();
    }

    public static class ChartLineBuilder {
        public Map<Integer, List<ChartBean>> curveDataLists = new HashMap();
        public Map<Integer, Integer> curvePaintColors = new HashMap();
        public Map<Integer, Integer> curveShaderColors = new HashMap();
        private int index;

        public ChartLineBuilder() {
        }

        public ChartLineBuilder builder(List<ChartBean> curveBeans, int coverLineColor, int shaderColor) {
            if (curveBeans != null && curveBeans.size() > 0) {
                int index = this.index;
                this.curveDataLists.put(Integer.valueOf(index), curveBeans);
                this.curvePaintColors.put(Integer.valueOf(index), Integer.valueOf(coverLineColor));
                this.curveShaderColors.put(Integer.valueOf(index), Integer.valueOf(shaderColor));
                ++this.index;
            }

            return this;
        }

        public void build(ChartLine chartLine) {
            chartLine.startDraw(this.curveDataLists, this.curvePaintColors, this.curveShaderColors);
        }
    }
}
