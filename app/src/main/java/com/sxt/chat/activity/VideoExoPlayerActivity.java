package com.sxt.chat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.drm.DrmStore;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
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
import com.sxt.chat.adapter.VideoListAdapter;
import com.sxt.chat.base.BaseActivity;
import com.sxt.chat.base.BaseRecyclerAdapter;
import com.sxt.chat.dialog.ProgressDrawable;
import com.sxt.chat.explayer.MyLoadControl;
import com.sxt.chat.json.VideoObject;
import com.sxt.chat.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 11837 on 2018/6/11.
 */

public class VideoExoPlayerActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = "Video";
    private SimpleExoPlayer player;
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private ImageView loading;
    private View drawer;

    private String[] urls = App.getCtx().getResources().getStringArray(R.array.videos);
    private String[] titles = App.getCtx().getResources().getStringArray(R.array.videos_name);
    private String img_url_1 = "http://f.hiphotos.baidu.com/image/pic/item/42166d224f4a20a403c7e0319c529822730ed06f.jpg";
    private String img_url_2 = "http://h.hiphotos.baidu.com/image/pic/item/43a7d933c895d14332bd91df7ff082025baf0706.jpg";
    private NetWorkReceiver netWorkReceiver;
    private MyLoadControl loadControler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exoplayer);

        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.quality).setOnClickListener(this);
        findViewById(R.id.select).setOnClickListener(this);
        drawerLayout = findViewById(R.id.drawerLayout);
        recyclerView = findViewById(R.id.recyclerView);
        drawer = findViewById(R.id.drawer);
        loading = findViewById(R.id.loading);

        ProgressDrawable drawable = new ProgressDrawable();
        drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(this, R.color.main_blue), PorterDuff.Mode.SRC_IN));
        loading.setImageDrawable(drawable);

        setDrawer();
        registerNetWorkReceiver();

        final PlayerView exoPlayerView = findViewById(R.id.exoplayer);
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        // 2. Create a default LoadControl
        loadControler = new MyLoadControl();
        // 3. Create the player
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControler);
        exoPlayerView.setPlayer(player);

        player.addListener(new Player.DefaultEventListener() {

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
                    if (loading.getVisibility() != View.VISIBLE) {
                        loading.setVisibility(View.VISIBLE);
                    }
                    Log.i(TAG, "playbackState == DrmStore.Playback.PAUSE");
                } else if (playbackState == DrmStore.Playback.RESUME) {//继续播放(加载完成)
                    if (loading.getVisibility() == View.VISIBLE) {
                        loading.setVisibility(View.GONE);
                    }
                    Log.i(TAG, "playbackState == DrmStore.Playback.RESUME");
                } else if (playbackState == DrmStore.Playback.STOP) {
                    if (loading.getVisibility() == View.VISIBLE) {
                        loading.setVisibility(View.GONE);
                    }
                    Log.i(TAG, "playbackState == DrmStore.Playback.STOP");
                }
                if (player.getCurrentPosition() == player.getDuration()) {
                    Log.i(TAG, "播放完成");
                }
                Log.i(TAG, "player.getCurrentPosition() = " + player.getCurrentPosition() + " , player.getContentPosition() " + player.getContentPosition() + " , player.getDuration() = " + player.getDuration());
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                super.onPlayerError(error);
                Log.i(TAG, "onPlayerError  error = " + error);
            }
        });
        // Prepare the player with the source.
        player.setPlayWhenReady(true);
//        player.setRepeatMode(Player.REPEAT_MODE_ONE);
        player.prepare(new ConcatenatingMediaSource(//播放一组视频
                getMediaSource(Uri.parse(urls[4])), getMediaSource(Uri.parse(urls[3])),
                getMediaSource(Uri.parse(urls[2])), getMediaSource(Uri.parse(urls[1]))));
    }

    private void registerNetWorkReceiver() {
        netWorkReceiver = new NetWorkReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(netWorkReceiver, filter);
    }

    private void setDrawer() {
        List<VideoObject> videoObjects = new ArrayList<>();
        VideoObject videoObject;
        for (int i = 0; i < urls.length; i++) {
            videoObject = new VideoObject();
            videoObject.setVideo_img_url(getString(R.string.test_img_url));
            videoObject.setVideo_url(urls[i]);
            videoObject.setTitle(titles[i]);
            videoObjects.add(videoObject);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        VideoListAdapter adapter = new VideoListAdapter(this, videoObjects);
        recyclerView.setAdapter(adapter);
        adapter.setOnClickListener(new BaseRecyclerAdapter.OnClickListener() {
            @Override
            public void onClick(int position, RecyclerView.ViewHolder holder, Object object) {
                drawerLayout.closeDrawers();
                player.stop(true);
                player.setPlayWhenReady(true);
                player.prepare(getMediaSource(Uri.parse(((VideoObject) object).getVideo_url())));
            }
        });
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
                drawerLayout.openDrawer(drawer);
                break;
            default:
                break;
        }
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
        if (player != null) {
            loadControler.shouldContinueLoading(NetworkUtils.isWifi(this));
            player.seekTo(player.getCurrentPosition());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.stop();
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
}
