package com.sxt.chat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sxt.chat.R;
import com.sxt.chat.base.BaseRecyclerAdapter;
import com.sxt.chat.db.User;
import com.sxt.chat.utils.glide.GlideCircleTransformer;

import java.util.List;


/**
 * Created by izhaohu on 2018/2/6.
 */

public class ChangeLoginAdapter extends BaseRecyclerAdapter<User> {

    private int index = -1000;
    public static final int Current_User = 1000;
    public static final int Other_USER = 1001;

    public void setIndex(int index) {
        this.index = index;
    }

    public ChangeLoginAdapter(Context context, List<User> data) {
        super(context, data);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_change_login, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        holder.name.setText(data.get(position).getUserName());

        if ("M".equals(data.get(position).getGender())) {
            update(data.get(position).getImgUri(), R.mipmap.men, holder.img);
        } else {
            update(data.get(position).getImgUri(), R.mipmap.female, holder.img);
        }

        if (index == position) {
            holder.arrow.setVisibility(View.VISIBLE);
        } else {
            holder.arrow.setVisibility(View.GONE);
        }
        if (position == getItemCount() - 1) {
            holder.line.setVisibility(View.INVISIBLE);
        } else {
            holder.line.setVisibility(View.VISIBLE);
        }
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    notifyItem(position);
                    onItemClickListener.onClick(position, holder, getItem(position));
                }
            }
        });
    }

    private void update(String url, int placeHolder, ImageView target) {
        Glide.with(context)
                .load(url)
                .error(placeHolder)
                .bitmapTransform(new GlideCircleTransformer(context))
//                .skipMemoryCache(true)//跳过内存
//                .diskCacheStrategy(DiskCacheStrategy.NONE)//想要生效必须添加 跳过内存
                .into(target);
    }

    public void notifyItem(int position) {
        index = position;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == index) {
            return Current_User;
        }
        return Other_USER;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View root;
        public View line;
        public TextView name;
        public ImageView img;
        public ImageView arrow;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            line = itemView.findViewById(R.id.line);
            name = (TextView) itemView.findViewById(R.id.name);
            img = (ImageView) itemView.findViewById(R.id.img);
            arrow = (ImageView) itemView.findViewById(R.id.arrow);
        }
    }
}
