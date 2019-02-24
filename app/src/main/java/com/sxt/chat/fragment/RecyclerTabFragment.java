package com.sxt.chat.fragment;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sxt.chat.R;
import com.sxt.chat.activity.MainActivity;
import com.sxt.chat.adapter.RecyclerTabAdapter;
import com.sxt.chat.base.BaseBottomSheetFragment;
import com.sxt.chat.base.BaseRecyclerAdapter;
import com.sxt.chat.base.LazyFragment;
import com.sxt.chat.fragment.bottonsheet.GallaryBottomSheetFragment;
import com.sxt.chat.json.Banner;
import com.sxt.chat.json.ResponseInfo;
import com.sxt.chat.utils.DateFormatUtil;
import com.sxt.chat.utils.Prefs;
import com.sxt.chat.utils.ToastUtil;
import com.sxt.chat.ws.BmobRequest;

import java.util.List;

/**
 * Created by 11837 on 2018/4/22.
 */

public class RecyclerTabFragment extends LazyFragment {

    private RecyclerView recyclerView;
    private RecyclerTabAdapter adapter;

    private final String CMD_GET_ROOM_LIST = this.getClass().getName() + "CMD_GET_ROOM_LIST";
    private LinearLayoutManager layoutManager;
    private List<Banner> bannerInfos;
    private TextView tabTitle;
    private TextView tabSubTitle;
    private View titleLayout;

    @Override
    protected int getDisplayView(LayoutInflater inflater, ViewGroup container) {
        return R.layout.fragment_recycler_tab;
    }

    @Override
    protected void initView() {
        titleLayout = contentView.findViewById(R.id.title_layout);
        tabTitle = titleLayout.findViewById(R.id.title);
        tabSubTitle = titleLayout.findViewById(R.id.subTitle);
        recyclerView = contentView.findViewById(R.id.recyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                MainActivity activity = (MainActivity) RecyclerTabFragment.this.activity;
                activity.setBottomBarTranslateY(dy, dy > 0);
            }
        });
    }

    @Override
    protected void checkUserVisibleHint(boolean isVisibleToUser) {
        super.checkUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) refresh();
    }

    private void refresh() {
        BmobRequest.getInstance(activity).getBanner(50, 0, CMD_GET_ROOM_LIST);
    }

    private void refreshList() {
        if (adapter == null) {
            layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setNestedScrollingEnabled(false);
            adapter = new RecyclerTabAdapter(activity, bannerInfos);
            adapter.setScrollContainers(recyclerView, layoutManager).setOnRecyclerViewScrollStateChangedListener(
                    new RecyclerTabAdapter.OnRecyclerViewScrollStateChangedListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                        }

                        @Override
                        public void onTabStateChanged(int preTabPosition, int currentTabPosition) {
                            long secondsCurrent = DateFormatUtil.getSecondsFromDate(bannerInfos.get(currentTabPosition).getCreatedAt());
                            String time = DateFormatUtil.getDateFromSeconds(String.valueOf(secondsCurrent), "MM-dd");
                            String[] split = time.split("-");
                            tabTitle.setText(String.format("%s月%s日", split[0], split[1]));
                        }
                    }).setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
                @Override
                public void onClick(int position, RecyclerView.ViewHolder holder, Object object) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Prefs.KEY_BANNER_INFO, (Banner) object);
                    BaseBottomSheetFragment sheetFragment = new GallaryBottomSheetFragment()
                            .setOnBottomSheetDialogCreateListener(new BaseBottomSheetFragment.OnBottomSheetDialogCreateListener() {
                                @Override
                                public void onBottomSheetDialogCreate(BaseBottomSheetFragment bottomSheetFragment, BottomSheetDialog bottomSheetDialog, View contentView) {
                                    bottomSheetFragment.defaultSettings(bottomSheetDialog, contentView);
                                    bottomSheetDialog.setCanceledOnTouchOutside(false);
                                }
                            });
                    sheetFragment.setArguments(bundle);
                    sheetFragment.show(getFragmentManager());
                }
            });
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged(bannerInfos);
        }
    }

    @Override
    public void onMessage(ResponseInfo resp) {
        if (ResponseInfo.OK == resp.getCode()) {
            if (CMD_GET_ROOM_LIST.equals(resp.getCmd())) {
                bannerInfos = resp.getBannerInfos();
                refreshList();
//                swipeRefreshLayout.setRefreshing(false);
            }
        } else {
//            swipeRefreshLayout.setRefreshing(false);
            ToastUtil.showToast(activity, resp.getError());
        }
    }

}
