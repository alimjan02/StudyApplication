package com.sxt.chat.activity


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.sxt.chat.R
import com.sxt.chat.base.HeaderActivity

/**
 * Created by izhaohu on 2018/2/5.
 */

class SelectSexActivity : HeaderActivity(), View.OnClickListener {

    private var tvWoman: TextView? = null
    private var imgWoman: ImageView? = null
    private var tvMan: TextView? = null
    private var imgMan: ImageView? = null
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_sex)
        setTitle(R.string.sex)
        findViewById<View>(R.id.woman).setOnClickListener(this)
        findViewById<View>(R.id.man).setOnClickListener(this)
        tvWoman = findViewById<View>(R.id.tv_woman) as TextView
        imgWoman = findViewById<View>(R.id.img_woman) as ImageView
        tvMan = findViewById<View>(R.id.tv_man) as TextView
        imgMan = findViewById<View>(R.id.img_man) as ImageView
    }

    @SuppressLint("ResourceAsColor")
    override fun onClick(view: View) {
        var sex: String? = null
        when (view.id) {
            R.id.woman -> {
                tvMan!!.setTextColor(ContextCompat.getColor(this, R.color.text_color_2))
                imgMan!!.background = ContextCompat.getDrawable(this, R.drawable.blue_stroke_circle_unselect)
                tvWoman!!.setTextColor(ContextCompat.getColor(this, R.color.main_blue))
                imgWoman!!.background = ContextCompat.getDrawable(this, R.drawable.blue_stroke_circle_select)
                sex = "F"
            }

            R.id.man -> {
                tvMan!!.setTextColor(ContextCompat.getColor(this, R.color.main_blue))
                imgMan!!.background = ContextCompat.getDrawable(this, R.drawable.blue_stroke_circle_select)
                tvWoman!!.setTextColor(ContextCompat.getColor(this, R.color.text_color_2))
                imgWoman!!.background = ContextCompat.getDrawable(this, R.drawable.blue_stroke_circle_unselect)
                sex = "M"
            }
        }
        handler.postDelayed({
            val intent = Intent()
            intent.putExtra("${BasicInfoActivity.REQUESTCODE_SEX}", sex)
            setResult(RESULT_OK, intent)
            finish()
        }, 500)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
