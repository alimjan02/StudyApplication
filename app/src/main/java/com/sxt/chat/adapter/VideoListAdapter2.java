package com.sxt.chat.adapter;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.sxt.chat.R;
import com.sxt.chat.base.BaseRecyclerAdapter;
import com.sxt.chat.json.VideoObject;
import com.sxt.chat.utils.glide.GlideCircleTransform;
import com.sxt.chat.utils.glide.GlideRoundTransform;

import java.util.List;

/**
 * Created by izhaohu on 2018/4/23.
 */

public class VideoListAdapter2 extends BaseRecyclerAdapter<VideoObject> {

    private int index;

    public void notifyIndex(int index) {
        this.index = index;
        notifyDataSetChanged();
    }

    public VideoListAdapter2(Context context, List<VideoObject> data) {
        super(context, data);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_video, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;

        if (index == position) {
            holder.root.setBackground(ContextCompat.getDrawable(context, R.drawable.white_stroke_round_8));
        } else {
            holder.root.setBackground(new ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent)));
        }
        holder.title.setText(data.get(position).getTitle());
        holder.ratingBar.setRating(getItemCount() / 2 - position);
        holder.title.setText(data.get(position).getTitle());
        Glide.with(context)
                .load(data.get(position).getVideo_img_url())
                .bitmapTransform(new CenterCrop(context), new GlideRoundTransform(context, 8))
                .into(holder.img);

        Glide.with(context)
                .load(data.get(position).getVideo_img_url())
                .bitmapTransform(new GlideCircleTransform(context))
                .into(holder.img_header);

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick(position, holder, getItem(position));
                    holder.root.clearAnimation();
                    @SuppressLint("ResourceType") AnimatorSet animation = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.anim.anim_scale_alpha);
                    animation.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            holder.root.clearAnimation();
                            notifyIndex(position);
                        }
                    });
                    animation.setTarget(holder.root);
                    animation.start();
                }
            }
        });
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
