package com.sxt.chat.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cn.bmob.v3.BmobUser
import com.bumptech.glide.Glide
import com.sxt.chat.R
import com.sxt.chat.base.HeaderActivity
import com.sxt.chat.db.User
import com.sxt.chat.utils.glide.GlideCircleTransformer
import com.sxt.chat.view.picker.StringScrollPicker
import java.util.*

/**
 * Created by izhaohu on 2018/1/26.
 */

class SelectAgeActivity : HeaderActivity(), View.OnClickListener {

    private var select: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_age)
        setTitle(R.string.age)
        val scrollPicker = findViewById<StringScrollPicker>(R.id.scrollPicker)
        select = findViewById<TextView>(R.id.select)
        val next = findViewById<TextView>(R.id.next)
        next.setOnClickListener(this)
        val ages = ArrayList<CharSequence>()
        for (i in 0..119) {
            ages.add(i.toString())
        }
        scrollPicker.data = ages
        scrollPicker.setOnSelectedListener { _, _ -> select!!.text = scrollPicker.selectedItem }
        select!!.text = scrollPicker.selectedItem
        updateHeadPortrait()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.next -> {
                val trim = select!!.text.toString().trim()
                val intent = Intent()
                intent.putExtra("${BasicInfoActivity.REQUESTCODE_AGE}", trim)
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
                .into(findViewById<ImageView>(R.id.img))
    }
}
