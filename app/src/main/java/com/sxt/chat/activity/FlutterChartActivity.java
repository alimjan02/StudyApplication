package com.sxt.chat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.sxt.chat.base.FlutterChannelActivity;
import com.sxt.chat.base.FlutterRoutes;
import com.sxt.chat.utils.SystemUiStyle;

import io.flutter.facade.Flutter;
import io.flutter.view.FlutterView;

public class FlutterChartActivity extends FlutterChannelActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemUiStyle.fitSystemWindow(this);
        FlutterView flutterView = Flutter.createView(this, getLifecycle(), FlutterRoutes.Route_Chart);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addContentView(flutterView, layoutParams);
    }
}
