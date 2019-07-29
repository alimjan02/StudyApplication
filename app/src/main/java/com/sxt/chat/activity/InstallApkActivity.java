package com.sxt.chat.activity;


import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sxt.chat.R;
import com.sxt.chat.base.HeaderActivity;
import com.sxt.chat.dialog.AlertDialogBuilder;
import com.sxt.chat.download.DownloadTask;
import com.sxt.chat.utils.Prefs;
import com.sxt.chat.utils.Utils;

/**
 * Created by xt.sun on 2018/3/13.
 */

public class InstallApkActivity extends HeaderActivity {
    private DownloadTask downloadTask;
    private boolean isCancelInstall;
    private AlertDialog alertDialog;
    private final int REQUEST_CODE_INSTALL_APK = 1000;
    private final int REQUEST_MANAGE_UNKNOWN_APP_SOURCES = 1001;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (downloadTask != null && !downloadTask.isCancelled()) {
            downloadTask.cancel(true);
        }
    }

    //----------------------------------------版本更新-----------------------------------------------
    private void checkUpdate(int serverVersion) {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            if (serverVersion != packageInfo.versionCode) {
                //判断WIFI情况
                if (Utils.isNetworkAvailable(this)) {//判断网络是否可用
                    if (Utils.isWifiEnabled(this)) {//判断WIFI是否打开
                        if (Utils.isWifi(this)) {//判断是wifi还是3g网络
                            startDownloadApkDialog(packageInfo.versionCode);
                        } else {
                            if (Utils.is3rd(this)) {
                                showWifiAlert(packageInfo.versionCode);
                            }
                        }
                    } else {
                        if (Utils.is3rd(this)) {//判断是否是3G网络
                            showWifiAlert(packageInfo.versionCode);
                        }
                    }
                }
            } else {
                Toast(getString(R.string.version_is_up_to_date));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void startDownloadApkDialog(final int serverVersion) {
        final Dialog dialog = new Dialog(this, R.style.AlertDialogStyle);
        View item = LayoutInflater.from(this).inflate(R.layout.item_update, null);
        final TextView progressTitle = item.findViewById(R.id.upgrade_title);
        final ProgressBar progressBar = item.findViewById(R.id.my_progress);
        final TextView tvProgress = item.findViewById(R.id.tv_progres);
        dialog.setContentView(item);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            layoutParams.width = (int) (displayMetrics.widthPixels * 0.875);
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
        }
        download(dialog, progressTitle, progressBar, tvProgress, serverVersion);
        item.findViewById(R.id.btn_download).setOnClickListener(view -> {
            downloadTask.downloadReset();
            download(dialog, progressTitle, progressBar, tvProgress, serverVersion);
        });
        item.findViewById(R.id.btn_download_cancel).setOnClickListener(view -> {
            downloadTask.cancel(true);
            dialog.dismiss();
        });
    }

    private void showWifiAlert(final int serverVersion) {
        new AlertDialogBuilder(this).setTitle(getString(R.string.prompt)).setMessage(getString(R.string.alert_update_message_not_wifi)).setLeftButton(getString(R.string.confirm), (dialog, which) -> {
            startDownloadApkDialog(serverVersion);
            dialog.dismiss();
        }).setRightButton(R.string.cancel, null).show();
    }

    private void download(final Dialog dialog, final TextView progressTitle, final ProgressBar progressBar, final TextView tvProgress, int serverVersion) {
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
                    tvProgress.setText(String.format("%s", progress));
                }
            }

            @Override
            public void onSuccessful(String apkFilePath) {
                dialog.dismiss();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (getPackageManager().canRequestPackageInstalls()) {//判断用户是否已经允许了安装未知来源的apk
                        installAPK();
                    } else {
                        checkInstallPermission();
                    }
                }
            }

            @Override
            public void onFinish() {
                dialog.dismiss();
            }
        });
        String serverUrl = Prefs.getInstance(this).getServerUrl();
        downloadTask.execute(serverUrl + Prefs.getInstance(this).KEY_APP_UPDATE_URL, String.valueOf(serverVersion));
    }

    private void checkInstallPermission() {
        boolean permission = checkPermission(REQUEST_CODE_INSTALL_APK, Manifest.permission.REQUEST_INSTALL_PACKAGES, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES});
        if (permission) {
            installAPK();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MANAGE_UNKNOWN_APP_SOURCES) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (getPackageManager().canRequestPackageInstalls()) {//判断用户是否已经允许了安装未知来源的apk
                    installAPK();
                } else {
                    Toast(getString(R.string.apk_install_message));
                    if (!isCancelInstall) {
                        checkInstallPermission();
                    }
                }
            }
        }
    }

    private void installAPK() {
        downloadTask.installApk();
    }

    private void goToInstallUnKonwAPKPage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES), REQUEST_MANAGE_UNKNOWN_APP_SOURCES);
        }
    }

    @Override
    public void onPermissionsAllowed(int requestCode, String[] permissions, int[] grantResults) {
        super.onPermissionsAllowed(requestCode, permissions, grantResults);
        if (REQUEST_CODE_INSTALL_APK == requestCode) {
            installAPK();
        }
    }

    @Override
    public void onPermissionsRefused(int requestCode, String[] permissions, int[] grantResults) {
        super.onPermissionsRefused(requestCode, permissions, grantResults);
        if (REQUEST_CODE_INSTALL_APK == requestCode) {
            alertInstallDialog();
        }
    }

    @Override
    public void onPermissionsRefusedNever(int requestCode, String[] permissions, int[] grantResults) {
        super.onPermissionsRefusedNever(requestCode, permissions, grantResults);
        if (REQUEST_CODE_INSTALL_APK == requestCode) {
            alertInstallDialog();
        }
    }

    private void alertInstallDialog() {
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setCancelable(false);
            alertDialog.setTitle("温馨提示");
            alertDialog.setMessage(getString(R.string.apk_install_message));
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", (dialog, which) -> {
                dialog.dismiss();
                isCancelInstall = true;
            });
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定", (dialog, which) -> {
                dialog.dismiss();
                isCancelInstall = false;
                goToInstallUnKonwAPKPage();
            });
        }
        if (!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }
}
