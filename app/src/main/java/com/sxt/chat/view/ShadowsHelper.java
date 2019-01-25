package com.sxt.chat.view;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.sxt.chat.R;
import com.sxt.chat.utils.Prefs;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by sxt on 2018/12/9.
 */
public class ShadowsHelper {

    private static Activity acty;
    private FrameLayout decorView;
    private FrameLayout shadowContainer;
    private OnShadowEventListener onShadowEventListener;
    private static ShadowsHelper shadowsHelper = new ShadowsHelper();
    private boolean finished = true;
    private static final String Location = "Location";

    public static ShadowsHelper getInstance(Activity activity) {
        acty = activity;
        return shadowsHelper;
    }

    public synchronized void showShadow(final View targetView, final View shadow, final View shadowChild, FrameLayout.LayoutParams layoutParams) {
        if (targetView == null) {
            throw new IllegalArgumentException("targetView view must be not null");
        }
        if (shadow == null) {
            throw new IllegalArgumentException("shadow view must be not null");
        }
        finished = false;//只要是显示引导页 , 就将标记置为false
        if (decorView == null || shadowContainer == null) {
            decorView = (FrameLayout) acty.getWindow().getDecorView();
            shadowContainer = new FrameLayout(acty);
            shadowContainer.setBackgroundColor(ContextCompat.getColor(acty, R.color.alpha));
            shadowContainer.setClickable(true);
            shadowContainer.setFocusable(true);
            decorView.addView(shadowContainer, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            shadowContainer.getParent().requestDisallowInterceptTouchEvent(true);
        }
        if (layoutParams == null) {
            layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        }
        final FrameLayout.LayoutParams finalLayoutParams = layoutParams;
        targetView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                targetView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                Log.e(Location, "targetView ... onGlobalLayout");
                int[] locations = new int[2];
                targetView.getLocationOnScreen(locations);
                shadow.setX(locations[0]);
                shadow.setY(locations[1]);
                Log.e(Location, Arrays.toString(locations));

                shadowContainer.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int l, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        shadowContainer.removeOnLayoutChangeListener(this);
                        Log.e(Location, "shadowContainer ... onLayoutChange");
                        if (shadowChild != null) {
                            int[] shadowLocations = new int[2];
                            int[] childLocations = new int[2];
                            shadow.getLocationOnScreen(shadowLocations);
                            shadowChild.getLocationOnScreen(childLocations);
                            int left = -(childLocations[0] - shadowLocations[0]);//找到目标view的横坐标
                            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) shadow.getLayoutParams();
                            lp.leftMargin = left - finalLayoutParams.rightMargin;
                            shadow.setLayoutParams(lp);

                            ViewGroup.LayoutParams childLayoutParams = shadowChild.getLayoutParams();
                            childLayoutParams.width = targetView.getMeasuredWidth() + finalLayoutParams.rightMargin;

                            shadowChild.setLayoutParams(childLayoutParams);

                            shadow.setVisibility(View.VISIBLE);//设置好参数以后才将View显示,避免抖动
                            Log.e(Location, "shadow : " + Arrays.toString(shadowLocations) + " ,,, child  : " + Arrays.toString(childLocations));
                        }
                    }
                });

                shadow.setVisibility(View.INVISIBLE);//先占个位置,等确定了坐标以后再显示出来
                shadowContainer.addView(shadow, finalLayoutParams);
                Log.e(Location, "shadowContainer ... addView");
                shadowContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shadowContainer.removeView(shadow);
                        if (onShadowEventListener != null) {
                            onShadowEventListener.onShadowRemoved(shadow);
                        }
                    }
                });
            }
        });
        targetView.requestLayout();
    }

    public boolean isShaowFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
        if (finished) {
            if (shadowContainer != null) shadowContainer.removeAllViews();
            if (decorView != null) decorView.removeView(shadowContainer);
        }
    }

    public ShadowsHelper setOnShadowEventListener(OnShadowEventListener onShadowEventListener) {
        this.onShadowEventListener = onShadowEventListener;
        return this;
    }

    public interface OnShadowEventListener {

        void onShadowRemoved(View removedShow);
    }


    /**
     * 引导页规则:
     * 只完全加载一遍,如果中途强制退出,下次继续显示引导页
     */
//    private void showShadow() {
//        if (menuRecyclerView == null || menuRecyclerView.getChildCount() == 0) return;
//        if (Prefs.getInstance(context).getBoolean(Prefs.KEY_IS_FIRST_ENTER_START_PAGE, true)) {
//            if (getUserVisibleHint() && currentSenior != null && (shadows == null || shadows.size() == 0)) {
//
//                View shadow1 = View.inflate(context, R.layout.item_shadow_1, null);
//                View shadow2 = View.inflate(context, R.layout.item_shadow_2, null);
//                View shadow3 = View.inflate(context, R.layout.item_shadow_3, null);
//
//                View targetView1 = menuRecyclerView.getChildAt(0).findViewById(R.id.look_detail);
//                View currentView = bannerView.getCurrentView();
//                View targetView2 = currentView.findViewById(R.id.healthImg);
//                View targetView3 = currentView.findViewById(R.id.time_line_bottom);
//
//                shadows = new ArrayList<>();
//                targetViews = new ArrayList<>();
//
//                shadows.add(shadow1);
//                shadows.add(shadow2);
//                shadows.add(shadow3);
//                targetViews.add(targetView1);
//                targetViews.add(targetView2);
//                targetViews.add(targetView3);
//
//                index = 0;
//                shadow = shadows.get(index);
//                targetView = targetViews.get(index);
//                final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
//                layoutParams.leftMargin = -dp * 10;
//                layoutParams.rightMargin = dp * 10;
//                layoutParams.topMargin = -dp * 3;
//
//                ShadowsHelper.getInstance((Activity) context)
//                        .setOnShadowEventListener(new ShadowsHelper.OnShadowEventListener() {
//                            @Override
//                            public void onShadowRemoved(View removedShow) {
//                                ShadowsHelper.getInstance((Activity) context).setFinished(index == shadows.size() - 1);
//                                if (index == shadows.size() - 1) {
//                                    Prefs.getInstance(context).putBoolean(Prefs.KEY_IS_FIRST_ENTER_START_PAGE, false);
//                                }
//                                Log.e("sxt", "index : " + index + " flag : " + Prefs.getInstance(context).getBoolean(Prefs.KEY_IS_FIRST_ENTER_START_PAGE, true));
//                                if (index < shadows.size() - 1) {
//                                    index++;
//                                    targetView = targetViews.get(index);
//                                    shadow = shadows.get(index);
//
//                                    ShadowsHelper.getInstance((Activity) context).showShadow(targetView, shadow, layoutParams);
//                                }
//                            }
//                        })
//                        .showShadow(targetView, shadow, layoutParams);
//            }
//        }
//    }
}
