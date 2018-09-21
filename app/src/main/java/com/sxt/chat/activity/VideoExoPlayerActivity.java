package com.sxt.chat.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.drm.DrmStore;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.sxt.chat.App;
import com.sxt.chat.R;
import com.sxt.chat.adapter.VideoListAdapter;
import com.sxt.chat.adapter.VideoListAdapter2;
import com.sxt.chat.adapter.config.NoScrollLinearLayoutManager;
import com.sxt.chat.base.BaseActivity;
import com.sxt.chat.base.BaseRecyclerAdapter;
import com.sxt.chat.dialog.ProgressDrawable;
import com.sxt.chat.explayer.ExoPlayerOnTouchListener;
import com.sxt.chat.explayer.MyLoadControl;
import com.sxt.chat.explayer.OnTouchInfoListener;
import com.sxt.chat.json.PlayInfo;
import com.sxt.chat.json.ResponseInfo;
import com.sxt.chat.json.VideoObject;
import com.sxt.chat.utils.DateFormatUtil;
import com.sxt.chat.utils.NetworkUtils;
import com.sxt.chat.utils.Px2DpUtil;
import com.sxt.chat.youtu.SignatureUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by sxt on 2018/6/11.
 */

public class VideoExoPlayerActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = "Video";
    private SimpleExoPlayer player;
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private ImageView loading;
    private ProgressDrawable loadingDrawable;
    private ImageView loading_drawer;
    private ProgressDrawable loading_drawerDrawable;
    private TextView videoTitle;
    private View drawer;

    private NetWorkReceiver netWorkReceiver;
    private MyLoadControl loadControler;
    private VideoListAdapter2 adapter;
    private Handler handler = new Handler();
    private PlayerView exoPlayerView;
    private boolean flag = true;
    private int videoIndex = 0;
    private ConcatenatingMediaSource mediaSource;
    private ViewSwitcher viewSwitcher;
    private String[] urls = App.getCtx().getResources().getStringArray(R.array.videos);
    private String[] titles = App.getCtx().getResources().getStringArray(R.array.videos_name);
    private String[] video_img_url = App.getCtx().getResources().getStringArray(R.array.video_img_url);
    private View progressTool;
    private ProgressBar progressBar;
    private TextView currentProgress;
    private TextView duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exoplayer);

        exoPlayerView = findViewById(R.id.exoplayer);
        videoTitle = findViewById(R.id.video_title);
        progressTool = findViewById(R.id.progressTool);
        progressBar = findViewById(R.id.progressBar);
        currentProgress = findViewById(R.id.currentPosition);
        duration = findViewById(R.id.duration);

        loading = findViewById(R.id.loading);
        loadingDrawable = new ProgressDrawable();
        loadingDrawable.setColor(ContextCompat.getColor(this, R.color.main_green));
        loading.setImageDrawable(loadingDrawable);

        initLayout();
        registerNetWorkReceiver();

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        // 2. Create a default LoadControl
        loadControler = new MyLoadControl();
        // 3. Create the player
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControler);
        exoPlayerView.setPlayer(player);

        player.addListener(new Player.DefaultEventListener() {
            final static String TAG = "playbackState";

            @Override
            public void onLoadingChanged(boolean isLoading) {//缓冲时会调用
                super.onLoadingChanged(isLoading);
                Log.i(TAG, "onLoadingChanged  isLoading = " + isLoading);
                if (isLoading) {//正在缓冲 可以在这里判断 当前是否处于无线网环境 , 可以提示用户是否耗费手机流量播放视频
                    if (NetworkUtils.isWifi(App.getCtx())) {
                        loadControler.shouldContinueLoading(true);
                        Log.i(TAG, "Wi-Fi状态下 自动缓冲模式开启");
                    } else {
                        loadControler.shouldContinueLoading(false);
                        Log.i(TAG, "检测到您正在使用手机流量 ,自动缓冲模式已关闭");
                    }
                } else {//缓冲结束

                }
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                super.onPlayerStateChanged(playWhenReady, playbackState);
                if (playbackState == DrmStore.Playback.START) {
                    Log.i(TAG, "playbackState == DrmStore.Playback.START");
                } else if (playbackState == DrmStore.Playback.PAUSE) {//暂停播放(加载中...)
                    showLoading();
                    Log.i(TAG, "playbackState == DrmStore.Playback.PAUSE 暂停播放(加载中...)");
                } else if (playbackState == DrmStore.Playback.RESUME) {//继续播放(加载完成)
                    dismissLoading();
                    Log.i(TAG, "playbackState == DrmStore.Playback.RESUME 继续播放(加载完成)");
                } else if (playbackState == DrmStore.Playback.STOP) {//播放停止
                    dismissLoading();
                    Log.i(TAG, "playbackState == DrmStore.Playback.STOP 播放停止");
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                super.onPlayerError(error);
                Log.i(TAG, "onPlayerError  error = " + error);
            }
        });
        // Prepare the player with the source.
        player.setPlayWhenReady(false);
        setPlayerHandle();
        player.setRepeatMode(Player.REPEAT_MODE_ONE);
        startPlay(urls[0]);
    }

    /**
     * 设置触摸监听
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setPlayerHandle() {
//        exoPlayerView.setControllerHideOnTouch(true);
        ExoPlayerOnTouchListener onTouchListener = new ExoPlayerOnTouchListener(this, player)
                .setOnTouchInfoListener(new OnTouchInfoListener() {

                    @Override
                    public void onProgressChanged(long currentPosition, long duration, int currentProgress) {
                        VideoExoPlayerActivity.this.progressTool.setVisibility(View.VISIBLE);
                        VideoExoPlayerActivity.this.currentProgress.setText(DateFormatUtil.getTimeHHMMSS(currentPosition));
                        VideoExoPlayerActivity.this.duration.setText(DateFormatUtil.getTimeHHMMSS(player.getDuration()));
                        VideoExoPlayerActivity.this.progressBar.setProgress(currentProgress);
                    }

                    @Override
                    public void onTouchUp(long targetPosition) {
                        player.seekTo(targetPosition);
                        VideoExoPlayerActivity.this.progressTool.setVisibility(View.GONE);
                    }

                    @Override
                    public void onVolumeChanged() {

                    }

                    @Override
                    public void onAlphaChanged() {

                    }
                });
        exoPlayerView.setOnTouchListener(onTouchListener);
    }

    private void startPlay(final String videoUrl) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaSource == null) {
                    mediaSource = new ConcatenatingMediaSource(//播放一组视频
//                getMediaSource(Uri.parse(urls[0]))
                            getMediaSource(Uri.parse(videoUrl))
                    );

                    mediaSource.addEventListener(new Handler(), new MediaSourceEventListener() {

                        @Override
                        public void onMediaPeriodCreated(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {
                            Log.i(TAG, "onMediaPeriodCreated " + " windowIndex = " + windowIndex);
                        }

                        @Override
                        public void onMediaPeriodReleased(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {
                            Log.i(TAG, "onMediaPeriodReleased " + " windowIndex = " + windowIndex);
                        }

                        @Override
                        public void onLoadStarted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
                            Log.i(TAG, "onLoadStarted " + " windowIndex = " + windowIndex);
                        }

                        /**
                         * 缓冲完毕
                         * @param windowIndex 队列中播放的资源索引
                         */
                        @Override
                        public void onLoadCompleted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
                            Log.i(TAG, "onLoadCompleted  缓冲完成  " + " windowIndex = " + windowIndex);
                        }

                        @Override
                        public void onLoadCanceled(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
                            Log.i(TAG, "onLoadCanceled " + " windowIndex = " + windowIndex);
                        }

                        @Override
                        public void onLoadError(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {
                            Log.i(TAG, "onLoadError " + " windowIndex = " + windowIndex);
                        }

                        @Override
                        public void onReadingStarted(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {
                            Log.i(TAG, "onReadingStarted " + " windowIndex = " + windowIndex);
                            if (adapter != null) {
                                adapter.notifyIndex(videoIndex % adapter.getItemCount());
                                recyclerView.smoothScrollToPosition(videoIndex % adapter.getItemCount());
                                videoTitle.setText(titles[videoIndex % adapter.getItemCount()]);
                                Log.i(TAG, "当前播放的视频 -->标题 " + titles[videoIndex % adapter.getItemCount()] + " videoIndex = " + (videoIndex % adapter.getItemCount()));
                                mediaSource.addMediaSource(mediaSource.getSize(), getMediaSource(Uri.parse(adapter.getItem((videoIndex++) % adapter.getItemCount()).getVideo_url())));
                                Log.i(TAG, "下一个播放的视频 -->标题 " + titles[videoIndex % adapter.getItemCount()] + " videoIndex = " + (videoIndex % adapter.getItemCount()));
                            }
                        }

                        @Override
                        public void onUpstreamDiscarded(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId, MediaLoadData mediaLoadData) {
                            Log.i(TAG, "onUpstreamDiscarded " + " windowIndex = " + windowIndex);
                        }

                        @Override
                        public void onDownstreamFormatChanged(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, MediaLoadData mediaLoadData) {
                            Log.i(TAG, "onDownstreamFormatChanged " + " windowIndex = " + windowIndex);
                        }
                    });
                    player.prepare(mediaSource);
                }
            }
        });
    }

    private void registerNetWorkReceiver() {
        netWorkReceiver = new NetWorkReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(netWorkReceiver, filter);
    }

    private void initLayout() {
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.quality).setOnClickListener(this);
        viewSwitcher = findViewById(R.id.viewSwitcher);
        findViewById(R.id.select).setOnClickListener(this);
        findViewById(R.id.switchScreen).setOnClickListener(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(findViewById(R.id.nestedScrollView));
        behavior.setSkipCollapsed(false);
        behavior.setHideable(false);
        behavior.setPeekHeight(Px2DpUtil.dip2px(this,80));
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

//        drawerLayout = findViewById(R.id.drawerLayout);
//        recyclerView = findViewById(R.id.recyclerView);
//        drawer = findViewById(R.id.drawer);
//
//        loading_drawer = findViewById(R.id.loading_drawer);
//        loading_drawerDrawable = new ProgressDrawable();
//        loading_drawer.setImageDrawable(loading_drawerDrawable);
//        showDrawerLoading();
//
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                dismissDrawerLoading();
//            }
//        }, 3000);
//
        List<VideoObject> videoObjects = new ArrayList<>();
        VideoObject videoObject;
        for (int i = 0; i < urls.length; i++) {
            videoObject = new VideoObject();
            videoObject.setVideo_img_url(video_img_url[i % urls.length]);
            videoObject.setVideo_url(urls[i % urls.length]);
            videoObject.setTitle(titles[i % urls.length]);
            videoObjects.add(videoObject);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new VideoListAdapter2(this, videoObjects);
        recyclerView.setAdapter(adapter);
        adapter.setOnClickListener(new BaseRecyclerAdapter.OnClickListener() {
            @Override
            public void onClick(final int position, RecyclerView.ViewHolder holder, final Object object) {
//                drawerLayout.closeDrawers();
                mediaSource.clear();
                videoIndex = position;
                player.setPlayWhenReady(true);
                mediaSource.addMediaSource(getMediaSource(Uri.parse(((VideoObject) object).getVideo_url())));
                player.prepare(mediaSource);
            }
        });
    }

    private void showLoading() {
        if (loading != null && loadingDrawable != null) {
            loadingDrawable.start();
            loading.setVisibility(View.VISIBLE);
        }
    }

    private void dismissLoading() {
        if (loading != null && loadingDrawable != null) {
            loadingDrawable.stop();
            loading.setVisibility(View.GONE);
        }
    }

    private void showDrawerLoading() {
        if (loading_drawer != null && loading_drawerDrawable != null) {
            loading_drawerDrawable.start();
            loading_drawer.setVisibility(View.VISIBLE);
        }
    }

    private void dismissDrawerLoading() {
        if (loading_drawer != null && loading_drawerDrawable != null) {
            loading_drawerDrawable.stop();
            loading_drawer.setVisibility(View.GONE);
        }
    }

    private class NetWorkReceiver extends BroadcastReceiver {

        private String getConnectionType(int type) {
            String connType = "";
            if (type == ConnectivityManager.TYPE_MOBILE) {
                connType = "手机网络数据";
            } else if (type == ConnectivityManager.TYPE_WIFI) {
                connType = "Wi-Fi网络";
            }
            return connType;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // 监听网络连接，包括wifi和移动数据的打开和关闭,以及连接上可用的连接都会接到监听
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                //获取联网状态的NetworkInfo对象
                NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (info != null) {
                    //如果当前的网络连接成功并且网络连接可用
                    if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                        if (info.getType() == ConnectivityManager.TYPE_WIFI) {

                            loadControler.shouldContinueLoading(true);
                            player.seekTo(player.getCurrentPosition());
                            Log.i(TAG, getConnectionType(info.getType()) + "连上");

                        } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {

                            Log.i(TAG, getConnectionType(info.getType()) + "连上");
                        }
                    } else {
                        Log.i(TAG, getConnectionType(info.getType()) + "断开");
                    }
                }
            }
        }
    }

    private MediaSource getMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                this,
                Util.getUserAgent(this, App.class.getName()),
                new TransferListener<DataSource>() {
                    @Override
                    public void onTransferStart(DataSource source, DataSpec dataSpec) {
//                        Log.i("Video", "onTransferStart");//缓冲开始
                    }

                    @Override
                    public void onBytesTransferred(DataSource source, int bytesTransferred) {
//                        Log.i("Video", "onTransferStart");//正在缓冲
                    }

                    @Override
                    public void onTransferEnd(DataSource source) {
//                        Log.i("Video", "onTransferEnd");//缓冲完成
                    }
                });

        // This is the MediaSource representing the media to be played.
        return new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                onBack();
                break;
            case R.id.quality:
                Toast("切换清晰度");
                break;
            case R.id.select:
                drawerLayout.openDrawer(drawer);
                break;
            case R.id.switchScreen:
                setRequestedOrientation(getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {//竖屏
            viewSwitcher.setDisplayedChild(0);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) exoPlayerView.getLayoutParams();
//            lp.height = Px2DpUtil.dip2px(this, 200);
            lp.height = FrameLayout.LayoutParams.WRAP_CONTENT;
            exoPlayerView.setLayoutParams(lp);

        } else if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {//横屏
            viewSwitcher.setDisplayedChild(1);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) exoPlayerView.getLayoutParams();
            lp.height = FrameLayout.LayoutParams.MATCH_PARENT;
            exoPlayerView.setLayoutParams(lp);
        }
    }

    private void onBack() {
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//            drawerLayout.closeDrawers();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            if (player != null) {
                player.stop();
                player.release();
            }
            super.onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
        initWindowStyle();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        getVideoList();
        if (player != null) {
            player.setPlayWhenReady(flag || player.getPlaybackState() == DrmStore.Playback.RESUME);
            flag = false;
//            player.setVolume(volume == 0 ? player.getVolume() : volume);
//            player.setVideoSurfaceView((SurfaceView) exoPlayerView.getVideoSurfaceView());
            loadControler.shouldContinueLoading(NetworkUtils.isWifi(this));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.setPlayWhenReady(false);
//            player.clearVideoSurface();//进入后台,清除画面
//            volume = player.getVolume();
//            player.setVolume(0);
            loadControler.shouldContinueLoading(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stop();
            player.release();
        }
        if (netWorkReceiver != null) {
            unregisterReceiver(netWorkReceiver);
        }
    }

    private void getVideoList() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).writeTimeout(20, TimeUnit.SECONDS);
        //生成私有参数，不同API需要修改
        Map<String, String> privateParams = SignatureUtil.generatePrivateParamters("baa55f3da3d1448a9f9c8f1374a767dd", "GetPlayInfo");
        //生成公共参数，不需要修改
        Map<String, String> publicParams = SignatureUtil.generatePublicParamters();
        //生成OpenAPI地址，不需要修改
        String URL = SignatureUtil.generateOpenAPIURL(publicParams, privateParams);

        clientBuilder.build().newCall(new Request.Builder().url(URL).get().build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("sxt", e.toString());
            }

            @Override
            public void onResponse(Call call, final Response response) {
                Log.e("sxt", response.toString());
                try {
                    ResponseInfo responseInfo = new Gson().fromJson(response.body().string(), ResponseInfo.class);
                    if (responseInfo.getVideoBase() != null && responseInfo.getPlayInfoList() != null) {
                        List<PlayInfo> playInfos = responseInfo.getPlayInfoList().getPlayInfo();
                        if (playInfos != null && playInfos.size() > 0) {
                            startPlay(playInfos.get(1).PlayURL);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
