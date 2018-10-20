package com.sxt.chat.fragment;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
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
import com.sxt.chat.adapter.NormalCardListAdapter;
import com.sxt.chat.adapter.NormalListAdapter;
import com.sxt.chat.adapter.config.NoScrollLinearLayoutManaget;
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

public class HomePageFragment extends LazyFragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewTop;
    private RecyclerView recyclerViewCenter;
    private RecyclerView recyclerViewBottom;
    private ViewSwitcher viewSwitcherBanner;
    private ViewSwitcher viewSwitcherTop;
    private ViewSwitcher viewSwitcherCenter;
    private ViewSwitcher viewSwitcherBottom;
    private NormalListAdapter adapterTop;
    private NormalListAdapter adapterCenter;
    private NormalCardListAdapter adapterBottom;
    private BannerView bannerView;

    private final String CMD_GET_ROOM_LIST = this.getClass().getName() + "CMD_GET_ROOM_LIST";
    private final String CMD_GET_Banner_LIST = this.getClass().getName() + "CMD_GET_Banner_LIST";

    @Override
    protected int getDisplayView(LayoutInflater inflater, ViewGroup container) {
        return R.layout.fragment_home_page;
    }

    @Override
    protected void initView() {
        swipeRefreshLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.swipeRefreshLayout);
        recyclerViewTop = (RecyclerView) contentView.findViewById(R.id.center_recyclerView);
        recyclerViewCenter = (RecyclerView) contentView.findViewById(R.id.bottom_recyclerView);
        recyclerViewBottom = (RecyclerView) contentView.findViewById(R.id.last_recyclerView);
        viewSwitcherBanner = (ViewSwitcher) contentView.findViewById(R.id.banner_viewSwitcher);
        viewSwitcherTop = (ViewSwitcher) contentView.findViewById(R.id.center_viewSitcher);
        viewSwitcherCenter = (ViewSwitcher) contentView.findViewById(R.id.bottom_viewSwitcher);
        viewSwitcherBottom = (ViewSwitcher) contentView.findViewById(R.id.last_viewSitcher);

        recyclerViewTop.setLayoutManager(new NoScrollLinearLayoutManaget(activity, LinearLayoutManager.HORIZONTAL, false).setCanScrollVertically(false));
        recyclerViewCenter.setLayoutManager(new NoScrollLinearLayoutManaget(activity, LinearLayoutManager.HORIZONTAL, false).setCanScrollVertically(false));
        recyclerViewBottom.setLayoutManager(new NoScrollLinearLayoutManaget(activity, LinearLayoutManager.HORIZONTAL, false).setCanScrollVertically(false));

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
        BmobRequest.getInstance(activity).getBanner(50, 6, CMD_GET_Banner_LIST);
    }

    private int getPageMargin() {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
    }

    private void refreshBanner(final List<Banner> banners) {
        if (bannerView == null) {
            viewSwitcherBanner.setDisplayedChild(1);
            bannerView = contentView.findViewById(R.id.banner);
            bannerView.setBannerStyle(BannerConfig.NOT_INDICATOR)
                    .setIndicatorGravity(BannerConfig.CENTER)
                    .setOffscreenPageLimit(3)
                    .setPageMargin(getPageMargin() / 4)
                    .setViewPagerMargins(getPageMargin() * 3, 0, getPageMargin() * 3, 0)
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

    private void refreshList(List<RoomInfo> list) {
        if (adapterTop == null || adapterCenter == null || adapterBottom == null) {
            adapterTop = new NormalListAdapter(activity, list);
            adapterCenter = new NormalListAdapter(activity, list);
            adapterBottom = new NormalCardListAdapter(activity, list);

            viewSwitcherTop.setDisplayedChild(1);
            viewSwitcherCenter.setDisplayedChild(1);
            viewSwitcherBottom.setDisplayedChild(1);

            recyclerViewTop.setNestedScrollingEnabled(false);
            recyclerViewCenter.setNestedScrollingEnabled(false);
            recyclerViewBottom.setNestedScrollingEnabled(false);

            recyclerViewTop.setAdapter(adapterTop);
            recyclerViewCenter.setAdapter(adapterCenter);
            recyclerViewBottom.setAdapter(adapterBottom);
        } else {
            adapterTop.notifyDataSetChanged(list);
            adapterCenter.notifyDataSetChanged(list);
            adapterBottom.notifyDataSetChanged(list);
        }
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
