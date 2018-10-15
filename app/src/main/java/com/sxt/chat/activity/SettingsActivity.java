package com.sxt.chat.activity;


import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sxt.chat.App;
import com.sxt.chat.R;
import com.sxt.chat.base.HeaderActivity;
import com.sxt.chat.dialog.AlertDialogBuilder;
import com.sxt.chat.download.DownloadTask;
import com.sxt.chat.receiver.WatchDogReceiver;
import com.sxt.chat.utils.NetworkUtils;
import com.sxt.chat.utils.Prefs;
import com.sxt.chat.utils.glide.CacheUtils;

/**
 * Created by izhaohu on 2018/3/13.
 */

public class SettingsActivity extends HeaderActivity implements View.OnClickListener {
    private TextView cacheSize;
    private TextView version;
    private DownloadTask downloadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(getString(R.string.nomal_settings));

        cacheSize = (TextView) findViewById(R.id.cache_size);
        version = (TextView) findViewById(R.id.version);
        findViewById(R.id.clean_cache).setOnClickListener(this);//清除缓存
        findViewById(R.id.current_version).setOnClickListener(this);//清除缓存
        findViewById(R.id.login_out).setOnClickListener(this);//退出登录
        cacheSize.setText(CacheUtils.getInstance().getCacheSize());
        version.setText("当前版本:" + Prefs.getVersionName(App.getCtx()) + "");
//        spinner.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//
//            @Override
//            public void onGlobalLayout() {
//                TextView textView = (TextView) spinner.getChildAt(0);
//                if (textView != null) {
//                    textView.setTextColor(ContextCompat.getColor(App.getCtx(), R.color.main_blue_press));
//                }
//            }
//        });
//        final AppCompatSpinner spinner = findViewById(R.id.spinner);
//        spinner.setAdapter(new SpinnerAdapter(Arrays.asList(getResources().getStringArray(R.array.spinner_list)), this));
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Log.i("position", String.valueOf(position));
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clean_cache://清除缓存
                clearCache();
                break;

            case R.id.current_version://检查更新
                checkUpdate((int) System.currentTimeMillis());
                break;
            case R.id.login_out://退出登录
                Intent intent = new Intent();
                intent.setAction(WatchDogReceiver.ACTION_LOGOUT);
                //android 8.0以后, 发送广播许需要添加包名和具体的接收广播类名
                intent.setComponent(new ComponentName(getApplication().getPackageName(), App.getCtx().getPackageName()+".receiver.WatchDogReceiver"));
                sendBroadcast(intent);
                break;
        }
    }

    private void clearCache() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(R.string.message_alert);
        builder.setMessage(R.string.clear_cache_confirm);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                CacheUtils.getInstance().clearCache();
                cacheSize.setText(CacheUtils.getInstance().getCacheSize());
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (downloadTask != null && !downloadTask.isCancelled()) {
            downloadTask.cancel(true);
        }
    }

    //----------------------------------------版本更新-----------------------------------------------
    private void checkUpdate(int serverVersion) {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            if (serverVersion > packageInfo.versionCode) {
                //判断WIFI情况
                if (NetworkUtils.isNetworkAvailable(this)) {//判断网络是否可用
                    if (NetworkUtils.isWifiEnabled(this)) {//判断WIFI是否打开
                        if (NetworkUtils.isWifi(this)) {//判断是wifi还是3g网络
                            startDownloadApkDialog();
                        } else {
                            if (NetworkUtils.is3rd(this)) {
                                showWifiAlert();
                            }
                        }
                    } else {
                        if (NetworkUtils.is3rd(this)) {//判断是否是3G网络
                            showWifiAlert();
                        }
                    }
                }
            } else {
                Toast("当前已是最新版本");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void startDownloadApkDialog() {
        final Dialog dialog = new Dialog(this, R.style.CustomDialog);
        View item = LayoutInflater.from(this).inflate(R.layout.item_update, null);
        final TextView progressTitle = (TextView) item.findViewById(R.id.upgrade_title);
        final ProgressBar progressBar = (ProgressBar) item.findViewById(R.id.my_progress);
        final TextView tvProgress = (TextView) item.findViewById(R.id.tv_progres);
        dialog.setContentView(item);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            WindowManager wm = this.getWindowManager();
            Display display = wm.getDefaultDisplay();
            layoutParams.width = (int) (display.getWidth() * 0.875);
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
        }
        download(dialog, progressTitle, progressBar, tvProgress);
        item.findViewById(R.id.btn_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                download(dialog, progressTitle, progressBar, tvProgress);
            }
        });
        item.findViewById(R.id.btn_download_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadTask.cancel(true);
                dialog.dismiss();
            }
        });
    }

    private void showWifiAlert() {
        new AlertDialogBuilder(this).setTitle(getString(R.string.prompt)).setMessage("您当前处于非WIFI状态，继续更新版本吗?").setLeftButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startDownloadApkDialog();
            }
        }).setRightButton(R.string.cancel, null).show();
    }

    private void download(final Dialog dialog, final TextView progressTitle, final ProgressBar progressBar, final TextView tvProgress) {
        if (downloadTask != null && !downloadTask.isCancelled()) {
            downloadTask.cancel(true);
        }
        downloadTask = new DownloadTask(this);
        downloadTask.setDownloadListener(new DownloadTask.DownloadListener() {
            @Override
            public void onError(Exception e) {
                dialog.dismiss();
                Toast("下载文件失败，请重新登录进行升级");
            }

            @Override
            public void onProgressUpdate(int progress, long max) {
                if (dialog != null) {
                    double size = max / 1024.0 / 1024.0;
                    String result = String.format("更新版本:(共%.2fMB)", size);
                    progressTitle.setText(result);
                    progressBar.setProgress(progress);
                    tvProgress.setText(progress + "%");
                }
            }

            @Override
            public void onSuccessful() {
                dialog.dismiss();
            }

            @Override
            public void onFinish() {
                dialog.dismiss();
            }
        });
        downloadTask.execute(Prefs.getInstance(this).getServerUrl() + Prefs.getInstance(this).KEY_APP_UPDATE_URL);
    }

}
