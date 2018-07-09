package com.sxt.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.sxt.chat.R;
import com.sxt.chat.base.HeaderActivity;


/**
 * Created by izhaohu on 2018/3/13.
 */

public class UpdateUserNameActivity extends HeaderActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_name);
        setTitle(getString(R.string.header_name_update));
        final EditText et = (EditText) findViewById(R.id.et_number);
        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String trim = et.getText().toString().trim();
                if (TextUtils.isEmpty(trim)) {
                    Toast("请填写姓名");
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra(String.valueOf(BasicInfoActivity.REQUESTCODE_USER_NAME), trim);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
