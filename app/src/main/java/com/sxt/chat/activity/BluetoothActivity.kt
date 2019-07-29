package com.sxt.chat.activity

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.TextAppearanceSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.bmob.v3.BmobUser
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.StringSignature
import com.sxt.chat.App
import com.sxt.chat.R
import com.sxt.chat.base.BaseRecyclerAdapter
import com.sxt.chat.base.HeaderActivity
import com.sxt.chat.db.User
import com.sxt.chat.dialog.AlertDialogBuilder
import com.sxt.chat.utils.Prefs
import com.sxt.chat.utils.ToastUtil
import com.sxt.chat.utils.glide.GlideCircleTransformer
import com.sxt.chat.view.BezierCurveRadar
import java.util.*

class BluetoothActivity : HeaderActivity(), View.OnClickListener {

    private val REQUEST_CODE_ENABLE_BLUETOOTH = 0
    private val REQUEST_CODE_LOCATION = 500
    private var status: ImageView? = null
    private var receiver: BluetoothReceiver? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: BluetoothDeviceAdapter? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bezierCurveRadar: BezierCurveRadar? = null
    private var image: ImageView? = null
    private val handler = Handler()
    private val bluetoothDeviceMap = LinkedHashMap<String, BluetoothDevice>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)
        setTitle(R.string.bluetooth)
        bezierCurveRadar = findViewById(R.id.bezier_radar)
        recyclerView = findViewById(R.id.recyclerView)
        image = findViewById(R.id.img)
        findViewById<View>(R.id.img_container).setOnClickListener(this)
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        receiver = BluetoothReceiver()
        val filter = IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)//连接状态改变
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)//蓝牙已连接
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)//断开连接
        filter.addAction(BluetoothDevice.ACTION_FOUND)//用于接收发现蓝牙设备的广播
        filter.priority = 1000
        registerReceiver(receiver, filter)
        updateHeadPortrait()

        adViewToRightContainer()
    }

    private fun adViewToRightContainer() {
        status = ImageView(this)
        refreshStatus()
        setRightContainer(status)
    }

    /**
     * 刷新蓝牙的状态
     */
    private fun refreshStatus() {
        if (bluetoothAdapter == null) {
            status!!.setImageResource(R.drawable.ic_vector_bluetooth_disabled_white_24dp)
        } else {
            if (!bezierCurveRadar!!.isStop) {
                status!!.setImageResource(R.drawable.ic_vector_bluetooth_searching_white_24dp)
            } else if (bluetoothAdapter!!.isEnabled) {
                status!!.setImageResource(R.drawable.ic_vector_bluetooth_connected_white_24dp)
            } else {
                status!!.setImageResource(R.drawable.ic_vector_bluetooth_disabled_white_24dp)
            }
        }
    }

    private fun updateHeadPortrait() {
        val user = BmobUser.getCurrentUser(User::class.java)
        if (user != null) {
            if ("M" == user.gender) {
                update(user.imgUri, R.mipmap.men)
            } else {
                update(user.imgUri, R.mipmap.women)
            }
        }
    }

    private fun update(url: String, placeHolder: Int) {
        Glide.with(this)
                .load(url)
                .error(placeHolder)
                .placeholder(placeHolder)
                .bitmapTransform(GlideCircleTransformer(this))
                //                .skipMemoryCache(true)//跳过内存
                //                .diskCacheStrategy(DiskCacheStrategy.NONE)//想要生效必须添加 跳过内存
                .signature(StringSignature(Prefs.getInstance(App.getCtx()).getString(Prefs.KEY_USER_HEADER_IMAGE_FLAG, "")))
                .into(findViewById<View>(R.id.img) as ImageView)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.img_container) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requestLocationPermission()
            } else {
                openBluetooth()
            }
        }
    }

    /**
     * 如果当前正在搜素，就停止搜索
     */
    private fun openBluetooth() {
        if (bluetoothAdapter == null) {
            ToastUtil.showToast(this, "当前设备不支持蓝牙")
            return
        }
        if (!bluetoothAdapter!!.isEnabled) {
            startOpenBluetooth()
            //            bluetoothAdapter.enable();
            return
        }
        if (bluetoothAdapter!!.isDiscovering) {
            bluetoothAdapter!!.cancelDiscovery()//取消搜索
            refreshRadar(true)
            return
        }
        startDiscovery()//开始搜索
    }

    /**
     * 请求开启蓝牙
     */
    private fun startOpenBluetooth() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent, REQUEST_CODE_ENABLE_BLUETOOTH)
    }

    /**
     * 开始扫描附近的蓝牙设备
     */
    private fun startDiscovery() {
        if (bluetoothAdapter != null) {
            refreshRadar(false)
            findViewById<View>(R.id.img_container).isEnabled = false//防止重复点击
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({
                bluetoothAdapter!!.startDiscovery()
                findViewById<View>(R.id.img_container).isEnabled = true
            }, 1000)
        }
    }

    /**
     * 刷新雷达绘制
     */
    private fun refreshRadar(isStop: Boolean) {
        bezierCurveRadar!!.isStop = isStop
        if (isStop) {
            bluetoothDeviceMap.clear()
            refreshList()
        }
        refreshStatus()
    }

    private fun refreshList() {
        val bluetoothDevices = ArrayList<BluetoothDevice>()
        val keys = bluetoothDeviceMap.keys.toTypedArray()
        for (key in keys) {
            bluetoothDeviceMap[key]?.let { bluetoothDevices.add(it) }
        }
        if (adapter == null) {
            adapter = BluetoothDeviceAdapter(this, bluetoothDevices)
            recyclerView!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            recyclerView!!.adapter = adapter
            //            adapter.setOnItemClickListener((position, bluetoothDevice) -> {
            //                new AcceptThread(bluetoothAdapter).start();
            //                new ConnectThread(bluetoothAdapter, bluetoothDevice).start();
            //            });
        } else {
            adapter!!.notifyDataSetChanged(bluetoothDevices)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                startDiscovery()
            }
        }
    }

    internal inner class BluetoothReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (TextUtils.isEmpty(action)) return
            when (action) {
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)) {
                        BluetoothAdapter.STATE_OFF -> refreshRadar(true)
                        BluetoothAdapter.STATE_ON -> refreshStatus()
                    }
                }
                BluetoothDevice.ACTION_FOUND//搜索附近的蓝牙设备
                -> {
                    // Discovery找到了一个设备。
                    //从Intent 获取BluetoothDevice对象及其信息。
                    val bluetoothDevice = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = bluetoothDevice.name
                    val deviceAddress = bluetoothDevice.address // MAC地址
                    val format = String.format("找到了一个蓝牙设备 ：deviceName -> %s ,address -> %s", deviceName, deviceAddress)
                    Log.e(TAG, format)

                    bluetoothDeviceMap[deviceAddress] = bluetoothDevice
                    refreshList()
                }
            }
        }
    }

    internal inner class BluetoothDeviceAdapter(context: Context, data: List<BluetoothDevice>) : BaseRecyclerAdapter<BluetoothDevice>(context, data) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return ViewHolder(layoutInflater.inflate(R.layout.item_bluetooth_advice, parent, false))
        }

        override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
            val holder = viewHolder as ViewHolder
            val name = data[position].name
            holder.title.text = if (TextUtils.isEmpty(name)) "未知" else name
            holder.address.text = data[position].address
            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener { onItemClickListener.onClick(position, getItem(position)) }
            }
        }

        internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var title: TextView = itemView.findViewById(R.id.title)
            var address: TextView = itemView.findViewById(R.id.address)
        }
    }

    override fun onPermissionsAllowed(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onPermissionsAllowed(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_LOCATION) {
            openBluetooth()
        }
    }

    override fun onPermissionsRefused(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onPermissionsRefused(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_LOCATION) {
            requestLocationPermission()
        }
    }

    override fun onPermissionsRefusedNever(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onPermissionsRefusedNever(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_LOCATION) {
            val appName = getString(R.string.app_name)
            val message = String.format(getString(R.string.permission_request_LOCATION), appName)
            val span = SpannableString(message)
            span.setSpan(TextAppearanceSpan(this, R.style.text_color_2_15_style), 0, message.indexOf(appName), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            val start = message.indexOf(appName) + appName.length
            span.setSpan(TextAppearanceSpan(this, R.style.text_color_1_17_bold_style), message.indexOf(appName), start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            span.setSpan(TextAppearanceSpan(this, R.style.text_color_2_15_style), start, message.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            showPermissionRefusedNeverDialog(span)
        }
    }

    /**
     * 请求位置权限
     */
    private fun requestLocationPermission() {
        checkPermission(REQUEST_CODE_LOCATION, Manifest.permission_group.LOCATION, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION))
    }

    /**
     * 权限被彻底禁止后 , 弹框提醒用户去开启
     */
    private fun showPermissionRefusedNeverDialog(message: CharSequence) {
        AlertDialogBuilder(this)
                .setTitle(R.string.message_alert, true)
                .setMessage(message)
                .setLeftButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                .setRightButton(R.string.confirm) { dialog, _ ->
                    dialog.dismiss()
                    goToAppSettingsPage()
                }
                .setShowLine(true)
                .setCanceledOnTouchOutside(false)
                .show()
    }

    override fun onDestroy() {
        if (receiver != null) unregisterReceiver(receiver)
        super.onDestroy()
    }
}
