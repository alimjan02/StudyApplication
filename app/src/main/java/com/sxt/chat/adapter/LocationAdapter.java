package com.sxt.chat.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sxt.chat.R;
import com.sxt.chat.base.BaseRecyclerAdapter;
import com.sxt.chat.json.LocationInfo;

import java.util.List;

public class LocationAdapter extends BaseRecyclerAdapter<LocationInfo> {

    private int index = -1;

    public LocationAdapter(Context context, List<LocationInfo> data) {
        super(context, data);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_location, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        holder.title.setText(String.format("%s, %s", position + 1, data.get(position).getAddressName()));
        holder.subTitle.setText(data.get(position).getAddress());
        holder.distance.setText(String.format("%s 公里", data.get(position).getDistance()));
        if (index == position) {
            holder.title.setTextColor(ContextCompat.getColor(context, R.color.main_blue));
            holder.subTitle.setTextColor(ContextCompat.getColor(context, R.color.main_blue));
        } else {
            holder.title.setTextColor(ContextCompat.getColor(context, R.color.text_color_1));
            holder.subTitle.setTextColor(ContextCompat.getColor(context, R.color.text_color_1));
        }
        if (onItemClickListener != null) {
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onClick(position, holder, getItem(position));
                }
            });
        }
    }

    public void refreshIndex(int index) {
        if (index >= 0 && index < getItemCount()) {
            this.index = index;
            notifyDataSetChanged();
        }
    }

    public LocationAdapter resetIndex() {
        this.index = -1;
        return this;
    }

    public int getIndex() {
        return index;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View root;
        public TextView title;
        public TextView subTitle;
        public TextView distance;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            title = (TextView) itemView.findViewById(R.id.title);
            subTitle = (TextView) itemView.findViewById(R.id.subTitle);
            distance = (TextView) itemView.findViewById(R.id.distance);
        }
    }
}
