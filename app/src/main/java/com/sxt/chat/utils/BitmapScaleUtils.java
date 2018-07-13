package com.sxt.chat.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.net.URL;

/**
 * Created by sxt on 2017/3/25.
 */
public class BitmapScaleUtils {

    private static BitmapScaleUtils bitmapScaleUtils;
    private Context context;

    private BitmapScaleUtils() {
    }

    private BitmapScaleUtils(Context context) {
        this.context = context;
    }

    public synchronized static BitmapScaleUtils getInstance(Context context) {

        if (bitmapScaleUtils == null) {
            bitmapScaleUtils = new BitmapScaleUtils(context);
        }

        return bitmapScaleUtils;
    }

    public Bitmap getScaleResuceBitmap(int resId, int targetWidth, int targetHeight) {
        // 创建盒子
        BitmapFactory.Options opts = new BitmapFactory.Options();
        /**
         * 将盒子的opts.inJustDecodeBounds 设置为true 以便于获取图片的宽高信息 而不会真正的将图片加载到内存
         * 只是获取额外的信息
         */
        opts.inJustDecodeBounds = true;
        // 加载图片
        // BitmapFactory.decodeFile(imagePath, opts); 这种方式容易产生 oom
        try {
            BitmapFactory.decodeResource(context.getResources(), resId, opts);
            // 获取图片的宽高
            int width = opts.outWidth;
            int height = opts.outHeight;
            // 求出图片的宽高和手机屏幕的宽高之比
            if (width > targetWidth || height > targetHeight) {
                int widthScale = width / targetWidth;
                int HeightScale = height / targetHeight;
                // 通过三元运算计算出两个比值的大小
                int scale = widthScale > HeightScale ? HeightScale : widthScale;
                // 设置盒子加载图片的比例
                opts.inSampleSize = scale;
            }
            // 将盒子的opts.inJustDecodeBounds设置为false 以便于将图片加载到内存中
            opts.inJustDecodeBounds = false;
            opts.inDither = false;/*不进行图片抖动处理*/
            opts.inPreferredConfig = null; /*设置让解码器以最佳方式解码*/
            // 在此加载图片 将盒子塞进去
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId, opts);
            if (bitmap.isRecycled()) {
                bitmap.recycle();
                System.gc();
            }
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取压缩后的本地图库图片
     *
     * @param imageUri     本地图库中的图片的uri
     * @param targetWidth  目标压缩宽度
     * @param targetHeight 目标压缩高度
     * @return 返回压缩后的bitmap
     */
    public Bitmap getLocalScaleBitmap(Uri imageUri, int targetWidth, int targetHeight) {

        // 创建盒子
        BitmapFactory.Options opts = new BitmapFactory.Options();

        /**
         * 将盒子的opts.inJustDecodeBounds 设置为true 以便于获取图片的宽高信息 而不会真正的将图片加载到内存
         * 只是获取额外的信息
         */
        opts.inJustDecodeBounds = true;
        // 加载图片
        // BitmapFactory.decodeFile(imagePath, opts); 这种方式容易产生 oom
        try {
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imageUri), null, opts);
            // 获取图片的宽高
            int width = opts.outWidth;
            int height = opts.outHeight;
            // 求出图片的宽高和手机屏幕的宽高之比
            if (width > targetWidth || height > targetHeight) {
                int widthScale = width / targetWidth;
                int HeightScale = height / targetHeight;
                // 通过三元运算计算出两个比值的大小
                int scale = widthScale > HeightScale ? HeightScale : widthScale;
                // 设置盒子加载图片的比例
                opts.inSampleSize = scale;
            }
            // 将盒子的opts.inJustDecodeBounds设置为false 以便于将图片加载到内存中
            opts.inJustDecodeBounds = false;
            opts.inDither = false;/*不进行图片抖动处理*/
            opts.inPreferredConfig = null; /*设置让解码器以最佳方式解码*/

            // 在此加载图片 将盒子塞进去
            Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imageUri), null, opts);
            if (bitmap.isRecycled()) {
                bitmap.recycle();
                System.gc();
            }

            return bitmap;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取压缩后的网络图片
     *
     * @param imgUrl       网络图片的url
     * @param targetWidth  目标压缩宽度
     * @param targetHeight 目标压缩高度
     * @return 返回压缩后的bitmap
     */
    public Bitmap getNetWorkScaledBitmap(String imgUrl, int targetWidth, int targetHeight) {

        // 创建盒子
        BitmapFactory.Options opts = new BitmapFactory.Options();
        URL url;

        /**
         * 将盒子的opts.inJustDecodeBounds 设置为true 以便于获取图片的宽高信息 而不会真正的将图片加载到内存
         * 只是获取额外的信息
         */
        opts.inJustDecodeBounds = true;
        // 加载图片
        // BitmapFactory.decodeFile(imagePath, opts); 这种方式容易产生 oom
        try {
            url = new URL(imgUrl);
            BitmapFactory.decodeStream(url.openStream(), null, opts);
            // 获取图片的宽高
            int width = opts.outWidth;
            int height = opts.outHeight;
            // 求出图片的宽高和手机屏幕的宽高之比
            if (width > targetWidth || height > targetHeight) {
                int widthScale = width / targetWidth;
                int HeightScale = height / targetHeight;
                // 通过三元运算计算出两个比值的大小
                int scale = widthScale > HeightScale ? HeightScale : widthScale;
                // 设置盒子加载图片的比例
                opts.inSampleSize = scale;
            }

            // 将盒子的opts.inJustDecodeBounds设置为false 以便于将图片加载到内存中
            opts.inJustDecodeBounds = false;
            opts.inDither = false;/*不进行图片抖动处理*/
            opts.inPreferredConfig = null; /*设置让解码器以最佳方式解码*/

            // 在此加载图片 将盒子塞进去
            Bitmap bitmap = BitmapFactory.decodeStream(url.openStream(), null, opts);
            if (bitmap.isRecycled()) {
                bitmap.recycle();
                System.gc();//提醒系统 看是否需要回收bitmap
            }

            return bitmap;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 保存图片至本地
     *
     * @param context  上下文
     * @param bitmap   要保存的bitmap
     * @param fileName 保存的文件名
     */
    public void saveNetWorkBitmapToSdcard(Context context, Bitmap bitmap, String fileName) {

//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            if (bitmap != null) {
//                File imgDir = new File(context.getResources().getString(R.string.save_image_file_folder));
//                if (!imgDir.exists()) {
//                    imgDir.mkdirs();
//                }
//                File file = new File(imgDir, fileName);
//                //压缩图片
//                OutputStream os;
//                try {
//                    os = new FileOutputStream(file);
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, os);
//                    os.flush();
//                    os.popub_close();
//
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

}
