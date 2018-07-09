package com.sxt.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sxt.chat.R;
import com.sxt.chat.base.HeaderActivity;
import com.sxt.chat.db.User;
import com.sxt.chat.utils.glide.GlideCircleTransform;
import com.sxt.chat.view.picker.ScrollPickerView;
import com.sxt.chat.view.picker.StringScrollPicker;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * Created by izhaohu on 2018/1/26.
 */

public class SelectAgeActivity extends HeaderActivity implements View.OnClickListener {

    private TextView select;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_age);
        setTitle(R.string.age);
        final StringScrollPicker scrollPicker = (StringScrollPicker) findViewById(R.id.scrollPicker);
        select = (TextView) findViewById(R.id.select);
        TextView next = (TextView) findViewById(R.id.next);
        next.setOnClickListener(this);
        List<CharSequence> ages = new ArrayList<>();
        for (int i = 0; i < 120; i++) {
            ages.add(String.valueOf(i));
        }
        scrollPicker.setData(ages);
        scrollPicker.setOnSelectedListener(new ScrollPickerView.OnSelectedListener() {
            @Override
            public void onSelected(ScrollPickerView scrollPickerView, int position) {
                select.setText(scrollPicker.getSelectedItem());
            }
        });
        select.setText(scrollPicker.getSelectedItem());

        updateHeadPortrait();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next:
                String trim = select.getText().toString().trim();
                if (TextUtils.isEmpty(trim)) {
                    Toast("请选择年龄");
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra(String.valueOf(BasicInfoActivity.REQUESTCODE_AGE), trim);
                setResult(RESULT_OK, intent);
                finish();
        }
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
                .bitmapTransform(new GlideCircleTransform(this))
//                .skipMemoryCache(true)//跳过内存
//                .diskCacheStrategy(DiskCacheStrategy.NONE)//想要生效必须添加 跳过内存
                .into((ImageView) findViewById(R.id.img));
    }
}
