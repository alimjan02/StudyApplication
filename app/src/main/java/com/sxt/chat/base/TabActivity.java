package com.sxt.chat.base;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by sxt on 2018/1/17.
 */
public abstract class TabActivity extends HeaderActivity {
    private Fragment preFragment;
    private FragmentManager fragmentManager;
    private Map<Integer, BaseFragment> fragmentMap;
    private OnCheckedChangeListener onCheckedChangeListener;
    private int containerId;

    protected void initFragment(Map<Integer, BaseFragment> fragmentMap, BottomNavigationView bottomNavigationView, int containerId, int defaultChecked) {
        fragmentManager = getSupportFragmentManager();
        this.fragmentMap = fragmentMap;
        this.containerId = containerId;
        if (bottomNavigationView != null && fragmentMap != null && bottomNavigationView.getMenu().size() == fragmentMap.size()) {
            bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
                changeFragment(menuItem);
                menuItem.setChecked(true);
                return false;
            });
            changeFragment(bottomNavigationView.getMenu().getItem(defaultChecked));
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (preFragment != null && preFragment.isResumed()) {
            preFragment.setUserVisibleHint(preFragment.getUserVisibleHint());
        }
    }

    protected void changeFragment(MenuItem menuItem) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        BaseFragment fragment = fragmentMap.get(menuItem.getItemId());
        if (fragment != null && !fragment.equals(preFragment)) {
            onTabCheckedChange(menuItem);
            if (preFragment != null) {
                transaction.hide(preFragment);
                preFragment.setUserVisibleHint(false);
            }
            if (!fragment.isAdded()) {
                transaction.add(containerId, fragment, fragment.getClass().getName());
            } else {
                transaction.show(fragment);
            }
            if (onCheckedChangeListener != null) {
                onCheckedChangeListener.onCheckedChange(menuItem.getItemId());
            }
            preFragment = fragment;
            preFragment.setUserVisibleHint(true);
            transaction.commit();
        }
    }

    protected void onTabCheckedChange(MenuItem menuItem) {

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
