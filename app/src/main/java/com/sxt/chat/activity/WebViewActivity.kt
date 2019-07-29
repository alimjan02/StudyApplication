package com.sxt.chat.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebViewClient

import com.sxt.chat.R
import com.sxt.chat.base.BaseActivity
import com.sxt.chat.view.WebView

class WebViewActivity : BaseActivity() {

    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var webView: WebView? = null
    private var url: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        webView = findViewById(R.id.webView)
        url = intent.getStringExtra(ACTION_URL)
        initRefreshLayout()
    }

    private fun initRefreshLayout() {
        //初始化刷新控件
        swipeRefreshLayout!!.setColorSchemeColors(ContextCompat.getColor(this, R.color.main_blue), ContextCompat.getColor(this, R.color.red_1), ContextCompat.getColor(this, R.color.line_yellow), ContextCompat.getColor(this, R.color.main_green), ContextCompat.getColor(this, R.color.red_1))
        swipeRefreshLayout!!.setOnRefreshListener { this.refresh() }
        swipeRefreshLayout!!.post {
            swipeRefreshLayout!!.isRefreshing = true
            initWebView()
        }
    }

    private fun refresh() {
        webView!!.reload()
        Log.e("webView", "refresh")
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        webView!!.setOnScrollChangedListener { _, _, _, _ -> swipeRefreshLayout!!.isEnabled = webView!!.scrollY == 0 }
        // 设置WebView属性
        webView!!.settings.javaScriptCanOpenWindowsAutomatically = true//设置js可以直接打开窗口，如window.open()，默认为false
        webView!!.settings.javaScriptEnabled = true//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        webView!!.settings.setSupportZoom(true)//是否可以缩放，默认true
        webView!!.settings.builtInZoomControls = true//是否开启缩放功能，默认false
        webView!!.settings.displayZoomControls = false//设置是否显示缩放按钮
        webView!!.settings.useWideViewPort = false//设置此属性，可任意比例缩放。大视图模式
        webView!!.settings.loadWithOverviewMode = true//和setUseWideViewPort(true)一起解决网页自适应问题
        webView!!.webChromeClient = WebChromeClient()
        webView!!.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: android.webkit.WebView, url: String): Boolean {
                view.loadUrl(url)
                if (!swipeRefreshLayout!!.isRefreshing) {
                    swipeRefreshLayout!!.isRefreshing = true
                }
                return super.shouldOverrideUrlLoading(view, url)
            }

            override fun onPageFinished(view: android.webkit.WebView, url: String) {
                super.onPageFinished(view, url)
                if (swipeRefreshLayout!!.isRefreshing) {
                    swipeRefreshLayout!!.isRefreshing = false
                }
                Log.e("webView", "onPageFinished")
            }
        }
        webView!!.loadUrl(url)
    }

    override fun onBackPressed() {
        if (webView!!.canGoBack()) {
            webView!!.goBack()
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        val ACTION_URL = "ACTION_URL"
    }
}
