package com.sxt.chat.activity

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.sxt.chat.R

class KotlinDemoActivity : AppCompatActivity() {
    val TAG: String = this.javaClass.name
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin_demo)
        val button = findViewById<TextView>(R.id.button)
        var languageName = "Kotlin"
        var snackBar: Snackbar? = null
        button.setOnClickListener { view ->
            Log.e(TAG, "snackBar -> $snackBar")
            if (snackBar == null) {
                snackBar = Snackbar.make(view, "Kotlin Demo", 1000)
                        .setAction("知道了 ${button.id}") {
                            languageName += "呵呵"
                            Log.e(TAG, languageName)
                        }
                        .setActionTextColor(ContextCompat.getColor(this, R.color.white))
            }
            snackBar!!.show()
        }

        val count = 40
        val answerString = when {
            count < 30 -> "count < 30."
            count == 40 -> "count ==50."
            else -> "count > 40."
        }
        Log.e(TAG, answerString)
        Log.e(TAG, generateAnswerString(generateAnswerNoName("generateAnswerNoName")))
        Log.e(TAG, "${
        generateAnswerHight("Kotlin", ({ input ->
            input.length
        }))}")
    }

    /**
     * 声明函数
     * 输入Int
     * 输出String 一般写法
     */
    private fun generateAnswerString(count: Int): String {
        return when {
            count < 30 -> "count < 30"
            count == 40 -> "count == 40"
            else -> "count > 40"
        }
    }

    /**
     * 声明函数
     * 输入Int
     * 输出String 简写
     */
    private fun generateAnswerString2(count: Int): String = when {
        count < 30 -> "count < 30"
        count == 40 -> "count == 40"
        else -> "count > 40"
    }

    /**
     * 匿名函数
     * 输入String
     * 输出Int
     */
    private var generateAnswerNoName: (String) -> Int = { input ->
        input.length
    }

    /**
     * 高阶函数
     * 输入String和一个匿名函数
     * 输出Int
     */
    private fun generateAnswerHight(str: String, methond: (String) -> Int): Int {
        return methond(str)
    }

}
