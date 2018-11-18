package com.sxt.chat.utils.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

public class GlideCircleTransformer extends BitmapTransformation {

    private static Context context;
    private static int padding;
    private static int colorResId = -1;

    public GlideCircleTransformer(Context context) {
        super(context);
    }

    public GlideCircleTransformer(Context context, int padding) {
        super(context);
        this.context = context;
        this.padding = padding;
    }

    public GlideCircleTransformer(Context context, int padding, int colorResId) {
        super(context);
        this.context = context;
        this.padding = padding;
        this.colorResId = colorResId;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return circleCrop(pool, toTransform);
    }

    private static Bitmap circleCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;
        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;
        // TODO this could be acquired from the pool too
        Bitmap squared = Bitmap.createBitmap(source, x, y, size - padding, size - padding);
        Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        float r = size / 2f;

        Paint p = new Paint();
        p.setColor(colorResId == -1 ? Color.GRAY : ContextCompat.getColor(context, colorResId));
        canvas.drawCircle(r, r, r, p);
        canvas.drawCircle(r, r, r - padding, paint);

        return result;
    }

    @Override
    public String getId() {
        return getClass().getName();
    }
}