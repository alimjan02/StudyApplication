package com.sxt.chat.record;

import java.util.List;

/**
 * 为了方便使用，我们可以创建一个Manager来管理合并视频功能，这样我们就可以在Activity、Fragment里面直接通过调用VideoCombineManager.getInstance().startVideoCombiner()方法，
 * 传递需要合并的路径列表、输出路径、合并状态监听接口参数，使用起来非常方便：
 */
public final class VideoCombineManager {

    private static final String TAG = "VideoCombineManage";

    private static VideoCombineManager mInstance;


    public static VideoCombineManager getInstance() {
        if (mInstance == null) {
            mInstance = new VideoCombineManager();
        }
        return mInstance;
    }

    /**
     * 初始化媒体合并器
     *
     * @param videoPath
     * @param destPath
     */
    public void startVideoCombiner(final List<String> videoPath, final String destPath,
                                   final VideoCombiner.VideoCombineListener listener) {
        VideoCombiner videoCombiner = new VideoCombiner(videoPath, destPath, listener);
        videoCombiner.combineVideo();//耗时操作,应放在子线程
    }
}