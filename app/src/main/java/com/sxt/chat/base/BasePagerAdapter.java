package com.sxt.chat.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by izhaohu on 2017/7/17.
 */

public abstract class BasePagerAdapter<T> extends PagerAdapter {

    protected Context context;
    protected List<T> datas;
    private String[] titles;

    protected BasePagerAdapter(Context context, List<T> datas, String... titles) {
        this.context = context;
        this.datas = datas;
        this.titles = titles;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (titles != null && titles.length == datas.size()) {
            return titles[position];
        }
        return super.getPageTitle(position);
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public abstract Object instantiateItem(@NonNull ViewGroup container, int position);
}
