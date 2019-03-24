package com.sxt.chat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.FrameLayout;

import com.sxt.chat.App;
import com.sxt.chat.R;
import com.sxt.chat.base.HeaderActivity;
import com.sxt.chat.utils.FlutterRoutes;
import com.sxt.chat.utils.Prefs;

import java.util.HashMap;
import java.util.Map;

import io.flutter.facade.Flutter;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.view.FlutterView;

/**
 * Created by sxt on 2019/3/12.
 */
public class FlutterDemoActivity extends HeaderActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showToolbar(false);
        FrameLayout frameLayout = new FrameLayout(this);
        FlutterView flutterView = Flutter.createView(this, getLifecycle(), FlutterRoutes.ROUTE_ORDER_HISTORY);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        frameLayout.addView(flutterView, layoutParams);
        addContentView(frameLayout, layoutParams);

        //Andrroid to Flutter
        new EventChannel(flutterView, FlutterRoutes.ORDER_TOFLUTTER)
                .setStreamHandler(new EventChannel.StreamHandler() {
                    @Override
                    public void onListen(Object o, EventChannel.EventSink eventSink) {
                        Map<String, Object> params = new HashMap<>();
                        Prefs instance = Prefs.getInstance(App.getCtx());
                        String userName = instance.getUserName();
                        String ticket = instance.getTicket();
                        params.put("userName", userName);
                        params.put("ticket", ticket);
                        eventSink.success(params);
                        Log.e("sxt", "发送数据 : " + params.toString());
                    }

                    @Override
                    public void onCancel(Object o) {
                        Log.e("sxt", "失败了 " + o.toString());
                    }
                });

        //Flutter to Andrroid
        new MethodChannel(flutterView, FlutterRoutes.NAVIGATOR_BACK)
                .setMethodCallHandler((methodCall, result) -> {
                    //接收来自flutter的指令oneAct
                    if (methodCall.method.equals("finish")) {
                        finish();
                        //返回给flutter的参数
                        result.success("success");
                    } else {
                        result.notImplemented();
                    }
                });
    }
}
