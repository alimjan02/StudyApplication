package com.sxt.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.comm.util.AdError;
import com.sxt.chat.R;
import com.sxt.chat.base.HeaderActivity;
import com.sxt.chat.db.SQLiteUserDao;
import com.sxt.chat.db.User;
import com.sxt.chat.utils.ArithTool;
import com.sxt.chat.utils.Constants;
import com.sxt.chat.utils.glide.GlideCircleTransform;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;
import cn.bmob.v3.listener.UpdateListener;

/* Created by izhaohu on 2018/1/25.
 */
public class BasicInfoActivity extends HeaderActivity implements View.OnClickListener {

    private ImageView userPortait;
    private TextView userName;
    private TextView bodyNumber;
    private TextView userSex;
    private TextView userAge;
    private TextView userWeight;
    private TextView userHight;
    private TextView userBmi;
    public static final int REQUESTCODE_USER_NAME = 998;
    public static final int REQUESTCODE_IMG = 999;
    public static final int REQUESTCODE_AGE = 1000;
    public static final int REQUESTCODE_Number = 1001;
    public static final int REQUESTCODE_SEX = 1002;
    public static final int REQUESTCODE_WEIGHT = 1003;
    public static final int REQUESTCODE_HIGHT = 1004;

    private final String CMD_SAVE_USER_IMG = "CMD_SAVE_USER_IMG";
    private final String CMD_SAVE_ID_CARD = "CMD_SAVE_ID_CARD";
    private final String CMD_SAVE_USER_NAME = "CMD_SAVE_USER_NAME";
    private final String CMD_SAVE_USER_SEX = "CMD_SAVE_USER_SEX";
    private final String CMD_SAVE_USER_AGE = "CMD_SAVE_USER_AGE";
    private final String CMD_SAVE_USER_Weight = "CMD_SAVE_USER_Weight";
    private final String CMD_SAVE_USER_Height = "CMD_SAVE_USER_Height";
    private String name;
    private String age;
    private String sex;
    private String idCard;
    private float weight;
    private float hight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_info);

        setTitle(R.string.basic_info);

        findViewById(R.id.user_portrait_layout).setOnClickListener(this);
        findViewById(R.id.user_name_layout).setOnClickListener(this);
        findViewById(R.id.body_number_layout).setOnClickListener(this);
        findViewById(R.id.user_sex_layout).setOnClickListener(this);
        findViewById(R.id.user_age_layout).setOnClickListener(this);
        findViewById(R.id.user_weight_layout).setOnClickListener(this);
        findViewById(R.id.user_hight_layout).setOnClickListener(this);

        userPortait = (ImageView) findViewById(R.id.user_portrait);
        userName = (TextView) findViewById(R.id.user_name);
        bodyNumber = (TextView) findViewById(R.id.body_number);
        userSex = (TextView) findViewById(R.id.user_sex);
        userAge = (TextView) findViewById(R.id.user_age);
        userWeight = (TextView) findViewById(R.id.user_weight);
        userHight = (TextView) findViewById(R.id.user_hight);
        userBmi = (TextView) findViewById(R.id.user_bmi);

        loadUserDetailInfo();
        initAdBanner();
    }

    private void loadUserDetailInfo() {
        User user = BmobUser.getCurrentUser(User.class);
        if (user != null) {
            userName.setText(user.getName() == null ? user.getUserName() : user.getName());
            bodyNumber.setText(user.getIdCard() == null ? "" : user.getIdCard());
            userSex.setText(user.getGender() == null ? "" : user.getGender().equals("M") ? "男" : "女");
            userAge.setText((user.getAge() == null ? 0 : user.getAge()) <= 0 ? "" : user.getAge() + "岁");
            userWeight.setText((user.getWeight() == null ? 0 : user.getWeight()) <= 0.0 ? "" : String.valueOf(user.getWeight() + "KG"));
            userHight.setText((user.getHeight() == null ? 0 : user.getHeight()) <= 0.0 ? "" : String.valueOf(user.getHeight() + "CM"));
            double pow = Math.pow(user.getHeight() == null ? 0 : user.getHeight(), 2);
            if (pow > 0.0) {
                userBmi.setText(ArithTool.div((user.getWeight() == null ? 0 : user.getWeight()) * 10000, pow, 1) + "");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateHeadPortrait();
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
                .bitmapTransform(new GlideCircleTransform(this))
//                .skipMemoryCache(true)//跳过内存
//                .diskCacheStrategy(DiskCacheStrategy.NONE)//想要生效必须添加 跳过内存
                .into(userPortait);
    }

    private void initAdBanner() {
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

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.user_portrait_layout:
                Intent intent = new Intent(this, UpdateUserImgActivity.class);
                startActivityForResult(intent, REQUESTCODE_IMG);
                break;

            case R.id.user_name_layout:
                startActivityForResult(new Intent(this, UpdateUserNameActivity.class), REQUESTCODE_USER_NAME);
                break;

            case R.id.body_number_layout:
                startActivityForResult(new Intent(this, SelectNumberActivity.class), REQUESTCODE_Number);
                break;

            case R.id.user_sex_layout:
                startActivityForResult(new Intent(this, SelectSexActivity.class), REQUESTCODE_SEX);
                break;

            case R.id.user_age_layout:
                Intent ageIntent = new Intent(this, SelectAgeActivity.class);
                ageIntent.putExtra(String.valueOf(REQUESTCODE_AGE), false);
                startActivityForResult(ageIntent, REQUESTCODE_AGE);
                break;

            case R.id.user_weight_layout:
                Intent weightIntent = new Intent(this, SelectWeightActivity.class);
                weightIntent.putExtra(String.valueOf(REQUESTCODE_WEIGHT), false);
                startActivityForResult(weightIntent, REQUESTCODE_WEIGHT);
                break;

            case R.id.user_hight_layout:
                Intent hightIntent = new Intent(this, SelectHeightActivity.class);
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
            userHight.setText((user.getHeight() == null ? 0 : user.getHeight()) <= 0.0 ? "" : String.valueOf(user.getHeight() + "CM"));
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
        BmobUser bmobUser = BmobUser.getCurrentUser();
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
