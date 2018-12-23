package com.sxt.chat.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.sxt.chat.R;
import com.sxt.chat.base.HeaderActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sxt on 2018/12/21.
 */
public class PdfActivity extends HeaderActivity {

    private List<Bitmap> pdfBitmaps = new ArrayList<>();
    //    private CustomRecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        setTitle("PDF解析");
        progressBar = findViewById(R.id.progressBar);
//        recyclerView = findViewById(R.id.recyclerView);
        WebView webView = findViewById(R.id.webView);
//        recyclerView.setAdapter(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false), new PdfAdapter(this, pdfBitmaps));

        initWebView(webView);
    }

    private void parsePdf(WebView webView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                String path = getExternalCacheDir() + File.separator + "hard.pdf";
                ParcelFileDescriptor input = ParcelFileDescriptor.open(new File(path), ParcelFileDescriptor.MODE_READ_WRITE);
                PdfRenderer renderer = new PdfRenderer(input);
                final int pageCount = renderer.getPageCount();
                Bitmap mBitmap;
                for (int i = 0; i < pageCount; i++) {
                    PdfRenderer.Page page = renderer.openPage(i);
                    mBitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                    //将当前页的内容渲染到bitmap中
                    page.render(mBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                    //存储当前的bitmap
                    pdfBitmaps.add(mBitmap);
                    //释放当前页
                    page.close();
                }
                //释放渲染器
                renderer.close();
                input.close();
                for (Bitmap bitmap : pdfBitmaps) {
                    webView.loadUrl("javascript:display('data:image/png;base64," + bitmaptoString(bitmap) + "')");
                }
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String bitmaptoString(Bitmap bitmap) {
        // 将Bitmap转换成Base64字符串
        StringBuilder string = new StringBuilder();
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();

        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
            bStream.flush();
            bStream.close();
            byte[] bytes = bStream.toByteArray();
            string.append(Base64.encodeToString(bytes, Base64.NO_WRAP));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return string.toString();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView(final WebView webView) {
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
        String path = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        webView.getSettings().setGeolocationEnabled(true);//启动地理定位,
        webView.getSettings().setGeolocationDatabasePath(path);//设置定位的数据库路径
        webView.getSettings().setDomStorageEnabled(true);//DOM Storage
        webView.getSettings().setAllowFileAccess(true);
        webView.setFocusable(true);
        webView.requestFocus();
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

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new android.webkit.WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                parsePdf(webView);
                Log.i("webView", "onPageFinished");
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }
        });
        webView.loadUrl("file:///android_asset/html/pdf.html");
    }
}
