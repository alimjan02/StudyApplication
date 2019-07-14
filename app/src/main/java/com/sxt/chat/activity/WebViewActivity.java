package com.sxt.chat.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;

import com.sxt.chat.R;
import com.sxt.chat.base.BaseActivity;
import com.sxt.chat.view.WebView;

public class WebViewActivity extends BaseActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private WebView webView;
    public static final String ACTION_URL = "ACTION_URL";
    private String url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        webView = findViewById(R.id.webView);
        url = getIntent().getStringExtra(ACTION_URL);
        initRefreshLayout();
    }

    private void initRefreshLayout() {
        //初始化刷新控件
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.main_blue), ContextCompat.getColor(this, R.color.red_1), ContextCompat.getColor(this, R.color.line_yellow), ContextCompat.getColor(this, R.color.main_green), ContextCompat.getColor(this, R.color.red_1));
        swipeRefreshLayout.setOnRefreshListener(this::refresh);
        swipeRefreshLayout.post(() -> {
            swipeRefreshLayout.setRefreshing(true);
            initWebView();
        });
    }

    private void refresh() {
        webView.reload();
        Log.e("webView", "refresh");
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        webView.setOnScrollChangedListener((scrollX, scrollY, oldScrollX, oldScrollY) -> swipeRefreshLayout.setEnabled(webView.getScrollY() == 0));
        // 设置WebView属性
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        webView.getSettings().setJavaScriptEnabled(true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        webView.getSettings().setSupportZoom(true);//是否可以缩放，默认true
        webView.getSettings().setBuiltInZoomControls(true);//是否开启缩放功能，默认false
        webView.getSettings().setDisplayZoomControls(false);//设置是否显示缩放按钮
        webView.getSettings().setUseWideViewPort(false);//设置此属性，可任意比例缩放。大视图模式
        webView.getSettings().setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String url) {
                view.loadUrl(url);
                if (!swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(true);
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(android.webkit.WebView view, String url) {
                super.onPageFinished(view, url);
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                Log.e("webView", "onPageFinished");
            }
        });
        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
