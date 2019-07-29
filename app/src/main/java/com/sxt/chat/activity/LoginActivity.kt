package com.sxt.chat.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.TextInputLayout
import android.text.TextUtils
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.EditText
import cn.bmob.v3.BmobUser
import com.sxt.chat.R
import com.sxt.chat.base.HeaderActivity
import com.sxt.chat.db.SQLiteUserDao
import com.sxt.chat.db.User
import com.sxt.chat.json.ResponseInfo
import com.sxt.chat.utils.Prefs
import com.sxt.chat.ws.BmobRequest
import java.security.MessageDigest

class LoginActivity : HeaderActivity(), View.OnClickListener {

    private var editTextUser: AutoCompleteTextView? = null
    private var inputUserName: TextInputLayout? = null
    private var inputPassword: TextInputLayout? = null
    private var editTextPwd: EditText? = null
    var userName: String? = null
    var handler: Handler = Handler()
    val CMD_LOGIN: String = javaClass.name + "CMD_LOGIN"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setWindowStatusBarColor(this, R.color.day_night_normal_color)
        }
        setContentView(R.layout.activity_login)
        setTitle(R.string.login)
        showToolbar(false)
        checkUser()
    }

    private fun checkUser() {
        val currentUser = BmobUser.getCurrentUser(User::class.java)
        if (currentUser == null) {
            initView()
        } else {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(MainActivity.KEY_IS_AUTO_LOGIN, true)
            startActivity(intent)
            finish()
        }
    }

    private fun initView() {
        findViewById<View>(R.id.root).visibility = View.VISIBLE
        inputUserName = findViewById(R.id.input_user_name)
        inputPassword = findViewById(R.id.input_password)
        editTextUser = findViewById(R.id.tv_user_name)
        editTextPwd = findViewById(R.id.tv_password)

        findViewById<View>(R.id.btn_login_confirm).setOnClickListener(this)
        findViewById<View>(R.id.icon_login).setOnClickListener(this)
        findViewById<View>(R.id.register).setOnClickListener(this)
        findViewById<View>(R.id.forget_pwd).setOnClickListener(this)
        val name = intent!!.getStringExtra(Prefs.KEY_CURRENT_USER_NAME)
        if (name != null) {
            this.userName = name
            editTextUser!!.setText(name)
            editTextPwd!!.requestFocus()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_login_confirm -> checkUserName()
            R.id.register -> startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun checkUserName() {
        inputUserName?.error = null
        inputPassword?.error = null
        userName = editTextUser!!.text.toString().trim()
        val pwd = editTextPwd!!.text.toString().trim()
        if (TextUtils.isEmpty(userName)) {
            inputUserName!!.error = getString(R.string.input_number)
            return
        }
        if (TextUtils.isEmpty(pwd)) {
            inputPassword!!.error = getString(R.string.input_pwd)
            return
        }
        loading.show()
        val digest = MessageDigest.getInstance("MD5").digest(pwd.toByteArray(Charsets.UTF_8))
        BmobRequest.getInstance(this).login(userName, String(digest), CMD_LOGIN)
    }

    override fun onMessage(resp: ResponseInfo) {
        super.onMessage(resp)
        if (resp.code == ResponseInfo.OK) {
            if (CMD_LOGIN == resp.cmd) {
                loading.dismiss()
                SQLiteUserDao.getInstance(this).addUser(resp.user)
                val prefs = Prefs.getInstance(this)
                prefs.setTicket(resp.user.userName, resp.user.ticket, resp.user.accountId)
                prefs.putString(Prefs.KEY_USER_HEADER_IMAGE_FLAG, "${prefs.userId}-" + System.currentTimeMillis())
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(MainActivity.KEY_IS_WILL_GO_LOGIN_ACTIVITY, true)
                startActivity(intent)
                finish()
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