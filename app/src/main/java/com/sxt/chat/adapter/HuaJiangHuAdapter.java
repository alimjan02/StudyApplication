package com.sxt.chat.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sxt.chat.R;
import com.sxt.chat.activity.RoomDetailActivity;
import com.sxt.chat.base.BaseRecyclerAdapter;
import com.sxt.chat.json.Banner;
import com.sxt.chat.json.RoomInfo;
import com.sxt.chat.utils.Prefs;

import java.util.List;

/**
 * Created by izhaohu on 2018/4/23.
 */

public class HuaJiangHuAdapter extends BaseRecyclerAdapter<Banner> {

    public HuaJiangHuAdapter(Context context, List<Banner> data) {
        super(context, data);
    }

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

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RoomDetailActivity.class);
                RoomInfo roomInfo = new RoomInfo();
                roomInfo.setHome_name(data.get(position).getDescription());
                roomInfo.setRoom_url(data.get(position).getUrl());
                Bundle bundle = new Bundle();
                bundle.putSerializable(Prefs.ROOM_INFO, roomInfo);
                intent.putExtra(Prefs.ROOM_INFO, bundle);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    context.startActivity(intent,
                            ActivityOptions.makeSceneTransitionAnimation
                                    ((Activity) context, holder.img, "shareView").toBundle());
                } else {
                    context.startActivity(intent);
                }
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
