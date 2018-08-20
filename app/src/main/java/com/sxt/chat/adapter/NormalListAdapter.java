package com.sxt.chat.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sxt.chat.R;
import com.sxt.chat.activity.RoomDetailActivity;
import com.sxt.chat.base.BaseRecyclerAdapter;
import com.sxt.chat.explayer.MyLoadControl;
import com.sxt.chat.json.RoomInfo;
import com.sxt.chat.utils.glide.GlideRoundTransform;

import java.util.List;

/**
 * Created by izhaohu on 2018/4/23.
 */

public class NormalListAdapter extends BaseRecyclerAdapter<RoomInfo> {
    public NormalListAdapter(Context context, List<RoomInfo> data) {
        super(context, data);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_normal, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        holder.title.setText(data.get(position).getAddress());
        holder.subTitle.setText("六居室-南卧-" + data.get(position).getRoom_size() + "㎡ 距7号线上海的大学361米");
        holder.price.setText(data.get(position).getPrice() + " 元/月");
        Glide.with(context)
                .load(data.get(position).getRoom_url())
                .error(R.mipmap.ic_no_img)
                .bitmapTransform(new GlideRoundTransform(context, 8))
                .into(holder.img);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RoomDetailActivity.class);
                intent.putExtra("url", data.get(position).getRoom_url());
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
        public TextView subTitle;
        public TextView address;
        public TextView price;
        public ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            title = itemView.findViewById(R.id.title);
            subTitle = itemView.findViewById(R.id.subTitle);
            price = itemView.findViewById(R.id.price);
            address = itemView.findViewById(R.id.address);
            img = itemView.findViewById(R.id.img);
        }
    }
}
