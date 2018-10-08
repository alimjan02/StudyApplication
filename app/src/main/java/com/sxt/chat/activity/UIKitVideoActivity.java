package com.sxt.chat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;

import com.sxt.chat.R;
import com.sxt.chat.base.BaseActivity;
import com.sxt.chat.view.EZUIPlayerView;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.EZPlayURLParams;
import com.videogo.openapi.EZPlayer;
import com.videogo.openapi.EZUIPlayer;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by sxt on 2018/10/8.
 */
public class UIKitVideoActivity extends BaseActivity implements SurfaceHolder.Callback {

    private EZUIPlayerView mEZUIPlayerView;
    private EZPlayer mEZPlayer;
    public final static int STATUS_INIT = 1;
    public final static int STATUS_START = 2;
    public final static int STATUS_PLAY = 3;
    public final static int STATUS_STOP = 4;

    /**
     * 播放器状态
     */
    public int mStatus = STATUS_INIT;
    /**
     * resume时是否恢复播放
     */
    private AtomicBoolean isResumePlay = new AtomicBoolean(true);
    /**
     * surface是否创建好
     */
    private AtomicBoolean isInitSurface = new AtomicBoolean(false);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uikit_video);
        //获取EZUIPlayer实例
        mEZUIPlayerView = (EZUIPlayerView) findViewById(R.id.player_ui);
        mEZUIPlayerView.setSurfaceHolderCallback(UIKitVideoActivity.this);
        mEZPlayer = EZPlayer.createPlayerWithUrl("ezopen://open.ys7.com/143814425/1.hd.live");
//        mEZPlayer = EZPlayer.createPlayer("171483705", 1);
//        mEZPlayer = EZPlayer.createPlayer("143814425", 1);
        mEZPlayer.openSound();
        mEZPlayer.startRealPlay();
//        getAccessToken();
    }

//    @Override
//    public void onPlaySuccess() {
//        Log.i("live", "onPlaySuccess");
//    }
//
//    @Override
//    public void onPlayFail(EZUIError ezuiError) {
//        Log.i("live", ezuiError.getErrorString());
//    }
//
//    @Override
//    public void onVideoSizeChange(int i, int i1) {
//        Log.i("live", "i = " + i + " i1 = " + i1);
//    }
//
//    @Override
//    public void onPrepared() {
//        Log.i("live", "onPrepared");
//    }
//
//    @Override
//    public void onPlayTime(Calendar calendar) {
//
//    }
//
//    @Override
//    public void onPlayFinish() {
//
//    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mEZPlayer != null) {
            mEZPlayer.setSurfaceHold(holder);
        }
        if (isInitSurface.compareAndSet(false, true) && isResumePlay.get()) {
            isResumePlay.set(false);
            startRealPlay();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isInitSurface.set(false);
    }

    /**
     * 更新播放状态显示UI
     */
    private void refreshPlayStutsUI() {
        switch (mStatus) {
            case STATUS_PLAY:
                mEZUIPlayerView.dismissomLoading();
//                mPlayUI.mPlayImg.setImageResource(R.drawable.btn_stop_n);
//                mPlayUI.mRecordImg.setEnabled(true);
//                mPlayUI.mPictureImg.setEnabled(true);
                break;
            case STATUS_STOP:
                mEZUIPlayerView.dismissomLoading();
//                mPlayUI.mPlayImg.setImageResource(R.drawable.btn_play_n);
//                mPlayUI.mRecordImg.setEnabled(false);
//                mPlayUI.mPictureImg.setEnabled(false);
                break;
            default:
                break;
        }
    }

    /**
     * 开始播放
     */
    private void startRealPlay() {
        if (mStatus == STATUS_START || mStatus == STATUS_PLAY) {
            return;
        }

    }

    /**
     * 停止播放
     */
    private void stopRealPlay() {
//        stopRealPlayUI();
        if (mEZPlayer != null) {
            mEZPlayer.stopRealPlay();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void getAccessToken() {
        loading.show();
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).writeTimeout(20, TimeUnit.SECONDS);
        String url = "https://open.ys7.com/api/lapp/token/get";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=utf-8"), "appKey=de05c197e58645f689ea0ab02bfef777&appSecret=b785ab143816bc4c9592d95beaf7916e");
        clientBuilder.build().newCall(new Request.Builder().url(url).post(requestBody)
                .build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("live", "onFailure : " + e.toString());
            }

            @Override
            public void onResponse(Call call, final Response response) {
                Log.e("live", response.toString());
                if (response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if ("200".equals(jsonObject.optString("code"))) {
                            JSONObject data = jsonObject.optJSONObject("data");
                            if (data != null) {
                                final String accessToken = data.optString("accessToken");
                                if (!TextUtils.isEmpty(accessToken)) {
                                    UIKitVideoActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            EZOpenSDK.setAccessToken(accessToken);
                                            mEZUIPlayerView.setSurfaceHolderCallback(UIKitVideoActivity.this);
//                                            mEZPlayer = EZPlayer.createPlayerWithUrl("ezopen://open.ys7.com/171483705/1.hd.live");
                                            mEZPlayer = EZPlayer.createPlayer("171483705", 1);
                                            mEZPlayer.startRealPlay();
                                        }
                                    });
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("live", e.toString());
                        UIKitVideoActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loading.dismiss();
                            }
                        });
                    }
                }
            }
        });
    }
}
