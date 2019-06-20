package com.sxt.chat.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.qq.e.ads.interstitial2.UnifiedInterstitialAD;
import com.qq.e.ads.interstitial2.UnifiedInterstitialADListener;
import com.qq.e.comm.util.AdError;
import com.sxt.chat.App;
import com.sxt.chat.R;
import com.sxt.chat.ad.AdBannerActivity;
import com.sxt.chat.db.SQLiteUserDao;
import com.sxt.chat.db.User;
import com.sxt.chat.utils.ArithTool;
import com.sxt.chat.utils.Constants;
import com.sxt.chat.utils.Prefs;
import com.sxt.chat.utils.glide.GlideCircleTransformer;

import java.util.Locale;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by sxt on 2018/1/25.
 */
public class BasicInfoActivity extends AdBannerActivity implements View.OnClickListener {

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
    private float weight, height;
    private long millis = 60 * 1000L;
    private InterstitialAd interstitialAd;
    private String KEY = this.getClass().getName() + "KEY_LAST_RESUME_MILLIS";
    private UnifiedInterstitialAD tencentAlertAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_info);

        setTitle(R.string.basic_info);
        initView();
        loadUserDetailInfo();
//        initGoogleAlertAds();
//        initGoogleAdBanner();
        initTencentAlert();
        initTencentAdBanner(Constants.BannerPosID_personal);
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
            userSex.setText(user.getGender() == null ? "" : user.getGender().equals("M") ? getString(R.string.man) : getString(R.string.woman));
            userAge.setText((user.getAge() == null ? 0 : user.getAge()) <= 0 ? "" : user.getAge() + "");
            userWeight.setText((user.getWeight() == null ? 0 : user.getWeight()) <= 0.0 ? "" : user.getWeight() + "KG");
            userHeight.setText((user.getHeight() == null ? 0 : user.getHeight()) <= 0.0 ? "" : user.getHeight() + "CM");
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
                        height = data.getFloatExtra(String.valueOf(REQUESTCODE_HIGHT), 0.0f);
                        newUser = new User();
                        newUser.setHeight(height);
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
            userSex.setText(user.getGender() == null ? "" : user.getGender().equals("M") ? getString(R.string.man) : getString(R.string.woman));
            userAge.setText((user.getAge() == null ? 0 : user.getAge()) <= 0 ? "" : user.getAge() + "");
            userWeight.setText((user.getWeight() == null ? 0 : user.getWeight()) <= 0.0 ? "" : user.getWeight() + "KG");
            userHeight.setText((user.getHeight() == null ? 0 : user.getHeight()) <= 0.0 ? "" : user.getHeight() + "CM");
            if (weight > 0.0 && height > 0.0) {
                userBmi.setText(String.valueOf(ArithTool.div(weight * 10000, Math.pow(height, 2), 1)));
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
                    user.setHeight(height);
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

    @Override
    public void onResume() {
        super.onResume();
        updateHeadPortrait();
//        loadAD();
    }

    /**
     * 初始化google插屏ad
     */
    private void initGoogleAlertAds() {
        if (interstitialAd == null) {
            interstitialAd = new InterstitialAd(this);
            interstitialAd.setAdUnitId(getString(R.string.adsense_app_ad_alert_personal));
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    Log.e(TAG, "插屏广告加载成功");
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    Log.e(TAG, "插屏广告加载失败 error code: " + errorCode);
                }

                @Override
                public void onAdClosed() {
                    Log.e(TAG, "插屏广告关闭");
                    restartAlertAds();
                }
            });
            restartAlertAds();
        }
    }

    /**
     * 预加载google插屏广告
     */
    private void restartAlertAds() {
        initGoogleAlertAds();
        if (!interstitialAd.isLoading() && !interstitialAd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            interstitialAd.loadAd(adRequest);
            Log.e(TAG, "加载下一个插屏广告");
        }
    }

    /**
     * google广告加载完成后，显示出来,然后预加载下一条广告
     */
    private void showAlertAds() {
        if (interstitialAd != null && interstitialAd.isLoaded()) {
            interstitialAd.show();
            Log.e(TAG, "显示插屏广告");
        } else {
            restartAlertAds();
        }
    }

    /**
     * 显示google插屏广告
     */
    private void loadAD() {
        long lastMillis = Prefs.getInstance(this).getLong(KEY, 0);
        if (System.currentTimeMillis() - lastMillis > millis) {
            Prefs.getInstance(this).putLong(KEY, System.currentTimeMillis());
            showAlertAds();
        }
    }

    /**
     * 初始化Tencent广告
     */
    private void initTencentAlert() {
        tencentAlertAd = new UnifiedInterstitialAD(this, Constants.APPID, Constants.AlertPosID, new UnifiedInterstitialADListener() {
            @Override
            public void onADReceive() {
                Log.e(TAG, "onADReceive : 广告加载成功");
                tencentAlertAd.show();
            }

            @Override
            public void onNoAD(AdError error) {
                String msg = String.format(Locale.getDefault(), "onNoAD, error code: %d, error msg: %s",
                        error.getErrorCode(), error.getErrorMsg());
                Log.e(TAG, "onADOpened msg : " + msg);
            }

            @Override
            public void onADOpened() {
                Log.e(TAG, "onADOpened");
            }

            @Override
            public void onADExposure() {
                Log.e(TAG, "onADExposure");
            }

            @Override
            public void onADClicked() {
                Log.e(TAG, "onADClicked");
            }

            @Override
            public void onADLeftApplication() {
                Log.e(TAG, "onADLeftApplication");
            }

            @Override
            public void onADClosed() {
                Log.e(TAG, "onADClosed");
                tencentAlertAd.close();
            }
        });
        tencentAlertAd.loadAD();
    }

    @Override
    public void onDestroy() {
        if (tencentAlertAd != null) {
            tencentAlertAd.close();
            tencentAlertAd.destroy();
        }
        super.onDestroy();
    }
}
