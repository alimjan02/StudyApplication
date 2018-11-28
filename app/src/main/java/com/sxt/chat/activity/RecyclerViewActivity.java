package com.sxt.chat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.sxt.chat.R;
import com.sxt.chat.adapter.ExpandableAdapter;
import com.sxt.chat.adapter.config.DividerItemDecoration;
import com.sxt.chat.base.HeaderActivity;

/**
 * Created by sxt on 2018/11/28.
 */

public class RecyclerViewActivity extends HeaderActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, ContextCompat.getDrawable(this, R.drawable.divider_bg)));
        recyclerView.setAdapter(new ExpandableAdapter());
    }
}
