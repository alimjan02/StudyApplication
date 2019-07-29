package com.sxt.chat.activity

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.TextView
import com.sxt.chat.App
import com.sxt.chat.R
import com.sxt.chat.base.HeaderActivity
import com.sxt.chat.receiver.WatchDogReceiver
import com.sxt.chat.utils.Prefs
import com.sxt.chat.utils.ToastUtil
import com.sxt.chat.utils.glide.CacheUtils

class SettingsActivity : HeaderActivity(), View.OnClickListener {

    private var cacheSize: TextView? = null
    private var version: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setTitle(getString(R.string.normal_settings))

        cacheSize = findViewById(R.id.cache_size)
        version = findViewById(R.id.version)
        findViewById<View>(R.id.csdn).setOnClickListener(this)//csdn
        findViewById<View>(R.id.blogger).setOnClickListener(this)//blogger
        findViewById<View>(R.id.privacy_notice).setOnClickListener(this)//隐私声明
        findViewById<View>(R.id.clean_cache).setOnClickListener(this)//清除缓存
        findViewById<View>(R.id.current_version).setOnClickListener(this)//清除缓存
        findViewById<View>(R.id.login_out).setOnClickListener(this)//退出登录
        cacheSize!!.text = (CacheUtils.getInstance().cacheSize)
        version!!.text = (String.format("%s%s", getString(R.string.version), Prefs.getVersionName(App.getCtx())))
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.csdn -> {
                val csdn = Intent(this, WebViewActivity::class.java)
                csdn.putExtra(WebViewActivity.ACTION_URL, getString(R.string.about_us_csdn_url))
                startActivity(csdn)
            }
            R.id.blogger -> {
                val blogger = Intent(this, WebViewActivity::class.java)
                blogger.putExtra(WebViewActivity.ACTION_URL, getString(R.string.about_us_blogger_url))
                startActivity(blogger)
            }
            //隐私声明
            R.id.privacy_notice -> {
                val privacyNotice = Intent(this, WebViewActivity::class.java)
                privacyNotice.putExtra(WebViewActivity.ACTION_URL, getString(R.string.privacy_notice_url))
                startActivity(privacyNotice)
            }
            //清除缓存
            R.id.clean_cache -> clearCache()
            //检查更新
            R.id.current_version ->
                try {
                    val uri = Uri.parse("market://details?id=$packageName")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    ToastUtil.showToast(this, R.string.market_is_none)
                }
            //退出登录
            R.id.login_out
            -> {
                val intent = Intent()
                intent.action = WatchDogReceiver.ACTION_LOGOUT
                //android 8.0以后, 发送广播许需要添加包名和具体的接收广播类名
                intent.component = ComponentName(application.packageName, App.getCtx().packageName + ".receiver.WatchDogReceiver")
                sendBroadcast(intent)
            }
        }
    }

    private fun clearCache() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.message_alert)
        builder.setMessage(R.string.clear_cache_confirm)
        builder.setPositiveButton(R.string.confirm) { dialog, _ ->
            CacheUtils.getInstance().clearCache()
            cacheSize!!.text = (CacheUtils.getInstance().cacheSize)
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.cancel) { dialog, i -> dialog.dismiss() }.show()
    }
}