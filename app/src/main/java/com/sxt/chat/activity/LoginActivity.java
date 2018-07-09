package com.sxt.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.sxt.chat.App;
import com.sxt.chat.R;
import com.sxt.chat.base.HeaderActivity;
import com.sxt.chat.db.SQLiteUserDao;
import com.sxt.chat.db.User;
import com.sxt.chat.utils.Prefs;
import com.sxt.chat.utils.ToastUtil;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

public class LoginActivity extends HeaderActivity implements View.OnClickListener {

    private EditText editTextUser;
    private EditText editTextPwd;
    private String userName;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.login);
        showToolbar(false);
        User currentUser = BmobUser.getCurrentUser(User.class);
        if (currentUser == null) {
            initView();
        } else {

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(MainActivity.KEY_IS_AUTO_LOGIN, true);
            startActivity(intent);
            finish();
        }
    }

    private void initView() {
        findViewById(R.id.root).setVisibility(View.VISIBLE);
        editTextUser = (EditText) findViewById(R.id.edit_text_user);
        editTextPwd = (EditText) findViewById(R.id.edit_text_pwd);
        findViewById(R.id.btn_login_confirm).setOnClickListener(this);
        findViewById(R.id.icon_login).setOnClickListener(this);
        findViewById(R.id.register).setOnClickListener(this);
        findViewById(R.id.forget_pwd).setOnClickListener(this);
        String name = getIntent().getStringExtra(Prefs.KEY_CURRENT_USER_NAME);
        if (name != null) {
            this.userName = name;
            editTextUser.setText(name);
            editTextPwd.requestFocus();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login_confirm:
                checkUserName();
                break;
            case R.id.register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }
    }

    private void checkUserName() {
        userName = editTextUser.getText().toString().trim();
        String passwd = editTextPwd.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            ToastUtil.showToast(App.getCtx(), getString(R.string.input_number));
            return;
        }
        if (TextUtils.isEmpty(passwd)) {
            ToastUtil.showToast(App.getCtx(), getString(R.string.input_pwd));
            return;
        }
        loginByAccount(userName, passwd);
    }

    private void loginByAccount(String userName, String passwd) {
        loading.show();
        BmobUser.loginByAccount(userName, passwd, new LogInListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                loading.dismiss();
                if (user != null) {
                    SQLiteUserDao.getInstance(App.getCtx()).addUser(user);
                    Prefs prefs = Prefs.getInstance(App.getCtx());
                    prefs.setTicket(user.getUsername(), user.getTicket(), user.getAccountId() == null ? 0 : user.getAccountId());
                    Intent intent = new Intent(App.getCtx(), MainActivity.class);
                    intent.putExtra(MainActivity.KEY_IS_WILL_GO_LOGIN_ACTIVITY, true);
                    startActivity(intent);
                    finish();
                } else {
                    Toast("errorCode: " + e.getErrorCode() + " , " + e.getMessage());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
