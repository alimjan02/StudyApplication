package com.sxt.chat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxt.chat.R;
import com.sxt.chat.base.BaseRecyclerAdapter;

import java.util.List;

/**
 * Created by izhaohu on 2018/4/23.
 */

public class NormalGridListAdapter extends BaseRecyclerAdapter<String> {

    private int[] resIds = new int[]{R.mipmap.room_config, R.mipmap.room_renter_info, R.mipmap.room_access_record, R.mipmap.room_like,
            R.mipmap.room_homologous, R.mipmap.room_location, R.mipmap.room_platform, R.mipmap.room_detail};

    public NormalGridListAdapter(Context context, List<String> data) {
        super(context, data);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_normal_grid, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        holder.title.setText(data.get(position));
        holder.img.setImageResource(resIds[position]);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    onClickListener.onClick(position, holder, getItem(position));
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View root;
        public View line;
        public TextView title;
        public ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            title = itemView.findViewById(R.id.title);
            img = itemView.findViewById(R.id.img);
        }
    }
}
