package com.sxt.chat.activity


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cn.bmob.v3.BmobUser
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.sxt.chat.R
import com.sxt.chat.base.HeaderActivity
import com.sxt.chat.db.User
import com.sxt.chat.utils.glide.GlideCircleTransformer
import com.sxt.chat.view.picker.RulerView

/**
 * Created by izhaohu on 2018/2/6.
 */

class SelectHeightActivity : HeaderActivity() {

    private var value: Float = 0.toFloat()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evaluate_height)
        setTitle(R.string.hight)

        val result = findViewById<View>(R.id.result) as TextView
        val rulerView = findViewById<View>(R.id.rulerView) as RulerView
        rulerView.setOnValueChangeListener { value ->
            this.value = value
            result.text = String.format("%sCM", value.toInt())
        }
        val next = findViewById<View>(R.id.next) as TextView
        next.setOnClickListener {
            val intent = Intent()
            intent.putExtra(BasicInfoActivity.REQUESTCODE_HIGHT.toString(), this.value)
            setResult(RESULT_OK, intent)
            finish()
        }

        updateHeadPortrait()
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
                .skipMemoryCache(true)//跳过内存
                .diskCacheStrategy(DiskCacheStrategy.NONE)//想要生效必须添加 跳过内存
                .into(findViewById<View>(R.id.img) as ImageView)
    }
}
