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
import com.sxt.chat.view.picker.RulerView

/**
 * Created by izhaohu on 2018/2/6.
 */

class SelectWeightActivity : HeaderActivity() {

    private var value: Float = 0.toFloat()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evaluate_weight)
        setTitle(R.string.weight)
        val result = findViewById<TextView>(R.id.result)
        val rulerView = findViewById<RulerView>(R.id.rulerView)
        rulerView.setOnValueChangeListener { value ->
            this.value = value
            result.text = String.format("%sKG", value.toInt())
        }
        val next = findViewById<TextView>(R.id.next)
        next.setText(R.string.save)
        findViewById<View>(R.id.next).setOnClickListener {
            val intent = Intent()
            intent.putExtra("${BasicInfoActivity.REQUESTCODE_WEIGHT}", value)
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
                //                .skipMemoryCache(true)//跳过内存
                //                .diskCacheStrategy(DiskCacheStrategy.NONE)//想要生效必须添加 跳过内存
                .into(findViewById<ImageView>(R.id.img))
    }
}
