package com.sxt.chat.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sxt.chat.R;
import com.sxt.chat.activity.RoomDetailActivity;
import com.sxt.chat.base.BaseRecyclerAdapter;
import com.sxt.chat.json.RoomInfo;
import com.sxt.chat.utils.Prefs;
import com.sxt.chat.utils.glide.GlideCircleTransform;
import com.sxt.chat.utils.glide.GlideRoundTransform;

import java.util.List;

/**
 * Created by izhaohu on 2018/4/23.
 */

public class NormalCardListAdapter extends BaseRecyclerAdapter<RoomInfo> {
    public NormalCardListAdapter(Context context, List<RoomInfo> data) {
        super(context, data);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_card, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;

        holder.title.setText(data.get(position).getHome_name());
        holder.ratingBar.setRating(getItemCount() / 2 - position);
        Glide.with(context)
                .load(data.get(position).getRoom_url())
                .error(R.mipmap.ic_no_img)
                .bitmapTransform(new GlideRoundTransform(context, 8))
                .into(holder.img);

        Glide.with(context)
                .load(data.get(position).getRoom_url())
                .error(R.mipmap.ic_no_img)
                .bitmapTransform(new GlideCircleTransform(context))
                .into(holder.img_header);

        holder.root.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RoomDetailActivity.class);
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
            }
        });
        holder.root.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_item_horizontal_percent_20));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View root;
        public TextView title;
        public RatingBar ratingBar;
        public ImageView img;
        public ImageView img_header;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            title = itemView.findViewById(R.id.title);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            img = itemView.findViewById(R.id.img);
            img_header = itemView.findViewById(R.id.img_header);
        }
    }
}
