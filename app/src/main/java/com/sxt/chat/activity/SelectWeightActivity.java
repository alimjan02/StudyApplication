package com.sxt.chat.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sxt.chat.R;
import com.sxt.chat.base.HeaderActivity;
import com.sxt.chat.db.User;
import com.sxt.chat.utils.glide.GlideCircleTransformer;
import com.sxt.chat.view.chart.RulerView;

import cn.bmob.v3.BmobUser;

/**
 * Created by izhaohu on 2018/2/6.
 */

public class SelectWeightActivity extends HeaderActivity {

    private float value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluate_weight);
        setTitle(R.string.weight);
        final TextView result = (TextView) findViewById(R.id.result);
        RulerView rulerView = (RulerView) findViewById(R.id.rulerView);
        rulerView.setOnValueChangeListener(new RulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                SelectWeightActivity.this.value = value;
                result.setText((int) value + "KG");
            }
        });
        TextView next = (TextView) findViewById(R.id.next);
        next.setText(R.string.save);
        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.putExtra(String.valueOf(BasicInfoActivity.REQUESTCODE_WEIGHT), value);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        updateHeadPortrait();
    }

    private void updateHeadPortrait() {
        User user = BmobUser.getCurrentUser(User.class);
        if (user != null) {
            if ("M".equals(user.getGender())) {
                update(user.getImgUri(), R.mipmap.men);
            } else {
                update(user.getImgUri(), R.mipmap.female);
            }
        }
    }

    private void update(String url, int placeHolder) {
        Glide.with(this)
                .load(url)
                .error(placeHolder)
                .bitmapTransform(new GlideCircleTransformer(this))
//                .skipMemoryCache(true)//跳过内存
//                .diskCacheStrategy(DiskCacheStrategy.NONE)//想要生效必须添加 跳过内存
                .into((ImageView) findViewById(R.id.img));
    }
}
