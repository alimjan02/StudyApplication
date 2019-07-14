package com.sxt.chat.base;

/**
 * Created by sxt on 2019/3/19.
 * Flutter路由名称
 */
public class FlutterRoutes {
    //路由
    public static final String ROUTE_ORDER_HISTORY = "ROUTE_ORDER_HISTORY"; //工单历史
    public static final String NAVIGATOR_BACK = "NAVIGATOR_BACK"; //返回
    public static final String LOGIN_OUT = "LOGIN_OUT"; //返回
    public static final String LOCAL_SAVE = "LOCAL_SAVE"; //本地保存
    public static final String LOCAL_GET = "LOCAL_GET"; //本地获取
    public static final String LOCAL_REMOVE = "LOCAL_REMOVE"; //本地获取

    //工单历史通道
    public static final String ORDER_TOANDROID = ROUTE_ORDER_HISTORY + "_METHOD";
    public static final String ORDER_TOFLUTTER = ROUTE_ORDER_HISTORY + "_EVENT";


}
