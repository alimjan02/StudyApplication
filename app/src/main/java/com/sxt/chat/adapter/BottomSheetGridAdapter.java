package com.sxt.chat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxt.chat.R;
import com.sxt.chat.base.BaseRecyclerAdapter;

import java.util.List;

/**
 * Created by sxt on 2018/4/23.
 */

public class BottomSheetGridAdapter extends BaseRecyclerAdapter<String> {

    private int[] imageDrawableRes = new int[]{
            R.drawable.ic_menu_food_white_24dp,
            R.drawable.ic_menu_cafe_white_24dp,
            R.drawable.ic_menu_store_white_24dp,
            R.drawable.ic_menu_beach_white_24dp,
            R.drawable.ic_menu_hotel_white_24dp,
            R.drawable.ic_menu_pharmacy_white_24dp,
            R.drawable.ic_menu_bar_white_24dp,
            R.drawable.ic_menu_more_white_24dp,
    };

    private int[] containerDrawableRes = new int[]{
            R.drawable.circle_blue,
            R.drawable.circle_red,
            R.drawable.circle_yellow,
            R.drawable.circle_green,
            R.drawable.circle_yellow_dark,
            R.drawable.circle_pink,
            R.drawable.circle_violet,
            R.drawable.circle_blue_white,
    };

    public BottomSheetGridAdapter(Context context, List<String> data) {
        super(context, data);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_gallery_menu, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        holder.title.setText(data.get(position));
        holder.container.setBackgroundResource(containerDrawableRes[position]);
        holder.img.setImageResource(imageDrawableRes[position]);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null)
                onItemClickListener.onClick(position, getItem(position));
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public FrameLayout container;
        public TextView title;
        public ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            title = itemView.findViewById(R.id.title);
            img = itemView.findViewById(R.id.img);
        }
    }
}
