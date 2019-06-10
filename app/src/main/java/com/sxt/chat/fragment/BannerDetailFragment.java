package com.sxt.chat.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewSwitcher;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.sxt.chat.App;
import com.sxt.chat.R;
import com.sxt.chat.activity.MainActivity;
import com.sxt.chat.adapter.BannerDetailAdapter;
import com.sxt.chat.base.BaseBottomSheetFragment;
import com.sxt.chat.base.LazyFragment;
import com.sxt.chat.fragment.bottonsheet.GalleryBottomSheetFragment;
import com.sxt.chat.json.Banner;
import com.sxt.chat.json.ResponseInfo;
import com.sxt.chat.json.VideoInfo;
import com.sxt.chat.utils.Prefs;
import com.sxt.chat.utils.ToastUtil;
import com.sxt.chat.ws.BmobRequest;

import java.util.List;


@SuppressLint("ValidFragment")
public class BannerDetailFragment extends LazyFragment {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private ViewSwitcher viewSwitcher;
    private BannerDetailAdapter adapter;
    private int type;
    boolean containerIsMainActivity = false;
    private long millis = 120 * 1000L;
    private InterstitialAd interstitialAd;
    private final String CMD = this + " CMD_GET_VIDEOS";
    private String KEY = Prefs.KEY_LAST_RESUME_MILLIS_2;
    public static float scrollY;

    public BannerDetailFragment(boolean useAlphaAnimator, int type) {
        super(useAlphaAnimator);
        this.type = type;
        KEY += type;
        Log.e(TAG, String.format("CMD - > %s", CMD));
    }

    public BannerDetailFragment setContainerIsMainActivity(boolean containerIsMainActivity) {
        this.containerIsMainActivity = containerIsMainActivity;
        if (containerIsMainActivity) {
            millis += millis;
        }
        return this;
    }

    @Override
    protected int getDisplayView(LayoutInflater inflater, ViewGroup container) {
        return R.layout.fragment_banner_detail;
    }

    @Override
    protected void initView() {
        refreshLayout = contentView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = contentView.findViewById(R.id.recyclerView);
        viewSwitcher = contentView.findViewById(R.id.viewSwitcher);
        NestedScrollView nestedScrollView = contentView.findViewById(R.id.nestedScrollView);
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(activity, R.color.main_blue), ContextCompat.getColor(activity, R.color.red), ContextCompat.getColor(activity, R.color.line_yellow), ContextCompat.getColor(activity, R.color.main_green), ContextCompat.getColor(activity, R.color.red));
        refreshLayout.setOnRefreshListener(this::refresh);
        initGoogleAlertAds();
        if (containerIsMainActivity) {
            //设置滑动监听,使得底部tab栏竖直滑动
            nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                this.scrollY = nestedScrollView.getScrollY();
                Log.e("scrollY", String.format("oldScrollY = %s ; scrollY = %s", oldScrollY, scrollY));
                MainActivity activity = (MainActivity) context;
                activity.setBottomBarTranslateY(scrollY, scrollY > oldScrollY);
            });
        }
    }

    @Override
    protected void loadData() {
        super.loadData();
        refreshLayout.setRefreshing(true);
        refreshLayout.postDelayed(this::refresh, 800);
    }

    @Override
    protected void checkUserVisibleHint(boolean isVisibleToUser) {
        super.checkUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            refresh();
            loadAD();
        }
    }

    private void refresh() {
        BmobRequest.getInstance(context).getVideosByType(type, CMD);
    }

    @Override
    public void onMessage(ResponseInfo resp) {
        super.onMessage(resp);
        if (resp.getCode() == ResponseInfo.OK) {
            if (CMD.equals(resp.getCmd())) {
                refreshLayout.setRefreshing(false);
                refresh(resp.getVideoInfoList());
            }
        } else {
            if (CMD.equals(resp.getCmd())) {
                refreshLayout.setRefreshing(false);
                ToastUtil.showToast(activity, resp.getError());
            }
        }
    }

    private void refresh(List<VideoInfo> videoInfos) {
        if (adapter == null) {
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            adapter = new BannerDetailAdapter(context, videoInfos);
            adapter.setOnItemClickListener((position, videoInfo) -> {
                Banner banner = new Banner();
                banner.setDescription(videoInfo.getTitle());
                banner.setUrl(videoInfo.getImageUrl());
                showBottomSheet(banner);
            });
            adapter.setContentObserver((count, object) -> {
                viewSwitcher.setDisplayedChild(count == 0 ? 0 : 1);
            });
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged(videoInfos);
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

    /**
     * 初始化google插屏ad
     */
    private void initGoogleAlertAds() {
        if (interstitialAd == null) {
            interstitialAd = new InterstitialAd(activity);
            int unitId;
            if (type == 0) {
                unitId = R.string.adsense_app_ad_alert_home_detail1;
            } else if (type == 1) {
                unitId = R.string.adsense_app_ad_alert_home_detail2;
            } else if (type == 2) {
                unitId = R.string.adsense_app_ad_alert_home_detail3;
            } else {
                unitId = R.string.adsense_app_ad_alert_home_detail4;
            }
            interstitialAd.setAdUnitId(getString(unitId));
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    Log.e(TAG, "插屏广告加载成功");
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    Log.e(TAG, "插屏广告加载失败 error code: " + errorCode);
                }

                @Override
                public void onAdClosed() {
                    Log.e(TAG, "插屏广告关闭");
                    restartAlertAds();
                }
            });
            restartAlertAds();
        }
    }

    /**
     * 预加载插屏广告
     */
    private void restartAlertAds() {
        initGoogleAlertAds();
        if (!interstitialAd.isLoading() && !interstitialAd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            interstitialAd.loadAd(adRequest);
            Log.e(TAG, "加载下一个插屏广告");
        }
    }

    /**
     * 广告加载完成后，显示出来,然后预加载下一条广告
     */
    private void showAlertAds() {
        if (interstitialAd != null && interstitialAd.isLoaded()) {
            interstitialAd.show();
            Log.e(TAG, "显示插屏广告");
        }
    }

    /**
     * 显示插屏广告
     */
    private void loadAD() {
        long lastMillis = Prefs.getInstance(context).getLong(KEY, 0);
        if (System.currentTimeMillis() - lastMillis > millis) {
            Prefs.getInstance(context).putLong(KEY, System.currentTimeMillis());
            showAlertAds();
        }
    }
}
