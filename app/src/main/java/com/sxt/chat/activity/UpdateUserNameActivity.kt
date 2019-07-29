package com.sxt.chat.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import com.sxt.chat.R
import com.sxt.chat.base.HeaderActivity


/**
 * Created by izhaohu on 2018/3/13.
 */

class UpdateUserNameActivity : HeaderActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_user_name)
        setTitle(getString(R.string.header_name_update))
        val et = findViewById<EditText>(R.id.et_number)
        findViewById<View>(R.id.save).setOnClickListener {
            val trim = et.text.toString().trim()
            if (TextUtils.isEmpty(trim)) {
                Toast("请填写姓名")
                return@setOnClickListener
            }
            val intent = Intent()
            intent.putExtra(BasicInfoActivity.REQUESTCODE_USER_NAME.toString(), trim)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}
