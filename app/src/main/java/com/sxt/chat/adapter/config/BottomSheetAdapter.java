package com.sxt.chat.adapter.config;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxt.chat.R;
import com.sxt.chat.base.BaseRecyclerAdapter;
import com.sxt.chat.json.RoomInfo;
import com.sxt.chat.utils.ToastUtil;

import java.util.List;

/**
 * Created by izhaohu on 2018/4/23.
 */

public class BottomSheetAdapter extends BaseRecyclerAdapter<String> {

    public BottomSheetAdapter(Context context, List<String> data) {
        super(context, data);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_gallary_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        holder.title.setText("城隍庙的小鸭子");
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showSnackBar((Activity) context, "祠堂里的小英子");
            }
        });
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
