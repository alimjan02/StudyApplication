package com.sxt.chat.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.PictureInPictureParams;
import android.app.RemoteAction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.drm.DrmStore;
import android.graphics.drawable.Icon;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Rational;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.android.exoplayer2.DefaultRenderersFactory;
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
import com.sxt.chat.base.BaseActivity;
import com.sxt.chat.dialog.ProgressDrawable;
import com.sxt.chat.explayer.ExoPlayerOnTouchListener;
import com.sxt.chat.explayer.MyLoadControl;
import com.sxt.chat.explayer.OnTouchInfoListener;
import com.sxt.chat.json.PlayInfo;
import com.sxt.chat.json.ResponseInfo;
import com.sxt.chat.json.VideoInfo;
import com.sxt.chat.utils.ArithTool;
import com.sxt.chat.utils.DateFormatUtil;
import com.sxt.chat.utils.Px2DpUtil;
import com.sxt.chat.utils.Utils;
import com.sxt.chat.ws.BmobRequest;
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

public class ExoPlayerActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = "Video";
    private ImageView loading;
    private ProgressDrawable loadingDrawable;
    private View videoTitleLayout;

    private ImageView menuArrow;
    private RecyclerView recyclerView;
    private VideoListAdapter adapter;

    private View touchToolsLayout, progressTool;
    private ProgressBar progressBar;
    private TextView currentProgress, duration;

    private View alphaTool;
    private ImageView alphaImg;
    private TextView alpha;

    private View volumeTool;
    private ImageView volumeImg;
    private TextView volume;

    private SimpleExoPlayer player;
    private PlayerView exoPlayerView;
    private int videoIndexNext = 0, videoIndexCurrent = 0;
    private ViewSwitcher viewSwitcher;

    private View controllerLayout;

    private VolumeChangeReceiver volumeChangeReceiver;
    private NetWorkReceiver netWorkReceiver;
    private MyLoadControl loadControler;
    private ConcatenatingMediaSource mediaSource;
    private BottomSheetBehavior bottomSheetBehavior;

    private TabLayout tabLayout;
    private TextView videoTitle;
    private ExoPlayerOnTouchListener exoPlayerOnTouchListener;

    private boolean isControllerVisiable = true, isUsePhoneData;
    private AlertDialog alertDialog;
    private List<VideoInfo> videoInfos;
    private final String ACTION_VOLUME_CHANGED = "android.media.VOLUME_CHANGED_ACTION";
    private final String CMD_GET_VIDEOS = "CMD_GET_VIDEOS";
    private boolean isInPictureInPictureMode;
    private static final int CONTROL_TYPE_PLAY = 1;
    private static final int CONTROL_TYPE_PAUSE = 2;
    private static final int CONTROL_TYPE_REMIND = 3;
    private static final int CONTROL_TYPE_FORWARD = 4;
    private static final String ACTION_MEDIA_CONTROL = "media_control";
    private static final String EXTRA_CONTROL_TYPE = "control_type";
    @RequiresApi(api = Build.VERSION_CODES.O)
    private PictureInPictureParams.Builder mPictureInPictureParamsBuilder = new PictureInPictureParams.Builder();
    private BroadcastReceiver pictureActionReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_exoplayer);
        initLayout();
        initPlayer();
        registerNetWorkReceiver();
        registerVolumeReceiver();
    }

    private void initPlayer() {
        //set video url
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        // 创建缓冲控制器
        loadControler = new MyLoadControl();
        // 创建播放器
        player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this), trackSelector, loadControler);
        exoPlayerView.setPlayer(player);
        player.addListener(new Player.DefaultEventListener() {
            final static String TAG = "playbackState";

            @Override
            public void onLoadingChanged(boolean isLoading) {//缓冲时会调用
                super.onLoadingChanged(isLoading);
                Log.i(TAG, "onLoadingChanged  isLoading = " + isLoading);
                if (isLoading) {
                    //正在缓冲 可以在这里判断 当前是否处于无线网环境 , 可以提示用户是否耗费手机流量播放视频
                    alertMobileDataDialog();
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
                    alertMobileDataDialog();
                    setPictureInPictureActions(R.drawable.ic_play_arrow_24dp, getString(R.string.play), CONTROL_TYPE_PLAY);
                    Log.i(TAG, "playbackState == DrmStore.Playback.PAUSE 暂停播放(加载中...)");
                } else if (playbackState == DrmStore.Playback.RESUME) {//继续播放(加载完成)
                    dismissLoading();
                    setPictureInPictureActions(R.drawable.ic_pause_24dp, getString(R.string.pause), CONTROL_TYPE_PAUSE);
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
        // 设置播放资源 开始播放视频
        player.setPlayWhenReady(true);
        exoPlayerView.setControllerAutoShow(false);
    }

    /**
     * 设置触摸监听
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setPlayerHandle() {
        if (exoPlayerOnTouchListener != null) return;
        exoPlayerView.setControllerVisibilityListener(visibility -> {
            isControllerVisiable = visibility == View.VISIBLE;
            if (!isInPictureInPictureMode && bottomSheetBehavior != null) {
                if (visibility == View.VISIBLE) {
                    translationYAnimatorForTitleLayout(-videoTitleLayout.getHeight(), 0, 0, 1);
                    hideBottomSheet(BottomSheetBehavior.STATE_EXPANDED, BottomSheetBehavior.STATE_HIDDEN);
                } else {
                    translationYAnimatorForTitleLayout(0, -videoTitleLayout.getHeight(), 1, 0);
                    if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                    }
                }
            }
        });
        exoPlayerOnTouchListener = new ExoPlayerOnTouchListener(this, exoPlayerView, player)
                .setOnTouchInfoListener(new OnTouchInfoListener() {

                    @Override
                    public void onProgressChanged(long currentPosition, long duration, int currentProgress) {
                        touchToolsLayout.setVisibility(View.VISIBLE);
                        progressTool.setVisibility(View.VISIBLE);
                        alphaTool.setVisibility(View.GONE);
                        volumeTool.setVisibility(View.GONE);

                        ExoPlayerActivity.this.currentProgress.setText(DateFormatUtil.getTimeHHMMSS(currentPosition));
                        ExoPlayerActivity.this.duration.setText(DateFormatUtil.getTimeHHMMSS(player.getDuration()));
                        ExoPlayerActivity.this.progressBar.setProgress(currentProgress);
                    }

                    @Override
                    public void onAlphaChanged(float screenBrightness) {
                        touchToolsLayout.setVisibility(View.VISIBLE);
                        progressTool.setVisibility(View.GONE);
                        volumeTool.setVisibility(View.GONE);
                        alphaTool.setVisibility(View.VISIBLE);
                        double v = ArithTool.div(screenBrightness, 1, 2) * 100;
                        if (v <= 0) v = 0;
                        if (v >= 100) v = 100;
                        if (v > 60) {
                            ExoPlayerActivity.this.alphaImg.setImageResource(R.drawable.ic_brightness_high_white_24dp);
                        } else if (v > 30) {
                            ExoPlayerActivity.this.alphaImg.setImageResource(R.drawable.ic_brightness_middle_white_24dp);
                        } else {
                            ExoPlayerActivity.this.alphaImg.setImageResource(R.drawable.ic_brightness_lower_white_24dp);
                        }
                        alpha.setText(String.format("%s", (int) v));
                    }

                    @Override
                    public void onVolumeChanged(float currentVolume, float maxVolume) {
                        touchToolsLayout.setVisibility(View.VISIBLE);
                        progressTool.setVisibility(View.GONE);
                        alphaTool.setVisibility(View.GONE);
                        volumeTool.setVisibility(View.VISIBLE);
                        double div = ArithTool.div(currentVolume, maxVolume, 2);
                        volume.setText(String.format("%s", (int) (div * 100)));
                        if (div <= 0) {
                            volumeImg.setImageResource(R.drawable.ic_volume_off_white_24dp);
                        } else {
                            volumeImg.setImageResource(R.drawable.ic_volume_up_white_24dp);
                        }
                    }

                    @Override
                    public void onProgressTouchUp(long targetPosition) {
                        player.seekTo(targetPosition);
                        hideAllTouchTools();
                    }

                    @Override
                    public void onAlphaTouchUp() {
                        hideAllTouchTools();
                    }

                    @Override
                    public void onVolumeTouchUp() {
                        hideAllTouchTools();
                    }
                });
        exoPlayerView.setOnTouchListener(exoPlayerOnTouchListener);
    }

    private void hideAllTouchTools() {
        touchToolsLayout.setVisibility(View.GONE);
        progressTool.setVisibility(View.GONE);
        alphaTool.setVisibility(View.GONE);
        volumeTool.setVisibility(View.GONE);
    }

    private void startPlay(final String videoUrl) {
        runOnUiThread(() -> {
            if (mediaSource == null) {
                mediaSource = new ConcatenatingMediaSource(//播放一组视频
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
                        setPlayerHandle();//设置触摸监听
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
                            videoIndexCurrent = videoIndexNext;
                            int index = videoIndexNext % adapter.getItemCount();
                            adapter.notifyIndex(index);
                            recyclerView.smoothScrollToPosition(index);
                            String videoTitle = videoInfos.get(index).getTitle();
                            ExoPlayerActivity.this.videoTitle.setText(videoTitle);
                            Log.i(TAG, "当前播放的视频 -->标题 " + videoTitle + " videoIndexCurrent = " + index);
                            videoIndexNext++;
                            mediaSource.addMediaSource(getMediaSource(Uri.parse(adapter.getItem((videoIndexNext) % adapter.getItemCount()).getVideoUrl())));
                            Log.i(TAG, "下一个播放的视频 -->标题 " + videoTitle + " videoIndexNext = " + index);
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
        });
    }

    private void initLayout() {
        exoPlayerView = findViewById(R.id.exo_player);

        videoTitleLayout = findViewById(R.id.video_title_layout);
        videoTitle = findViewById(R.id.video_title);
        touchToolsLayout = findViewById(R.id.touch_tools_layout);
        progressTool = findViewById(R.id.progressTool);
        progressBar = findViewById(R.id.progressBar);
        currentProgress = findViewById(R.id.currentPosition);
        duration = findViewById(R.id.duration);

        alphaTool = findViewById(R.id.alphaTool);
        alpha = findViewById(R.id.alpha);
        alphaImg = findViewById(R.id.img_alpha);

        volumeTool = findViewById(R.id.volumeTool);
        volume = findViewById(R.id.volume);
        volumeImg = findViewById(R.id.img_volume);

        loading = findViewById(R.id.loading);
        loadingDrawable = new ProgressDrawable();
        loadingDrawable.setColor(ContextCompat.getColor(this, R.color.main_green));
        loading.setImageDrawable(loadingDrawable);

        viewSwitcher = findViewById(R.id.viewSwitcher);
        controllerLayout = findViewById(R.id.control_layout);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.picture_in_picture).setOnClickListener(this);
        findViewById(R.id.picture_in_picture).setOnClickListener(this);
        findViewById(R.id.switchScreen).setOnClickListener(this);

        tabLayout = findViewById(R.id.tablayout);
        findViewById(R.id.menu_search).setOnClickListener(this);
        menuArrow = findViewById(R.id.menu_expand_arrow);
        menuArrow.setOnClickListener(this);
        findViewById(R.id.menu_more).setOnClickListener(this);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.recommend));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.attention));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.popular));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.history));
        tabLayout.setSmoothScrollingEnabled(true);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.nestedScrollView));
        bottomSheetBehavior.setSkipCollapsed(false);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setPeekHeight(Px2DpUtil.dip2px(this, 50));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    menuArrow.setImageResource(R.drawable.ic_expand_arrow_down_white_24dp);
                    if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {//横平时隐藏视频的Title
                        videoTitleLayout.setVisibility(View.INVISIBLE);
                    }
                } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    if (isControllerVisiable) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }
                    videoTitleLayout.setVisibility(View.VISIBLE);
                    menuArrow.setImageResource(R.drawable.ic_expand_arrow_up_white_24dp);
                } else {
                    menuArrow.setImageResource(R.drawable.ic_expand_arrow_down_white_24dp);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    /**
     * 获取视频列表
     */
    private void refresh() {
        super.loading.show();
        handler.postDelayed(() -> BmobRequest.getInstance(App.getCtx()).getVideos(CMD_GET_VIDEOS), 1000);
    }

    /**
     * 刷新视频列表
     */
    private void refreshVideos() {
        if (adapter == null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            adapter = new VideoListAdapter(this, videoInfos);
            adapter.setOnItemClickListener((position, videoInfo) -> {
                if (position != videoIndexCurrent) {
                    mediaSource.clear();
                    videoIndexCurrent = position;
                    videoIndexNext = videoIndexCurrent;
                    player.setPlayWhenReady(true);
                    mediaSource.addMediaSource(getMediaSource(Uri.parse((videoInfo).getVideoUrl())));
                    player.prepare(mediaSource);
                    tabLayout.setScrollPosition(position % tabLayout.getTabCount(), (position % tabLayout.getTabCount()) - tabLayout.getSelectedTabPosition(), true);
                }
            });
            adapter.setContentObserver((count, object) -> viewSwitcher.setDisplayedChild(count == 0 ? 0 : 1));
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged(videoInfos);
        }
        if (videoInfos != null && videoInfos.size() > 0) {
            startPlay(videoInfos.get(0).getVideoUrl());
        }
    }

    @Override
    public void onMessage(ResponseInfo resp) {
        super.onMessage(resp);
        if (resp.getCode() == ResponseInfo.OK) {
            if (CMD_GET_VIDEOS.equals(resp.getCmd())) {
                super.loading.dismiss();
                videoInfos = resp.getVideoInfoList();
                refreshVideos();
            }
        } else {
            if (CMD_GET_VIDEOS.equals(resp.getCmd())) super.loading.dismiss();
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

    /**
     * title栏Y轴执行位移动画
     */
    private void translationYAnimatorForTitleLayout(int startTranslationY, int endTranslationY, int startAlpha, int endAlpha) {
        ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(videoTitleLayout, "translationY", startTranslationY, endTranslationY).setDuration(200);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(videoTitleLayout, "alpha", startAlpha, endAlpha);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.playTogether(translationAnimator, alphaAnimator);
        animatorSet.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                onBack(false);
                break;
            case R.id.picture_in_picture:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    hideBottomSheet(BottomSheetBehavior.STATE_HIDDEN, BottomSheetBehavior.STATE_EXPANDED);
                    isInPictureInPictureMode = false;
                } else {
                    enterPictureMode();
                    isInPictureInPictureMode = true;
                }
                break;
            case R.id.switchScreen:
                setRequestedOrientation(getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case R.id.menu_expand_arrow:
                if (bottomSheetBehavior != null) {
                    if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void hideBottomSheet(int stateHidden, int stateExpanded) {
        if (bottomSheetBehavior.getState() == stateHidden
                || bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.setState(stateExpanded);
        }
    }

    /**
     * 横竖屏切换时,更新触摸事件的View
     */
    private void updateVideoUIParams() {
        if (exoPlayerOnTouchListener != null) {
            exoPlayerOnTouchListener.updateVideoUIParmeras(exoPlayerView);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {//竖屏
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) exoPlayerView.getLayoutParams();
            lp.height = FrameLayout.LayoutParams.WRAP_CONTENT;
            exoPlayerView.setLayoutParams(lp);
            videoTitleLayout.setVisibility(View.VISIBLE);
            updateVideoUIParams();

        } else if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {//横屏
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) exoPlayerView.getLayoutParams();
            lp.height = FrameLayout.LayoutParams.MATCH_PARENT;
            exoPlayerView.setLayoutParams(lp);
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                videoTitleLayout.setVisibility(View.INVISIBLE);
            }
            updateVideoUIParams();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        this.isInPictureInPictureMode = isInPictureInPictureMode;
        //进入画中画模式时隐藏控制栏和标题
        if (isInPictureInPictureMode) {
            hideAllTouchTools();
            updatePictureController(true);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            registerPictureActionReceiver();
            Log.e(TAG, "进入画中画模式");
        } else {
            updatePictureController(false);
            unregisterReceiver(pictureActionReceiver);
            pictureActionReceiver = null;
            Log.e(TAG, "退出画中画模式");
        }
    }

    /**
     * 进出画中画模式，更新控制栏
     */
    private void updatePictureController(boolean isInPictureInPictureMode) {
        if (isInPictureInPictureMode) {
            controllerLayout.setVisibility(View.GONE);
            currentProgress.setVisibility(View.GONE);
            duration.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            videoTitleLayout.setVisibility(View.GONE);
        } else {
            controllerLayout.setVisibility(View.VISIBLE);
            currentProgress.setVisibility(View.VISIBLE);
            duration.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            videoTitleLayout.setVisibility(View.VISIBLE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void registerPictureActionReceiver() {
        pictureActionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null
                        || !ACTION_MEDIA_CONTROL.equals(intent.getAction())) {
                    return;
                }
                final int controlType = intent.getIntExtra(EXTRA_CONTROL_TYPE, 0);
                switch (controlType) {
                    case CONTROL_TYPE_PLAY:
                        play(player != null);
                        setPictureInPictureActions(R.drawable.ic_pause_24dp, getString(R.string.pause), CONTROL_TYPE_PAUSE);
                        break;
                    case CONTROL_TYPE_PAUSE:
                        pause();
                        setPictureInPictureActions(R.drawable.ic_play_arrow_24dp, getString(R.string.play), CONTROL_TYPE_PLAY);
                        break;
                    case CONTROL_TYPE_REMIND:
                        long pre = player.getCurrentPosition() - 10 * 1000;
                        long prePosition = pre < 0 ? 0 : pre;
                        player.seekTo(prePosition);
                        break;
                    case CONTROL_TYPE_FORWARD:
                        long target = player.getCurrentPosition() + 10 * 1000;
                        long position = target > player.getDuration() ? player.getDuration() : target;
                        player.seekTo(position);
                        break;
                }
            }
        };
        registerReceiver(pictureActionReceiver, new IntentFilter(ACTION_MEDIA_CONTROL));
    }

    /**
     * 进入画中画模式
     * Aspect ratio is too extreme (must be between 0.418410 and 2.390000).
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void enterPictureMode() {
        Rational aspectRatio = new Rational(3, 2);
        mPictureInPictureParamsBuilder.setAspectRatio(aspectRatio).build();
        enterPictureInPictureMode(mPictureInPictureParamsBuilder.build());
    }

    /**
     * 设置画中画按钮
     */
    void setPictureInPictureActions(@DrawableRes int iconId, String title, int controlType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final ArrayList<RemoteAction> actions = new ArrayList<>();
            //播放/暂停按钮
            final PendingIntent intentPlayOrPause = getBroadcast(controlType);
            final PendingIntent intentRemind = getBroadcast(CONTROL_TYPE_REMIND);
            final PendingIntent intentForward = getBroadcast(CONTROL_TYPE_FORWARD);
            final Icon iconRewind = Icon.createWithResource(this, R.drawable.exo_controls_rewind);
            final Icon iconForward = Icon.createWithResource(this, R.drawable.exo_controls_fastforward);
            final Icon iconPlay = Icon.createWithResource(this, iconId);
            actions.add(new RemoteAction(iconRewind, getString(R.string.fast_rewind), getString(R.string.fast_rewind), intentRemind));//快退
            actions.add(new RemoteAction(iconPlay, title, title, intentPlayOrPause));//播放，暂停
            actions.add(new RemoteAction(iconForward, getString(R.string.fast_forward), getString(R.string.fast_forward), intentForward));//快进
            mPictureInPictureParamsBuilder.setActions(actions);
            setPictureInPictureParams(mPictureInPictureParamsBuilder.build());
        }
    }

    private PendingIntent getBroadcast(int controlType) {
        return PendingIntent.getBroadcast(
                this,
                controlType,
                new Intent(ACTION_MEDIA_CONTROL).putExtra(EXTRA_CONTROL_TYPE, controlType),
                0);
    }

    private void showLoading() {
        if (loading != null && loadingDrawable != null) {
            loadingDrawable.start();
            loading.setVisibility(View.VISIBLE);
            if (exoPlayerOnTouchListener != null) {//视频加载过程中 禁止触摸
                exoPlayerOnTouchListener.setCanTouch(false);
            }
        }
    }

    private void dismissLoading() {
        if (loading != null && loadingDrawable != null) {
            loadingDrawable.stop();
            loading.setVisibility(View.GONE);
            if (exoPlayerOnTouchListener != null) {//视频加载完成 允许手势控制亮度、音量
                exoPlayerOnTouchListener.setCanTouch(true);
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (volumeTool != null && volumeTool.getVisibility() == View.VISIBLE) {
                volumeTool.setVisibility(View.GONE);
            }
        }
    };

    private void registerVolumeReceiver() {
        volumeChangeReceiver = new VolumeChangeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_VOLUME_CHANGED);
        registerReceiver(volumeChangeReceiver, filter);
    }

    private void registerNetWorkReceiver() {
        netWorkReceiver = new NetWorkReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(netWorkReceiver, filter);
    }

    private class VolumeChangeReceiver extends BroadcastReceiver {

        private int maxVolume;
        private AudioManager audiomanager;

        public VolumeChangeReceiver() {
            audiomanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audiomanager != null) {
                maxVolume = audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 获取系统最大音量
            }
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // 获取当前值
            if (ACTION_VOLUME_CHANGED.equals(intent.getAction())) {
                int currentVolume = audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);
                touchToolsLayout.setVisibility(View.VISIBLE);
                progressTool.setVisibility(View.GONE);
                alphaTool.setVisibility(View.GONE);
                volumeTool.setVisibility(View.VISIBLE);
                double div = ArithTool.div(currentVolume, maxVolume, 2);
                volume.setText((int) (div * 100) + "%");
                if (div <= 0) {
                    volumeImg.setImageResource(R.drawable.ic_volume_off_white_24dp);
                } else {
                    volumeImg.setImageResource(R.drawable.ic_volume_up_white_24dp);
                }
                handler.removeCallbacksAndMessages(null);
                handler.sendEmptyMessageDelayed(0, 2000);
            }
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

                            player.setPlayWhenReady(true);
                            loadControler.shouldContinueLoading(true);
                            player.seekTo(player.getCurrentPosition());
                            Log.i(TAG, getConnectionType(info.getType()) + "连上");

                        } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                            alertMobileDataDialog();
                            Log.i(TAG, getConnectionType(info.getType()) + "连上");
                        }
                    } else {
                        Log.i(TAG, getConnectionType(info.getType()) + "断开");
                    }
                }
            }
        }
    }

    /**
     * 可以在这里判断 当前是否处于无线网环境 , 可以提示用户是否耗费手机流量播放视频
     */
    private void alertMobileDataDialog() {
        if (!Utils.isWifi(this) && !isUsePhoneData) {
            if (alertDialog == null) {
                alertDialog = new AlertDialog.Builder(ExoPlayerActivity.this).create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setCancelable(false);
                alertDialog.setTitle(R.string.message_alert);
                alertDialog.setMessage(getString(R.string.net_message_alert));
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, which) -> {
                    dialog.dismiss();
                    isUsePhoneData = false;
                    player.setPlayWhenReady(false);
                    loadControler.shouldContinueLoading(false);
                });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.confirm), (dialog, which) -> {
                    dialog.dismiss();
                    isUsePhoneData = true;
                    player.setPlayWhenReady(true);
                    loadControler.shouldContinueLoading(true);
                });
            }
            dismissLoading();
            player.setPlayWhenReady(false);
            loadControler.shouldContinueLoading(false);
            if (!alertDialog.isShowing()) {
                alertDialog.show();
            }
        } else {
            player.setPlayWhenReady(true);
            loadControler.shouldContinueLoading(true);
        }
    }

    private void onBack(boolean isOnBackPressed) {
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            if (isOnBackPressed && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                enterPictureMode();
                return;
            }
            if (player != null) {
                player.stop();
                player.release();
            }
            super.onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        onBack(true);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        initWindowStyle();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        if (videoInfos == null) {
            refresh();
            return;
        }
        play(player != null);
    }

    /**
     * 进入画中画模式后,onPause方法被调用，但是不会调用onStop方法
     */
    @Override
    protected void onStop() {
        Log.e(TAG, "onStop");
        pause();
        super.onStop();
    }

    /**
     * 开始播放
     */
    private void play(boolean b) {
        if (b) {
            alertMobileDataDialog();
        }
    }

    /**
     * 暂停播放
     */
    private void pause() {
        if (player != null) {
            player.setPlayWhenReady(false);
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
        if (volumeChangeReceiver != null) {
            unregisterReceiver(volumeChangeReceiver);
        }
        if (pictureActionReceiver != null) {
            unregisterReceiver(pictureActionReceiver);
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 阿里云视频点播 : 通过视频ID获取视频播放URl
     */
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
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("sxt", e.toString());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) {
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
