package com.sxt.chat.fragment;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.sxt.banner.BannerConfig;
import com.sxt.banner.BannerView;
import com.sxt.banner.Transformer;
import com.sxt.banner.listener.OnBannerListener;
import com.sxt.banner.loader.UILoaderInterface;
import com.sxt.banner.transformer.ScaleInTransformer;
import com.sxt.chat.R;
import com.sxt.chat.activity.RoomDetailActivity;
import com.sxt.chat.adapter.HomeAdapter;
import com.sxt.chat.base.LazyFragment;
import com.sxt.chat.json.Banner;
import com.sxt.chat.json.ResponseInfo;
import com.sxt.chat.json.RoomInfo;
import com.sxt.chat.utils.Prefs;
import com.sxt.chat.utils.ToastUtil;
import com.sxt.chat.ws.BmobRequest;

import java.util.List;

/**
 * Created by 11837 on 2018/4/22.
 */

public class GithubFragment extends LazyFragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private BannerView bannerView;
    private ViewSwitcher viewSwitcherBanner;
    private RecyclerView recyclerView;
    private HomeAdapter adapter;
    private ViewSwitcher viewSwitcher;

    private final String CMD_GET_ROOM_LIST = this.getClass().getName() + "CMD_GET_ROOM_LIST";
    private final String CMD_GET_Banner_LIST = this.getClass().getName() + "CMD_GET_Banner_LIST";

    @Override
    protected int getDisplayView(LayoutInflater inflater, ViewGroup container) {
        return R.layout.fragment_github;
    }

    @Override
    protected void initView() {
        swipeRefreshLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) contentView.findViewById(R.id.recyclerView);
        viewSwitcherBanner = (ViewSwitcher) contentView.findViewById(R.id.banner_viewSwitcher);
        viewSwitcher = (ViewSwitcher) contentView.findViewById(R.id.viewSitcher);
        recyclerView.setLayoutManager(new GridLayoutManager(activity, 2));
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorAccent, R.color.main_blue, R.color.main_green);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                refresh();
            }
        });

        //解决SwipeRefreshLayout 嵌套滑动冲突
        AppBarLayout appBarLayout = (AppBarLayout) contentView.findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (verticalOffset >= 0) {
                    swipeRefreshLayout.setEnabled(true);
                } else {
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });
    }

    private void refresh() {
        BmobRequest.getInstance(activity).getBanner(50, 0, CMD_GET_Banner_LIST);
    }

    private int getPageMargin() {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
    }

    private void refreshList(List<RoomInfo> list) {
        if (adapter == null) {
            adapter = new HomeAdapter(activity, list);
            viewSwitcher.setDisplayedChild(1);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged(list);
        }
    }

    private void refreshBanner(final List<Banner> banners) {
//        imgs.add("http://bmob-cdn-18541.b0.upaiyun.com/2018/05/22/dd5ca0a0400a87b7800ae9a6f107b562.jpg");
//        imgs.add("http://bmob-cdn-18541.b0.upaiyun.com/2018/05/22/13cecf96407145708071d88037547c7f.jpg");
//        imgs.add("http://bmob-cdn-18541.b0.upaiyun.com/2018/05/22/20799e5a4012706c80f83276a47b7f89.jpg");
        if (bannerView == null) {
            viewSwitcherBanner.setDisplayedChild(1);
            bannerView = contentView.findViewById(R.id.banner);
            bannerView.setBannerStyle(BannerConfig.NOT_INDICATOR)
                    .setIndicatorGravity(BannerConfig.CENTER)
                    .setBannerAnimation(Transformer.Default)
                    .setOffscreenPageLimit(3)
                    .setPageMargin(getPageMargin() / 8)
                    .setViewPagerMargins(getPageMargin(), 0, getPageMargin(), 0)
                    .setPageTransformer(false, new ScaleInTransformer())
                    .setUILoader(banners, new UILoaderInterface<View>() {

                        @Override
                        public void displayView(Context context, Object path, View displayView) {
                            Glide.with(context).load(((Banner) path).getUrl())
                                    .placeholder(R.mipmap.ic_banner_placeholder)
                                    .error(R.mipmap.ic_banner_placeholder)
                                    .into((ImageView) displayView.findViewById(R.id.img));
                        }

                        @Override
                        public View createView(Context context) {
                            View view = LayoutInflater.from(context).inflate(R.layout.item_banner, null, false);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                view.setTransitionName("shareView");
                            }
                            return view;
                        }
                    })
                    .setOnBannerListener(new OnBannerListener() {
                        @Override
                        public void OnBannerClick(int position) {
                            Intent intent = new Intent(context, RoomDetailActivity.class);
                            Bundle bundle = new Bundle();
                            RoomInfo roomInfo = new RoomInfo();
                            roomInfo.setHome_name("房间详情");
                            roomInfo.setRoom_url(banners.get(position).getUrl());
                            bundle.putSerializable(Prefs.ROOM_INFO, roomInfo);
                            intent.putExtra(Prefs.ROOM_INFO, bundle);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                context.startActivity(intent,
                                        ActivityOptions.makeSceneTransitionAnimation
                                                ((Activity) context, bannerView, "shareView").toBundle());
                            } else {
                                context.startActivity(intent);
                            }
                        }
                    })
                    .setDelayTime(3000)
                    .isAutoPlay(true);
        } else {
            bannerView.update(banners);
        }
        bannerView.start();
    }

    @Override
    public void onMessage(ResponseInfo resp) {
        if (ResponseInfo.OK == resp.getCode()) {
            if (CMD_GET_Banner_LIST.equals(resp.getCmd())) {
                refreshBanner(resp.getBannerInfos());
                BmobRequest.getInstance(activity).getRoomList(50, 0, CMD_GET_ROOM_LIST);
            } else if (CMD_GET_ROOM_LIST.equals(resp.getCmd())) {
                refreshList(resp.getRoomInfoList());
                swipeRefreshLayout.setRefreshing(false);
            }
        } else {
            swipeRefreshLayout.setRefreshing(false);
            ToastUtil.showToast(activity, resp.getError());
        }
    }

    @Override
    protected void checkUserVisibleHint(boolean isVisibleToUser) {
        super.checkUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //开始轮播
            if (bannerView != null) {
                bannerView.startAutoPlay();
            }
        } else {
            //结束轮播
            if (bannerView != null) {
                bannerView.stopAutoPlay();
            }
        }
    }
}
