package com.sxt.chat.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.http.SslError;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sxt.chat.App;
import com.sxt.chat.R;
import com.sxt.chat.base.BasePagerAdapter;
import com.sxt.chat.base.LazyFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 11837 on 2018/4/22.
 */

public class GitHubFragment extends LazyFragment {

    private final String GIT_HUB_URL = "https://github.com/good-good-study";
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected int getDisplayView(LayoutInflater inflater, ViewGroup container) {
        return R.layout.fragment_github;
    }

    @Override
    protected void initView() {
        swipeRefreshLayout = contentView.findViewById(R.id.swipeRefreshLayout);
        final ViewPager viewPager = contentView.findViewById(R.id.viewPager);
        final WebView webView = contentView.findViewById(R.id.webView);
        initWebView(webView);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorAccent, R.color.main_blue, R.color.main_green);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);//第一次来 并不会调用onRefresh方法  android bug
                webView.loadUrl(GIT_HUB_URL);
            }
        });
        //解决SwipeRefreshLayout 嵌套滑动冲突
        AppBarLayout appBarLayout = (AppBarLayout) contentView.findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (verticalOffset >= 0) {
                    swipeRefreshLayout.setEnabled(true);
                } else {
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });
        viewPager.setOffscreenPageLimit(3);
        viewPager.setPageMargin((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));
        setViewPagerData(viewPager);
    }

    private void setViewPagerData(ViewPager viewPager) {
        final List<String> imgs = new ArrayList<>();
        imgs.add("http://bmob-cdn-18541.b0.upaiyun.com/2018/05/22/dd5ca0a0400a87b7800ae9a6f107b562.jpg");
        imgs.add("http://bmob-cdn-18541.b0.upaiyun.com/2018/05/22/13cecf96407145708071d88037547c7f.jpg");
        imgs.add("http://bmob-cdn-18541.b0.upaiyun.com/2018/05/22/20799e5a4012706c80f83276a47b7f89.jpg");
        imgs.add("http://bmob-cdn-18541.b0.upaiyun.com/2018/05/21/77a27d12401d6964807090cafca10f5e.jpg");
        imgs.add("http://bmob-cdn-18541.b0.upaiyun.com/2018/05/21/51e795bc405863d5805af06327c0f208.png");
        viewPager.setAdapter(new BasePagerAdapter<String>(activity, imgs) {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView img = new ImageView(activity);
                Glide.with(activity).load(imgs.get(position))
                        .placeholder(R.mipmap.ic_banner_placeholder)
                        .error(R.mipmap.ic_banner_placeholder)
                        .into(img);
                container.addView(img, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                return img;
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView(WebView webView) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            webView.setNestedScrollingEnabled(false);
//        }
        // 设置WebView属性
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        webView.getSettings().setJavaScriptEnabled(true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        webView.getSettings().setSupportZoom(true);//是否可以缩放，默认true
        webView.getSettings().setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
        webView.getSettings().setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
        webView.getSettings().setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
        webView.getSettings().setAppCacheEnabled(true);//是否使用缓存
        //用于显示地图 需要启用数据库
        webView.getSettings().setDatabaseEnabled(true);
        String path = App.getCtx().getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        webView.getSettings().setGeolocationEnabled(true);//启动地理定位,
        webView.getSettings().setGeolocationDatabasePath(path);//设置定位的数据库路径
        webView.getSettings().setDomStorageEnabled(true);//DOM Storage
        webView.getSettings().setAllowFileAccess(true);
        webView.setFocusable(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                    super.onReceivedSslError(view, handler, error);
                // handler.cancel();// Android默认的处理方式
                handler.proceed();// 接受所有网站的证书
                // handleMessage(Message msg);// 进行其他处理
            }
        });
    }
}
