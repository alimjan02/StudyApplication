package com.sxt.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.sxt.chat.App;
import com.sxt.chat.R;
import com.sxt.chat.base.HeaderActivity;
import com.sxt.chat.db.SQLiteUserDao;
import com.sxt.chat.json.ResponseInfo;
import com.sxt.chat.utils.Prefs;
import com.sxt.chat.ws.BmobRequest;

public class RegisterActivity extends HeaderActivity implements View.OnClickListener {

    private AutoCompleteTextView editTextUser;
    private TextInputLayout input_user_name, input_password;
    private EditText editTextPwd;
    private String userName;
    private Handler handler = new Handler();
    private final String CMD_REGISTER_USER = this.getClass().getName() + "CMD_REGISTER_USER";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.register);
        showToolbar(false);

        input_user_name = findViewById(R.id.input_user_name);
        input_password = findViewById(R.id.input_password);
        editTextUser = findViewById(R.id.tv_user_name);
        editTextPwd = findViewById(R.id.tv_password);

        TextView tvLogin = (TextView) findViewById(R.id.btn_login_confirm);
        tvLogin.setText(R.string.register2);
        tvLogin.setOnClickListener(this);
        findViewById(R.id.icon_login).setOnClickListener(this);
        findViewById(R.id.register).setVisibility(View.GONE);
        findViewById(R.id.forget_pwd).setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login_confirm:
                checkUserName();
                break;
        }
    }

    private void checkUserName() {
        input_user_name.setError(null);
        input_password.setError(null);

        userName = editTextUser.getText().toString();
        String passwd = editTextPwd.getText().toString();

        if (TextUtils.isEmpty(userName)) {
            input_user_name.setError(getString(R.string.input_number));
            return;
        }
        if (TextUtils.isEmpty(passwd)) {
            input_password.setError(getString(R.string.input_pwd));
            return;
        }
        loading.show();
        BmobRequest.getInstance(this).register(userName, passwd, CMD_REGISTER_USER);
    }

    @Override
    public void onMessage(ResponseInfo resp) {
        if (ResponseInfo.OK == resp.getCode()) {
            if (CMD_REGISTER_USER.equals(resp.getCmd())) {
                findViewById(R.id.btn_login_confirm).setEnabled(false);
                Toast(R.string.register_successful);
                SQLiteUserDao.getInstance(App.getCtx()).addUser(resp.getUser());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(App.getCtx(), LoginActivity.class);
                        intent.putExtra(Prefs.KEY_CURRENT_USER_NAME, userName);
                        startActivity(intent);
                        finish();
                    }
                }, 800);
            }
        } else {
            loading.dismiss();
            Toast(resp.getError());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
