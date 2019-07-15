package com.sxt.chat.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ViewSwitcher;

import com.sxt.chat.base.BaseRecyclerAdapter;


/**
 * Created by izhaohu on 2018/1/19.
 */

public class CustomRecyclerView extends ViewSwitcher {
    private FrameLayout emptyViewLayout;
    private RecyclerView recyclerView;

    public CustomRecyclerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CustomRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        //添加RecyclerView
        recyclerView = new RecyclerView(context);
        recyclerView.setLayoutParams(new CustomRecyclerView.LayoutParams(CustomRecyclerView.LayoutParams.MATCH_PARENT, CustomRecyclerView.LayoutParams.WRAP_CONTENT));
        addView(recyclerView);
        emptyViewLayout = new FrameLayout(context);
        emptyViewLayout.setLayoutParams(new CustomRecyclerView.LayoutParams(CustomRecyclerView.LayoutParams.MATCH_PARENT, CustomRecyclerView.LayoutParams.MATCH_PARENT));
        addView(emptyViewLayout);
    }

    public CustomRecyclerView setAdapter(RecyclerView.LayoutManager layoutManager, BaseRecyclerAdapter adapter) {
        if (adapter != null) {
            if (layoutManager == null) {
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            } else {
                recyclerView.setLayoutManager(layoutManager);
            }
            recyclerView.setAdapter(adapter);
            adapter.setContentObserver((count, object) -> setEmptyViewHeightWeightStyle(count));
        }
        return this;
    }

    public CustomRecyclerView setEmptyView(View emptyView) {
        if (emptyView != null) {
            emptyViewLayout.addView(emptyView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        return this;
    }

    private void setEmptyViewHeightWeightStyle(int count) {
        setDisplayedChild(count > 0 ? 0 : 1);
    }

    public RecyclerView getRecyclerView() {
        return this.recyclerView;
    }
}
