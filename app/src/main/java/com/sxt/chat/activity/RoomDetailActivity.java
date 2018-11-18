package com.sxt.chat.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sxt.chat.R;
import com.sxt.chat.base.BaseActivity;
import com.sxt.chat.json.RoomInfo;
import com.sxt.chat.utils.Constants;
import com.sxt.chat.utils.Prefs;

public class RoomDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION/*虚拟按键的导航栏半透明*/);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);/*设置状态栏全透明*/
            findViewById(R.id.image_scrolling_top).setTransitionName("shareView");
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getBundleExtra(Prefs.ROOM_INFO);
        if (bundle != null) {
            RoomInfo roomInfo = (RoomInfo) bundle.getSerializable(Prefs.ROOM_INFO);
            CollapsingToolbarLayout toolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
            toolbarLayout.setTitle(roomInfo != null ? roomInfo.getHome_name() : "房间详情");
            Glide.with(this)
                    .load(roomInfo != null ? roomInfo.getRoom_url() : "")
                    .placeholder(R.mipmap.ic_banner_placeholder)
                    .error(R.mipmap.ic_no_img)
//                .bitmapTransform(new GlideRoundTransformer(this, 8))
                    .into((ImageView) findViewById(R.id.image_scrolling_top));
        }
        //让点击导航的逻辑箭头与后键相同，手动finish掉 没有动画效果
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab_my_location);
//        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.yellow_rgb_253_202_78)));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
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
