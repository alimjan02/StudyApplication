package com.sxt.chat.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sxt.chat.R;
import com.sxt.chat.base.BaseActivity;
import com.sxt.chat.utils.Constants;

public class RoomDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION/*虚拟按键的导航栏半透明*/);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);/*设置状态栏全透明*/
        }

        setContentView(R.layout.activity_share_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("房间详情");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        //让点击导航的逻辑箭头与后键相同，手动finish掉 没有动画效果
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

//        FloatingActionButton fab = findViewById(R.id.fab_scrolling);
//        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this,R.color.yellow_rgb_253_202_78)));
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_SEND);
//                intent.putExtra(Intent.EXTRA_TEXT, Constants.SHARE_CONTENT);
//                intent.setType("text/plain");
//                startActivity(Intent.createChooser(intent, getString(R.string.share_with)));
//            }
//        });

        String url = getIntent().getStringExtra("url");
        ImageView img = findViewById(R.id.image_scrolling_top);
        Glide.with(this)
                .load(url)
                .placeholder(R.mipmap.ic_no_img)
                .error(R.mipmap.ic_no_img)
//                .bitmapTransform(new GlideRoundTransform(this, 8))
                .into(img);
    }
}
