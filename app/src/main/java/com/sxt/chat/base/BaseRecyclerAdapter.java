/*
 * Copyright 2017 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sxt.chat.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by sxt on 2017/10/3.
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater mInflater;
    protected Context context;
    protected List<T> data;
    protected OnItemClickListener<T> onItemClickListener;
    private ContentObserVer contentObserVer;

    public BaseRecyclerAdapter(Context context) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    public BaseRecyclerAdapter(Context context, List<T> data) {
        this(context);
        this.data = data;
    }

    public LayoutInflater getInflater() {
        return mInflater;
    }

    @Override
    public abstract RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position);

    @Override
    public int getItemCount() {
        int count = data == null ? 0 : data.size();
        if (contentObserVer != null) {
            contentObserVer.notify(count, this.data);
        }
        return count;
    }

    public T getItem(int position) {
        if (data != null && data.size() > position) {
            return data.get(position);
        }
        return null;
    }

    public void notifyDataSetChanged(List<T> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged(List<T> data, boolean isRefesh) {
        if (isRefesh) {
            if (data != null) {
                this.data = data;
            }
        } else {
            if (this.data == null && data != null) {
                this.data = data;
            } else {
                if (data != null) {
                    this.data.addAll(data);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener<T> {
        void onClick(int position, T t);
    }

    public void setContentObserver(ContentObserVer contentObserVer) {
        this.contentObserVer = contentObserVer;
    }

    public interface ContentObserVer {
        void notify(int count, Object object);
    }
}
