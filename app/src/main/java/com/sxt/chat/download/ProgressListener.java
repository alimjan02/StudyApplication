package com.sxt.chat.download;

/**
 * Created by izhaohu on 2018/2/27.
 */

public interface ProgressListener {

    void onProgress(int progress, long max);
}
