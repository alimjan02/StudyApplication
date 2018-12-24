package com.sxt.chat.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.sxt.chat.R;
import com.sxt.chat.base.BaseRecyclerAdapter;
import com.sxt.chat.json.VideoObject;
import com.sxt.chat.utils.glide.GlideCircleTransformer;
import com.sxt.chat.utils.glide.GlideRoundTransformer;

import java.util.List;

/**
 * Created by izhaohu on 2018/4/23.
 */

public class VideoListAdapter extends BaseRecyclerAdapter<VideoObject> {

    private int index;
    private Animation scaleAnimation;

    public void notifyIndex(int index) {
        this.index = index;
        notifyDataSetChanged();
    }

    public VideoListAdapter(Context context, List<VideoObject> data) {
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
        holder.ratingBar.setRating(getItemCount());
        holder.title.setText(data.get(position).getTitle());
        Glide.with(context)
                .load(data.get(position).getVideo_img_url())
                .bitmapTransform(new CenterCrop(context), new GlideRoundTransformer(context, 8))
                .into(holder.img);

        Glide.with(context)
                .load(data.get(position).getVideo_img_url())
                .bitmapTransform(new GlideCircleTransformer(context))
                .into(holder.img_header);

        holder.root.setTag(position);
        holder.root.setOnTouchListener(new View.OnTouchListener() {

            private long downMillis;
            private long upMillis;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downMillis = System.currentTimeMillis();
                        Log.e("onTouch", "downMillis : " + downMillis);
                        holder.root.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        holder.root.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                    case MotionEvent.ACTION_UP:
                        holder.root.getParent().requestDisallowInterceptTouchEvent(false);
                        upMillis = System.currentTimeMillis();
                        if (upMillis - downMillis <= 200) {//小于200ms 算点击事件
                            if (onItemClickListener != null) {
                                holder.root.clearAnimation();
                                if (scaleAnimation != null) {
                                    scaleAnimation.cancel();
                                }
                                scaleAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_item_scale_alpha);
                                scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        notifyIndex(position);
                                        onItemClickListener.onClick(position, holder, getItem(position));
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                holder.root.startAnimation(scaleAnimation);
                                Log.e("onTouch", "小于200ms 算点击事件");
                            }
                        } else {//大于200ms 算触摸事件
                            Log.e("onTouch", "大于200ms 算触摸事件");
                        }
                        downMillis = 0;
                        upMillis = 0;
                        break;
                }
                holder.root.performClick();
                return false;
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
