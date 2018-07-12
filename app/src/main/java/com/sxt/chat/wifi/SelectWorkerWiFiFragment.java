package com.sxt.chat.wifi;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxt.chat.App;
import com.sxt.chat.R;
import com.sxt.chat.base.BaseFragment;
import com.sxt.chat.base.HeaderActivity;
import com.sxt.chat.dialog.LoadingDialog;
import com.sxt.chat.utils.Prefs;
import com.sxt.chat.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import static android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION;
import static android.net.wifi.WifiManager.WIFI_STATE_CHANGED_ACTION;

/**
 * Created by izhaohu on 2017/12/15.
 */
public class SelectWorkerWiFiFragment extends BaseFragment implements View.OnClickListener {

    private HeaderActivity activity;
    private ImageView imgLevel;
    private TextView tvName;
    //    private EditText tvPwd;
    private TextView tvNext;
    private Bundle bundle;
    private WifiManager wifiManager;
    private WifiReceiver wifiReceiver;
    private View view;
    private View wifiArrowRoot;
    private DialogBuilder builder;

    private ExpandableListView expandableListView;
    private ArrayList<ScanResult> scanResults = new ArrayList<>();
    private ArrayList<WifiUtils.WifiScanResult> scanResultHaveLinked = new ArrayList<>();
    private ArrayList<WifiUtils.WifiScanResult> scanResultNoLinked = new ArrayList<>();
    private WifiUtils.WifiScanResult selectedScanResult;
    private WiFiListAdapter wifiAdapter;
    private View inputWifiItem;
    private DialogBuilder inputWifiBuilder;
    private TextView tvWifiName;
    private EditText etWifiPwd;
    private LoadingDialog loadingDialog;
    //    private WiFiCountDownTimer wiFiCountDownTimer;
    private final int REQUEST_OPEN_WIFI = 1;
    private final int REQUEST_OPEN_WIFI2 = 2;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (HeaderActivity) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();
        loadingDialog = new LoadingDialog(activity);
        boolean b = checkPermission(REQUEST_OPEN_WIFI);
        if (b) {
            initWifiSettings();
        }
    }

    private boolean checkPermission(int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(App.getCtx(), Manifest.permission_group.LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_WIFI_STATE,
                        }, requestCode);
                return false;
            }
            return true;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_OPEN_WIFI:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initWifiSettings();
                } else {
                    if (!shouldShowRequestPermissionRationale(permissions[0])) {
                        //此时的意思是 用户设置了不再提醒 权限授权
                        ToastUtil.showToast(activity, R.string.allow_wifi);
                    }
                }
                break;
            case REQUEST_OPEN_WIFI2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initWifiSettings();
                    showDialog();
                } else {
                    if (!shouldShowRequestPermissionRationale(permissions[0])) {
                        //此时的意思是 用户设置了不再提醒 权限授权
                        ToastUtil.showToast(activity, R.string.allow_wifi);
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = LayoutInflater.from(activity).inflate(R.layout.fragment_select_worker_wifi_layout, null);
            wifiArrowRoot = view.findViewById(R.id.wifi_arrow);
            imgLevel = (ImageView) view.findViewById(R.id.img_wifi_level);
            tvName = (TextView) view.findViewById(R.id.tv_wifi_name);
//            tvPwd = (EditText) view.findViewById(R.id.et_wifi_pwd);
            tvNext = (TextView) view.findViewById(R.id.tv_next);
            tvNext.setOnClickListener(this);
            wifiArrowRoot.setOnClickListener(this);
        }
        activity.setTitle("选择WIFI");

        return view;
    }

    private void initWifiSettings() {
        if (wifiManager == null) {
            wifiManager = WifiUtils.getInstance().getmWifiManager();
        }
        if (!WifiUtils.getInstance().isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);//开启WIFI
        }
        if (wifiReceiver == null) {
            wifiReceiver = new WifiReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(SCAN_RESULTS_AVAILABLE_ACTION);
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            filter.setPriority(1000);
            getActivity().registerReceiver(wifiReceiver, filter);
        }
        wifiManager.startScan();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.wifi_arrow://选择WIFI
                boolean b = checkPermission(REQUEST_OPEN_WIFI2);
                if (b) {
                    initWifiSettings();
                    showDialog();
                }
                break;

            case R.id.tv_next:
//                Intent it = new Intent();
//                ComponentName cn = new ComponentName("com.android.settings","com.android.settings.wifi.WifiSettings");
//                it.setComponent(cn);
//                startActivity(it);
                next();
                break;
        }
    }

    private void showDialog() {
        if (builder == null) {
            builder = new DialogBuilder(activity, R.style.Theme_Design_BottomSheetDialog);
            View wifiItem = LayoutInflater.from(activity).inflate(R.layout.item_wifi_list_layout, null);
            initWifiItem(wifiItem);
            builder.replaceView(wifiItem).setCancelableOutSide(false);
        }
        builder.show(1, Gravity.TOP);
    }

    private void next() {
        if (selectedScanResult == null || selectedScanResult.SSID == null || !WifiUtils.getInstance().initSSID(selectedScanResult.SSID).equals(WifiUtils.getInstance().initSSID(wifiManager.getConnectionInfo().getSSID()))) {
            ToastUtil.showToast(activity, "请确认您选择的Wi-Fi是否正常连接");
            return;
        }
    }

    private void initWifiItem(View wifiItem) {
        expandableListView = (ExpandableListView) wifiItem.findViewById(R.id.wifi_lsitview);
        expandableListView.setGroupIndicator(null);//隐藏系统自带的箭头
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                return true;//屏蔽 分组点击展开
            }
        });
        dealwithLinkedWifiData();
        wifiAdapter = new WiFiListAdapter(activity, scanResultHaveLinked, scanResultNoLinked);
        expandableListView.setAdapter(wifiAdapter);
        for (int i = 0; i < wifiAdapter.getGroupCount(); i++) {
            expandableListView.expandGroup(i);//将全部分组展开
        }
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                selectedScanResult = (WifiUtils.WifiScanResult) wifiAdapter.getChild(groupPosition, childPosition);
                final String ssid = selectedScanResult.SSID.replaceAll("\"", "");
                if ("[ESS]".equals(selectedScanResult.capabilities)) {
                    WifiUtils.getInstance().connectWifi(ssid, "", WifiUtils.WIFICIPHER_NOPASS).setWifiStateListener(new WifiUtils.WifiStateListener() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onFailed(Exception e) {
                            stopWatch(null, null);
                        }

                        @Override
                        public void success(String SSID, String pwd) {

                        }
                    });
                } else {
                    connectWiFi(selectedScanResult);
                }
                tvName.setText(ssid);
                return false;
            }
        });
    }

    public void connectWiFi(final WifiUtils.WifiScanResult result) {
        if (inputWifiBuilder == null) {
            inputWifiBuilder = new DialogBuilder(activity);
            inputWifiItem = LayoutInflater.from(activity).inflate(R.layout.item_input_wifi_pwd, null);
            tvWifiName = (TextView) inputWifiItem.findViewById(R.id.tv_wifi_name);
            etWifiPwd = (EditText) inputWifiItem.findViewById(R.id.et_wifi_pwd);
            inputWifiItem.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    inputWifiBuilder.dismiss();
                }
            });
            inputWifiBuilder.replaceView(inputWifiItem).setCancelableOutSide(false);
        }
        inputWifiItem.findViewById(R.id.tv_wifi_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String trim = etWifiPwd.getText().toString().trim();
                if (TextUtils.isEmpty(trim)) {
                    ToastUtil.showToast(activity, "请输入Wi-Fi密码");
                    return;
                }
//                startWatch();
                if (selectedScanResult != null) {
                    selectedScanResult.PWD = trim;
                }
                WifiUtils.getInstance().setWifiStateListener(new WifiUtils.WifiStateListener() {
                    @Override
                    public void onStart() {
                        startWatch();
                    }

                    @Override
                    public void onFailed(Exception e) {
                        stopWatch(null, null);
                    }

                    @Override
                    public void success(String SSID, String pwd) {
                        stopWatch(SSID, pwd);
                    }
                }).connectWifi(result.SSID.replaceAll("\"", ""), trim, WifiUtils.WIFICIPHER_WPA);
            }
        });
        tvWifiName.setText(result.SSID.replaceAll("\"", ""));
        etWifiPwd.setText("");
        inputWifiBuilder.show(0.875f, Gravity.CENTER);
    }

    private WifiUtils.WifiScanResult currentLinked;

    private void dealwithLinkedWifiData() {
        scanResultHaveLinked.clear();
        scanResultNoLinked.clear();
        List<WifiConfiguration> networks = wifiManager.getConfiguredNetworks();
        if (networks != null) {
            for (WifiConfiguration config : networks) {
                WifiUtils.WifiScanResult result = new WifiUtils.WifiScanResult(
                        config.BSSID,
                        config.SSID,
                        WifiUtils.getInstance().getSecurity(config),
                        0);
                if (wifiManager.getConnectionInfo().getSSID().equals(config.SSID)) {
                    currentLinked = result;
                }
                scanResultHaveLinked.add(result);
            }
        }

        if (scanResultHaveLinked.contains(currentLinked)) {
            scanResultHaveLinked.remove(currentLinked);
            scanResultHaveLinked.add(0, currentLinked);
        }

        if (scanResults != null) {
            for (ScanResult s : scanResults) {
                if (s != null && s.SSID != null && !TextUtils.isEmpty(s.SSID)) {
                    WifiUtils.WifiScanResult result = new WifiUtils.WifiScanResult(
                            s.BSSID,
                            s.SSID,
                            s.capabilities,
                            s.level);
                    scanResultNoLinked.add(result);
                }
            }
        }
        if (scanResultNoLinked.size() == 0) {
            ToastUtil.showToast(getActivity(), "请确认设备Wi-Fi和GPS已开启");
        }
    }

    private class WifiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SCAN_RESULTS_AVAILABLE_ACTION)) {
                scanResults = (ArrayList<ScanResult>) wifiManager.getScanResults();
                if (scanResults != null && scanResults.size() > 0) {
//                    wifiArrowRoot.setEnabled(true);
                    tvName.setText(wifiManager.getConnectionInfo().getSSID().replaceAll("\"", ""));
                } else {
//                    wifiManager.startScan();
//                    wifiArrowRoot.setEnabled(false);
                    tvName.setText("暂未发现周边W-Fi,请确认设备Wi-Fi和GPS已开启");
                }
                notifyDataSetChanged();

            } else if (intent.getAction().equals(WIFI_STATE_CHANGED_ACTION)) {
            }

        }
    }

    private void notifyDataSetChanged() {
        dealwithLinkedWifiData();
        if (builder != null) {
            wifiAdapter.notifyAdapter(scanResultHaveLinked, scanResultNoLinked);
        }
    }

    private Handler handler = new Handler();

    private void startWatch() {
//        if (wiFiCountDownTimer == null) {
//            wiFiCountDownTimer = new WiFiCountDownTimer(16 * 1000, 4 * 1000);
//        }
//        wiFiCountDownTimer.start();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingDialog.show();
            }
        });
    }

    public void stopWatch(final String SSID, final String pwd) {

//        handler.removeCallbacksAndMessages(null);
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (wiFiCountDownTimer != null) {
//                    wiFiCountDownTimer.cancel();
//                    wiFiCountDownTimer = null;
//                }

//                dismissDialogs();

//                if (selectedScanResult != null) {
//                    String ssid = wifiManager.getConnectionInfo().getSSID();
//                    if (ssid != null) {
//                        if (selectedScanResult.SSID.contains(ssid) || ssid.contains(selectedScanResult.SSID)) {
//                            ToastUtil.showToast(activity, "连接成功");
//                            tvName.setText(selectedScanResult.SSID.replaceAll("\"", ""));
//                            tvPwd.setText(selectedScanResult.PWD);
//                        } else {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissDialogs();
                if (!TextUtils.isEmpty(SSID) && !TextUtils.isEmpty(pwd)) {
                    ToastUtil.showToast(activity, "连接成功");
                    tvName.setText(SSID);
                    selectedScanResult = new WifiUtils.WifiScanResult(null, SSID, "", 0);
                } else {
                    ToastUtil.showToast(activity, "连接出错,请重试");
                    selectedScanResult = null;
                }
            }
        });
//                        }
//                    }
//                } else {
//        ToastUtil.showToast(activity, "连接出错,请重新选择Wi-Fi");
//                }
//            }
//        }, 3000);
    }


//    private class WiFiCountDownTimer extends CountDownTimer {
//
//        public WiFiCountDownTimer(long millisInFuture, long countDownInterval) {
//            super(millisInFuture, countDownInterval);
//        }
//
//        @Override
//        public void onTick(long millisUntilFinished) {
//            if (selectedScanResult != null && wifiManager.getConnectionInfo() != null && wifiManager.getConnectionInfo().getSSID() != null) {
//                String ssid = wifiManager.getConnectionInfo().getSSID();
//                if (WifiUtils.getInstance().initSSID(ssid).equals(WifiUtils.getInstance().initSSID(selectedScanResult.SSID))) {
//                    stopWatch(SSID, pwd);
//                }
//            }
//        }
//
//        @Override
//        public void onFinish() {
//            stopWatch(SSID, pwd);
//        }
//    }

    private void dismissDialogs() {
        if (inputWifiBuilder != null) {
            inputWifiBuilder.dismiss();
        }
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        if (builder != null) {
            builder.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        if (wifiReceiver != null) {
            activity.unregisterReceiver(wifiReceiver);
        }
//        if (wiFiCountDownTimer != null) {
//            wiFiCountDownTimer.cancel();
//        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        WifiUtils.getInstance().cancel();
        super.onDestroy();
    }

}
