package com.sxt.chat;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.sxt.chat.activity.LoginActivity;
import com.sxt.chat.utils.ActivityCollector;
import com.sxt.chat.utils.ActivityManager;
import com.tencent.bugly.crashreport.CrashReport;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;

/**
 * Created by Administrator on 2017/3/20.
 */
public class App extends MultiDexApplication {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        CrashReport.initCrashReport(this, "1419862dc6", true);
        initBmob();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                //退出登录逻辑 :
                // 1 .当用户触发退出登录 , 则先打开LoginActivity ,
                // 2 .然后在LoginActivity创建完成以后 , 再结束掉栈里面的其他Activity
                if (activity instanceof LoginActivity) {
                    ActivityCollector.finishAllExceptLogin();
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                ActivityManager.getInstance(App.getCtx()).setCurrentActivity(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    private void initBmob() {
        //提供以下两种方式进行初始化操作：

        //第一：默认初始化
//        Bmob.initialize(this, "d83d882102197404bb1fda4740c7a1aa");
        // 注:自v3.5.2开始，数据sdk内部缝合了统计sdk，开发者无需额外集成，传渠道参数即可，不传默认没开启数据统计功能
        //Bmob.initialize(this, "Your Application ID","bmob");

        //第二：自v3.4.7版本开始,设置BmobConfig,允许设置请求超时时间、文件分片上传时每片的大小、文件的过期时间(单位为秒)，
        BmobConfig config = new BmobConfig.Builder(this)
                //设置appkey
                .setApplicationId("d83d882102197404bb1fda4740c7a1aa")
                //请求超时时间（单位为秒）：默认15s
                .setConnectTimeout(15)
                //文件分片上传时每片的大小（单位字节），默认512*1024
                .setUploadBlockSize(1024 * 1024)
                //文件的过期时间(单位为秒)：默认1800s
                .setFileExpiration(2500)
                .build();
        Bmob.initialize(config);
//        BmobUpdateAgent.initAppVersion();
    }

    public static Context getCtx() {
        return mContext;
    }
}
