package com.sxt.chat.activity

import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import com.sxt.chat.base.FlutterChannelActivity
import com.sxt.chat.base.FlutterRoutes
import io.flutter.facade.Flutter

class FlutterChartActivity : FlutterChannelActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //将内容提升至状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            setWindowStatusBarColor(this, android.R.color.transparent)
        }
        val flutterView = Flutter.createView(this, lifecycle, FlutterRoutes.Route_Chart)
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        addContentView(flutterView, layoutParams)
    }
}
