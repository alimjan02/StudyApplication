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
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by izhaohu on 2018/3/26.
 */
public class LruCacheUtil {
    private static Activity activity;
    private static LruCache<String, Bitmap> lruCache;
    private static LruCacheUtil lruCacheUtil = new LruCacheUtil();
    private Map<String, String> urlMaps;
    private int resId;
    private String url;
    private ShortScreenTask task;

    public static LruCacheUtil getInstance(Activity activity) {
        LruCacheUtil.activity = activity;
        if (lruCache == null) {
            lruCache = new LruCache<String, Bitmap>((int) (Runtime.getRuntime().maxMemory() / 5)) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getByteCount();
                }
            };
        }
        return lruCacheUtil;
    }

    public LruCacheUtil load(String url) {
        this.url = url;
        if (urlMaps == null) {
            urlMaps = new HashMap<>();
        }
        urlMaps.put(url, url);
        return lruCacheUtil;
    }

    public LruCacheUtil placeHolder(int resId) {
        this.resId = resId;
        return lruCacheUtil;
    }

    public LruCacheUtil saveMirrorBitmap(View decorView, String url) {
        Bitmap cache = getBitmapFromMemoryCache(url);
        if (cache == null) {
            if (task != null && !task.isCancelled()) {
                task.cancel(true);
            }
            task = new ShortScreenTask();
            task.execute(decorView, url);
        }
        return this;
    }

    public void into(View decorView, ImageView targetView) {
        if (targetView != null) {
            String url = urlMaps.get(this.url);
            Bitmap cache = getBitmapFromMemoryCache(url);
            if (cache == null) {
                Bitmap bitmap = getMirrorBitmap(activity, decorView);
                if (bitmap != null) {
                    addBitmapToMemoryCache(url, bitmap);
                    targetView.setImageBitmap(bitmap);
                } else {
                    if (resId != 0) {
                        targetView.setImageResource(resId);
                    }
                }
            } else {
                targetView.setImageBitmap(cache);
            }
        } else {
           throw  new NullPointerException("the target imageView must be not null");
        }
    }

    public Bitmap getBitmapFromMemoryCache(String url) {
        if (url == null) return null;
        return lruCache.get(url);
    }

    private void addBitmapToMemoryCache(String url, Bitmap bitmap) {
        if (url != null) {
            if (lruCache.get(url) == null) {
                lruCache.put(url, bitmap);
                if (urlMaps == null) {
                    urlMaps = new HashMap<>();
                }
                urlMaps.put(url, url);
            }
        }
    }

    private Bitmap getMirrorBitmap(Activity activity, View decorView) {

        if (activity != null & decorView != null) {
            if (!(decorView instanceof WebView)) {
                decorView.destroyDrawingCache();
                /**
                 * 在调用getDrawingCache()方法从ImageView对象获取图像之前，否则无法获取到
                 */
                decorView.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(decorView.getDrawingCache());
                /**
                 * 在调用getDrawingCache()方法从ImageView对象获取图像之后，一定要调用setDrawingCacheEnabled(false)方法：
                 * iv_photo.setDrawingCacheEnabled(false);以清空画图缓冲区，否则，下一次从ImageView对象iv_photo中获取的图像，还是原来的图像。
                 */
                decorView.setDrawingCacheEnabled(false);
                return bitmap;
            } else {

                WebView webView = (WebView) decorView;
                float scaleX = webView.getScaleX();
                float scaleY = webView.getScaleY();
                Bitmap bitmap = Bitmap.createBitmap((int) (webView.getWidth() * scaleX),
                        (int) (webView.getHeight() * scaleY), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                Paint paint = new Paint();
                canvas.drawBitmap(bitmap, 0, webView.getHeight() * scaleY, paint);
                webView.draw(canvas);

                Log.i("webView", "getMirrorBitmap ");

                return bitmap;
            }
        }
        return null;
    }

    class ShortScreenTask extends AsyncTask<Object, Integer, Bitmap> {
        private String url;

        @Override
        protected Bitmap doInBackground(Object... objects) {
            if (objects != null && objects.length == 2) {
                url = (String) objects[1];
                if (objects[0] instanceof View) {
                    return getMirrorBitmap(activity, (View) objects[0]);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                addBitmapToMemoryCache(url, bitmap);
            }
        }
    }

    public void onDestroy() {
        if (urlMaps != null) {
            Set<Map.Entry<String, String>> entries = urlMaps.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                if (lruCache != null) {
                    lruCache.remove(entry.getKey());
                }
            }
            lruCache = null;
            urlMaps.clear();
        }
        if (task != null && !task.isCancelled()) {
            task.cancel(true);
        }
        Log.i("webView", "onDestroy");
    }
}
