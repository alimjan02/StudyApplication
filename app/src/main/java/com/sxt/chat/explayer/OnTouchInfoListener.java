package com.sxt.chat.explayer;

public interface OnTouchInfoListener {

    void onTouch();

    void onTouchUp(long targetPosition);

    void onProgressChanged(long currentPosition, long duration, int currentProgress);

    void onVolumeChanged();

    void onAlphaChanged();
}