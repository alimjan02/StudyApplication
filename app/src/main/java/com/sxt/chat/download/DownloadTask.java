package com.sxt.chat.download;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.sxt.chat.utils.Prefs;
import com.sxt.chat.utils.ToastUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by izhaohu on 2018/5/28.
 */

public class DownloadTask extends AsyncTask<String, Integer, String> {

    private String TAG = this.getClass().getName();
    private Activity activity;
    private File apkFile;
    private OkHttpClient downloadAPKClient;
    private DownloadListener downloadListener;

    public DownloadTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(final String... strings) {
        try {
            apkFile = new File(activity.getExternalFilesDir("apk") + File.separator + Prefs.KEY_APP_NAME + strings[1] + ".apk");
            final long downloadedLength = apkFile.length();
            if (downloadAPKClient != null) {
                downloadAPKClient.dispatcher().cancelAll();
            }
            downloadAPKClient = new OkHttpClient.Builder().build();

            //先开启一个线程 获取apk文件的总长度
            Response response = downloadAPKClient.newCall(new Request.Builder()
                    .get()
                    .url(strings[0])
                    .build())
                    .execute();

            if (response.isSuccessful()) {
                //本地已有完整的apk文件,直接安装
                final long contentLength = response.body().contentLength();
                if (downloadedLength == contentLength) {
                    return apkFile.getPath();
                } else {
                    if (downloadedLength > contentLength) {
                        apkFile.delete();
                    }
                    ProgressInterceptor.addListener(strings[0], new ProgressListener() {
                        @Override
                        public void onProgress(final int progress, final long max) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (downloadListener != null) {
                                        int realProgress = (int) ((downloadedLength + (float) progress / 100 * max) / contentLength * 100);
                                        downloadListener.onProgressUpdate(realProgress, contentLength);
                                        Log.e(TAG, "onProgress : downloadedLength : " + downloadedLength + " downloadedLength + max : " + (downloadedLength + max) + " contentLength = " + contentLength);
                                    }
                                }
                            });
                        }
                    });
                    response.close();
                    downloadAPKClient = new OkHttpClient.Builder()
                            .addInterceptor(new ProgressInterceptor())
                            .build();

                    //本地文件不完整时才开始断点续传
                    Request request = new Request.Builder()
                            .header("RANGE", "bytes=" + downloadedLength + "-")
                            .get()
                            .url(strings[0])
                            .build();

                    Response resp = downloadAPKClient.newCall(request).execute();
                    return parseAPK(resp, apkFile);
                }
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String path) {
        super.onPostExecute(path);
        if (path != null && new File(path).exists()) {
            if (downloadListener != null) {
                downloadListener.onSuccessful(path);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    installApk();
                }
            }
        } else {
            if (downloadListener != null) {
                downloadListener.onError(new FileNotFoundException(" the apk file is bad"));
            }
        }
        if (downloadListener != null) {
            downloadListener.onFinish();
        }
    }

    private String parseAPK(Response response, File localApkFile) {
        FileOutputStream os = null;
        BufferedInputStream is = null;
        try {
            Log.e(TAG, "response : " + ((float) response.body().contentLength() / 1024 / 1024) + "本地已有 : " + (localApkFile.length() / 1024 / 1024));

            os = new FileOutputStream(localApkFile, true);
            is = new BufferedInputStream(response.body().byteStream());
            byte[] b = new byte[1024 * 1024];
            int len;
            while ((len = is.read(b)) != -1) {
                os.write(b, 0, len);
            }
            os.flush();
            os.close();
            is.close();
            return localApkFile.getPath();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void installApk() {
        if (apkFile != null && apkFile.exists()) {
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

    public void downloadReset() {
        if (apkFile != null && apkFile.exists()) {
            apkFile.delete();
        }
    }

    private void downloadCancel() {
        if (downloadAPKClient != null) downloadAPKClient.dispatcher().cancelAll();
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

        void onProgressUpdate(final int progress, final long max);

        void onSuccessful(String apkFilePath);

        void onFinish();
    }
}
