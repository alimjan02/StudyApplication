package com.sxt.chat.activity;

import android.content.Intent;
import android.drm.DrmStore;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
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
import com.sxt.chat.App;
import com.sxt.chat.R;
import com.sxt.chat.base.BaseActivity;

/**
 * Created by 11837 on 2018/6/11.
 */

public class VideoExoPlayerActivity extends BaseActivity implements View.OnClickListener {

    private String URL1 = "https://media.w3.org/2010/05/sintel/trailer.mp4";
    private String URL2 = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";

    private String img_url_1 = "http://f.hiphotos.baidu.com/image/pic/item/42166d224f4a20a403c7e0319c529822730ed06f.jpg";
    private String img_url_2 = "http://h.hiphotos.baidu.com/image/pic/item/43a7d933c895d14332bd91df7ff082025baf0706.jpg";
    private SimpleExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exoplayer);

        PlayerView exoPlayerView = findViewById(R.id.exoplayer);
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        // 2. Create a default LoadControl
        LoadControl loadControl = new DefaultLoadControl();
        // 3. Create the player
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        exoPlayerView.setPlayer(player);

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
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(URL1));
        player.addListener(new Player.DefaultEventListener() {

            @Override
            public void onLoadingChanged(boolean isLoading) {
                super.onLoadingChanged(isLoading);
                Log.i("Video", "onLoadingChanged  isLoading = " + isLoading);
                if (isLoading) {
                    if (!loading.isShowing()) {
                        loading.show();
                    }
                } else {
                    loading.dismiss();
                    player.seekTo(player.getCurrentPosition());
                }
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                super.onPlayerStateChanged(playWhenReady, playbackState);
                Log.i("Video", "onPlayerStateChanged playWhenReady = " + playWhenReady);
                if (playbackState == DrmStore.Playback.START) {
                    Log.i("Video", "playbackState == DrmStore.Playback.START");
                } else if (playbackState == DrmStore.Playback.PAUSE) {
                    Log.i("Video", "playbackState == DrmStore.Playback.PAUSE");
                } else if (playbackState == DrmStore.Playback.RESUME) {
                    Log.i("Video", "playbackState == DrmStore.Playback.RESUME");
                } else if (playbackState == DrmStore.Playback.STOP) {
                    Log.i("Video", "playbackState == DrmStore.Playback.STOP");
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                super.onPlayerError(error);
                Log.i("Video", "onPlayerError  error = " + error);
            }

            @Override
            public void onPositionDiscontinuity(int reason) {
                super.onPositionDiscontinuity(reason);
                Log.i("Video", "onPositionDiscontinuity");
            }

            @Override
            public void onSeekProcessed() {
                super.onSeekProcessed();
                Log.i("Video", "onSeekProcessed");
            }
        });
        // Prepare the player with the source.
        player.prepare(videoSource);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.quality).setOnClickListener(this);
        findViewById(R.id.select).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_header_small://个人中心
            case R.id.img_header_large:
                startActivity(new Intent(this, BasicInfoActivity.class));
                break;

            case R.id.back:
                if (player != null) {
                    player.stop();
                    player.release();
                }
                finish();
                break;
            case R.id.quality:
                Toast("切换清晰度");
                break;
            case R.id.select:
                Toast("切换视频");
                break;
            default:
                break;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            initWindowStyle();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stop();
            player.release();
        }
    }
}
