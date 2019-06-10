package com.sxt.chat.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sxt.chat.R;
import com.sxt.chat.base.BaseActivity;
import com.sxt.chat.base.BaseFragmentStatePagerAdapter;
import com.sxt.chat.fragment.BannerDetailFragment;
import com.sxt.chat.json.RoomInfo;
import com.sxt.chat.utils.Constants;
import com.sxt.chat.utils.Prefs;

import java.util.ArrayList;

public class BannerDetailActivity extends BaseActivity {

    private AppBarLayout appBarLayout;
    private TextView title;
    private RoomInfo roomInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_detail_view);
        initToolbar();
        initViewPager();
    }

    private void initToolbar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.WHITE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);/*设置状态栏全透明*/
            findViewById(R.id.image_scrolling_top).setTransitionName("shareView");
        }
        title = findViewById(R.id.title);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        //让点击导航的逻辑箭头与后键相同，手动finish掉 没有动画效果
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        Bundle bundle = getIntent().getBundleExtra(Prefs.ROOM_INFO);
        if (bundle != null) {
            roomInfo = (RoomInfo) bundle.getSerializable(Prefs.ROOM_INFO);
            title.setText(roomInfo.getHome_name());
            Glide.with(this)
                    .load(roomInfo != null ? roomInfo.getRoom_url() : "")
                    .placeholder(R.mipmap.ic_banner_placeholder)
                    .error(R.mipmap.ic_no_img)
                    .into((ImageView) findViewById(R.id.image_scrolling_top));
        }
        final float[] maxY = {0};
        appBarLayout = findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            float rate = (float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange();
            if (verticalOffset == 0) {//完全展开
                toolbar.setTranslationY(0);
                title.setTranslationX(0);
                title.setTranslationY(0);
                toolbar.setTitle("");
                title.setVisibility(View.VISIBLE);
                if (maxY[0] == 0) {
                    maxY[0] = title.getY() - toolbar.getY();
                }
                Log.e(TAG, String.format("maxY = %s", maxY[0]));
            } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {//完全折叠
                toolbar.setTranslationY(-toolbar.getHeight());
                title.setTranslationX(-toolbar.getHeight());
                title.setTranslationX(-maxY[0]);
                toolbar.setTitle(roomInfo.getHome_name());
                title.setVisibility(View.GONE);
            } else {//中间状态
                title.setTranslationX(-(float) title.getLeft() / 2 * rate);
                int last = appBarLayout.getTotalScrollRange() - Math.abs(verticalOffset);
                if (last <= (toolbar.getHeight())) {
                    float result = (toolbar.getHeight()) - last;
                    if (result <= toolbar.getHeight()) {
                        toolbar.setTranslationY(-result);//>0向下，<0向上
                        toolbar.setTitle(roomInfo.getHome_name());
                    }
                    title.setVisibility(View.GONE);
                } else {
                    title.setVisibility(View.VISIBLE);
                    toolbar.setTitle("");
                    title.setTranslationY(-maxY[0] * rate);
                }
            }
        });
    }

    private void initViewPager() {
        TabLayout tabLayout = findViewById(R.id.tablayout);
        ViewPager viewPager = findViewById(R.id.viewPager);

        ArrayList<Fragment> fragments = new ArrayList<>();
        String[] titles = getResources().getStringArray(R.array.tab_titles);
        for (int i = 0; i < titles.length; i++) {
            fragments.add(new BannerDetailFragment(false, i % 4));
            tabLayout.addTab(tabLayout.newTab().setText(titles[i]), i == 0);
        }
        viewPager.setAdapter(new BaseFragmentStatePagerAdapter<Fragment>(
                getSupportFragmentManager(), this, fragments, titles));
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_share_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                share();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void share() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, Constants.SHARE_CONTENT);
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, getString(R.string.share_with)));
    }
}
