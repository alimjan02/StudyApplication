package com.sxt.chat.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.sxt.chat.App;
import com.sxt.chat.R;
import com.sxt.chat.activity.MainActivity;
import com.sxt.chat.adapter.BannerDetailAdapter;
import com.sxt.chat.base.BaseBottomSheetFragment;
import com.sxt.chat.base.LazyFragment;
import com.sxt.chat.fragment.bottonsheet.GalleryBottomSheetFragment;
import com.sxt.chat.json.Banner;
import com.sxt.chat.json.ResponseInfo;
import com.sxt.chat.json.VideoInfoCopy;
import com.sxt.chat.utils.Prefs;
import com.sxt.chat.utils.ToastUtil;
import com.sxt.chat.view.CustomRecyclerView;
import com.sxt.chat.ws.BmobRequest;

import java.util.List;


@SuppressLint("ValidFragment")
public class BannerDetailFragment extends LazyFragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private CustomRecyclerView recyclerView;
    private BannerDetailAdapter adapter;
    private int type;
    boolean containerIsMainActivity = false;
    private long millis = 120 * 1000L;
    private final String CMD = this + " CMD_GET_VIDEOS";
    public static float scrollY;

    public BannerDetailFragment(boolean useAlphaAnimator, int type) {
        super(useAlphaAnimator);
        this.type = type;
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
        swipeRefreshLayout = contentView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = contentView.findViewById(R.id.recyclerView);
        recyclerView.setEmptyView(View.inflate(context, R.layout.item_no_data, null));
        NestedScrollView nestedScrollView = contentView.findViewById(R.id.nestedScrollView);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.day_night_normal_color);
        swipeRefreshLayout.setProgressViewOffset(true, -swipeRefreshLayout.getProgressCircleDiameter(), 100);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(activity, R.color.main_blue), ContextCompat.getColor(activity, R.color.red_1), ContextCompat.getColor(activity, R.color.line_yellow), ContextCompat.getColor(activity, R.color.main_green), ContextCompat.getColor(activity, R.color.red_1));
        swipeRefreshLayout.setOnRefreshListener(this::refresh);
        if (containerIsMainActivity) {
            //设置滑动监听,使得底部tab栏竖直滑动
            nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                BannerDetailFragment.scrollY = nestedScrollView.getScrollY();
                Log.e("scrollY", String.format("oldScrollY = %s ; scrollY = %s", oldScrollY, scrollY));
                MainActivity activity = (MainActivity) context;
                activity.setBottomBarTranslateY(scrollY, scrollY > oldScrollY);
            });
        }
    }

    @Override
    protected void loadData() {
        super.loadData();
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.postDelayed(this::refresh, 800);
    }

//    @Override
//    protected void checkUserVisibleHint(boolean isVisibleToUser) {
//        super.checkUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser) {
//            refresh();
//        }
//    }

    private void refresh() {
        BmobRequest.getInstance(context).getVideosByType(type, CMD);
    }

    @Override
    public void onMessage(ResponseInfo resp) {
        super.onMessage(resp);
        if (resp.getCode() == ResponseInfo.OK) {
            if (CMD.equals(resp.getCmd())) {
                swipeRefreshLayout.setRefreshing(false);
                refresh(resp.getVideoInfoCopyList());
            }
        } else {
            if (CMD.equals(resp.getCmd())) {
                swipeRefreshLayout.setRefreshing(false);
                ToastUtil.showToast(activity, resp.getError());
            }
        }
    }

    private void refresh(List<VideoInfoCopy> videoInfos) {
        if (adapter == null) {
            recyclerView.getRecyclerView().setNestedScrollingEnabled(false);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            adapter = new BannerDetailAdapter(context, videoInfos);
            adapter.setOnItemClickListener((position, videoInfo) -> {
                Banner banner = new Banner();
                banner.setDescription(videoInfo.getTitle());
                banner.setUrl(videoInfo.getImageUrl());
                showBottomSheet(banner);
            });
            recyclerView.setAdapter(layoutManager, adapter);
        } else {
            adapter.notifyDataSetChanged(videoInfos);
        }
        recyclerView.getRecyclerView().setLayoutAnimation(AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_vertical));
    }

    private void showBottomSheet(Banner banner) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Prefs.KEY_BANNER_INFO, banner);
        BaseBottomSheetFragment sheetFragment = new GalleryBottomSheetFragment().setOnBottomSheetDialogCreateListener((baseBottomSheetFragment, bottomSheetDialog, contentView) -> {
            int peekHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, App.getCtx().getResources().getDisplayMetrics());
            baseBottomSheetFragment
                    .setCancelableOutside(false)
                    .setPeekHeight(peekHeight)
                    .setBottomSheetState(BottomSheetBehavior.STATE_EXPANDED)
                    .setBackgtoundColor(Color.TRANSPARENT);
        });
        sheetFragment.setArguments(bundle);
        sheetFragment.show(getFragmentManager());
    }
}
