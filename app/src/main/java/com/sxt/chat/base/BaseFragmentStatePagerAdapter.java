package com.sxt.chat.base;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;

import java.util.List;

/**
 * Created by izhaohu on 2017/10/12.
 */

public class BaseFragmentStatePagerAdapter<T> extends FragmentStatePagerAdapter {

    private String[] titles;
    private FragmentManager fm;
    private Context context;
    private List<Fragment> fragments;
    private List<T> childData;

    public BaseFragmentStatePagerAdapter(FragmentManager fm, Context context, List<Fragment> fragments, String[] titles) {
        super(fm);
        this.fm = fm;
        this.context = context;
        this.fragments = fragments;
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments == null ? 0 : fragments.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    public List<T> getChildData() {
        return childData;
    }

    public void setChildData(List<T> childData) {
        this.childData = childData;
    }

    @Override
    public void notifyDataSetChanged() {
        if (this.fragments != null) {
            FragmentTransaction transaction = fm.beginTransaction();
            for (int i = 0; i < this.fragments.size(); i++) {
                transaction.remove(fragments.get(i));
            }
            transaction.commitAllowingStateLoss();
            fm.executePendingTransactions();
        }
        super.notifyDataSetChanged();
    }
}
