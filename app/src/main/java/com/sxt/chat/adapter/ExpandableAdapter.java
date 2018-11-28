package com.sxt.chat.adapter;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sxt.chat.App;
import com.sxt.chat.R;
import com.sxt.chat.view.ExpandableLinearLayout;

/**
 * Created by sxt on 2018/11/28.
 */

public class ExpandableAdapter extends RecyclerView.Adapter {
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(App.getCtx()).inflate(R.layout.item_expandable, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
//        viewHolder.expandableLinearLayout.setInRecyclerView(true);
        ((ViewHolder) holder).button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewHolder) holder).expandableLinearLayout.toggle();
            }
        });
    }

    @Override
    public int getItemCount() {
        return 8;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ExpandableLinearLayout expandableLinearLayout;
        public View button;

        public ViewHolder(View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.button);
            expandableLinearLayout = itemView.findViewById(R.id.expandableLinearLayout);
        }
    }
}
