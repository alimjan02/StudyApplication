package com.sxt.chat.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sxt.chat.R;
import com.sxt.chat.base.BaseRecyclerAdapter;
import com.sxt.chat.json.Banner;
import com.sxt.chat.utils.DateFormatUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by izhaohu on 2018/4/23.
 */

public class RecyclerTabAdapter extends BaseRecyclerAdapter<Banner> {

    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private int tabPosition;
    private OnRecyclerViewScrollStateChangedListener onRecyclerViewScrollStateChangedListener;

    public RecyclerTabAdapter(Context context, List<Banner> data) {
        super(context, data);
        Collections.sort(data, new Comparator<Banner>() {
            @Override
            public int compare(Banner o1, Banner o2) {
                long o1CreateAt = DateFormatUtil.getSecondsFromDate(o1.getCreatedAt());
                long o2CreateAt = DateFormatUtil.getSecondsFromDate(o2.getCreatedAt());
                if (o1CreateAt == o2CreateAt) {
                    return 0;
                } else if (o1CreateAt > o2CreateAt) {
                    return 1;
                } else {
                    return 2;
                }
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_recycler_tab, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
//        holder.title.setText(data.get(position).getHome_name());

        long secondsCurrent = DateFormatUtil.getSecondsFromDate(data.get(position).getCreatedAt());
        String time = DateFormatUtil.getDateFromSeconds(String.valueOf(secondsCurrent), "MM-dd");
        if (position != 0) {
            long secondsPre = DateFormatUtil.getSecondsFromDate(data.get(position - 1).getCreatedAt());
            String timePre = DateFormatUtil.getDateFromSeconds(String.valueOf(secondsPre), "MM-dd");
            if (time.equals(timePre)) {
                holder.titleLayout.setVisibility(View.GONE);
            } else {
                holder.titleLayout.setVisibility(View.VISIBLE);
            }
        } else {
            tabPosition = position;
            holder.titleLayout.setVisibility(View.VISIBLE);
            if (onRecyclerViewScrollStateChangedListener != null) {
                onRecyclerViewScrollStateChangedListener.onTabStateChanged(tabPosition, tabPosition);
            }
        }
        String[] split = time.split("-");
        holder.title.setText(String.format("%s月%s日", split[0], split[1]));


        Glide.with(context)
                .load(data.get(position).getUrl())
                .placeholder(R.mipmap.ic_banner_placeholder)
                .error(R.mipmap.ic_banner_placeholder)
                .into(holder.img);

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(position, holder, getItem(position));
                }
            }
        });
    }

    public RecyclerTabAdapter setScrollContainers(RecyclerView recyclerView, final LinearLayoutManager layoutManager) {
        this.recyclerView = recyclerView;
        this.layoutManager = layoutManager;
        if (recyclerView != null && layoutManager != null) {
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (onRecyclerViewScrollStateChangedListener != null) {
                        //当前列表中 可视范围内的第一条数据
                        int currentPosition = layoutManager.findFirstVisibleItemPosition();
                        long secondsCurrent = DateFormatUtil.getSecondsFromDate(getItem(currentPosition).getCreatedAt());
                        String time = DateFormatUtil.getDateFromSeconds(String.valueOf(secondsCurrent), "MM-dd");
                        onRecyclerViewScrollStateChangedListener.onTabStateChanged(tabPosition, currentPosition);
                        onRecyclerViewScrollStateChangedListener.onScrolled(recyclerView, dx, dy);
                        //上次固定在顶部的tab
                        long secondsTab = DateFormatUtil.getSecondsFromDate(getItem(tabPosition).getCreatedAt());
                        String timePre = DateFormatUtil.getDateFromSeconds(String.valueOf(secondsTab), "MM-dd");
                        if (!time.equals(timePre)) {
                            tabPosition = currentPosition;
                        }
                    }
                }
            });
        }
        return this;
    }

    public RecyclerTabAdapter setOnRecyclerViewScrollStateChangedListener(OnRecyclerViewScrollStateChangedListener onRecyclerViewScrollStateChangedListener) {
        this.onRecyclerViewScrollStateChangedListener = onRecyclerViewScrollStateChangedListener;
        return this;
    }

    public interface OnRecyclerViewScrollStateChangedListener {

        void onScrolled(RecyclerView recyclerView, int dx, int dy);

        void onTabStateChanged(int preTabPosition, int currentTabPosition);
    }

    @Override
    public void notifyDataSetChanged(List<Banner> data, boolean isRefesh) {
        Collections.sort(data, new Comparator<Banner>() {
            @Override
            public int compare(Banner o1, Banner o2) {
                long o1CreateAt = DateFormatUtil.getSecondsFromDate(o1.getCreatedAt());
                long o2CreateAt = DateFormatUtil.getSecondsFromDate(o2.getCreatedAt());
                if (o1CreateAt == o2CreateAt) {
                    return 0;
                } else if (o1CreateAt > o2CreateAt) {
                    return 1;
                } else {
                    return 2;
                }
            }
        });
        super.notifyDataSetChanged(data, isRefesh);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View root;
        public View line;
        public TextView title;
        public TextView subTitle;
        public View titleLayout;
        public ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            title = itemView.findViewById(R.id.title);
            subTitle = itemView.findViewById(R.id.subTitle);
            titleLayout = itemView.findViewById(R.id.title_layout);
            img = itemView.findViewById(R.id.img);
        }
    }
}
