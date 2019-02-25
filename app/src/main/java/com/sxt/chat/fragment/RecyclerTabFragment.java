package com.sxt.chat.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sxt.chat.R;
import com.sxt.chat.activity.MainActivity;
import com.sxt.chat.adapter.LinearRecyclerAdapter;
import com.sxt.chat.adapter.hover.PinnedHeaderItemDecoration;
import com.sxt.chat.adapter.hover.PinnedHeaderRecyclerView;
import com.sxt.chat.base.LazyFragment;
import com.sxt.chat.json.Banner;
import com.sxt.chat.json.ResponseInfo;
import com.sxt.chat.utils.ToastUtil;
import com.sxt.chat.ws.BmobRequest;

import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by 11837 on 2018/4/22.
 */

public class RecyclerTabFragment extends LazyFragment {

    private PinnedHeaderRecyclerView recyclerView;
    private LinearRecyclerAdapter adapter;

    private final String CMD_GET_ROOM_LIST = this.getClass().getName() + "CMD_GET_ROOM_LIST";
    private List<Banner> bannerInfos;

    @Override
    protected int getDisplayView(LayoutInflater inflater, ViewGroup container) {
        return R.layout.fragment_recycler_tab;
    }

    @Override
    protected void initView() {
        recyclerView = contentView.findViewById(R.id.recyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                MainActivity activity = (MainActivity) RecyclerTabFragment.this.activity;
                activity.setBottomBarTranslateY(dy, dy > 0);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new PinnedHeaderItemDecoration());
        recyclerView.setOnPinnedHeaderClickListener(new PinnedHeaderRecyclerView.OnPinnedHeaderClickListener() {
            @Override
            public void onPinnedHeaderClick(int adapterPosition) {
                Toast.makeText(context, "点击了悬浮标题 position = " + adapterPosition, LENGTH_SHORT).show();
            }
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
        if (isVisibleToUser) refresh();
    }

    private void refresh() {
        BmobRequest.getInstance(activity).getBanner(50, 0, CMD_GET_ROOM_LIST);
    }

    private void refreshList() {
        if (adapter == null) {
            adapter = new LinearRecyclerAdapter(bannerInfos);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setData(bannerInfos);
        }
    }

    @Override
    public void onMessage(ResponseInfo resp) {
        if (ResponseInfo.OK == resp.getCode()) {
            if (CMD_GET_ROOM_LIST.equals(resp.getCmd())) {
                bannerInfos = resp.getBannerInfos();
                refreshList();
            }
        } else {
            ToastUtil.showToast(activity, resp.getError());
        }
    }

}
