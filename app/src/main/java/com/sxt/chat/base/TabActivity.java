package com.sxt.chat.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioButton;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by sxt on 2018/1/17.
 */
public abstract class TabActivity extends HeaderActivity {
    private Fragment prefragment;
    private FragmentManager fragmentManager;
    private Map<Integer, BaseFragment> fragmentMap;
    private OnCheckedChangeListener onCheckedChangeListener;
    private int containerId;
    private Map<Integer, RadioButton> tabs;
    private String[] titles;

    protected void initFragment(Map<Integer, BaseFragment> fragmentMap, Map<Integer, RadioButton> tabs, String[] titles, int containerId, int defaultChecked) {
        fragmentManager = getSupportFragmentManager();
        this.fragmentMap = fragmentMap;
        this.containerId = containerId;
        this.tabs = tabs;
        this.titles = titles;
        if (tabs != null && fragmentMap != null && tabs.size() == fragmentMap.size()) {
            for (Map.Entry<Integer, RadioButton> entry : tabs.entrySet()) {
                entry.getValue().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changeFragment((Integer) view.getTag());
                    }
                });
                entry.getValue().setTag(entry.getKey());
            }
            changeFragment(defaultChecked);
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (prefragment != null && prefragment.isResumed()) {
            prefragment.setUserVisibleHint(prefragment.getUserVisibleHint());
        }
    }

    protected void changeFragment(int checkedId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        BaseFragment fragment = fragmentMap.get(checkedId);
        if (fragment != null && !fragment.equals(prefragment)) {
            changeTab(checkedId);
            if (prefragment != null) {
                transaction.hide(prefragment);
                prefragment.setUserVisibleHint(false);
            }
            if (!fragment.isAdded()) {
                transaction.add(containerId, fragment, fragment.getClass().getName());
            } else {
                transaction.show(fragment);
            }
            if (onCheckedChangeListener != null) {
                onCheckedChangeListener.onCheckedChange(checkedId);
            }
            prefragment = fragment;
            prefragment.setUserVisibleHint(true);
            transaction.commit();

        }
    }

    private void changeTab(int checkedId) {
        if (tabs != null) {
            if (titles != null && titles.length > checkedId) {
                onTabCheckedChange(titles, checkedId);
            }
            for (Map.Entry<Integer, RadioButton> entry : tabs.entrySet()) {
                if (entry.getKey() == checkedId) {
                    entry.getValue().setChecked(true);
                } else {
                    entry.getValue().setChecked(false);
                }
            }
        }
    }

    protected void onTabCheckedChange(String[] titles, int checkedId) {

    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChange(int checkedId);
    }

    protected void clearFragments() {
        try {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            Iterator<Map.Entry<Integer, BaseFragment>> iterator = fragmentMap.entrySet().iterator();
            while (iterator.hasNext()) {
                transaction.remove(iterator.next().getValue());
            }
            transaction.commitAllowingStateLoss();//尽管状态丢失 也要销毁这些fragment
            fragmentManager.executePendingTransactions();//立即执行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        clearFragments();
        super.onDestroy();
    }
}
