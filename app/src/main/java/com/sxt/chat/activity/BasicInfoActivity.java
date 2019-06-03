package com.sxt.chat.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.comm.util.AdError;
import com.sxt.chat.App;
import com.sxt.chat.R;
import com.sxt.chat.base.HeaderActivity;
import com.sxt.chat.db.SQLiteUserDao;
import com.sxt.chat.db.User;
import com.sxt.chat.utils.ArithTool;
import com.sxt.chat.utils.Constants;
import com.sxt.chat.utils.Prefs;
import com.sxt.chat.utils.glide.GlideCircleTransformer;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by sxt on 2018/1/25.
 */
public class BasicInfoActivity extends HeaderActivity implements View.OnClickListener {

    private ImageView userPortait;
    private TextView userName, bodyNumber, userSex, userAge, userWeight, userHeight, userBmi;
    public static final int REQUESTCODE_USER_NAME = 998;
    public static final int REQUESTCODE_IMG = 999, REQUESTCODE_AGE = 1000;
    public static final int REQUESTCODE_Number = 1001, REQUESTCODE_SEX = 1002;
    public static final int REQUESTCODE_WEIGHT = 1003, REQUESTCODE_HIGHT = 1004;

    private final String CMD_SAVE_USER_IMG = "CMD_SAVE_USER_IMG";
    private final String CMD_SAVE_ID_CARD = "CMD_SAVE_ID_CARD";
    private final String CMD_SAVE_USER_NAME = "CMD_SAVE_USER_NAME";
    private final String CMD_SAVE_USER_SEX = "CMD_SAVE_USER_SEX";
    private final String CMD_SAVE_USER_AGE = "CMD_SAVE_USER_AGE";
    private final String CMD_SAVE_USER_Weight = "CMD_SAVE_USER_Weight";
    private final String CMD_SAVE_USER_Height = "CMD_SAVE_USER_Height";
    private String name, age, sex, idCard;
    private float weight, hight;
    private AdView adGoogleBannerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_info);

        setTitle(R.string.basic_info);
        initView();
        loadUserDetailInfo();
        initGoogleAdBanner();
    }

    private void initView() {
        findViewById(R.id.user_portrait_layout).setOnClickListener(this);
        findViewById(R.id.user_name_layout).setOnClickListener(this);
        findViewById(R.id.body_number_layout).setOnClickListener(this);
        findViewById(R.id.user_sex_layout).setOnClickListener(this);
        findViewById(R.id.user_age_layout).setOnClickListener(this);
        findViewById(R.id.user_weight_layout).setOnClickListener(this);
        findViewById(R.id.user_hight_layout).setOnClickListener(this);

        userPortait = findViewById(R.id.user_portrait);
        userName = findViewById(R.id.user_name);
        bodyNumber = findViewById(R.id.body_number);
        userSex = findViewById(R.id.user_sex);
        userAge = findViewById(R.id.user_age);
        userWeight = findViewById(R.id.user_weight);
        userHeight = findViewById(R.id.user_hight);
        userBmi = findViewById(R.id.user_bmi);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            userPortait.setTransitionName("shareView");
        }
    }

    private void loadUserDetailInfo() {
        User user = BmobUser.getCurrentUser(User.class);
        if (user != null) {
            userName.setText(user.getName() == null ? user.getUserName() : user.getName());
            bodyNumber.setText(user.getIdCard() == null ? "" : user.getIdCard());
            userSex.setText(user.getGender() == null ? "" : user.getGender().equals("M") ? "男" : "女");
            userAge.setText((user.getAge() == null ? 0 : user.getAge()) <= 0 ? "" : user.getAge() + "岁");
            userWeight.setText((user.getWeight() == null ? 0 : user.getWeight()) <= 0.0 ? "" : String.valueOf(user.getWeight() + "KG"));
            userHeight.setText((user.getHeight() == null ? 0 : user.getHeight()) <= 0.0 ? "" : String.valueOf(user.getHeight() + "CM"));
            double pow = Math.pow(user.getHeight() == null ? 0 : user.getHeight(), 2);
            if (pow > 0.0) {
                userBmi.setText(ArithTool.div((user.getWeight() == null ? 0 : user.getWeight()) * 10000, pow, 1) + "");
            }
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
                .signature(new StringSignature(Prefs.getInstance(App.getCtx()).getString(Prefs.KEY_USER_HEADER_IMAGE_FLAG, "")))
                .into(userPortait);
    }

    /**
     * 腾讯Banner广告位
     */
    private void initTencentAdBanner() {
        FrameLayout bannerContainer = (FrameLayout) findViewById(R.id.ad_banner_container);
        // 创建Banner广告AdView对象
        // appId : 在 http://e.qq.com/dev/ 能看到的app唯一字符串
        // posId : 在 http://e.qq.com/dev/ 生成的数字串，并非 appid 或者 appkey
        BannerView banner = new BannerView(this, com.qq.e.ads.banner.ADSize.BANNER, Constants.APPID, Constants.BannerPosID);
        //设置广告轮播时间，为0或30~120之间的数字，单位为s,0标识不自动轮播
        banner.setRefresh(30);
        banner.setADListener(new AbstractBannerADListener() {

            @Override
            public void onNoAD(AdError error) {
                Log.i("AD_DEMO", "BannerNoAD，eCode=" + error.getErrorCode());
            }

            @Override
            public void onADReceiv() {
                Log.i("AD_DEMO", "ONBannerReceive");

            }
        });
        bannerContainer.addView(banner);
        /* 发起广告请求，收到广告数据后会展示数据   */
        banner.loadAD();
    }

    /**
     * Google Banner广告位
     */
    private void initGoogleAdBanner() {
        adGoogleBannerView = findViewById(R.id.ad_view);
        //製作廣告請求。檢查您的logcat輸出中的散列設備ID，
        // 以在物理設備上獲取測試廣告。例如
        // “使用AdRequest.Builder.addTestDevice（”ABCDEF012345“）在此設備上獲取測試廣告。”
        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        // 開始在後台加載廣告。
        adGoogleBannerView.loadAd(adRequest);
    }

    @Override
    public void onPause() {
        if (adGoogleBannerView != null) {
            adGoogleBannerView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateHeadPortrait();
        if (adGoogleBannerView != null) {
            adGoogleBannerView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (adGoogleBannerView != null) {
            adGoogleBannerView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onGoBack(View view) {
//        super.onGoBack(view);
        onBackPressed();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.user_portrait_layout:
                Intent intent = new Intent(this, com.sxt.chat.activity.UpdateUserImgActivity.class);
                startActivityForResult(intent, REQUESTCODE_IMG);
                break;

            case R.id.user_name_layout:
                startActivityForResult(new Intent(this, com.sxt.chat.activity.UpdateUserNameActivity.class), REQUESTCODE_USER_NAME);
                break;

            case R.id.body_number_layout:
                startActivityForResult(new Intent(this, com.sxt.chat.activity.SelectNumberActivity.class), REQUESTCODE_Number);
                break;

            case R.id.user_sex_layout:
                startActivityForResult(new Intent(this, com.sxt.chat.activity.SelectSexActivity.class), REQUESTCODE_SEX);
                break;

            case R.id.user_age_layout:
                Intent ageIntent = new Intent(this, com.sxt.chat.activity.SelectAgeActivity.class);
                ageIntent.putExtra(String.valueOf(REQUESTCODE_AGE), false);
                startActivityForResult(ageIntent, REQUESTCODE_AGE);
                break;

            case R.id.user_weight_layout:
                Intent weightIntent = new Intent(this, com.sxt.chat.activity.SelectWeightActivity.class);
                weightIntent.putExtra(String.valueOf(REQUESTCODE_WEIGHT), false);
                startActivityForResult(weightIntent, REQUESTCODE_WEIGHT);
                break;

            case R.id.user_hight_layout:
                Intent hightIntent = new Intent(this, com.sxt.chat.activity.SelectHeightActivity.class);
                hightIntent.putExtra(String.valueOf(REQUESTCODE_HIGHT), false);
                startActivityForResult(hightIntent, REQUESTCODE_HIGHT);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            User newUser = null;
            switch (requestCode) {
                case REQUESTCODE_IMG:
                    updateUserInfo(BmobUser.getCurrentUser(User.class), CMD_SAVE_USER_NAME);
                    break;
                case REQUESTCODE_USER_NAME:
                    if (data != null) {
                        name = data.getStringExtra(String.valueOf(REQUESTCODE_USER_NAME));
                        newUser = new User();
                        newUser.setName(name);
                        updateUserInfo(newUser, CMD_SAVE_USER_NAME);
                    }
                    break;
                case REQUESTCODE_AGE:
                    if (data != null) {
                        age = data.getStringExtra(String.valueOf(REQUESTCODE_AGE));
                        newUser = new User();
                        newUser.setAge(Integer.parseInt(age));
                        updateUserInfo(newUser, CMD_SAVE_USER_AGE);
                    }
                    break;
                case REQUESTCODE_SEX:
                    if (data != null) {
                        sex = data.getStringExtra(String.valueOf(REQUESTCODE_SEX));
                        newUser = new User();
                        if ("M".equals(sex)) {
                            newUser.setGender("M");
                        } else if ("F".equals(sex)) {
                            newUser.setGender("F");
                        } else {
                            newUser.setGender("M");
                        }
                        updateUserInfo(newUser, CMD_SAVE_USER_SEX);
                    }
                    break;
                case REQUESTCODE_Number:
                    if (data != null) {
                        idCard = data.getStringExtra(String.valueOf(REQUESTCODE_Number));
                        bodyNumber.setText(idCard);
                        newUser = new User();
                        newUser.setIdCard(idCard);
                        updateUserInfo(newUser, CMD_SAVE_ID_CARD);
                    }
                    break;
                case REQUESTCODE_WEIGHT:
                    if (data != null) {
                        weight = data.getFloatExtra(String.valueOf(REQUESTCODE_WEIGHT), 0.0f);
                        newUser = new User();
                        newUser.setWeight(weight);
                        updateUserInfo(newUser, CMD_SAVE_USER_Weight);
                    }
                    break;
                case REQUESTCODE_HIGHT:
                    if (data != null) {
                        hight = data.getFloatExtra(String.valueOf(REQUESTCODE_HIGHT), 0.0f);
                        newUser = new User();
                        newUser.setHeight(hight);
                        updateUserInfo(newUser, CMD_SAVE_USER_Height);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void response(String cmd_save_id, User user) {
        if (user != null) {
            userName.setText(user.getName() == null ? user.getUserName() : user.getName());
            bodyNumber.setText(user.getIdCard() == null ? "" : user.getIdCard());
            userSex.setText(user.getGender() == null ? "" : user.getGender().equals("M") ? "男" : "女");
            userAge.setText((user.getAge() == null ? 0 : user.getAge()) <= 0 ? "" : user.getAge() + "岁");
            userWeight.setText((user.getWeight() == null ? 0 : user.getWeight()) <= 0.0 ? "" : String.valueOf(user.getWeight() + "KG"));
            userHeight.setText((user.getHeight() == null ? 0 : user.getHeight()) <= 0.0 ? "" : String.valueOf(user.getHeight() + "CM"));
            if (weight > 0.0 && hight > 0.0) {
                userBmi.setText(String.valueOf(ArithTool.div(weight * 10000, Math.pow(hight, 2), 1)));
            }
            switch (cmd_save_id) {
                case CMD_SAVE_USER_IMG:
                    user.setImgUri(user.getImgUri());
                    break;
                case CMD_SAVE_ID_CARD:
                    user.setIdCard(idCard);
                    break;
                case CMD_SAVE_USER_NAME:
                    user.setName(name);
                    break;
                case CMD_SAVE_USER_AGE:
                    user.setAge(Integer.parseInt(age));
                    break;
                case CMD_SAVE_USER_SEX:
                    user.setGender(sex);
                    break;
                case CMD_SAVE_USER_Weight:
                    user.setWeight(weight);
                    break;
                case CMD_SAVE_USER_Height:
                    user.setHeight(hight);
                    break;
                default:
                    break;
            }
            //将用户的详细信息保存至本地
            SQLiteUserDao.getInstance(this).updateUserByUserName(user.getUsername(), user);
        }
    }

    private void updateUserInfo(final User newUser, final String cmd) {
        loading.show();
        BmobUser bmobUser = BmobUser.getCurrentUser(User.class);
        newUser.update(bmobUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e != null) {
                    loading.dismiss();
                    Toast("errorCode: " + e.getErrorCode() + " , " + e.getMessage());
                } else {
                    //更新本地User信息 ; 注意：需要先登录，否则会报9024错误
                    BmobUser.fetchUserJsonInfo(new FetchUserInfoListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            loading.dismiss();
                            if (e != null) {
                                Toast("errorCode: " + e.getErrorCode() + " , " + e.getMessage());
                            } else {
                                response(cmd, BmobUser.getCurrentUser(User.class));
                            }
                        }
                    });
                }
            }
        });
    }


}
