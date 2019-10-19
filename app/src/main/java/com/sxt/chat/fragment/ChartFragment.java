package com.sxt.chat.fragment;

import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher;

import com.sxt.chat.R;
import com.sxt.chat.activity.MainActivity;
import com.sxt.chat.base.LazyFragment;
import com.sxt.chat.view.ChartLine;
import com.sxt.library.chart.BeizerCurveLine;
import com.sxt.library.chart.ChartBar;
import com.sxt.library.chart.ChartPie;
import com.sxt.library.chart.CircleProgressView;
import com.sxt.library.chart.bean.ChartBean;
import com.sxt.library.chart.bean.ChartPieBean;
import com.sxt.library.chart.listener.LineOnScrollChangeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by 11837 on 2018/4/22.
 */
public class ChartFragment extends LazyFragment {

    private Handler handler = new Handler();
    private SwipeRefreshLayout swipeRefreshLayout;
    String[] lineName;
    String[] lineUnit;
    int[] lineColor;
    int[] shaderColor;
    private LinearLayout lineLayoutList;
    private List<ChartBean> chartBeanList0;
    private List<ChartBean> chartBeanList;
    private NestedScrollView nestedScrollView;
    private LineOnScrollChangeListener onScrollChangeListener;
    private List<ChartPieBean> pieBeanList;

    @Override
    protected int getDisplayView(LayoutInflater inflater, ViewGroup container) {
        return R.layout.fragment_chart;
    }

    @Override
    protected void initView() {
        nestedScrollView = contentView.findViewById(R.id.nestedScrollView);
        lineLayoutList = contentView.findViewById(R.id.line_layout_list);
        swipeRefreshLayout = contentView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.day_night_dark_color);
        swipeRefreshLayout.setProgressViewOffset(true, -swipeRefreshLayout.getProgressCircleDiameter(), 100);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(activity, R.color.main_blue), ContextCompat.getColor(activity, R.color.red_1), ContextCompat.getColor(activity, R.color.line_yellow), ContextCompat.getColor(activity, R.color.main_green), ContextCompat.getColor(activity, R.color.red_1));
        swipeRefreshLayout.setOnRefreshListener(() -> handler.postDelayed(() -> {
            lineLayoutList.removeAllViews();
            swipeRefreshLayout.setRefreshing(false);
            lineLayoutList.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(activity, R.anim.layout_animation_vertical));
            init();
        }, 2000));
        initData();
        swipeRefreshLayout.post(() -> {
            swipeRefreshLayout.setRefreshing(true);//第一次来 并不会调用onRefresh方法  android bug
            handler.postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false);
                init();
            }, 2000);
        });
        //设置滑动监听,使得底部tab栏竖直滑动
        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            Log.e("scrollY", String.format("oldScrollY = %s ; scrollY = %s", oldScrollY, scrollY));
            MainActivity activity = (MainActivity) ChartFragment.this.activity;
            activity.setBottomBarTranslateY(scrollY, scrollY > oldScrollY);
        });
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (onScrollChangeListener == null) {
                onScrollChangeListener = new LineOnScrollChangeListener();
                nestedScrollView.setOnScrollChangeListener(onScrollChangeListener);
            } else {
                onScrollChangeListener.clearLines();
            }
        }
        for (int i = 0; i < 5; i++) {
            if (i == 0) {
                drawPie();
            } else if (i == 1) {
                drawBar();
            } else if (i == 2) {
                drawCurveLine();
            } else if (i == 4) {
                drawCircleProgress();
            } else {
                drawPie();
            }
        }
    }

    private void drawCircleProgress() {
        View view = View.inflate(activity, R.layout.item_circle_progress, null);
        lineLayoutList.addView(view);
        CircleProgressView itemView = view.findViewById(R.id.chart_circle_progress);
        itemView
                .setDuration(2000)
                .setLabels(
                        new String[]{"运动详情"},
                        new int[]{R.color.colorPrimaryDark}).setProgress(new Random().nextInt(361),
                new Random().nextInt(10001),
                "今日步数");
    }

    //柱状图-------------------------------------------------------------------------------------
    private void drawBar() {
        View barView = View.inflate(activity, R.layout.item_chart_bar, null);
        lineLayoutList.addView(barView);
        barView.setTag(lineLayoutList.getChildCount() - 1);

        final ChartBar chartBar = barView.findViewById(R.id.chartbar);
        //设置柱状图的数据源
        chartBar
                .setRectData(chartBeanList)
                .setLabels(
                        new String[]{getString(R.string.string_label_smzl), getString(R.string.string_label_smzl_bad), getString(R.string.string_label_smzl_good)},
                        new int[]{lineColor[0], lineColor[1], lineColor[3]})
                .start();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            onScrollChangeListener.addLine(chartBar);
        }
        chartBar.start();
    }

    private void initData() {
        lineName = new String[]{getString(R.string.string_label_press), getString(R.string.string_label_xt), getString(R.string.string_label_hb), getString(R.string.string_label_bt)};
        lineColor = new int[]{R.color.violet_rgb_185_101_255, R.color.red_rgb_255_127_87, R.color.red_1, R.color.blue_rgba_24_261_255, R.color.green_rgb_40_220_162};
        shaderColor = new int[]{R.color.violet_shader, R.color.red_shader, R.color.red_shader, R.color.blue_shader, R.color.green_shader};
        lineUnit = new String[]{getString(R.string.string_unit_xt), getString(R.string.string_unit_hb), getString(R.string.string_unit_press), getString(R.string.string_unit_bt)};

        chartBeanList = new ArrayList<>();
        chartBeanList.add(new ChartBean("9月", 10));
        chartBeanList.add(new ChartBean("1", 30));
        chartBeanList.add(new ChartBean("2", 69));
        chartBeanList.add(new ChartBean("3", 100));
        chartBeanList.add(new ChartBean("4", 34));
        chartBeanList.add(new ChartBean("5", 85));
        chartBeanList.add(new ChartBean("6", 26));
        chartBeanList.add(new ChartBean("6", 9));
        chartBeanList.add(new ChartBean("6", 60));

        chartBeanList0 = new ArrayList<>();
        chartBeanList0.add(new ChartBean("9月", 1));
        chartBeanList0.add(new ChartBean("1", 88));
        chartBeanList0.add(new ChartBean("2", 30));
        chartBeanList0.add(new ChartBean("3", 108));
        chartBeanList0.add(new ChartBean("4", 34));
        chartBeanList0.add(new ChartBean("5", 90));
        chartBeanList0.add(new ChartBean("6", 33));

        pieBeanList = new ArrayList<>();
        pieBeanList.add(new ChartPieBean(3090, getString(R.string.chart_pie_yjsy), R.color.main_green));
        pieBeanList.add(new ChartPieBean(501f, getString(R.string.chart_pie_shopping), R.color.blue_rgba_24_261_255));
        pieBeanList.add(new ChartPieBean(800, getString(R.string.chart_pie_rcharge), R.color.orange));
        pieBeanList.add(new ChartPieBean(1000, getString(R.string.chart_pie_living_payment), R.color.red_2));
        pieBeanList.add(new ChartPieBean(2300, getString(R.string.chart_pie_breakfast), R.color.progress_color_default));
    }

    private void drawPie() {
        //底部的曲线图
        View childAt = View.inflate(activity, R.layout.item_chart_pie, null);
        lineLayoutList.addView(childAt);
        ChartPie chartPie = childAt.findViewById(R.id.chart_pie);
        chartPie.setData(pieBeanList).start();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //将当前曲线添加到ScrollView的滑动监听中
            onScrollChangeListener.addLine(chartPie);
        }
        chartPie.start();
    }

    private void drawLine() {
        //底部的折线图
        View childAt = View.inflate(activity, R.layout.item_chart_line, null);
        lineLayoutList.addView(childAt);
        ChartLine chartLine = childAt.findViewById(R.id.chart_line);
        ChartLine.ChartLineBuilder builder = new ChartLine.ChartLineBuilder();
        List<ChartBean> chartBeans = new ArrayList<>();
        List<ChartBean> chartBeans2 = new ArrayList<>();

        for (int y = 0; y < chartBeanList.size(); y++) {
            ChartBean chartBean = chartBeanList.get(y);
            ChartBean chartBean2 = chartBeanList0.get(y);
            chartBeans.add(new ChartBean(chartBean.x, chartLine.parseFloat(String.valueOf(chartBean.y))));
            chartBeans2.add(new ChartBean(chartBean2.x, chartLine.parseFloat(String.valueOf(chartBean2.y))));
        }
        chartLine.setMaxXNum(6);
        builder.builder(chartBeans, lineColor[0], shaderColor[0])
                .builder(chartBeans2, lineColor[1], shaderColor[1]);

        builder.build(chartLine);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //将当前曲线添加到ScrollView的滑动监听中
            onScrollChangeListener.addLine(chartLine);
        }
        chartLine.start();
    }

    private void drawCurveLine() {
        //底部的曲线图
        View childAt = View.inflate(activity, R.layout.item_chart_curve_line, null);
        lineLayoutList.addView(childAt);
        BeizerCurveLine chartLine = childAt.findViewById(R.id.chart_curve_line);
        BeizerCurveLine.CurveLineBuilder builder = new BeizerCurveLine.CurveLineBuilder();
        List<ChartBean> chartBeans = new ArrayList<>();

        for (int y = 0; y < chartBeanList0.size(); y++) {
            ChartBean chartBean = chartBeanList0.get(y);
            chartBeans.add(new ChartBean(chartBean.x, chartLine.parseFloat(String.valueOf(chartBean.y))));
        }
        chartLine
                .setMaxXNum(6)
                .setFillState(true)
                .setXYColor(R.color.text_color_3)
                .setHintLineColor(R.color.text_color_3)
                .setUnit(lineUnit[0]);

        chartLine.setLabels(new String[]{lineName[0], getString(R.string.string_label_press_hight), getString(R.string.string_label_press_lower)}, new int[]{lineColor[0], lineColor[2], lineColor[1]});
        builder.builder(chartBeans, lineColor[0], shaderColor[0]);

        builder.build(chartLine);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //将当前曲线添加到ScrollView的滑动监听中
            onScrollChangeListener.addLine(chartLine);
        }
        chartLine.start();
    }
}