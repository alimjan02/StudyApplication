package com.sxt.chat.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sxt.chat.R;
import com.sxt.chat.activity.BannerDetailActivity;
import com.sxt.chat.base.BaseRecyclerAdapter;
import com.sxt.chat.databinding.ItemCardBinding;
import com.sxt.chat.json.RoomInfo;
import com.sxt.chat.utils.Prefs;
import com.sxt.chat.utils.glide.GlideCircleTransformer;
import com.sxt.chat.utils.glide.GlideRoundTransformer;

import java.util.List;

/**
 * Created by sxt on 2018/4/23.
 */

public class CardListAdapter extends BaseRecyclerAdapter<RoomInfo> {
    public CardListAdapter(Context context, List<RoomInfo> data) {
        super(context, data);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = getInflater().inflate(R.layout.item_card, parent, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        holder.itemCardBinding.setRoomInfo(data.get(position));
        holder.itemCardBinding.setItemCount(getItemCount());
        holder.itemCardBinding.setPosition(position);
        holder.itemCardBinding.root.setOnClickListener(v -> {
            Intent intent = new Intent(context, BannerDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(Prefs.ROOM_INFO, getItem(position));
            intent.putExtra(Prefs.ROOM_INFO, bundle);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                context.startActivity(intent,
                        ActivityOptions.makeSceneTransitionAnimation
                                ((Activity) context, holder.itemCardBinding.img, "shareView").toBundle());
            } else {
                context.startActivity(intent);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemCardBinding itemCardBinding;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemCardBinding = DataBindingUtil.bind(itemView);
        }
    }
}
