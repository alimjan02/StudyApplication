package com.sxt.chat.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import cn.bmob.v3.BmobUser
import com.bumptech.glide.Glide
import com.sxt.chat.R
import com.sxt.chat.base.HeaderActivity
import com.sxt.chat.db.User
import com.sxt.chat.utils.glide.GlideCircleTransformer


/**
 * Created by izhaohu on 2018/2/5.
 */

class SelectNumberActivity : HeaderActivity(), View.OnClickListener {

    private var etName: EditText? = null
    private var etNumber: EditText? = null
    private var idCard: String? = null
    private var name: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_body_number)
        setTitle(R.string.body_number)
        etName = findViewById(R.id.et_name)
        etNumber = findViewById(R.id.et_number)
        val save = findViewById<TextView>(R.id.save)
        save.setOnClickListener(this)
        save.setText(R.string.save)
        etName!!.visibility = View.GONE
        updateHeadPortrait()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.save -> {
                name = etName!!.text.toString().trim { it <= ' ' }
                idCard = etNumber!!.text.toString().trim { it <= ' ' }

                if (TextUtils.isEmpty(idCard)) {
                    Toast(getString(R.string.input_body_number))
                    return
                }
                if (!isLegalId(idCard!!)) {
                    Toast("身份证格式不正确")
                    return
                }

                val intent = Intent()
                intent.putExtra(BasicInfoActivity.REQUESTCODE_Number.toString(), idCard)
                setResult(RESULT_OK, intent)
                finish()
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
                .bitmapTransform(GlideCircleTransformer(this))
                //                .skipMemoryCache(true)//跳过内存
                //                .diskCacheStrategy(DiskCacheStrategy.NONE)//想要生效必须添加 跳过内存
                .into(findViewById<View>(R.id.img) as ImageView)
    }

    companion object {
        fun isLegalId(id: String): Boolean = when {
            id.toUpperCase().matches("(^\\d{15}$)|(^\\d{17}([0-9]|X)$)".toRegex()) -> true
            else -> false
        }
    }
}
