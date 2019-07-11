package com.sxt.chat.fragment;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.sxt.banner.BannerConfig;
import com.sxt.banner.BannerView;
import com.sxt.banner.loader.UILoaderInterface;
import com.sxt.chat.App;
import com.sxt.chat.R;
import com.sxt.chat.activity.BannerDetailActivity;
import com.sxt.chat.activity.MainActivity;
import com.sxt.chat.adapter.BannerListAdapter;
import com.sxt.chat.adapter.GalleryAdapter;
import com.sxt.chat.adapter.NormalCardListAdapter;
import com.sxt.chat.adapter.NormalListAdapter;
import com.sxt.chat.adapter.config.NoScrollLinearLayoutManaget;
import com.sxt.chat.base.BaseBottomSheetFragment;
import com.sxt.chat.base.LazyFragment;
import com.sxt.chat.fragment.bottonsheet.GalleryBottomSheetFragment;
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
    private RecyclerView recyclerViewGallary;
    private ViewSwitcher viewSwitcherBanner;
    private ViewSwitcher viewSwitcherTop;
    private ViewSwitcher viewSwitcherCenter;
    private ViewSwitcher viewSwitcherBottom;
    private ViewSwitcher viewSwitcherGallary;
    private NormalListAdapter adapterTop;
    private BannerListAdapter adapterCenter;
    private NormalCardListAdapter adapterBottom;
    private GalleryAdapter adapterGallary;
    private BannerView bannerView;

    private final String CMD_GET_BANNER = this.getClass().getName() + "CMD_GET_BANNER";
    private final String CMD_GET_ROOM = this.getClass().getName() + "CMD_GET_ROOM";
    private final String CMD_GET_HJH = this.getClass().getName() + "CMD_GET_HJH";
    private final String CMD_GET_GALLARY = this.getClass().getName() + "CMD_GET_GALLARY";

    @Override
    protected int getDisplayView(LayoutInflater inflater, ViewGroup container) {
        return R.layout.fragment_home_page;
    }

    @Override
    protected void initView() {
        swipeRefreshLayout = contentView.findViewById(R.id.swipeRefreshLayout);
        NestedScrollView nestedScrollView = contentView.findViewById(R.id.nestedScrollView);
        recyclerViewTop = contentView.findViewById(R.id.top_recyclerView);
        recyclerViewCenter = contentView.findViewById(R.id.center_recyclerView);
        recyclerViewBottom = contentView.findViewById(R.id.bottom_recyclerView);
        recyclerViewGallary = contentView.findViewById(R.id.gallery_recyclerView);
        viewSwitcherBanner = contentView.findViewById(R.id.banner_viewSwitcher);
        viewSwitcherTop = contentView.findViewById(R.id.top_viewSitcher);
        viewSwitcherCenter = contentView.findViewById(R.id.center_viewSwitcher);
        viewSwitcherBottom = contentView.findViewById(R.id.bottom_viewSwitcher);
        viewSwitcherGallary = contentView.findViewById(R.id.gallery_viewSwitcher);

        recyclerViewTop.setLayoutManager(new NoScrollLinearLayoutManaget(activity, LinearLayoutManager.HORIZONTAL, true).setCanScrollVertically(false));
        recyclerViewCenter.setLayoutManager(new NoScrollLinearLayoutManaget(activity, LinearLayoutManager.HORIZONTAL, false).setCanScrollVertically(false));
        recyclerViewBottom.setLayoutManager(new NoScrollLinearLayoutManaget(activity, LinearLayoutManager.HORIZONTAL, false).setCanScrollVertically(false));
        recyclerViewGallary.setLayoutManager(new StaggeredGridLayoutManager(5, LinearLayoutManager.HORIZONTAL));

        recyclerViewTop.setNestedScrollingEnabled(false);
        recyclerViewCenter.setNestedScrollingEnabled(false);
        recyclerViewBottom.setNestedScrollingEnabled(false);
        recyclerViewGallary.setNestedScrollingEnabled(false);

        //解决SwipeRefreshLayout 嵌套滑动冲突
        AppBarLayout appBarLayout = contentView.findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {

            if (verticalOffset >= 0) {
                swipeRefreshLayout.setEnabled(true);
            } else {
                swipeRefreshLayout.setEnabled(false);
            }
        });
        //初始化刷新控件
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(activity, R.color.main_blue), ContextCompat.getColor(activity, R.color.red), ContextCompat.getColor(activity, R.color.line_yellow), ContextCompat.getColor(activity, R.color.main_green), ContextCompat.getColor(activity, R.color.red));
        swipeRefreshLayout.setOnRefreshListener(this::refreshData);
        //设置滑动监听,使得底部tab栏竖直滑动
        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            Log.e("scrollY", String.format("oldScrollY = %s ; scrollY = %s", oldScrollY, scrollY));
            MainActivity activity = (MainActivity) HomePageFragment.this.activity;
            activity.setBottomBarTranslateY(scrollY, scrollY > oldScrollY);
        });
    }

    @Override
    protected void loadData() {
        super.loadData();
        refresh();
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

    private void refresh() {
        if (swipeRefreshLayout != null)
            //为什么要手动刷新? , 因为swipRefreshLayout初始化并不会调用onRefresh
            swipeRefreshLayout.post(() -> {
                swipeRefreshLayout.setRefreshing(true);
                refreshData();
            });
    }

    private void refreshData() {
        BmobRequest.getInstance(activity).getBannersByType("0", CMD_GET_BANNER);
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
                    .setOnBannerListener(position -> {
                        Intent intent = new Intent(context, BannerDetailActivity.class);
                        Bundle bundle = new Bundle();
                        RoomInfo roomInfo = new RoomInfo();
                        roomInfo.setHome_name(banners.get(position).getDescription());
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
                    })
                    .setDelayTime(3000)
                    .isAutoPlay(true);
        } else {
            bannerView.update(banners);
        }
        bannerView.start();
    }

    private void refreshRoom(List<RoomInfo> list) {
        if (adapterTop == null || adapterBottom == null) {
            adapterTop = new NormalListAdapter(activity, list);
            adapterBottom = new NormalCardListAdapter(activity, list);

            viewSwitcherTop.setDisplayedChild(1);
            viewSwitcherCenter.setDisplayedChild(1);
            viewSwitcherBottom.setDisplayedChild(1);

            recyclerViewTop.setAdapter(adapterTop);
            recyclerViewBottom.setAdapter(adapterBottom);
        } else {
            adapterTop.notifyDataSetChanged(list);
            adapterBottom.notifyDataSetChanged(list);
        }
    }

    @Override
    public void onMessage(ResponseInfo resp) {
        if (ResponseInfo.OK == resp.getCode()) {
            if (CMD_GET_BANNER.equals(resp.getCmd())) {
                refreshBanner(resp.getBannerInfos());
                BmobRequest.getInstance(activity).getRoomList(10, 0, CMD_GET_ROOM);
            } else if (CMD_GET_ROOM.equals(resp.getCmd())) {
                refreshRoom(resp.getRoomInfoList());
                BmobRequest.getInstance(activity).getBannersByType("1", CMD_GET_HJH);
            } else if (CMD_GET_HJH.equals(resp.getCmd())) {
                if (adapterCenter == null) {
                    adapterCenter = new BannerListAdapter(activity, resp.getBannerInfos());
                    recyclerViewCenter.setAdapter(adapterCenter);
                } else {
                    adapterCenter.notifyDataSetChanged(resp.getBannerInfos());
                }
                BmobRequest.getInstance(activity).getBanner(10, 0, CMD_GET_GALLARY);
            } else if (CMD_GET_GALLARY.equals(resp.getCmd())) {
                refreshGallery(resp.getBannerInfos());
                swipeRefreshLayout.setRefreshing(false);
            }
        } else {
            swipeRefreshLayout.setRefreshing(false);
            ToastUtil.showToast(activity, resp.getError());
        }
    }

    private void refreshGallery(final List<Banner> list) {
        if (list == null || list.size() == 0) {
            viewSwitcherGallary.setDisplayedChild(0);
        } else {
            viewSwitcherGallary.setDisplayedChild(1);
        }
        if (adapterGallary == null) {
            recyclerViewGallary.setLayoutManager(new StaggeredGridLayoutManager(5, LinearLayoutManager.HORIZONTAL));
            adapterGallary = new GalleryAdapter(activity, list);
            viewSwitcherGallary.setDisplayedChild(1);
            recyclerViewGallary.setNestedScrollingEnabled(false);
            recyclerViewGallary.setAdapter(adapterGallary);
            adapterGallary.setOnItemClickListener((position, banner) -> {
                showBottomSheet(banner);
            });
        } else {
            adapterGallary.notifyDataSetChanged(list);
        }
    }

    private void showBottomSheet(Banner banner) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Prefs.KEY_BANNER_INFO, banner);
        BaseBottomSheetFragment sheetFragment = new GalleryBottomSheetFragment().setOnBottomSheetDialogCreateListener(new BaseBottomSheetFragment.OnBottomSheetDialogCreateListener() {
            @Override
            public void onBottomSheetDialogCreate(BaseBottomSheetFragment baseBottomSheetFragment, BottomSheetDialog bottomSheetDialog, View contentView) {
                int peekHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, App.getCtx().getResources().getDisplayMetrics());
                baseBottomSheetFragment
                        .setCancelableOutside(false)
                        .setPeekHeight(peekHeight)
                        .setBottomSheetState(BottomSheetBehavior.STATE_EXPANDED)
                        .setBackgtoundColor(Color.TRANSPARENT);
            }
        });
        sheetFragment.setArguments(bundle);
        sheetFragment.show(getFragmentManager());
    }
}
