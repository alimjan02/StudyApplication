package com.sxt.chat.utils.glide;

import android.os.Looper;

import com.bumptech.glide.Glide;
import com.sxt.chat.App;
import com.sxt.chat.utils.Prefs;

import java.io.File;
import java.math.BigDecimal;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class CacheUtils {
    private static CacheUtils instance;

    public static CacheUtils getInstance() {
        if (null == instance) {
            instance = new CacheUtils();
        }
        return instance;
    }

    // 获取Glide磁盘缓存大小
    public String getCacheSize() {
        try {
            File dir_glide = new File(App.getCtx().getExternalCacheDir() + File.separator + GlideCatchConfig.GLIDE_CARCH_DIR);
            if (!dir_glide.exists()) {
                dir_glide.mkdir();
            }
            File dir_crop_img = new File(Prefs.KEY_PATH_CROP_IMG);
            if (!dir_crop_img.exists()) {
                dir_crop_img.mkdir();
            }
            File dir_take_photo = new File(Prefs.KEY_PATH_TAKE_PHOTO_IMG);
            if (!dir_take_photo.exists()) {
                dir_take_photo.mkdir();
            }
            return getFormatSize(getFolderSize(dir_glide) + getFolderSize(dir_crop_img) + getFolderSize(dir_take_photo));
        } catch (Exception e) {
            e.printStackTrace();
            return "获取失败";
        }
    }

    public void clearCache() {
        clearCustomFolderCache();
        cleanCacheDisk();
        clearCacheMemory();
        clearCacheDiskSelf();
    }

    //清楚自定义的文件夹缓存文件
    private void clearCustomFolderCache() {
        deleteFolderFile(Prefs.KEY_PATH_TAKE_PHOTO_IMG, true);
        deleteFolderFile(Prefs.KEY_PATH_CROP_IMG, true);
    }

    // 清除Glide磁盘缓存，自己获取缓存文件夹并删除方法
    private boolean cleanCacheDisk() {
        return deleteFolderFile(App.getCtx().getExternalCacheDir() + File.separator + GlideCatchConfig.GLIDE_CARCH_DIR, true);
    }

    // 清除图片磁盘缓存，调用Glide自带方法
    private boolean clearCacheDiskSelf() {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(App.getCtx()).clearDiskCache();
                    }
                }).start();
            } else {
                Glide.get(App.getCtx()).clearDiskCache();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 清除Glide内存缓存
    private boolean clearCacheMemory() {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) { //只能在主线程执行
                Glide.get(App.getCtx()).clearMemory();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    // 获取指定文件夹内所有文件大小的和
    private long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File aFileList : fileList) {
                if (aFileList.isDirectory()) {
                    size = size + getFolderSize(aFileList);
                } else {
                    size = size + aFileList.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    // 格式化单位
    private static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "B";
        }
        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }
        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

    // 按目录删除文件夹文件方法
    private boolean deleteFolderFile(String filePath, boolean deleteThisPath) {
        try {
            File file = new File(filePath);
            if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (File file1 : files) {
                    deleteFolderFile(file1.getAbsolutePath(), true);
                }
            }
            if (deleteThisPath) {
                if (!file.isDirectory()) {
                    file.delete();
                } else {
                    if (file.listFiles().length == 0) {
                        file.delete();
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}