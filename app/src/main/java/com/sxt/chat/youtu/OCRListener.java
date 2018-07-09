package com.sxt.chat.youtu;

import com.sxt.chat.json.OCRObject;

/**
 * Created by 11837 on 2018/6/9.
 */

public interface OCRListener {
    void onStart();

    void onFaied(Exception e);

    /**
     * @param ocrResult 识别的结果
     * @param result    基于ocrResult封装的结果String
     */
    void onSuccess(OCRObject ocrResult, String result);
}
