package com.sxt.chat.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.sxt.chat.R;
import com.sxt.chat.base.BaseRecyclerAdapter;
import com.sxt.chat.json.VideoObject;
import com.sxt.chat.utils.glide.GlideRoundTransform;

import java.util.List;

/**
 * Created by izhaohu on 2018/7/10.
 */

public class VideoListAdapter extends BaseRecyclerAdapter<VideoObject> {

    private int index;

    public void notifyIndex(int index) {
        this.index = index;
        notifyDataSetChanged();
    }

    public VideoListAdapter(Context context, List<VideoObject> data) {
        super(context, data);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_video_list, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        holder.title.setText(data.get(position).getTitle());
        if (index == position) {
            holder.title.setTextColor(ContextCompat.getColor(context, R.color.main_blue_press));
        } else {
            holder.title.setTextColor(ContextCompat.getColor(context, R.color.white));
        }
        Glide.with(context)
                .load(data.get(position).getVideo_img_url())
                .bitmapTransform(new CenterCrop(context), new GlideRoundTransform(context, 4))
                .into(holder.img);

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick(position, holder, getItem(position));
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View root;
        public ImageView img;
        public TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            title = itemView.findViewById(R.id.title);
            img = itemView.findViewById(R.id.img);
        }
    }
}
