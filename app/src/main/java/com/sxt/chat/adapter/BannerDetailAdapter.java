package com.sxt.chat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sxt.chat.R;
import com.sxt.chat.base.BaseRecyclerAdapter;
import com.sxt.chat.json.VideoInfo;
import com.sxt.chat.utils.glide.GlideRoundTransformer;

import java.util.List;

/**
 * Created by izhaohu on 2018/4/23.
 */

public class BannerDetailAdapter extends BaseRecyclerAdapter<VideoInfo> {
    public BannerDetailAdapter(Context context, List<VideoInfo> data) {
        super(context, data);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_banner_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        Glide.with(context)
                .load(data.get(position).getImageUrl())
                .error(R.mipmap.ic_banner_placeholder)
                .placeholder(R.mipmap.ic_banner_placeholder)
                .bitmapTransform(new GlideRoundTransformer(context, 8))
                .into(holder.img);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null)
                onItemClickListener.onClick(position, getItem(position));
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
        }
    }
}
