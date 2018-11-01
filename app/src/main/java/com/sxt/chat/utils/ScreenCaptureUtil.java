package com.sxt.chat.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.webkit.WebView;

import java.io.File;

/**
 * Created by sxt on 2018/3/26.
 */
public class ScreenCaptureUtil {

    private ShortScreenTask task;
    private static Activity activity;
    private static ScreenCaptureUtil screenCaptureUtil = new ScreenCaptureUtil();
    private OnScreenCaptureListener onScreenCaptureListener;
    private static LruCache<String, Bitmap> lruCache;

    public static ScreenCaptureUtil getInstance(Activity activity) {
        ScreenCaptureUtil.activity = activity;
        if (lruCache == null) {
            lruCache = new LruCache<String, Bitmap>((int) (Runtime.getRuntime().maxMemory() / 5)) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getByteCount();
                }
            };
        }
        return screenCaptureUtil;
    }

    private void saveCapture2Memory(String path, Bitmap bitmap) {
        if (bitmap != null && path != null) {
            if (lruCache.get(path) == null) {
                lruCache.put(path, bitmap);
            }
        }
    }

    public Bitmap getBitmapFromMemory(String path) {
        if (path != null) {
            return lruCache.get(path);
        }
        return null;
    }

    public ScreenCaptureUtil capture(View targetView) {
        if (targetView == null) {
            throw new NullPointerException("this capture.mp3 screen targetView must be not null");
        }
        if (task != null && !task.isCancelled()) {
            task.cancel(true);
        }
        task = new ShortScreenTask();
        task.execute(targetView);

        return this;
    }

    private String captureScreen(Activity activity, View targetView) {
        Bitmap bitmap;
        if (activity != null & targetView != null) {
            if (!(targetView instanceof WebView)) {
                targetView.destroyDrawingCache();
                /**
                 * 在调用getDrawingCache()方法从ImageView对象获取图像之前，否则无法获取到
                 */
                targetView.setDrawingCacheEnabled(true);
                bitmap = Bitmap.createBitmap(targetView.getDrawingCache());
                /**
                 * 在调用getDrawingCache()方法从ImageView对象获取图像之后，一定要调用setDrawingCacheEnabled(false)方法：
                 * iv_photo.setDrawingCacheEnabled(false);以清空画图缓冲区，否则，下一次从ImageView对象iv_photo中获取的图像，还是原来的图像。
                 */
                targetView.setDrawingCacheEnabled(false);

            } else {

                WebView webView = (WebView) targetView;
                float scaleX = webView.getScaleX();
                float scaleY = webView.getScaleY();
                bitmap = Bitmap.createBitmap((int) (webView.getWidth() * scaleX),
                        (int) (webView.getHeight() * scaleY), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                Paint paint = new Paint();
                canvas.drawBitmap(bitmap, 0, webView.getHeight() * scaleY, paint);
                webView.draw(canvas);

                Log.i("webView", "captureScreen ");
            }
            File files = new File(Prefs.KEY_PATH_CAPTURE_IMG);
            if (!files.exists()) {
                files.mkdirs();
            }
            String path = files.getPath() + File.separator + System.currentTimeMillis() + ".png";
            saveCapture2Memory(path, bitmap);
//            try {
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                FileOutputStream fos = new FileOutputStream(file);
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                fos.write(baos.toByteArray());
//                fos.flush();
//                fos.close();
//                baos.close();
//                if (!bitmap.isRecycled()) {
//                    bitmap.recycle();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                return null;
//            }
            return path;
        }
        return null;
    }

    class ShortScreenTask extends AsyncTask<View, Integer, String> {

        @Override
        protected String doInBackground(View... views) {
            return captureScreen(activity, views[0]);
        }

        @Override
        protected void onPostExecute(String path) {
            super.onPostExecute(path);
            if (path != null) {
                if (onScreenCaptureListener != null)
                    onScreenCaptureListener.onCaptureSuccessed(path);
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

        void onCaptureSuccessed(String path);

        void onCaptureFailed();
    }
}
