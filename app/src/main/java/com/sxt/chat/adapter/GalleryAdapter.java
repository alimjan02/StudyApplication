package com.sxt.chat.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sxt.chat.R;
import com.sxt.chat.base.BaseRecyclerAdapter;
import com.sxt.chat.databinding.ItemNormalVerticalBinding;
import com.sxt.chat.json.Banner;

import java.util.List;

/**
 * Created by sxt on 2018/4/23.
 */

public class GalleryAdapter extends BaseRecyclerAdapter<Banner> {

    public GalleryAdapter(Context context, List<Banner> data) {
        super(context, data);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_normal_vertical, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        holder.binding.setBanner(getItem(position));
        holder.binding.root.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onClick(position, getItem(position));
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemNormalVerticalBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
