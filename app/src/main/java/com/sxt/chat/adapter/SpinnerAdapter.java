package com.sxt.chat.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sxt.chat.R;

import java.util.List;

/**
 * Created by 11837 on 2018/6/29.
 */

public class SpinnerAdapter implements android.widget.SpinnerAdapter {
    private List<String> data;
    private Context context;

    public SpinnerAdapter(List<String> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) LayoutInflater.from(context).inflate(R.layout.item_spinner, parent, false);
        textView.setText(data.get(position));
//        ViewHolder holder = null;
//        if (convertView == null) {
//            convertView = LayoutInflater.from(context).inflate(R.layout.item_spinner, parent, false);
//            holder = new ViewHolder();
//            holder.title = convertView.findViewById(R.id.title);
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//            holder.title.setText(data.get(position));
//        }
        return textView;
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView title = (TextView) LayoutInflater.from(context).inflate(R.layout.item_spinner_drop, parent, false);
        title.setText(data.get(position));
        return title;
    }
}
