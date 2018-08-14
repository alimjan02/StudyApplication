package com.sxt.chat.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sxt.chat.R;
import com.sxt.chat.base.HeaderActivity;
import com.sxt.chat.utils.glide.GlideRoundTransform;

public class ShareViewActivity extends HeaderActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_view);
        String url = getIntent().getStringExtra("url");
        ImageView img = findViewById(R.id.img);
        Glide.with(this)
                .load(url)
                .error(R.mipmap.ic_no_img)
//                .bitmapTransform(new GlideRoundTransform(this, 8))
                .into(img);
    }

    @Override
    public void onGoBack(View view) {
        super.onBackPressed();
    }
}
