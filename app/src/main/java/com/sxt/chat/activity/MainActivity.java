package com.sxt.chat.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sxt.chat.App;
import com.sxt.chat.R;
import com.sxt.chat.activity.ad.SplashActivity;
import com.sxt.chat.base.BaseFragment;
import com.sxt.chat.base.TabActivity;
import com.sxt.chat.db.User;
import com.sxt.chat.dialog.AlertDialogBuilder;
import com.sxt.chat.download.DownloadTask;
import com.sxt.chat.fragment.Fragment1;
import com.sxt.chat.fragment.Fragment2;
import com.sxt.chat.fragment.Fragment3;
import com.sxt.chat.fragment.Fragment4;
import com.sxt.chat.utils.ArithTool;
import com.sxt.chat.utils.NetworkUtils;
import com.sxt.chat.utils.Prefs;
import com.sxt.chat.utils.glide.GlideCircleTransform;

import java.util.HashMap;
import java.util.Map;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;

public class MainActivity extends TabActivity implements View.OnClickListener {

    private boolean isFirst = true;
    private ImageView userIcon;
    private TextView userInfo;
    private TextView userName;
    private LinearLayout tabGroup;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private final long millis = 5 * 60 * 1000L;
    public static String KEY_IS_AUTO_LOGIN = "KEY_IS_AUTO_LOGIN";
    public static final String KEY_IS_WILL_GO_LOGIN_ACTIVITY = "KEY_IS_WILL_GO_LOGIN_ACTIVITY";
    private DownloadTask downloadTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BmobUser.getCurrentUser() == null) {
            startActivity(new Intent(App.getCtx(), LoginActivity.class));
            finish();
        } else {
            setContentView(R.layout.activity_main);
            drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
            tabGroup = (LinearLayout) findViewById(R.id.radio_group);
            findViewById(R.id.basic_info).setOnClickListener(this);
            findViewById(R.id.ocr_scan_id_card).setOnClickListener(this);
            findViewById(R.id.change_login).setOnClickListener(this);
            findViewById(R.id.normal_settings).setOnClickListener(this);
            findViewById(R.id.ocr_scan).setOnClickListener(this);
            userIcon = (ImageView) findViewById(R.id.user_icon);
            userInfo = (TextView) findViewById(R.id.user_info);
            userName = (TextView) findViewById(R.id.user_name);

            initDrawer();
            initFragment();
        }
    }

    private void initFragment() {
        Map<Integer, BaseFragment> fragmentMap = new HashMap<>();
        fragmentMap.put(0, new Fragment1());
        fragmentMap.put(1, new Fragment2());
        fragmentMap.put(2, new Fragment3());
        fragmentMap.put(3, new Fragment4());

        Map<Integer, RadioButton> tabMap = new HashMap<>();
        for (int i = 0; i < tabGroup.getChildCount(); i++) {
            tabMap.put(i, (RadioButton) tabGroup.getChildAt(i));
        }
        String[] titles = new String[]{getString(R.string.string_tab_home), getString(R.string.string_tab_github), getString(R.string.string_tab_chart), getString(R.string.string_tab_me)};
        initFragment(fragmentMap, tabMap, titles, R.id.container, 0);
    }

    private void initDrawer() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
//                View content = drawerLayout.getChildAt(0); // 得到contentView
//                int offset = (int) (drawerView.getWidth() * slideOffset);
//                content.setTranslationX(offset);
            }
        };
        drawerToggle.syncState();//实现箭头和三条杠图案切换和侧滑菜单开关的同步
        drawerLayout.addDrawerListener(drawerToggle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUser();
    }

    private void checkUser() {
//      更新本地用户信息
//      注意：需要先登录，否则会报9024错误
        BmobUser.fetchUserJsonInfo(new FetchUserInfoListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    updateUserInfo(BmobUser.getCurrentUser(User.class));
                    loadAD();
                } else {
                    startActivity(new Intent(App.getCtx(), LoginActivity.class));
                }
            }
        });
    }

    private void loadAD() {
        if (isFirst) {
            isFirst = false;
        } else {
            long lastMillis = Prefs.getInstance(this).getLong(Prefs.KEY_LAST_RESUME_MILLIS, 0);
            if (System.currentTimeMillis() - lastMillis > millis) {
                Prefs.getInstance(this).putLong(Prefs.KEY_LAST_RESUME_MILLIS, System.currentTimeMillis());
                //如果上次可视的时间距离现在短于10秒钟,就去赚取广告费 嘎嘎嘎嘎
                Intent intent = new Intent(App.getCtx(), SplashActivity.class);
                intent.putExtra(MainActivity.KEY_IS_WILL_GO_LOGIN_ACTIVITY, false);
                startActivity(intent);
            }
        }
    }

    private void updateUserInfo(User user) {

        if (user != null) {
            if ("M".equals(user.getGender())) {
                update(user.getImgUri(), R.mipmap.men);
            } else {
                update(user.getImgUri(), R.mipmap.female);
            }
            userName.setText(user.getName());
            String info = "";
            if (0 != (user.getAge() == null ? 0 : user.getAge())) {
                if ("F".equals(user.getGender())) {
                    info = user.getAge() + "岁" + "  女  ";
                } else {
                    info = user.getAge() + "岁" + "  男  ";
                }
            } else {
                if ("F".equals(user.getGender())) {
                    info = "女  ";
                } else {
                    info = "男  ";
                }
            }
            double pow = Math.pow((user.getHeight() == null ? 0 : user.getHeight()), 2);
            if (pow > 0) {
                info = info + " BMI:" + (ArithTool.div((user.getWeight() == null ? 0 : user.getWeight()) * 10000, pow, 1));
            }
            userInfo.setText(info);
        }
    }

    private void update(String url, int placeHolder) {
        Glide.with(this)
                .load(url)
                .error(placeHolder)
                .bitmapTransform(new GlideCircleTransform(this))
//                .skipMemoryCache(true)//跳过内存
//                .diskCacheStrategy(DiskCacheStrategy.NONE)//想要生效必须添加 跳过内存
                .into(userIcon);
    }

    @Override
    public void onGoBack(View view) {
        moveTaskToBack(true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long preMillis = Prefs.getInstance(App.getCtx()).getLong(Prefs.KEY_EXIT_ACTIVITY, 0);
            if (System.currentTimeMillis() - preMillis > 2000) {
                Prefs.getInstance(App.getCtx()).putLong(Prefs.KEY_EXIT_ACTIVITY, System.currentTimeMillis());
                Toast(getString(R.string.exit_press_again));
            } else {
                Prefs.getInstance(App.getCtx()).putLong(Prefs.KEY_EXIT_ACTIVITY, 0);
                onBackPressed();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        onGoBack(null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.basic_info:
                startActivity(new Intent(this, BasicInfoActivity.class));
                break;
            case R.id.normal_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.change_login:
                startActivity(new Intent(this, ChangeLoginActivity.class));
                break;
            case R.id.ocr_scan:
                startActivity(new Intent(this, YouTuActivity.class));
                break;
            case R.id.ocr_scan_id_card:
                startActivity(new Intent(this, YouTuIdCardActivity.class));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (downloadTask != null && !downloadTask.isCancelled()) {
            downloadTask.cancel(true);
        }
    }


    //----------------------------------------版本更新-----------------------------------------------
    private void checkUpdate(int serverVersion) {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            if (serverVersion > packageInfo.versionCode) {
                //判断WIFI情况
                if (NetworkUtils.isNetworkAvailable(this)) {//判断网络是否可用
                    if (NetworkUtils.isWifiEnabled(this)) {//判断WIFI是否打开
                        if (NetworkUtils.isWifi(this)) {//判断是wifi还是3g网络
                            startDownloadApkDialog();
                        } else {
                            if (NetworkUtils.is3rd(this)) {
                                showWifiAlert();
                            }
                        }
                    } else {
                        if (NetworkUtils.is3rd(this)) {//判断是否是3G网络
                            showWifiAlert();
                        }
                    }
                }
            } else {
                Toast("当前已是最新版本");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void startDownloadApkDialog() {
        final Dialog dialog = new Dialog(this, R.style.CustomDialog);
        View item = LayoutInflater.from(this).inflate(R.layout.item_update, null);
        final TextView progressTitle = (TextView) item.findViewById(R.id.upgrade_title);
        final ProgressBar progressBar = (ProgressBar) item.findViewById(R.id.my_progress);
        final TextView tvProgress = (TextView) item.findViewById(R.id.tv_progres);
        dialog.setContentView(item);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            WindowManager wm = this.getWindowManager();
            Display display = wm.getDefaultDisplay();
            layoutParams.width = (int) (display.getWidth() * 0.875);
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
        }
        download(dialog, progressTitle, progressBar, tvProgress);
        item.findViewById(R.id.btn_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                download(dialog, progressTitle, progressBar, tvProgress);
            }
        });
        item.findViewById(R.id.btn_download_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadTask.cancel(true);
                dialog.dismiss();
            }
        });
    }

    private void showWifiAlert() {
        new AlertDialogBuilder(this).setTitle(getString(R.string.prompt)).setMessage("您当前处于非WIFI状态，继续更新版本吗?").setLeftButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startDownloadApkDialog();
            }
        }).setRightButton(R.string.cancel, null).show();
    }

    private void download(final Dialog dialog, final TextView progressTitle, final ProgressBar progressBar, final TextView tvProgress) {
        if (downloadTask != null && !downloadTask.isCancelled()) {
            downloadTask.cancel(true);
        }
        downloadTask = new DownloadTask(this);
        downloadTask.setDownloadListener(new DownloadTask.DownloadListener() {
            @Override
            public void onError(Exception e) {
                dialog.dismiss();
                Toast("下载文件失败，请重新登录进行升级");
            }

            @Override
            public void onProgressUpdate(int progress, long max) {
                if (dialog != null) {
                    double size = max / 1024.0 / 1024.0;
                    String result = String.format("更新版本:(共%.2fMB)", size);
                    progressTitle.setText(result);
                    progressBar.setProgress(progress);
                    tvProgress.setText(progress + "%");
                }
            }

            @Override
            public void onSuccessful() {
                dialog.dismiss();
            }

            @Override
            public void onFinish() {
                dialog.dismiss();
            }
        });
        downloadTask.execute(Prefs.getInstance(this).getServerUrl() + Prefs.getInstance(this).KEY_APP_UPDATE_URL);
    }

}
