package com.sxt.chat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import com.sxt.chat.R;
import com.sxt.chat.base.BaseRecyclerAdapter;

import java.util.List;

/**
 * Created by izhaohu on 2018/6/12.
 */

public class StarAdapter extends BaseRecyclerAdapter<String> {

    public StarAdapter(Context context, List<String> data) {
        super(context, data);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_star_bottom, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        if (position % 2 == 0) {
            holder.ratingBar.setRating(0.5f);
        } else {
            holder.ratingBar.setRating(1);
        }
        holder.title.setText(data.get(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View root;
        public TextView title;
        public RatingBar ratingBar;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            title = itemView.findViewById(R.id.title);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
