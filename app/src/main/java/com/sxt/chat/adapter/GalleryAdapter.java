package com.sxt.chat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sxt.chat.R;
import com.sxt.chat.base.BaseRecyclerAdapter;
import com.sxt.chat.json.Banner;

import java.util.List;

/**
 * Created by izhaohu on 2018/4/23.
 */

public class GalleryAdapter extends BaseRecyclerAdapter<Banner> {

    public GalleryAdapter(Context context, List<Banner> data) {
        super(context, data);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_normal_vertical, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
//        holder.title.setText(data.get(position).getHome_name());
        Glide.with(context)
                .load(data.get(position).getUrl())
                .placeholder(R.mipmap.ic_banner_placeholder)
                .error(R.mipmap.ic_banner_placeholder)
                .into(holder.img);

        holder.root.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onClick(position, getItem(position));
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View root;
        public View line;
        public TextView title;
        public ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
//            title = itemView.findViewById(R.id.title);
            img = itemView.findViewById(R.id.img);
        }
    }
}
