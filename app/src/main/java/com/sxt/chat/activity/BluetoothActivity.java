package com.sxt.chat.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.sxt.chat.App;
import com.sxt.chat.R;
import com.sxt.chat.base.BaseRecyclerAdapter;
import com.sxt.chat.base.HeaderActivity;
import com.sxt.chat.db.User;
import com.sxt.chat.dialog.AlertDialogBuilder;
import com.sxt.chat.utils.Prefs;
import com.sxt.chat.utils.ToastUtil;
import com.sxt.chat.utils.glide.GlideCircleTransformer;
import com.sxt.chat.view.BezierCurveRadar;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobUser;

public class BluetoothActivity extends HeaderActivity implements View.OnClickListener {

    private final int REQUEST_CODE_ENABLE_BLUETOOTH = 0;
    private final int REQUEST_CODE_LOCATION = 500;
    private ImageView status;
    private BluetoothReceiver receiver;
    private RecyclerView recyclerView;
    private BluetoothDeviceAdapter adapter;
    private BluetoothAdapter bluetoothAdapter;
    private BezierCurveRadar bezierCurveRadar;
    private ImageView image;
    private Handler handler = new Handler();
    private Map<String, BluetoothDevice> bluetoothDeviceMap = new LinkedHashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        setTitle(R.string.bluetooth);
        bezierCurveRadar = findViewById(R.id.bezier_radar);
        recyclerView = findViewById(R.id.recyclerView);
        image = findViewById(R.id.img);
        findViewById(R.id.img_container).setOnClickListener(this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        receiver = new BluetoothReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//连接状态改变
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);//蓝牙已连接
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);//断开连接
        filter.addAction(BluetoothDevice.ACTION_FOUND);//用于接收发现蓝牙设备的广播
        filter.setPriority(1000);
        registerReceiver(receiver, filter);
        updateHeadPortrait();

        adViewToRightContainer();
    }

    private void adViewToRightContainer() {
        status = new ImageView(this);
        refreshStatus();
        setRightContainer(status);
    }

    /**
     * 刷新蓝牙的状态
     */
    private void refreshStatus() {
        if (bluetoothAdapter == null) {
            status.setImageResource(R.drawable.ic_vector_bluetooth_disabled_white_24dp);
        } else {
            if (!bezierCurveRadar.isStop()) {
                status.setImageResource(R.drawable.ic_vector_bluetooth_searching_white_24dp);
            } else if (bluetoothAdapter.isEnabled()) {
                status.setImageResource(R.drawable.ic_vector_bluetooth_connected_white_24dp);
            } else {
                status.setImageResource(R.drawable.ic_vector_bluetooth_disabled_white_24dp);
            }
        }
    }

    private void updateHeadPortrait() {
        User user = BmobUser.getCurrentUser(User.class);
        if (user != null) {
            if ("M".equals(user.getGender())) {
                update(user.getImgUri(), R.mipmap.men);
            } else {
                update(user.getImgUri(), R.mipmap.female);
            }
        }
    }

    private void update(String url, int placeHolder) {
        Glide.with(this)
                .load(url)
                .error(placeHolder)
                .placeholder(placeHolder)
                .bitmapTransform(new GlideCircleTransformer(this))
//                .skipMemoryCache(true)//跳过内存
//                .diskCacheStrategy(DiskCacheStrategy.NONE)//想要生效必须添加 跳过内存
                .signature(new StringSignature(Prefs.getInstance(App.getCtx()).getString(Prefs.KEY_USER_HEADER_IMAGE_FLAG, "")))
                .into((ImageView) findViewById(R.id.img));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.img_container) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requestLocationPermission();
            } else {
                openBluetooth();
            }
        }
    }

    /**
     * 如果当前正在搜素，就停止搜索
     */
    private void openBluetooth() {
        if (bluetoothAdapter == null) {
            ToastUtil.showToast(this, "当前设备不支持蓝牙");
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            startOpenBluetooth();
//            bluetoothAdapter.enable();
            return;
        }
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();//取消搜索
            refreshRadar(true);
            return;
        }
        startDiscovery();//开始搜索
    }

    /**
     * 请求开启蓝牙
     */
    private void startOpenBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_CODE_ENABLE_BLUETOOTH);
    }

    /**
     * 开始扫描附近的蓝牙设备
     */
    private void startDiscovery() {
        if (bluetoothAdapter != null) {
            refreshRadar(false);
            findViewById(R.id.img_container).setEnabled(false);//防止重复点击
            handler.removeCallbacksAndMessages(null);
            handler.postDelayed(() -> {
                bluetoothAdapter.startDiscovery();
                findViewById(R.id.img_container).setEnabled(true);
            }, 1000);
        }
    }

    /**
     * 刷新雷达绘制
     */
    private void refreshRadar(boolean isStop) {
        bezierCurveRadar.setStop(isStop);
        if (isStop) {
            bluetoothDeviceMap.clear();
            refreshList();
        }
        refreshStatus();
    }

    private void refreshList() {
        List<BluetoothDevice> bluetoothDevices = new ArrayList<>();
        String[] keys = bluetoothDeviceMap.keySet().toArray(new String[]{});
        for (String key : keys) {
            bluetoothDevices.add(bluetoothDeviceMap.get(key));
        }
        if (adapter == null) {
            adapter = new BluetoothDeviceAdapter(this, bluetoothDevices);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(adapter);
//            adapter.setOnItemClickListener((position, bluetoothDevice) -> {
//                new AcceptThread(bluetoothAdapter).start();
//                new ConnectThread(bluetoothAdapter, bluetoothDevice).start();
//            });
        } else {
            adapter.notifyDataSetChanged(bluetoothDevices);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                startDiscovery();
            }
        }
    }

    class BluetoothReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) return;
            switch (action) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_OFF:
                            refreshRadar(true);
                            break;
                        case BluetoothAdapter.STATE_ON:
                            refreshStatus();
                            break;
                    }
                    break;
                case BluetoothDevice.ACTION_FOUND://搜索附近的蓝牙设备
                    // Discovery找到了一个设备。
                    //从Intent 获取BluetoothDevice对象及其信息。
                    BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String deviceName = bluetoothDevice.getName();
                    String deviceAddress = bluetoothDevice.getAddress(); // MAC地址
                    String format = String.format("找到了一个蓝牙设备 ：deviceName -> %s ,address -> %s", deviceName, deviceAddress);
                    Log.e(TAG, format);

                    bluetoothDeviceMap.put(deviceAddress, bluetoothDevice);
                    refreshList();

                    break;
            }
        }
    }

    class BluetoothDeviceAdapter extends BaseRecyclerAdapter<BluetoothDevice> {

        public BluetoothDeviceAdapter(Context context, List<BluetoothDevice> data) {
            super(context, data);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.item_bluetooth_advice, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            ViewHolder holder = (ViewHolder) viewHolder;
            String name = data.get(position).getName();
            holder.title.setText(TextUtils.isEmpty(name) ? "未知" : name);
            holder.address.setText(data.get(position).getAddress());
            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(v -> {
                    onItemClickListener.onClick(position, getItem(position));
                });
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            public TextView title, address;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.title);
                address = itemView.findViewById(R.id.address);
            }
        }
    }

    @Override
    public void onPermissionsAllowed(int requestCode, String[] permissions, int[] grantResults) {
        super.onPermissionsAllowed(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION) {
            openBluetooth();
        }
    }

    @Override
    public void onPermissionsRefused(int requestCode, String[] permissions, int[] grantResults) {
        super.onPermissionsRefused(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION) {
            requestLocationPermission();
        }
    }

    @Override
    public void onPermissionsRefusedNever(int requestCode, String[] permissions, int[] grantResults) {
        super.onPermissionsRefusedNever(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION) {
            String appName = getString(R.string.app_name);
            String message = String.format(getString(R.string.permission_request_LOCATION), appName);
            SpannableString span = new SpannableString(message);
            span.setSpan(new TextAppearanceSpan(this, R.style.text_color_2_15_style), 0, message.indexOf(appName), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            int start = message.indexOf(appName) + appName.length();
            span.setSpan(new TextAppearanceSpan(this, R.style.text_color_1_17_bold_style), message.indexOf(appName), start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            span.setSpan(new TextAppearanceSpan(this, R.style.text_color_2_15_style), start, message.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            showPermissionRefusedNeverDialog(span);
        }
    }

    /**
     * 请求位置权限
     */
    private void requestLocationPermission() {
        checkPermission(REQUEST_CODE_LOCATION, Manifest.permission_group.LOCATION, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,});
    }

    /**
     * 权限被彻底禁止后 , 弹框提醒用户去开启
     */
    private void showPermissionRefusedNeverDialog(CharSequence message) {
        new AlertDialogBuilder(this)
                .setTitle(R.string.message_alert, true)
                .setMessage(message)
                .setLeftButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setRightButton(R.string.confirm, (dialog, which) -> {
                    dialog.dismiss();
                    goToAppSettingsPage();
                })
                .setShowLine(true)
                .setCanceledOnTouchOutside(false)
                .show();
    }

    @Override
    protected void onDestroy() {
        if (receiver != null) unregisterReceiver(receiver);
        super.onDestroy();
    }
}
