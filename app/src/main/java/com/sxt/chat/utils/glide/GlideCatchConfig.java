package com.sxt.chat.utils.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.module.GlideModule;

/**
 * 必须要在清单文件中 声明 , 才能生效
 * <meta-data
 * android:name=".utils.glide.GlideCatchConfig"
 * android:value="GlideModule" />
 */
public class GlideCatchConfig implements GlideModule {

    // 图片缓存最大容量，150M，根据自己的需求进行修改
    public static final int GLIDE_CATCH_SIZE = 150 * 1000 * 1000;

    // 图片缓存子目录
    public static final String GLIDE_CARCH_DIR = "glide_cache";

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        /**
         * 更改缓存总文件夹名称
         *
         * 是在sdcard/Android/data/包名/cache/DISK_CACHE_NAME目录当中
         */
        builder.setDiskCache(new ExternalCacheDiskCacheFactory(context, GLIDE_CARCH_DIR, GLIDE_CATCH_SIZE));

    }

    @Override
    public void registerComponents(Context context, Glide glide) {

    }
}