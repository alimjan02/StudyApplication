package com.sxt.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sxt.chat.R;
import com.sxt.chat.base.HeaderActivity;
import com.sxt.chat.db.User;
import com.sxt.chat.utils.glide.GlideCircleTransformer;

import cn.bmob.v3.BmobUser;


/**
 * Created by izhaohu on 2018/2/5.
 */

public class SelectNumberActivity extends HeaderActivity implements View.OnClickListener {

    private EditText et_name;
    private EditText et_number;
    private String idCard;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_body_number);
        setTitle(R.string.body_number);
        et_name = (EditText) findViewById(R.id.et_name);
        et_number = (EditText) findViewById(R.id.et_number);
        TextView save = (TextView) findViewById(R.id.save);
        save.setOnClickListener(this);
        save.setText(R.string.save);
        et_name.setVisibility(View.GONE);
        updateHeadPortrait();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save:
                name = et_name.getText().toString().trim();
                idCard = et_number.getText().toString().trim();

                if (TextUtils.isEmpty(idCard)) {
                    Toast(getString(R.string.input_body_number));
                    return;
                }
                if (!isLegalId(idCard)) {
                    Toast("身份证格式不正确");
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra(String.valueOf(BasicInfoActivity.REQUESTCODE_Number), idCard);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    public static boolean isLegalId(String id) {
        if (id.toUpperCase().matches("(^\\d{15}$)|(^\\d{17}([0-9]|X)$)")) {
            return true;
        } else {
            return false;
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
                .bitmapTransform(new GlideCircleTransformer(this))
//                .skipMemoryCache(true)//跳过内存
//                .diskCacheStrategy(DiskCacheStrategy.NONE)//想要生效必须添加 跳过内存
                .into((ImageView) findViewById(R.id.img));
    }
}
