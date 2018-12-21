package com.sxt.chat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sxt.chat.R;
import com.sxt.chat.base.BaseRecyclerAdapter;

import java.util.List;

/**
 * Created by izhaohu on 2018/4/23.
 */

public class PdfAdapter extends BaseRecyclerAdapter<Bitmap> {

    public PdfAdapter(Context context, List<Bitmap> data) {
        super(context, data);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_pdf, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        holder.img.setImageBitmap(data.get(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View root;
        public ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
        }
    }
}
