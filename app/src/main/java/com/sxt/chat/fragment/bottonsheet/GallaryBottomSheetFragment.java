package com.sxt.chat.fragment.bottonsheet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.sxt.chat.R;
import com.sxt.chat.base.BaseBottomSheetFragment;
import com.sxt.chat.json.Banner;
import com.sxt.chat.utils.Prefs;

/**
 * Created by sxt on 2018/10/22.
 */
public class GallaryBottomSheetFragment extends BaseBottomSheetFragment {

    private ProgressBar progressBar;

    @Override
    protected View getDisplayView() {
        return LayoutInflater.from(context).inflate(R.layout.item_gallary_bottom_sheet, null, false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setData();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void setData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            Banner banner = (Banner) bundle.getSerializable(Prefs.KEY_BANNER_INFO);
            if (banner != null) {
                Log.i(TAG, "banner : " + banner.getUrl());
                Glide.with(context).load(banner.getUrl())//.transform(new CenterCrop(context), new GlideRoundTransformer(context, 8))
                        .placeholder(R.mipmap.ic_placeholder)
                        .error(R.mipmap.ic_banner_placeholder)
                        .into((ImageView) contentView.findViewById(R.id.img));

                progressBar = contentView.findViewById(R.id.progressBar);
                WebView webView = contentView.findViewById(R.id.webView);
                initWebView(webView);
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView(WebView webView) {
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
        String path = context.getDir("database", Context.MODE_PRIVATE).getPath();
        webView.getSettings().setGeolocationEnabled(true);//启动地理定位,
        webView.getSettings().setGeolocationDatabasePath(path);//设置定位的数据库路径
        webView.getSettings().setDomStorageEnabled(true);//DOM Storage
        webView.getSettings().setAllowFileAccess(true);
        webView.setFocusable(true);
        //TODO  android 5.0以上默认不支持Mixed Content
        /**
         * 在Android5.0中，WebView方面做了些修改，如果你的系统target api为21以上:
         * 系统默认禁止了mixed content和第三方cookie。可以使用setMixedContentMode() 和 setAcceptThirdPartyCookies()以分别启用。
         系统现在可以智能选择HTML文档的portion来绘制。这种新特性可以减少内存footprint并改进性能。若要一次性渲染整个HTML文档，可以调用这个方法enableSlowWholeDocumentDraw()
         如果你的app的target api低于21:系统允许mixed content和第三方cookie，并且总是一次性渲染整个HTML文档。
         在使用WebView的类中添加如下代码：

         在安卓5.0之后，默认不允许加载http与https混合内容，需要设置webview允许其加载混合网络协议内容
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(
                    WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.setWebChromeClient(new WebViewClient());
        webView.setWebViewClient(new android.webkit.WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(final WebView view, final String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                    super.onReceivedSslError(view, handler, error);
                // handler.cancel();// Android默认的处理方式
                handler.proceed();// 接受所有网站的证书
                // handleMessage(Message msg);// 进行其他处理
            }
        });

        webView.loadUrl("https://blog.csdn.net/sxt_zls");
    }

    private class WebViewClient extends WebChromeClient {
        @Override
        public void onReceivedTitle(WebView view, String urlTitle) {
            super.onReceivedTitle(view, urlTitle);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {

            if (newProgress == 100) {
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }
    }
}
