package com.sxt.chat.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sxt.chat.App;
import com.sxt.chat.R;
import com.sxt.chat.base.HeaderActivity;
import com.sxt.chat.db.SQLiteUserDao;
import com.sxt.chat.db.User;
import com.sxt.chat.utils.ToastUtil;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends HeaderActivity implements View.OnClickListener {

    private EditText editTextUser;
    private EditText editTextPwd;
    private String userName;
    private Handler handler = new Handler();
    private final int REQUEST_READ_PHONE_STATE_CODE = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.register);
        showToolbar(false);

        editTextUser = (EditText) findViewById(R.id.edit_text_user);
        editTextPwd = (EditText) findViewById(R.id.edit_text_pwd);
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
        boolean permission = checkPermission();
        if (!permission) {
            Toast("请允许读取手机状态的权限");
            return;
        }
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
        loading.show();
        User user = new User();
        user.setUsername(userName);
        user.setPassword(passwd);
        user.setUserName(userName);
        user.setName(userName);
        user.setUserPwd(passwd);
        user.setPhone(userName);
        user.signUp(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                loading.dismiss();
                if (e == null) {//注册成功
                    findViewById(R.id.btn_login_confirm).setEnabled(false);
                    Toast(R.string.register_successful);
                    SQLiteUserDao.getInstance(App.getCtx()).addUser(user);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 800);
                } else {
                    Toast("errorCode: " + e.getErrorCode() + " , " + e.getMessage());
                }
            }
        });

//        //保存当前登录者的信息
//        List<User> userList = SQLiteUserDao.getInstance(App.getCtx()).queryUserByUserName(userName);
//        if (userList == null || userList.size() == 0) {//新账户 , 存入数据库
//            final User userNew = new User();
//            userNew.setId(0);
//            userNew.setAccountId(0);
//            userNew.setTicket("");
//            userNew.setUserPwd(passwd);
//            userNew.setPhone("123456789");
//            userNew.setName(userName);
//            userNew.setUserName(userName);
//
//            userNew.save(new SaveListener<String>() {
//                @Override
//                public void done(String objectId, BmobException e) {
//                    loading.dismiss();
//                    if (e == null) {
//                        SQLiteUserDao.getInstance(App.getCtx()).addUser(userNew);
//                        Prefs prefs = Prefs.getInstance(App.getCtx());
//                        prefs.setTicket(userName, null, 0);
//                        Toast("添加数据成功，返回objectId为：" + objectId);
//                        startActivity(new Intent(App.getCtx(), MainActivity.class));
//                        finish();
//                    } else {
//                        Toast("创建数据失败：" + e.getMessage());
//                    }
//                }
//            });
//        } else {
//            if (!passwd.equals(userList.get(0).getUserPwd())) {
//                Toast("密码错误");
//                loading.dismiss();
//                return;
//            }
//            User userNew = new User();
//            userNew.setId(userList.get(0).getId());
//            userNew.setAccountId(userList.get(0).getId());
//            userNew.setTicket(userList.get(0).getTicket());
//            userNew.setPhone(userList.get(0).getPhone());
//            userNew.setName(userName);
//            userNew.setUserName(userName);
//            userNew.setUserPwd(passwd);
//            userNew.setImgUri(userList.get(0).getImgUri());
//
//            Prefs prefs = Prefs.getInstance(App.getCtx());
//            prefs.setTicket(userName, userList.get(0).getTicket(), userList.get(0).getId());
//
//            SQLiteUserDao.getInstance(App.getCtx()).updateUserByUserName(userList.get(0).getUserName(), userNew);
//            loading.dismiss();
//        }
    }

    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(App.getCtx(), Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    REQUEST_READ_PHONE_STATE_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                break;
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
