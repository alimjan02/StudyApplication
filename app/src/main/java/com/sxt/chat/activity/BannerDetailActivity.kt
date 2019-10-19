package com.sxt.chat.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.sxt.chat.R
import com.sxt.chat.base.BaseActivity
import com.sxt.chat.base.BaseFragmentStatePagerAdapter
import com.sxt.chat.fragment.BannerDetailFragment
import com.sxt.chat.json.RoomInfo
import com.sxt.chat.utils.Constants
import com.sxt.chat.utils.Prefs
import kotlinx.android.synthetic.main.item_ad.view.*
import java.util.*
import kotlin.math.abs

class BannerDetailActivity : BaseActivity() {

    private var appBarLayout: AppBarLayout? = null
    private var nestedScrollView: NestedScrollView? = null
    private var roomInfo: RoomInfo? = null
    private var viewPager: ViewPager? = null
    private var tabLayout: TabLayout? = null
    private var collapsingToolbarLayout: CollapsingToolbarLayout? = null
    private var toolbar: Toolbar? = null
    private var verticalOffset: Float = 0.toFloat()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner_detail_view)
        initView()
        initToolbar()
        initViewPager()
    }

    private fun initView() {
        tabLayout = findViewById(R.id.tablayout)
        nestedScrollView = findViewById(R.id.nestedScrollView)
        appBarLayout = findViewById(R.id.app_bar_layout)
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout)
        toolbar = findViewById(R.id.toolbar)
        viewPager = findViewById(R.id.viewPager)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            findViewById<View>(R.id.image_scrolling_top).transitionName = "shareView"
            setWindowStatusBarColor(this, android.R.color.transparent)
        }
        toolbar!!.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        setSupportActionBar(toolbar)
        //让点击导航的逻辑箭头与后键相同，手动finish掉 没有动画效果
        toolbar!!.setNavigationOnClickListener { onBackPressed() }
        val bundle = intent.getBundleExtra(Prefs.ROOM_INFO)
        if (bundle != null) {
            roomInfo = bundle.getSerializable(Prefs.ROOM_INFO) as RoomInfo
//            collapsingToolbarLayout!!.title = roomInfo!!.home_name
            toolbar!!.title = roomInfo!!.home_name
            Glide.with(this)
                    .load(if (roomInfo != null) roomInfo!!.room_url else "")
                    .placeholder(R.drawable.ic_placeholder)
                    .into(findViewById<View>(R.id.image_scrolling_top) as ImageView)
        }
    }

    private fun initToolbar() {
        val maxY = floatArrayOf(0f)
        appBarLayout!!.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, offset ->
            val totalScrollRange: Float = appBarLayout.totalScrollRange.toFloat()
            val verticalOffset: Float = offset.toFloat()
            val rate: Float = abs(verticalOffset / totalScrollRange)
            when {
                verticalOffset.compareTo(0) == 0 -> {//完全展开
                    Log.e(TAG, String.format("完全展开 maxY = %s ，verticalOffset = %s", maxY[0], verticalOffset))
                }
                abs(verticalOffset) >= totalScrollRange -> {//完全折叠
                    Log.e(TAG, String.format("完全折叠 maxY = %s ，verticalOffset = %s", maxY[0], verticalOffset))
                }
                else -> {//中间状态
                    val last: Float = totalScrollRange - abs(verticalOffset)
                }
            }
            this@BannerDetailActivity.verticalOffset = verticalOffset
        })
    }

    private fun initViewPager() {
        val fragments = ArrayList<Fragment>()
        val titles = resources.getStringArray(R.array.tab_titles)
        for (i in titles.indices) {
            fragments.add(BannerDetailFragment(true, i % 4))
            tabLayout!!.addTab(tabLayout!!.newTab().setText(titles[i]), i == 0)
        }
        viewPager!!.adapter = BaseFragmentStatePagerAdapter<Fragment>(
                supportFragmentManager, this, fragments, titles)
        tabLayout!!.setupWithViewPager(viewPager)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.item_share_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share -> share()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun share() {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, Constants.SHARE_CONTENT)
        intent.type = "text/plain"
        startActivity(Intent.createChooser(intent, getString(R.string.share_with)))
    }
}
