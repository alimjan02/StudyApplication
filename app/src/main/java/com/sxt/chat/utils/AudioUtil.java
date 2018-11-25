package com.sxt.chat.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import com.sxt.chat.R;

/**
 * Created by sxt on 2018/10/30.
 */

public class AudioUtil {

    private static AudioUtil audioUtil = new AudioUtil();
    private static Context context;

    public static AudioUtil getInstance(Context ctx) {
        context = ctx;
        return audioUtil;
    }

    public void playCaptureSound() {
        try {
            MediaPlayer player = MediaPlayer.create(context, R.raw.notify_message);
            player.setLooping(false);
            player.setVolume(1, 1);
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
