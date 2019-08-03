package com.sxt.chat.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.sxt.chat.R;
import com.sxt.chat.base.BaseRecyclerAdapter;
import com.sxt.chat.databinding.ItemVideoBinding;
import com.sxt.chat.json.VideoInfo;
import com.sxt.chat.utils.glide.GlideCircleTransformer;
import com.sxt.chat.utils.glide.GlideRoundTransformer;

import java.util.List;

/**
 * Created by izhaohu on 2018/4/23.
 */

public class VideoListAdapter extends BaseRecyclerAdapter<VideoInfo> {

    private int index;

    public void notifyIndex(int index) {
        this.index = index;
        notifyDataSetChanged();
    }

    public VideoListAdapter(Context context, List<VideoInfo> data) {
        super(context, data);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_video, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        holder.itemVideoBinding.setVideoInfo(getItem(position));
        if (index == position) {
            holder.itemVideoBinding.root.setBackground(ContextCompat.getDrawable(context, R.drawable.white_stroke_round_8));
        } else {
            holder.itemVideoBinding.root.setBackground(new ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent)));
        }
        Glide.with(context)
                .load(data.get(position).getImageUrl())
                .bitmapTransform(new CenterCrop(context), new GlideRoundTransformer(context, 8))
                .into(holder.itemVideoBinding.img);

        holder.itemView.setOnClickListener(v -> {
            index = position;
            if (onItemClickListener != null)
                onItemClickListener.onClick(position, getItem(position));
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemVideoBinding itemVideoBinding;

        public ViewHolder(View itemView) {
            super(itemView);
            itemVideoBinding = DataBindingUtil.bind(itemView);
        }
    }
}
