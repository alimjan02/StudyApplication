package com.sxt.chat.download;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.FileProvider;

import com.sxt.chat.utils.ToastUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by izhaohu on 2018/5/28.
 */

public class DownloadTask extends AsyncTask<String, Integer, Exception> {

    private Activity activity;
    private OkHttpClient downloadAPKClient;
    private DownloadListener downloadListener;

    public DownloadTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected Exception doInBackground(final String... strings) {
        final File apkFile = new File(activity.getExternalFilesDir("chat") + File.separator + activity.getPackageName() + "_web.apk");
        apkFile.deleteOnExit();
        final long downloadedLength = apkFile.length();
        ProgressInterceptor.addListener(strings[0], new ProgressListener() {
            @Override
            public void onProgress(final int progress, final long max) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (downloadListener != null) {
                            downloadListener.onProgressUpdate(progress, max);
                        }
                    }
                });
            }
        });
        if (downloadAPKClient == null) {
            downloadAPKClient = new OkHttpClient.Builder()
                    .addInterceptor(new ProgressInterceptor())
                    .build();
        } else {
            downloadAPKClient.dispatcher().cancelAll();
        }
        Request request = new Request.Builder()
//                .header("RANGE", "bytes=" + downloadedLength + "-")
                .get()
                .url(strings[0])
                .build();
        try {
            Response response = downloadAPKClient.newCall(request).execute();
            parseAPK(response, apkFile);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return e;
        }
    }

    @Override
    protected void onPostExecute(Exception e) {
        super.onPostExecute(e);
        if (e == null) {
            if (downloadListener != null) {
                downloadListener.onSuccessful();
            }
        } else {
            if (downloadListener != null) {
                downloadListener.onError(e);
            }
        }
        if (downloadListener != null) {
            downloadListener.onFinish();
        }
    }

    private void parseAPK(Response response, File localApkFile) {
        BufferedWriter bw = null;
        BufferedReader br = null;
//        final File apkFile = new File(getExternalFilesDir("apk") + File.separator + getPackageName() + ".apk");
        localApkFile.deleteOnExit();

        FileOutputStream os = null;
        InputStream is = null;
        try {
//            RandomAccessFile savedFile = new RandomAccessFile(localApkFile, "rw");
//            savedFile.seek(localApkFile.length());
            os = new FileOutputStream(localApkFile);
            is = response.body().byteStream();
            byte[] b = new byte[1024];
            int len;
            while ((len = is.read(b)) != -1) {
                os.write(b, 0, len);
//                savedFile.write(b, 0, len);
            }
//            os.popub_close();
            is.close();
            installApk(localApkFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void installApk(final File apkFile) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (apkFile.exists()) {
                    Intent installApkIntent = new Intent();
                    installApkIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    installApkIntent.setAction(Intent.ACTION_VIEW);
                    installApkIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        installApkIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        installApkIntent.setDataAndType(FileProvider.getUriForFile(activity.getApplicationContext(), activity.getPackageName() + ".fileprovider", apkFile), "application/vnd.android.package-archive");
                    } else {
                        installApkIntent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                    }
                    if (activity.getPackageManager().queryIntentActivities(installApkIntent, 0).size() > 0) {
                        activity.getApplicationContext().startActivity(installApkIntent);
                    }
                } else {
                    ToastUtil.showToast(activity, "下载文件失败，请重新登录进行升级");
                }
            }
        });
    }

    private void downloadCancel() {
        if (downloadAPKClient != null) {
            downloadAPKClient.dispatcher().cancelAll();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        downloadCancel();
    }

    public void setDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    public interface DownloadListener {
        void onError(Exception e);

        void onSuccessful();

        void onFinish();

        void onProgressUpdate(int progress, long max);
    }
}
