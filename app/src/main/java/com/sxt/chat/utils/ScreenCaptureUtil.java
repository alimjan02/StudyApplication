package com.sxt.chat.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

/**
 * Created by sxt on 2018/3/26.
 */
public class ScreenCaptureUtil {

    private ShortScreenTask task;
    private static Activity activity;
    private static ScreenCaptureUtil screenCaptureUtil = new ScreenCaptureUtil();
    private OnScreenCaptureListener onScreenCaptureListener;

    public static ScreenCaptureUtil getInstance(Activity activity) {
        ScreenCaptureUtil.activity = activity;
        return screenCaptureUtil;
    }

    public ScreenCaptureUtil capture(View targetView) {
        if (targetView == null) {
            throw new NullPointerException("this capture screen targetView must be not null");
        }
        if (task != null && !task.isCancelled()) {
            task.cancel(true);
        }
        task = new ShortScreenTask();
        task.execute(targetView);

        return this;
    }

    private Bitmap captureScreen(Activity activity, View targetView) {

        if (activity != null & targetView != null) {
            if (!(targetView instanceof WebView)) {
                targetView.destroyDrawingCache();
                /**
                 * 在调用getDrawingCache()方法从ImageView对象获取图像之前，否则无法获取到
                 */
                targetView.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(targetView.getDrawingCache());
                /**
                 * 在调用getDrawingCache()方法从ImageView对象获取图像之后，一定要调用setDrawingCacheEnabled(false)方法：
                 * iv_photo.setDrawingCacheEnabled(false);以清空画图缓冲区，否则，下一次从ImageView对象iv_photo中获取的图像，还是原来的图像。
                 */
                targetView.setDrawingCacheEnabled(false);
                return bitmap;
            } else {

                WebView webView = (WebView) targetView;
                float scaleX = webView.getScaleX();
                float scaleY = webView.getScaleY();
                Bitmap bitmap = Bitmap.createBitmap((int) (webView.getWidth() * scaleX),
                        (int) (webView.getHeight() * scaleY), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                Paint paint = new Paint();
                canvas.drawBitmap(bitmap, 0, webView.getHeight() * scaleY, paint);
                webView.draw(canvas);

                Log.i("webView", "captureScreen ");

                return bitmap;
            }
        }
        return null;
    }

    class ShortScreenTask extends AsyncTask<View, Integer, Bitmap> {

        @Override
        protected Bitmap doInBackground(View... views) {
            return captureScreen(activity, views[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                if (onScreenCaptureListener != null)
                    onScreenCaptureListener.onCaptureSuccessed(bitmap);
            } else {
                if (onScreenCaptureListener != null) onScreenCaptureListener.onCaptureFailed();
            }
        }
    }

    public void onDestroy() {
        if (task != null && !task.isCancelled()) {
            task.cancel(true);
        }
    }

    public ScreenCaptureUtil setOnScreenCaptureListener(OnScreenCaptureListener onScreenCaptureListener) {
        this.onScreenCaptureListener = onScreenCaptureListener;
        return this;
    }

    public interface OnScreenCaptureListener {

        void onCaptureSuccessed(Bitmap bitmap);

        void onCaptureFailed();
    }
}
