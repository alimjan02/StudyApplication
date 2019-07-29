package com.sxt.chat.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.signature.StringSignature;
import com.steelkiwi.cropiwa.util.UriUtil;
import com.sxt.chat.R;
import com.sxt.chat.base.HeaderActivity;
import com.sxt.chat.dialog.AlertDialogBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sxt on 2018/12/21.
 * 演示Android PdfRenderer 解析Pdf文档
 * 但是最低支持SDK 21
 */
public class PdfActivity extends HeaderActivity {

    private ProgressBar progressBar;
    private WebView webView;
    private ParseTask parseTask;
    private final int REQUEST_CODE_SELECT_FILE = 10086;
    private final int REQUEST_CODE_PERMISSION_STORAGE = 10087;
    private String pdfFilePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        setTitle("PDF解析");
        progressBar = findViewById(R.id.progressBar);
        webView = findViewById(R.id.webView);
        initWebView();
        setRightContainer(null);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        // 设置WebView属性
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        webView.getSettings().setJavaScriptEnabled(true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        webView.getSettings().setSupportZoom(true);//是否可以缩放，默认true
        webView.getSettings().setBuiltInZoomControls(true);//是否开启缩放功能，默认false
        webView.getSettings().setDisplayZoomControls(false);//设置是否显示缩放按钮
        webView.getSettings().setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
        webView.getSettings().setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
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
                if (parseTask != null) {
                    parseTask.cancel(true);
                }
                parseTask = new ParseTask();
                parseTask.execute(pdfFilePath);
                Log.i(TAG, "onPageFinished");
            }
        });
    }

    @Override
    public void setRightContainer(View rightContainer) {
        TextView textView = new TextView(this);
        textView.setText(R.string.select_file);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        textView.setTextColor(ContextCompat.getColor(this, R.color.white));
        textView.setOnClickListener(v -> checkStoragePermission());
        super.setRightContainer(textView);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) textView.getLayoutParams();
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        lp.setMargins(margin, margin / 2, margin, margin / 2);
        textView.setLayoutParams(lp);
    }

    private void checkStoragePermission() {
        boolean flag = checkPermission(REQUEST_CODE_PERMISSION_STORAGE, Manifest.permission_group.STORAGE, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
        if (flag) {
            openStorage();
        }
    }

    private void openStorage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //intent.setType(“image/*”);//选择图片
        //intent.setType(“audio/*”); //选择音频
        //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
        //intent.setType(“video/*;image/*”);//同时选择视频和图片
        intent.setType("*/*");//无类型限制
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
    }

    @Override
    public void onPermissionsAllowed(int requestCode, String[] permissions, int[] grantResults) {
        super.onPermissionsAllowed(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION_STORAGE) {
            openStorage();
        }
    }

    @Override
    public void onPermissionsRefusedNever(int requestCode, String[] permissions, int[] grantResults) {
        super.onPermissionsRefusedNever(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION_STORAGE) {
            onPermissionRefuseNever(R.string.permission_request_READ_EXTERNAL_STORAGE);
        }
    }

    private void onPermissionRefuseNever(int stringRes) {
        String appName = getString(R.string.app_name);
        String message = String.format(getString(stringRes), appName);
        SpannableString span = new SpannableString(message);
        span.setSpan(new TextAppearanceSpan(this, R.style.text_color_2_15_style), 0, message.indexOf(appName), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        int start = message.indexOf(appName) + appName.length();
        span.setSpan(new TextAppearanceSpan(this, R.style.text_color_1_17_bold_style), message.indexOf(appName), start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new TextAppearanceSpan(this, R.style.text_color_2_15_style), start, message.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        showPermissionRefusedNeverDialog(span);
    }

    /**
     * 权限被彻底禁止后 , 弹框提醒用户去开启
     */
    private void showPermissionRefusedNeverDialog(CharSequence message) {
        new AlertDialogBuilder(this)
                .setTitle(R.string.message_alert, true)
                .setMessage(message)
                .setLeftButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setRightButton(R.string.confirm, (dialog, which) -> {
                    dialog.dismiss();
                    goToAppSettingsPage();
                })
                .setShowLine(true)
                .setCanceledOnTouchOutside(false)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_SELECT_FILE && data != null) {
                String path = UriUtil.uri2Path(this, data.getData());
                if (path != null && path.endsWith(".pdf")) {
                    pdfFilePath = path;
                    reload();
                } else {
                    Toast("您选择的文件格式有误,请重新选择");
                }
                try {
                    Log.e(TAG, String.format("%s", path));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void reload() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if (webView != null) {
            webView.reload();
        }
    }

    class ParseTask extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... strings) {
            List<String> pdfBitmapBytes = new ArrayList<>();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    if (strings == null || strings.length == 0) {
                        throw new IllegalArgumentException("文件路径不能为空...");
                    }
                    ParcelFileDescriptor input = ParcelFileDescriptor.open(new File(strings[0]), ParcelFileDescriptor.MODE_READ_WRITE);
                    PdfRenderer renderer = new PdfRenderer(input);
                    final int pageCount = renderer.getPageCount();
                    Bitmap mBitmap;
                    for (int i = 0; i < pageCount; i++) {
                        PdfRenderer.Page page = renderer.openPage(i);
                        mBitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                        //将当前页的内容渲染到bitmap中
                        page.render(mBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                        //存储当前的bitmap
                        pdfBitmapBytes.add(bitmaptoString(mBitmap));
                        //释放当前页
                        page.close();
                    }
                    //释放渲染器
                    renderer.close();
                    input.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return pdfBitmapBytes;
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            if (strings != null && strings.size() > 0) {
                for (String bitmapStrs : strings) {
                    //在页面上添加标签,传递图片参数
                    webView.loadUrl("javascript:display('data:image/png;base64," + bitmapStrs + "')");
                }
            }
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
        }

        private String bitmaptoString(Bitmap bitmap) {
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (parseTask != null) {
            parseTask.cancel(true);
        }
    }
}
