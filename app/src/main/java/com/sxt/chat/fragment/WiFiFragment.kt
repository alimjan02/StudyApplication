package com.sxt.chat.fragment

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION
import android.net.wifi.WifiManager.WIFI_STATE_CHANGED_ACTION
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.TextAppearanceSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ExpandableListView
import android.widget.TextView
import com.sxt.chat.App
import com.sxt.chat.R
import com.sxt.chat.adapter.WiFiListAdapter
import com.sxt.chat.base.BaseFragment
import com.sxt.chat.base.HeaderActivity
import com.sxt.chat.dialog.AlertDialogBuilder
import com.sxt.chat.dialog.DialogBuilder
import com.sxt.chat.dialog.LoadingDialog
import com.sxt.chat.utils.ToastUtil
import com.sxt.chat.utils.WifiUtils
import java.util.*

/**
 * Created by izhaohu on 2017/12/15.
 */
class WiFiFragment : BaseFragment(), View.OnClickListener {

    private var activity: HeaderActivity? = null
    private var tvName: TextView? = null
    private var tvNext: TextView? = null
    private var wifiManager: WifiManager? = null
    private var wifiReceiver: WifiReceiver? = null
    private var rootView: View? = null
    private var builder: DialogBuilder? = null
    private var scanResults: ArrayList<ScanResult>? = ArrayList()
    private val scanResultHaveLinked = ArrayList<WifiUtils.WifiScanResult>()
    private val scanResultNoLinked = ArrayList<WifiUtils.WifiScanResult>()
    private var selectedScanResult: WifiUtils.WifiScanResult? = null
    private var wifiAdapter: WiFiListAdapter? = null
    private var inputWifiItem: View? = null
    private var inputWifiBuilder: DialogBuilder? = null
    private var tvWifiName: TextView? = null
    private var etWifiPwd: EditText? = null
    private var loadingDialog: LoadingDialog? = null
    private val REQUEST_OPEN_WIFI = 100
    private val REQUEST_OPEN_WIFI2 = 101
    private var SUCCESS: Boolean = false

    private var currentLinked: WifiUtils.WifiScanResult? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        this.activity = context as HeaderActivity?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity!!.setTitle(getString(R.string.wifi_settings))
        loadingDialog = LoadingDialog(activity)
        val b = checkPermission(REQUEST_OPEN_WIFI)
        if (b) {
            initWifiSettings()
        }
    }

    private fun checkPermission(requestCode: Int): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(App.getCtx(), Manifest.permission_group.LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE), requestCode)
                return false
            }
            return true
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_OPEN_WIFI -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initWifiSettings()
            } else if (permissions.isNotEmpty() && !shouldShowRequestPermissionRationale(permissions[0])) {
                //此时的意思是 用户设置了不再提醒 权限授权
                onPermissionRefuseNever()
            }
            REQUEST_OPEN_WIFI2 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initWifiSettings()
                showDialog()
            } else if (permissions.isNotEmpty() && !shouldShowRequestPermissionRationale(permissions[0])) {
                onPermissionRefuseNever()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun onPermissionRefuseNever() {
        //此时的意思是 用户设置了不再提醒 权限授权
        val appName = getString(R.string.app_name)
        val message = String.format(getString(R.string.permission_request_WiFi), appName)
        val span = SpannableString(message)
        span.setSpan(TextAppearanceSpan(context, R.style.text_color_2_15_style), 0, message.indexOf(appName), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val start = message.indexOf(appName) + appName.length
        span.setSpan(TextAppearanceSpan(context, R.style.text_color_1_17_bold_style), message.indexOf(appName), start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        span.setSpan(TextAppearanceSpan(context, R.style.text_color_2_15_style), start, message.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        showPermissionRefusedNeverDialog(span)
    }

    /**
     * 权限被彻底禁止后 , 弹框提醒用户去开启
     */
    private fun showPermissionRefusedNeverDialog(message: CharSequence) {
        AlertDialogBuilder(activity)
                .setTitle(R.string.message_alert, true)
                .setMessage(message)
                .setLeftButton(R.string.cancel) { dialog, which -> dialog.dismiss() }
                .setRightButton(R.string.confirm) { dialog, which ->
                    dialog.dismiss()
                    goToAppSettingsPage()
                }
                .setShowLine(true)
                .setCanceledOnTouchOutside(false)
                .show()
    }

    fun goToAppSettingsPage() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:" + context.packageName)
        startActivity(intent)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (rootView == null) {
            rootView = LayoutInflater.from(activity).inflate(R.layout.fragment_select_worker_wifi_layout, null)
            val wifiArrowRoot = rootView!!.findViewById<View>(R.id.wifi_arrow)
            tvName = rootView!!.findViewById(R.id.tv_wifi_name)
            tvNext = rootView!!.findViewById(R.id.tv_next)
            tvNext!!.setOnClickListener(this)
            wifiArrowRoot.setOnClickListener(this)
        }
        return rootView
    }

    private fun initWifiSettings() {
        if (wifiManager == null) {
            wifiManager = WifiUtils.getInstance().getmWifiManager()
        }
        if (!WifiUtils.getInstance().isWifiEnabled) {
            wifiManager!!.isWifiEnabled = true//开启WIFI
        }
        if (wifiReceiver == null) {
            wifiReceiver = WifiReceiver()
            val filter = IntentFilter()
            filter.addAction(SCAN_RESULTS_AVAILABLE_ACTION)
            filter.addAction(WIFI_STATE_CHANGED_ACTION)
            filter.priority = 1000
            activity!!.registerReceiver(wifiReceiver, filter)
        }
        wifiManager!!.startScan()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.wifi_arrow//选择WIFI
            -> {
                val b = checkPermission(REQUEST_OPEN_WIFI2)
                if (b) {
                    initWifiSettings()
                    showDialog()
                }
            }
            R.id.tv_next -> if (SUCCESS) {
                activity!!.finish()
            } else {
                ToastUtil.showToast(activity, R.string.check_wifi_connect)
            }
        }
    }

    private fun showDialog() {
        if (builder == null) {
            builder = DialogBuilder(activity, R.style.Theme_Design_BottomSheetDialog)
            val wifiItem = LayoutInflater.from(activity).inflate(R.layout.item_wifi_list_layout, null)
            initWifiItem(wifiItem)
            builder!!.replaceView(wifiItem).setCancelableOutSide(false)
        }
        builder!!.show(1.0, Gravity.TOP)
    }

    private fun initWifiItem(wifiItem: View) {
        val expandableListView = wifiItem.findViewById<ExpandableListView>(R.id.wifi_listView)
        expandableListView.setGroupIndicator(null)//隐藏系统自带的箭头
        expandableListView.setOnGroupClickListener { expandableListView1, view, i, l ->
            true//屏蔽 分组点击展开
        }
        dealWithLinkedWifiData()
        wifiAdapter = WiFiListAdapter(activity, scanResultHaveLinked, scanResultNoLinked)
        expandableListView.setAdapter(wifiAdapter)
        for (i in 0 until wifiAdapter!!.groupCount) {
            expandableListView.expandGroup(i)//将全部分组展开
        }
        expandableListView.setOnChildClickListener { expandableListView12, view, groupPosition, childPosition, l ->
            selectedScanResult = wifiAdapter!!.getChild(groupPosition, childPosition) as WifiUtils.WifiScanResult
            val ssid = selectedScanResult!!.SSID.replace("\"".toRegex(), "")
            if ("[ESS]" == selectedScanResult!!.capabilities) {
                WifiUtils.getInstance().connectWifi(ssid, "", WifiUtils.WIFICIPHER_NOPASS).setWifiStateListener(object : WifiUtils.WifiStateListener {
                    override fun onStart() {

                    }

                    override fun onFailed(e: Exception, SSID: String, updateFailed: Boolean) {
                        stopWatch(null, null)
                    }

                    override fun success(SSID: String, pwd: String) {

                    }
                })
            } else {
                connectWiFi(selectedScanResult!!)
            }
            tvName!!.text = ssid
            false
        }
    }

    private fun connectWiFi(result: WifiUtils.WifiScanResult) {
        if (inputWifiBuilder == null) {
            inputWifiBuilder = DialogBuilder(activity)
            inputWifiItem = LayoutInflater.from(activity).inflate(R.layout.item_input_wifi_pwd, null)
            tvWifiName = inputWifiItem!!.findViewById(R.id.tv_wifi_name)
            etWifiPwd = inputWifiItem!!.findViewById(R.id.et_wifi_pwd)
            inputWifiItem!!.findViewById<View>(R.id.close).setOnClickListener { inputWifiBuilder!!.dismiss() }
            inputWifiBuilder!!.replaceView(inputWifiItem).setCancelableOutSide(false)
        }
        inputWifiItem!!.findViewById<View>(R.id.tv_wifi_connect).setOnClickListener {

            val trim = etWifiPwd!!.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(trim)) {
                ToastUtil.showToast(activity, R.string.please_input_wifi_password)
                return@setOnClickListener
            }
            if (selectedScanResult != null) {
                selectedScanResult!!.PWD = trim
            }
            WifiUtils.getInstance().setWifiStateListener(object : WifiUtils.WifiStateListener {
                override fun onStart() {
                    startWatch()
                }

                override fun onFailed(e: Exception, ssid: String, updateFailed: Boolean) {
                    if (updateFailed) {
                        loadingDialog!!.dismiss()
                        val builder = AlertDialogBuilder(activity)
                        builder.setTitle(R.string.prompt).setMessage("您需要先到系统设置界面删除已经保存的 WiFi : $ssid  \r\n才可以重新连接哟 !")
                                .setLeftButton(R.string.cancel) { dialog, which -> dialog.dismiss() }
                                .setRightButton(R.string.confirm) { dialog, which ->
                                    startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                                    dialog.dismiss()
                                }
                                .setCloseCliklistener { v -> builder.dismiss() }
                                .setShowLine(true)
                                .setCanceledOnTouchOutside(false)
                                .show()

                    } else {
                        stopWatch(null, null)
                    }
                }

                override fun success(SSID: String, pwd: String) {
                    stopWatch(SSID, pwd)
                }
            }).connectWifi(result.SSID.replace("\"".toRegex(), ""), trim, WifiUtils.WIFICIPHER_WPA)
        }
        tvWifiName!!.text = result.SSID.replace("\"".toRegex(), "")
        etWifiPwd!!.setText("")
        inputWifiBuilder!!.show(0.875, Gravity.CENTER)
    }

    private fun dealWithLinkedWifiData() {
        scanResultHaveLinked.clear()
        scanResultNoLinked.clear()
        val networks = wifiManager!!.configuredNetworks
        if (networks != null) {
            for (config in networks) {
                val result = WifiUtils.WifiScanResult(
                        config.BSSID,
                        config.SSID,
                        WifiUtils.getInstance().getSecurity(config),
                        0)
                if (wifiManager!!.connectionInfo.ssid == config.SSID) {
                    currentLinked = result
                }
                scanResultHaveLinked.add(result)
            }
        }

        if (scanResultHaveLinked.contains(currentLinked)) {
            scanResultHaveLinked.remove(currentLinked)
            scanResultHaveLinked.add(0, currentLinked!!)
        }

        if (scanResults != null) {
            for (s in scanResults!!) {
                if (s.SSID != null && !TextUtils.isEmpty(s.SSID)) {
                    val result = WifiUtils.WifiScanResult(
                            s.BSSID,
                            s.SSID,
                            s.capabilities,
                            s.level)
                    scanResultNoLinked.add(result)
                }
            }
        }
        if (scanResultNoLinked.size == 0) {
            ToastUtil.showToast(getActivity(), "请确认设备Wi-Fi和GPS已开启")
        }
    }

    private inner class WifiReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (SCAN_RESULTS_AVAILABLE_ACTION == intent.action) {
                scanResults = wifiManager!!.scanResults as ArrayList<ScanResult>
                if (scanResults != null && scanResults!!.size > 0) {
                    //                    wifiArrowRoot.setEnabled(true);
                    tvName!!.text = wifiManager!!.connectionInfo.ssid.replace("\"".toRegex(), "")
                } else {
                    //                    wifiManager.startScan();
                    //                    wifiArrowRoot.setEnabled(false);
                    tvName!!.text = "暂未发现周边W-Fi,请确认设备Wi-Fi和GPS已开启"
                }
                notifyDataSetChanged()

            } else if (intent.action == WIFI_STATE_CHANGED_ACTION) {
            }

        }
    }

    private fun notifyDataSetChanged() {
        dealWithLinkedWifiData()
        if (builder != null) {
            wifiAdapter!!.notifyAdapter(scanResultHaveLinked, scanResultNoLinked)
        }
    }

    private fun startWatch() {
        loadingDialog!!.show()
    }

    fun stopWatch(ssid: String?, pwd: String?) {
        dismissDialogs()
        tvNext!!.visibility = View.VISIBLE
        if (!TextUtils.isEmpty(ssid) && !TextUtils.isEmpty(pwd)) {
            SUCCESS = true
            tvName!!.text = ssid
            tvNext!!.text = "连接成功"
            ToastUtil.showToast(activity, "连接成功")
            selectedScanResult = WifiUtils.WifiScanResult(null, ssid, "", 0)
        } else {
            SUCCESS = false
            tvNext!!.text = "连接出错"
            selectedScanResult = null
            ToastUtil.showToast(activity, "连接出错,请重试")
        }
    }

    private fun dismissDialogs() {
        if (inputWifiBuilder != null) {
            inputWifiBuilder!!.dismiss()
        }
        if (loadingDialog != null) {
            loadingDialog!!.dismiss()
        }
        if (builder != null) {
            builder!!.dismiss()
        }
    }

    override fun onDestroy() {
        if (wifiReceiver != null) {
            activity!!.unregisterReceiver(wifiReceiver)
        }
        dismissDialogs()
        WifiUtils.getInstance().cancel()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        //        super.onSaveInstanceState(outState);
    }
}
