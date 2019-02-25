package com.sxt.chat.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sxt.chat.App;
import com.sxt.chat.R;
import com.sxt.chat.adapter.hover.PinnedHeaderAdapter;
import com.sxt.chat.json.Banner;

import java.util.List;

public class LinearRecyclerAdapter extends PinnedHeaderAdapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM_TITLE = 0;
    private static final int VIEW_TYPE_ITEM_CONTENT = 1;

    private List<Banner> mDataList;

    public LinearRecyclerAdapter() {
        this(null);
    }

    public LinearRecyclerAdapter(List<Banner> dataList) {
        mDataList = dataList;
    }

    public void setData(List<Banner> dataList) {
        mDataList = dataList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM_TITLE) {
            return new TitleHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_tab_top, parent, false));
        } else {
            return new ContentHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_tab, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_ITEM_TITLE) {
            TitleHolder titleHolder = (TitleHolder) holder;
            titleHolder.mTextTitle.setText(mDataList.get(position).getDescription());
        } else {
            ContentHolder contentHolder = (ContentHolder) holder;
            Glide.with(App.getCtx()).load(mDataList.get(position).getUrl())
//                    .placeholder(R.mipmap.ic_placeholder)
                    .into(contentHolder.img);
        }
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 3 == 0) {
            return VIEW_TYPE_ITEM_TITLE;
        } else {
            return VIEW_TYPE_ITEM_CONTENT;
        }
    }

    @Override
    public boolean isPinnedPosition(int position) {
        return getItemViewType(position) == VIEW_TYPE_ITEM_TITLE;
    }

    static class ContentHolder extends RecyclerView.ViewHolder {

        ImageView img;

        ContentHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
        }
    }

    static class TitleHolder extends RecyclerView.ViewHolder {

        TextView mTextTitle;

        TitleHolder(View itemView) {
            super(itemView);
            mTextTitle = itemView.findViewById(R.id.title);
        }
    }

}
