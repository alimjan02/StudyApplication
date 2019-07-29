package com.sxt.chat.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sxt.chat.R;
import com.sxt.chat.activity.BannerDetailActivity;
import com.sxt.chat.base.BaseRecyclerAdapter;
import com.sxt.chat.json.RoomInfo;
import com.sxt.chat.utils.Prefs;
import com.sxt.chat.utils.glide.GlideRoundTransformer;

import java.util.List;

/**
 * Created by izhaohu on 2018/4/23.
 */

public class NormalListAdapter extends BaseRecyclerAdapter<RoomInfo> {
    public NormalListAdapter(Context context, List<RoomInfo> data) {
        super(context, data);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_normal, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        holder.title.setText(data.get(position).getHome_name());
        holder.price.setText(String.valueOf(data.get(position).getPrice()));
        Glide.with(context)
                .load(data.get(position).getRoom_url())
                .error(R.mipmap.ic_no_img)
                .bitmapTransform(new GlideRoundTransformer(context, 8))
                .into(holder.img);
        holder.root.setOnClickListener(v -> {
            Intent intent = new Intent(context, BannerDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(Prefs.ROOM_INFO, getItem(position));
            intent.putExtra(Prefs.ROOM_INFO, bundle);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                context.startActivity(intent,
                        ActivityOptions.makeSceneTransitionAnimation
                                ((Activity) context, holder.img, "shareView").toBundle());
            } else {
                context.startActivity(intent);
            }
        });
        holder.root.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_item_horizontal_percent_20));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View root;
        public TextView title;
        public TextView price;
        public ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            title = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);
            img = itemView.findViewById(R.id.img);
        }
    }
}
