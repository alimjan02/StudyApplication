package com.sxt.chat.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.sxt.chat.base.FlutterChannelActivity;
import com.sxt.chat.base.FlutterRoutes;

import io.flutter.facade.Flutter;
import io.flutter.view.FlutterView;

public class FlutterChartActivity extends FlutterChannelActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //将内容提升至状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        FrameLayout frameLayout = new FrameLayout(this);
        FlutterView flutterView = Flutter.createView(this, getLifecycle(), FlutterRoutes.ROUTE_ORDER_HISTORY);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        frameLayout.addView(flutterView, layoutParams);
        addContentView(frameLayout, layoutParams);
    }
}
