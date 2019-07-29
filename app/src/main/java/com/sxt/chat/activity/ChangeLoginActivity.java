package com.sxt.chat.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.sxt.chat.App;
import com.sxt.chat.R;
import com.sxt.chat.ad.AdBannerActivity;
import com.sxt.chat.adapter.ChangeLoginAdapter;
import com.sxt.chat.db.SQLiteUserDao;
import com.sxt.chat.db.User;
import com.sxt.chat.utils.Constants;
import com.sxt.chat.utils.Prefs;
import com.sxt.chat.utils.ToastUtil2;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

/**
 * Created by izhaohu on 2018/2/6.
 */

public class ChangeLoginActivity extends AdBannerActivity {

    private ChangeLoginAdapter adapter;
    private SwipeMenuRecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_login);
        setTitle(getString(R.string.change_login));
        boolean flag = Prefs.getInstance(this).getBoolean(Prefs.KEY_IS_SHOW_GOOGLE_AD, false);
        Log.e(TAG, "Google admob 显示状态 ：flag " + flag);
        if (flag) {
            initGoogleAdBanner();
        } else {
            initTencentAdBanner2(Constants.BannerPosID_personal_change_Login);
        }

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_vertical));
        mRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        mRecyclerView.setSwipeMenuItemClickListener(mMenuItemClickListener);
        List<User> userList = SQLiteUserDao.getInstance(this).queryUser();
        adapter = new ChangeLoginAdapter(this, userList);
        adapter.setIndex(findUser(userList));
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((position, user) -> {
            loading.show();
            if (user != null) {
                final List<User> userList1 = SQLiteUserDao.getInstance(App.getCtx()).queryUserByUserName(user.getUserName());
                if (userList1 != null && userList1.size() > 0) {
                    if (BmobUser.getCurrentUser(User.class).getUsername().equals(userList1.get(0).getUserName())) {
                        Toast("当前账号已登录,无需切换");
                    } else {
                        if (userList1.get(0).getUserName() != null && userList1.get(0).getUserPwd() != null) {
                            BmobUser.logOut();
                            BmobUser.loginByAccount(userList1.get(0).getUserName(), userList1.get(0).getUserPwd(), new LogInListener<User>() {
                                @Override
                                public void done(User user, BmobException e) {
                                    loading.dismiss();
                                    if (user != null) {
                                        Prefs.getInstance(App.getCtx()).setTicket(userList1.get(0).getUserName(), userList1.get(0).getTicket(), userList1.get(0).getAccountId());
                                        ToastUtil2.showToast(App.getCtx(), "切换成功");
                                        finish();
                                    } else if (e != null) {
                                        Toast("errorCode: " + e.getErrorCode() + " , " + e.getMessage());
                                    }
                                }
                            });
                            return;
                        }
                    }
                }
            }
            loading.dismiss();
        });
    }

    private int findUser(List<User> userList) {
        if (userList != null) {
            BmobUser currentUser = BmobUser.getCurrentUser(User.class);
            String userName = null;
            if (currentUser != null) {
                userName = currentUser.getUsername();
            }
            for (int i = 0; i < userList.size(); i++) {
                if (userName != null && userName.equals(userList.get(i).getUserName())) {
                    return i;
                }
            }
        }
        return -1000;
    }

    /**
     * 菜单创建器，在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = (swipeLeftMenu, swipeRightMenu, viewType) -> {
        int width = getResources().getDimensionPixelSize(R.dimen.dp_70);

        // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
        // 2. 指定具体的高，比如80;
        // 3. WRAP_CONTENT，自身高度，不推荐;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        // 添加左侧的，如果不添加，则左侧不会出现菜单。
//            {
//                SwipeMenuItem addItem = new SwipeMenuItem(App.getCtx())
//                        .setBackground(R.color.yellow_green_middle)
//                        .setImage(R.mipmap.xuetang)
//                        .setWidth(width)
//                        .setHeight(height);
//                swipeLeftMenu.addMenuItem(addItem); // 添加菜单到左侧。
//
//                SwipeMenuItem closeItem = new SwipeMenuItem(App.getCtx())
//                        .setBackground(R.color.alpha_2)
//                        .setImage(R.mipmap.heart)
//                        .setWidth(width)
//                        .setHeight(height);
//                swipeLeftMenu.addMenuItem(closeItem); // 添加菜单到左侧。
//            }

        // 添加右侧的，如果不添加，则右侧不会出现菜单。
        {
            if (viewType == ChangeLoginAdapter.Other_USER) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(App.getCtx())
                        .setBackground(R.color.red_4)
//                        .setImage(R.mipmap.xuetang)
                        .setText("删除")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。
            }

//                SwipeMenuItem addItem = new SwipeMenuItem(App.getCtx())
//                        .setBackground(R.color.dividing_line)
//                        .setText("修改")
//                        .setTextColor(ContextCompat.getColor(App.getCtx(), R.color.text_color_1))
//                        .setWidth(width)
//                        .setHeight(height);
//                swipeRightMenu.addMenuItem(addItem); // 添加菜单到右侧。
        }
    };

    /**
     * RecyclerView的Item的Menu点击监听。
     */
    private SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(final SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();

            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            final int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。

            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {

                if (adapter.getItemViewType(adapterPosition) != ChangeLoginAdapter.Current_User) {

                    if (menuPosition == 0) {
                        menuBridge.closeMenu();
                        //删除用户选中的 账号
                        loading.show();
                        User user = adapter.getItem(adapterPosition);
                        SQLiteUserDao.getInstance(App.getCtx()).deleteUser(user.getUserName());
                        handler.postDelayed(() -> {
                            List<User> userList = SQLiteUserDao.getInstance(App.getCtx()).queryUser();
                            adapter.setIndex(findUser(userList));
                            adapter.notifyDataSetChanged(userList);
                            loading.dismiss();
                        }, 1500);
                    }
                }

            } else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION) {
                Toast("list第" + adapterPosition + "; 左侧菜单第" + menuPosition);
            }
        }
    };

    private Handler handler = new Handler();

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
