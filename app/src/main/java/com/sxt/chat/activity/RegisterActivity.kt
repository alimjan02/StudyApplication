package com.sxt.chat.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.TextInputLayout
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import com.sxt.chat.App
import com.sxt.chat.R
import com.sxt.chat.base.HeaderActivity
import com.sxt.chat.db.SQLiteUserDao
import com.sxt.chat.json.ResponseInfo
import com.sxt.chat.utils.Prefs
import com.sxt.chat.ws.BmobRequest
import java.security.MessageDigest

class RegisterActivity : HeaderActivity(), View.OnClickListener {

    private var editTextUser: AutoCompleteTextView? = null
    private var input_user_name: TextInputLayout? = null
    private var input_password: TextInputLayout? = null
    private var editTextPwd: EditText? = null
    private var userName: String? = null
    private var handler: Handler = Handler()
    private var CMD_REGISTER_USER: String = javaClass.name + "CMD_REGISTER_USER"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setWindowStatusBarColor(this, R.color.day_night_normal_color)
        }
        setContentView(R.layout.activity_login)
        setTitle(R.string.register)
        showToolbar(false)
        input_user_name = findViewById(R.id.input_user_name)
        input_password = findViewById(R.id.input_password)
        editTextUser = findViewById(R.id.tv_user_name)
        editTextPwd = findViewById(R.id.tv_password)

        val tvLogin: TextView = findViewById(R.id.btn_login_confirm)
        tvLogin.setText(R.string.register2)
        tvLogin.setOnClickListener(this)
        findViewById<View>(R.id.icon_login).setOnClickListener(this)
        findViewById<View>(R.id.register).visibility = View.GONE
        findViewById<View>(R.id.forget_pwd).visibility = View.GONE
    }

    override fun onClick(v: View) {
        if (v.id == R.id.btn_login_confirm) {
            checkUserName()
        }
    }

    private fun checkUserName() {
        input_user_name!!.error = null
        input_password!!.error = null
        userName = editTextUser!!.text.toString().trim()
        val pwd = editTextPwd!!.text.toString().trim()
        if (TextUtils.isEmpty(userName)) {
            input_user_name!!.error = getString(R.string.input_number)
            return
        }
        if (TextUtils.isEmpty(pwd)) {
            input_password!!.error = getString(R.string.input_pwd)
            return
        }
        loading.show()
        val digest = MessageDigest.getInstance("MD5").digest(pwd.toByteArray(Charsets.UTF_8))
        BmobRequest.getInstance(this).register(userName, String(digest), CMD_REGISTER_USER)
    }

    override fun onMessage(resp: ResponseInfo) {
        super.onMessage(resp)
        if (ResponseInfo.OK == resp.code) {
            if (CMD_REGISTER_USER == resp.cmd) {
                findViewById<View>(R.id.btn_login_confirm).isEnabled = false
                Toast(R.string.register_successful)
                SQLiteUserDao.getInstance(App.getCtx()).addUser(resp.user)
                handler.postDelayed({
                    val intent = Intent(App.getCtx(), LoginActivity::class.java)
                    intent.putExtra(Prefs.KEY_CURRENT_USER_NAME, userName)
                    startActivity(intent)
                    finish()
                }, 800)
            }
        } else {
            loading.dismiss()
            Toast(resp.error)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
