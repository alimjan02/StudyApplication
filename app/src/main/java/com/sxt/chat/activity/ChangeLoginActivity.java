package com.sxt.chat.activity;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.sxt.chat.App;
import com.sxt.chat.R;
import com.sxt.chat.adapter.ChangeLoginAdapter;
import com.sxt.chat.base.BaseRecyclerAdapter;
import com.sxt.chat.base.HeaderActivity;
import com.sxt.chat.db.SQLiteUserDao;
import com.sxt.chat.db.User;
import com.sxt.chat.utils.Prefs;
import com.sxt.chat.utils.ToastUtil;
import com.sxt.chat.utils.ToastUtil2;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
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

public class ChangeLoginActivity extends HeaderActivity {

    private ChangeLoginAdapter adapter;
    private SwipeMenuRecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_login);
        setTitle(getString(R.string.change_login));

        mRecyclerView = (SwipeMenuRecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        mRecyclerView.setSwipeMenuItemClickListener(mMenuItemClickListener);
        List<User> userList = SQLiteUserDao.getInstance(this).queryUser();
        adapter = new ChangeLoginAdapter(this, userList);
        adapter.setIndex(findUser(userList));
        mRecyclerView.setAdapter(adapter);
        adapter.setOnClickListener(new BaseRecyclerAdapter.OnClickListener() {
            @Override
            public void onClick(int position, RecyclerView.ViewHolder holder, Object object) {
                loading.show();
                User user = (User) object;
                if (user != null) {
                    final List<User> userList = SQLiteUserDao.getInstance(App.getCtx()).queryUserByUserName(user.getUserName());
                    if (userList != null && userList.size() > 0) {
                        if (BmobUser.getCurrentUser().getUsername().equals(userList.get(0).getUserName())) {
                            Toast("当前账号已登录,无需切换");
                        } else {
                            if (userList.get(0).getUserName() != null && userList.get(0).getUserPwd() != null) {
                                BmobUser.logOut();
                                BmobUser.loginByAccount(userList.get(0).getUserName(), userList.get(0).getUserPwd(), new LogInListener<User>() {
                                    @Override
                                    public void done(User user, BmobException e) {
                                        loading.dismiss();
                                        if (user != null) {
                                            Prefs.getInstance(App.getCtx()).setTicket(userList.get(0).getUserName(), userList.get(0).getTicket(), userList.get(0).getAccountId() == null ? 0 : userList.get(0).getAccountId());
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
            }
        });
    }

    private int findUser(List<User> userList) {
        if (userList != null) {
            BmobUser currentUser = BmobUser.getCurrentUser();
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
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
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
                            .setBackground(R.color.red_6)
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
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                List<User> userList = SQLiteUserDao.getInstance(App.getCtx()).queryUser();
                                adapter.setIndex(findUser(userList));
                                adapter.notifyDataSetChanged(userList);
                                loading.dismiss();
                            }
                        }, 1500);
                    }
                }

            } else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION) {
                ToastUtil.showToast(App.getCtx(), "list第" + adapterPosition + "; 左侧菜单第" + menuPosition);
            }
        }
    };

    private Handler handler = new Handler();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
