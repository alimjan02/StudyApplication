package com.sxt.chat.activity;

import android.os.Bundle;

import com.sxt.chat.base.HeaderActivity;

import io.flutter.facade.Flutter;
import io.flutter.view.FlutterView;

/**
 * Created by sxt on 2019/3/12.
 */
public class FlutterDemoActivity extends HeaderActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlutterView route1 = Flutter.createView(this, getLifecycle(), "route1");
        setContentView(route1);
    }
}
