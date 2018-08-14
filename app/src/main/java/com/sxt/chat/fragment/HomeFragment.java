package com.sxt.chat.fragment;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewSwitcher;

import com.sxt.chat.App;
import com.sxt.chat.R;
import com.sxt.chat.adapter.NormalCardListAdapter;
import com.sxt.chat.adapter.NormalGridListAdapter;
import com.sxt.chat.adapter.NormalListAdapter;
import com.sxt.chat.adapter.config.NoScrollLinearLayoutManaget;
import com.sxt.chat.base.LazyFragment;
import com.sxt.chat.json.ResponseInfo;
import com.sxt.chat.json.RoomInfo;
import com.sxt.chat.utils.ToastUtil;
import com.sxt.chat.utils.glide.GlideImageLoader;
import com.sxt.chat.ws.BmobRequest;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 11837 on 2018/4/22.
 */

public class HomeFragment extends LazyFragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewTop;
    private RecyclerView recyclerViewCenter;
    private RecyclerView recyclerViewBottom;
    private RecyclerView recyclerViewLast;
    private ViewSwitcher viewSwitcherBanner;
    private ViewSwitcher viewSwitcherCenter;
    private ViewSwitcher viewSwitcherBottom;
    private ViewSwitcher viewSwitcherLast;
    private Handler handler = new Handler();
    private NormalGridListAdapter adapter0;
    private NormalListAdapter adapter1;
    private NormalListAdapter adapter2;
    private NormalCardListAdapter adapter3;
    private Banner banner;

    private final String CMD_GET_ROOM_LIST = this.getClass().getName() + "CMD_GET_ROOM_LIST";

    @Override
    protected int getDisplayView(LayoutInflater inflater, ViewGroup container) {
        return R.layout.fragment_home_page;
    }

    @Override
    protected void initView() {
        banner = contentView.findViewById(R.id.banner);
        swipeRefreshLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.swipeRefreshLayout);
        recyclerViewTop = (RecyclerView) contentView.findViewById(R.id.top_recyclerView);
        recyclerViewCenter = (RecyclerView) contentView.findViewById(R.id.center_recyclerView);
        recyclerViewBottom = (RecyclerView) contentView.findViewById(R.id.bottom_recyclerView);
        recyclerViewLast = (RecyclerView) contentView.findViewById(R.id.last_recyclerView);
        viewSwitcherBanner = (ViewSwitcher) contentView.findViewById(R.id.banner_viewSwitcher);
        viewSwitcherCenter = (ViewSwitcher) contentView.findViewById(R.id.center_viewSitcher);
        viewSwitcherBottom = (ViewSwitcher) contentView.findViewById(R.id.bottom_viewSwitcher);
        viewSwitcherLast = (ViewSwitcher) contentView.findViewById(R.id.last_viewSitcher);

        recyclerViewTop.setLayoutManager(new GridLayoutManager(activity, 4));
        recyclerViewCenter.setLayoutManager(new NoScrollLinearLayoutManaget(activity, LinearLayoutManager.HORIZONTAL, false).setCanScrollVertically(false));
        recyclerViewBottom.setLayoutManager(new NoScrollLinearLayoutManaget(activity, LinearLayoutManager.HORIZONTAL, false).setCanScrollVertically(false));
        recyclerViewLast.setLayoutManager(new NoScrollLinearLayoutManaget(activity, LinearLayoutManager.HORIZONTAL, false).setCanScrollVertically(false));

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

        contentView.findViewById(R.id.more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showToast(App.getCtx(), "更多房源");
            }
        });
    }

    private void refresh() {
        List<String> imgs = new ArrayList<>();
        imgs.add("http://bmob-cdn-18541.b0.upaiyun.com/2018/05/22/dd5ca0a0400a87b7800ae9a6f107b562.jpg");
        imgs.add("http://bmob-cdn-18541.b0.upaiyun.com/2018/05/22/13cecf96407145708071d88037547c7f.jpg");
        imgs.add("http://bmob-cdn-18541.b0.upaiyun.com/2018/05/22/20799e5a4012706c80f83276a47b7f89.jpg");
        imgs.add("http://bmob-cdn-18541.b0.upaiyun.com/2018/05/21/77a27d12401d6964807090cafca10f5e.jpg");
        imgs.add("http://bmob-cdn-18541.b0.upaiyun.com/2018/05/21/51e795bc405863d5805af06327c0f208.png");

        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                ToastUtil.showToast(App.getCtx(), String.valueOf(position));
            }
        });
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR)
                .setBannerAnimation(Transformer.Default)
                .setImageLoader(new GlideImageLoader())
                .setIndicatorGravity(BannerConfig.CENTER)
                .setImages(imgs)
                .setDelayTime(2000)
                .isAutoPlay(true)
                .start();

        BmobRequest.getInstance(activity).getRoomList(50, 0, CMD_GET_ROOM_LIST);
    }

    private void setAdapter(List<RoomInfo> list) {
        if (adapter0 == null || adapter1 == null || adapter2 == null || adapter3 == null) {

            String[] strings = App.getCtx().getResources().getStringArray(R.array.grid_list_text);
            adapter0 = new NormalGridListAdapter(activity, Arrays.asList(strings));
            adapter1 = new NormalListAdapter(activity, list);
            adapter2 = new NormalListAdapter(activity, list);
            adapter3 = new NormalCardListAdapter(activity, list);

            viewSwitcherBanner.setDisplayedChild(1);
            viewSwitcherCenter.setDisplayedChild(1);
            viewSwitcherBottom.setDisplayedChild(1);
            viewSwitcherLast.setDisplayedChild(1);
            recyclerViewTop.setAdapter(adapter0);
            recyclerViewCenter.setAdapter(adapter1);
            recyclerViewBottom.setAdapter(adapter2);
            recyclerViewLast.setAdapter(adapter3);
        } else {
            adapter1.notifyDataSetChanged(list);
            adapter2.notifyDataSetChanged(list);
            adapter3.notifyDataSetChanged(list);
        }
    }

    @Override
    public void onMessage(ResponseInfo resp) {
        if (ResponseInfo.OK == resp.getCode()) {
            if (CMD_GET_ROOM_LIST.equals(resp.getCmd())) {
                swipeRefreshLayout.setRefreshing(false);
                setAdapter(resp.getRoomInfoList());
            }
        } else {
            swipeRefreshLayout.setRefreshing(false);
            ToastUtil.showToast(activity, resp.getError());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //开始轮播
        if (banner != null) {
            banner.startAutoPlay();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //结束轮播
        if (banner != null) {
            banner.stopAutoPlay();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
