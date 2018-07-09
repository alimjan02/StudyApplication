package com.sxt.chat.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sxt on 2016/5/17.
 */

public abstract class LazyFragment extends BaseFragment {
    public View contentView;
    public boolean isInit = false;
    public boolean isFirst = true;
    private boolean flag = true;
    protected Activity activity;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater.inflate(getDisplayView(inflater, container), container, false);
            initView();
            isInit = true;
        }
        readyLoadData();
        return contentView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isInit && !isFirst) {//已经初始化&&是不是首次进入
            checkUserVisibleHint(isVisibleToUser);
        }
        readyLoadData();
    }

    private void readyLoadData() {
        if (!isInit) {//setUserVisibleHint( ) 方法是系统自动回调 在fragment生命周期调用之前
            return;
        }
        if (isFirst && getUserVisibleHint()) {
            loadData();
            isFirst = false;//为防止重复加载数据 將标记置为false
        }
    }

    /**
     * 每次点击tab都会调用该方法
     */
    protected void checkUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * fragment创建后 主动加载数据 一次
     */
    protected void loadData() {
    }

    protected abstract int getDisplayView(LayoutInflater inflater, ViewGroup container);

    protected abstract void initView();
}
