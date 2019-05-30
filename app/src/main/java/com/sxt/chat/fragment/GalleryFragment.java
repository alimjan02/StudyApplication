package com.sxt.chat.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.sxt.banner.loader.UILoaderInterface;
import com.sxt.banner.transformer.ScaleInTransformer;
import com.sxt.chat.R;
import com.sxt.chat.activity.RoomDetailActivity;
import com.sxt.chat.adapter.GalleryAdapter;
import com.sxt.chat.base.BaseBottomSheetFragment;
import com.sxt.chat.base.LazyFragment;
import com.sxt.chat.fragment.bottonsheet.GallaryBottomSheetFragment;
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

public class GalleryFragment extends LazyFragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private BannerView bannerView;
    private ViewSwitcher viewSwitcherBanner;
    private RecyclerView recyclerView;
    private GalleryAdapter adapter;
    private ViewSwitcher viewSwitcher;

    private final String CMD_GET_ROOM_LIST = this.getClass().getName() + "CMD_GET_ROOM_LIST";
    private final String CMD_GET_Banner_LIST = this.getClass().getName() + "CMD_GET_Banner_LIST";

    @Override
    protected int getDisplayView(LayoutInflater inflater, ViewGroup container) {
        return R.layout.fragment_gallary;
    }

    @Override
    protected void initView() {
        swipeRefreshLayout = contentView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = contentView.findViewById(R.id.recyclerView);
        viewSwitcherBanner = contentView.findViewById(R.id.banner_viewSwitcher);
        viewSwitcher = contentView.findViewById(R.id.viewSitcher);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(activity, R.color.main_blue), ContextCompat.getColor(activity, R.color.red), ContextCompat.getColor(activity, R.color.line_yellow), ContextCompat.getColor(activity, R.color.main_green), ContextCompat.getColor(activity, R.color.red));
        swipeRefreshLayout.setOnRefreshListener(this::refresh);
        swipeRefreshLayout.post(() -> {
            swipeRefreshLayout.setRefreshing(true);
            refresh();
        });

        //解决SwipeRefreshLayout 嵌套滑动冲突
        AppBarLayout appBarLayout = contentView.findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {

            if (verticalOffset >= 0) {
                swipeRefreshLayout.setEnabled(true);
            } else {
                swipeRefreshLayout.setEnabled(false);
            }
        });
    }

    private void refresh() {
        BmobRequest.getInstance(activity).getBannersByType("1", CMD_GET_Banner_LIST);
    }

    private int getPageMargin() {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
    }

    private void refreshList(List<Banner> list) {
        if (adapter == null) {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(5, LinearLayoutManager.HORIZONTAL));
            adapter = new GalleryAdapter(activity, list);
            viewSwitcher.setDisplayedChild(1);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener((position, holder, object) -> {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Prefs.KEY_BANNER_INFO, (Banner) object);
                BaseBottomSheetFragment sheetFragment = new GallaryBottomSheetFragment()
                        .setOnBottomSheetDialogCreateListener((bottomSheetFragment, bottomSheetDialog, contentView) -> {
                            bottomSheetFragment.defaultSettings(bottomSheetDialog, contentView);
                            bottomSheetDialog.setCanceledOnTouchOutside(false);
                        });
                sheetFragment.setArguments(bundle);
                sheetFragment.show(getFragmentManager());
            });
        } else {
            adapter.notifyDataSetChanged(list);
        }
    }

    private void refreshBanner(final List<Banner> banners) {
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
                            @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.item_banner, null, false);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                view.setTransitionName("shareView");
                            }
                            return view;
                        }
                    })
                    .setOnBannerListener(position -> {
                        Intent intent = new Intent(context, RoomDetailActivity.class);
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

    @Override
    public void onMessage(ResponseInfo resp) {
        if (ResponseInfo.OK == resp.getCode()) {
            if (CMD_GET_Banner_LIST.equals(resp.getCmd())) {
                refreshBanner(resp.getBannerInfos());
                BmobRequest.getInstance(activity).getBanner(50, 0, CMD_GET_ROOM_LIST);
            } else if (CMD_GET_ROOM_LIST.equals(resp.getCmd())) {
                refreshList(resp.getBannerInfos());
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
