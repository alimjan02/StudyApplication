package com.sxt.chat.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ViewSwitcher;

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
        refreshLayout = contentView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = contentView.findViewById(R.id.recyclerView);
        viewSwitcher = contentView.findViewById(R.id.viewSwitcher);
        NestedScrollView nestedScrollView = contentView.findViewById(R.id.nestedScrollView);
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(activity, R.color.main_blue), ContextCompat.getColor(activity, R.color.red), ContextCompat.getColor(activity, R.color.line_yellow), ContextCompat.getColor(activity, R.color.main_green), ContextCompat.getColor(activity, R.color.red));
        refreshLayout.setOnRefreshListener(this::refresh);
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
        refreshLayout.setRefreshing(true);
        refreshLayout.postDelayed(this::refresh, 800);
    }

    @Override
    protected void checkUserVisibleHint(boolean isVisibleToUser) {
        super.checkUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            refresh();
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
            adapter.setContentObserver((count, object) -> viewSwitcher.setDisplayedChild(count == 0 ? 0 : 1));
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged(videoInfos);
        }
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
