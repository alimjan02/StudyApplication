package com.sxt.chat.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxt.chat.R;
import com.sxt.chat.utils.Px2DpUtil;
import com.sxt.chat.utils.WifiUtils;

import java.util.ArrayList;
import java.util.List;

public class WiFiListAdapter extends BaseExpandableListAdapter {

    protected Context context;
    private List<WifiUtils.WifiScanResult> groupDATA1;
    private List<WifiUtils.WifiScanResult> groupDATA2;
    private List<String> titles;

    public WiFiListAdapter(Context context, List<WifiUtils.WifiScanResult> groupDATA1, List<WifiUtils.WifiScanResult> groupDATA2) {
        this.context = context;
        this.groupDATA1 = groupDATA1;
        this.groupDATA2 = groupDATA2;
        initData();
    }

    private void initData() {
        titles = new ArrayList<>();
        titles.add("连接的WLAN");
        titles.add("选择附近的WLAN");
    }

    /**
     * 分组数量
     */
    @Override
    public int getGroupCount() {
        return titles == null ? 0 : titles.size();
    }

    /**
     * 每个组 对应的 子view的数量
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        if (groupPosition == 0) return groupDATA1 == null ? 0 : groupDATA1.size();
        if (groupPosition == 1) return groupDATA2 == null ? 0 : groupDATA2.size();
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return titles.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (groupPosition == 0) return groupDATA1.get(childPosition);
        if (groupPosition == 1) return groupDATA2.get(childPosition);
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Log.i("sxt", "getGroupView  组标题 开始刷新 groupPosition = " + groupPosition + " isExpanded = " + isExpanded);

        TitleViewHolder holder;
        if (convertView == null) {
            holder = new TitleViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_wifi_title, null);
            holder.line = convertView.findViewById(R.id.line);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        } else {
            holder = (TitleViewHolder) convertView.getTag();
        }
        holder.title.setText(titles.get(groupPosition));
        if (groupPosition == 0) {
            holder.line.setVisibility(View.GONE);
            ViewGroup.LayoutParams lp = holder.line.getLayoutParams();
            lp.height = Px2DpUtil.dip2px(context, 1);
            holder.line.setLayoutParams(lp);
        } else {
            holder.line.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams lp = holder.line.getLayoutParams();
            lp.height = Px2DpUtil.dip2px(context, 10);
            holder.line.setLayoutParams(lp);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Log.i("sxt", "getChildView  childView 开始刷新 groupPosition = " + groupPosition + " childPosition = " + childPosition);

        ContentViewHolder holder;
        if (convertView == null) {
            holder = new ContentViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_select_wifi, null);
            holder.root = convertView.findViewById(R.id.item_select_wifi_root);
            holder.line = convertView.findViewById(R.id.item_wifi_line);
            holder.tvWifiName = (TextView) convertView.findViewById(R.id.item_wifi_name);
            holder.tvWifiType = (TextView) convertView.findViewById(R.id.item_wifi_type);
            holder.imgWifiLevel = (ImageView) convertView.findViewById(R.id.item_wifi_level);
            holder.imgWifiLock = (ImageView) convertView.findViewById(R.id.item_wifi_lock);
            convertView.setTag(holder);
        } else {
            holder = (ContentViewHolder) convertView.getTag();
        }
        WifiUtils.WifiScanResult result;
        if (groupPosition == 0) {
            result = groupDATA1.get(childPosition);
            if (WifiUtils.getInstance().getmWifiManager().getConnectionInfo().getSSID().equals(result.SSID)) {
                holder.tvWifiType.setText("已连接");
                holder.tvWifiName.setTextColor(ContextCompat.getColor(context, R.color.main_green));
            } else {
                holder.tvWifiType.setText("已保存");
                holder.tvWifiName.setTextColor(ContextCompat.getColor(context, R.color.text_color_1));
            }
        } else {
            result = groupDATA2.get(childPosition);
            if (WifiUtils.getInstance().getmWifiManager().getConnectionInfo().getSSID().equals(result.SSID)) {
                holder.tvWifiType.setText("");
                holder.tvWifiName.setTextColor(ContextCompat.getColor(context, R.color.text_color_1));
            } else {
                holder.tvWifiType.setText("");
                holder.tvWifiName.setTextColor(ContextCompat.getColor(context, R.color.text_color_1));
            }
        }
        if ("[ESS]".equals(result.capabilities)) {
            holder.imgWifiLock.setVisibility(View.INVISIBLE);
        } else {
            holder.imgWifiLock.setVisibility(View.VISIBLE);
        }
        holder.line.setVisibility(View.VISIBLE);
        if (result.SSID != null) {
            holder.tvWifiName.setText(result.SSID.replaceAll("\"", ""));
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;//响应childItem的点击事件 默认返回false(不可点击)
    }

    public void notifyAdapter(ArrayList<WifiUtils.WifiScanResult> scanResultHaveLinked, ArrayList<WifiUtils.WifiScanResult> scanResultNoLinked) {
        this.groupDATA1 = scanResultHaveLinked;
        this.groupDATA2 = scanResultNoLinked;
        notifyDataSetChanged();
    }

    protected class TitleViewHolder {
        public TextView title;
        public View line;
    }


    class ContentViewHolder {
        public View root;
        public View line;
        public TextView tvWifiName;
        public TextView tvWifiType;
        public ImageView imgWifiLevel;
        public ImageView imgWifiLock;
    }
}