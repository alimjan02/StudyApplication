package com.sxt.chat.explayer;

public interface OnTouchInfoListener {

    void onProgressChanged(long currentPosition, long duration, int currentProgress);

    void onAlphaChanged(float screenBrightness);

    void onVolumeChanged(float currentVolume, float maxVolume);

    void onProgressTouchUp(long targetPosition);

    void onAlphaTouchUp();

    void onVolumeTouchUp();
}