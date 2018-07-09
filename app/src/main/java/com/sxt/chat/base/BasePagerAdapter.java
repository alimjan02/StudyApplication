package com.sxt.chat.base;

import android.content.Context;
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

    public BasePagerAdapter(Context context, List<T> datas) {
        this.context = context;
        this.datas = datas;
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

    @Override
    public abstract Object instantiateItem(ViewGroup container, int position);
}
