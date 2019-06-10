package com.sxt.chat.base;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by izhaohu on 2017/10/12.
 */

public class BaseFragmentPagerAdapter extends FragmentPagerAdapter {

    private FragmentManager fm;
    private String[] titles;
    private Context context;
    private List<Fragment> fragments;

    public BaseFragmentPagerAdapter(FragmentManager fm, Context context, List<Fragment> fragments, String[] titles) {
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
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    //    public void notifyDataSetChanged(List<Fragment> fragments) {
//        if (fragments != null) {
//            this.fragments = fragments;
//            notifyDataSetChanged();
//        }
//    }
//    @Override
//    public int getItemPosition(Object object) {
//        return PagerAdapter.POSITION_NONE;
//    }
//
//    @Override
//    public void notifyDataSetChanged() {
//
//        if (this.fragments != null) {
//            FragmentTransaction transaction = fm.beginTransaction();
//            for (int i = 0; i < this.fragments.size(); i++) {
//                transaction.remove(this.fragments.get(i));
//            }
//            transaction.commit();
//            fm.executePendingTransactions();
//        }
//        super.notifyDataSetChanged();
//    }
}
