package com.sxt.chat.adapter;

import android.content.Context;
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
import com.sxt.chat.json.VideoInfo;
import com.sxt.chat.utils.glide.GlideCircleTransformer;
import com.sxt.chat.utils.glide.GlideRoundTransformer;

import java.util.List;

/**
 * Created by izhaohu on 2018/4/23.
 */

public class VideoListAdapter extends BaseRecyclerAdapter<VideoInfo> {

    private int index;
    private Animation scaleAnimation;

    public void notifyIndex(int index) {
        this.index = index;
        notifyDataSetChanged();
    }

    public VideoListAdapter(Context context, List<VideoInfo> data) {
        super(context, data);
    }

    @NonNull
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
                .load(data.get(position).getImageUrl())
                .bitmapTransform(new CenterCrop(context), new GlideRoundTransformer(context, 8))
                .into(holder.img);

        Glide.with(context)
                .load(data.get(position).getImageUrl())
                .bitmapTransform(new GlideCircleTransformer(context))
                .into(holder.img_header);

        holder.itemView.setOnClickListener(v -> {
            index = position;
            if (onItemClickListener != null)
                onItemClickListener.onClick(position, getItem(position));
        });

//        holder.itemView.setTag(position);
//
//        if (scaleAnimation != null) {
//            scaleAnimation.cancel();
//        }
//        holder.itemView.clearAnimation();
//        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
//
//            private long downMillis;
//            private long upMillis;
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        downMillis = System.currentTimeMillis();
//                        Log.e("onTouch", "downMillis : " + downMillis);
//                        holder.itemView.getParent().requestDisallowInterceptTouchEvent(true);
//
//
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        holder.itemView.getParent().requestDisallowInterceptTouchEvent(false);
//                        return false;//如果触摸移动,此时应该屏蔽 ACTION_UP 事件
//                    case MotionEvent.ACTION_UP:
//                        holder.itemView.getParent().requestDisallowInterceptTouchEvent(false);
//                        upMillis = System.currentTimeMillis();
//                        if (!holder.itemView.isPressed() && upMillis - downMillis <= 100) {//小于100ms 算点击事件
//                            if (onItemClickListener != null) {
//                                scaleAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_item_scale_alpha);
//                                scaleAnimation.setInterpolator(new DecelerateInterpolator());
//                                scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
//                                    @Override
//                                    public void onAnimationStart(Animation animation) {
//
//                                    }
//
//                                    @Override
//                                    public void onAnimationEnd(Animation animation) {
//                                        if (index != position) {
//                                            notifyIndex(position);
//                                            onItemClickListener.onClick(position, getItem(position));
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onAnimationRepeat(Animation animation) {
//
//                                    }
//                                });
//                                View view = holder.itemView.getRootView().findViewWithTag(position);
//                                if (view != null) {
//                                    view.startAnimation(scaleAnimation);
//                                }
//                                Log.e("onTouch", "小于200ms 算点击事件");
//                            }
//                        } else {//大于200ms 算触摸事件
//                            Log.e("onTouch", "大于200ms 算触摸事件");
//                        }
//                        downMillis = 0;
//                        upMillis = 0;
//                        break;
//                }
//                holder.root.performClick();
//                return false;
//            }
//        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View itemView;
        public View root;
        public TextView title;
        public RatingBar ratingBar;
        public ImageView img;
        public ImageView img_header;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            root = itemView.findViewById(R.id.root);
            title = itemView.findViewById(R.id.title);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            img = itemView.findViewById(R.id.img);
            img_header = itemView.findViewById(R.id.img_header);
        }
    }
}
