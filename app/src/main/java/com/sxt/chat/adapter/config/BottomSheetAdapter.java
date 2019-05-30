package com.sxt.chat.adapter.config;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sxt.chat.R;
import com.sxt.chat.base.BaseRecyclerAdapter;
import com.sxt.chat.json.Banner;
import com.sxt.chat.utils.ToastUtil;
import com.sxt.chat.utils.glide.GlideCircleTransformer;

import java.util.List;

/**
 * Created by izhaohu on 2018/4/23.
 */

public class BottomSheetAdapter extends BaseRecyclerAdapter<Banner> {

    public BottomSheetAdapter(Context context, List<Banner> data) {
        super(context, data);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_gallary_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        holder.title.setText(data.get(position).getDescription());
        Glide.with(context)
                .load(data.get(position).getUrl())
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .bitmapTransform(new GlideCircleTransformer(context))
                .into(holder.img);
        holder.root.setOnClickListener(v -> ToastUtil.showSnackBar((Activity) context, "祠堂里的小英子"));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View root;
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
