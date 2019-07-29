package com.sxt.chat.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sxt.chat.R;
import com.sxt.chat.base.BaseRecyclerAdapter;
import com.sxt.chat.databinding.ItemChangeLoginBinding;
import com.sxt.chat.db.User;
import com.sxt.chat.utils.glide.GlideCircleTransformer;

import java.util.List;


/**
 * Created by sxt on 2018/2/6.
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

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_change_login, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        holder.binding.setUser(getItem(position));
        holder.binding.setItemCount(getItemCount());
        holder.binding.setPosition(position);
        holder.binding.setIndex(index);
        holder.binding.root.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                notifyItem(position);
                onItemClickListener.onClick(position, getItem(position));
            }
        });
    }

    private void notifyItem(int position) {
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

        ItemChangeLoginBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
