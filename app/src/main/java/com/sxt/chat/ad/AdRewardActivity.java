package com.sxt.chat.ad;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.sxt.chat.App;
import com.sxt.chat.R;
import com.sxt.chat.activity.MainActivity;
import com.sxt.chat.activity.SplashActivity;
import com.sxt.chat.base.TabActivity;
import com.sxt.chat.utils.Prefs;

public class AdRewardActivity extends TabActivity {

    private boolean isFirst = true;
    private final long millis = 2 * 60 * 1000L;
    private InterstitialAd alertAd;
    private RewardedVideoAd rewardedVideoAd;
    protected final int REQUEST_CODE_LOCATION_AD = 201;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGoogleAds();
    }

    /**
     * google ads 初始化
     */
    private void initGoogleAds() {
        initGoogleAlertAds();
//        initGoogleRewardedAds();
    }

    private void prepareGoogleAds() {
        restartAlertAds();
//        restartRewardedVideo();
    }

    /**
     * 初始化google插屏ad
     */
    private void initGoogleAlertAds() {
        if (alertAd == null) {
            alertAd = new InterstitialAd(this);
            alertAd.setAdUnitId(getString(R.string.adsense_app_ad_alert_home));
            alertAd.setAdListener(new AdListener() {
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
        }
    }

    /**
     * 预加载插屏广告
     */
    private void restartAlertAds() {
        initGoogleAlertAds();
        if (!alertAd.isLoading() && !alertAd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            alertAd.loadAd(adRequest);
            Log.e(TAG, "加载下一个插屏广告");
        }
    }

    /**
     * 广告加载完成后，显示出来,然后预加载下一条广告
     */
    private void showAlertAds() {
        if (alertAd != null && alertAd.isLoaded()) {
            alertAd.show();
            Log.e(TAG, "显示插屏广告");
        } else {
            restartAlertAds();
        }
    }

    /**
     * 初始化google激励广告
     */
    private void initGoogleRewardedAds() {
        if (rewardedVideoAd == null) {
            rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
            rewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
                @Override
                public void onRewardedVideoAdLoaded() {
                    Log.e(TAG, "onRewardedVideoAdLoaded : 激励广告加载完成");
                }

                @Override
                public void onRewardedVideoAdOpened() {
                    Log.e(TAG, "onRewardedVideoAdOpened : 激励广告被点开");
                }

                @Override
                public void onRewardedVideoStarted() {
                    Log.e(TAG, "onRewardedVideoAdLeftApplication : 激励广告加载开始");
                }

                @Override
                public void onRewardedVideoAdClosed() {
                    Log.e(TAG, "onRewardedVideoAdClosed : 激励广告被关闭");
                }

                @Override
                public void onRewarded(RewardItem rewardItem) {
                    Log.e(TAG, "onRewarded : 激励广告获得奖励");
                }

                @Override
                public void onRewardedVideoAdLeftApplication() {
                    Log.e(TAG, "onRewardedVideoAdLeftApplication");
                }

                @Override
                public void onRewardedVideoAdFailedToLoad(int i) {
                    Log.e(TAG, "onRewardedVideoAdFailedToLoad  : 激励广告加载失败 i = " + i);
                }

                @Override
                public void onRewardedVideoCompleted() {
                    Log.e(TAG, "onRewardedVideoCompleted : 激励广告加载播放完成");
                    restartRewardedVideo();
                }
            });
        }
    }

    /**
     * 视频广告加载完成后，显示出来，然后预加载下一个激励广告
     */
    private void showRewardedAds() {
        if (rewardedVideoAd != null && rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.show();
            Log.e(TAG, "显示激励广告");
        } else {
            showAlertAds();
        }
        restartRewardedVideo();
    }

    /**
     * 准备加载激励广告
     */
    private void restartRewardedVideo() {
        initGoogleRewardedAds();
        if (!rewardedVideoAd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            rewardedVideoAd.loadAd(getString(R.string.adsense_app_ad_video),
                    adRequest);
            Log.e(TAG, "加载下一个激励广告");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rewardedVideoAd != null) {
            rewardedVideoAd.resume(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (rewardedVideoAd != null) {
            rewardedVideoAd.pause(this);
        }
    }

    @Override
    protected void onDestroy() {
        if (rewardedVideoAd != null) {
            rewardedVideoAd.destroy(this);
        }
        super.onDestroy();
    }

    /**
     * 加载广告
     * 因为腾讯的广告需要读取手机状态的权限，真坑，如果用户选择了拒绝并且不再提醒的话，
     * 就加载google的广告，google的广告不需要任何用户权限
     * <p>
     * 但是google的激励广告需要定位权限而且需要手机安装google play store
     */
    protected void loadAD() {
        boolean flag = Prefs.getInstance(this).getBoolean(Prefs.KEY_IS_SHOW_GOOGLE_AD, false);
        Log.e(TAG, "Google admob 显示状态 ：flag " + flag);
        if (isFirst) {
            isFirst = false;
            if (flag) {
                prepareGoogleAds();
            }
            return;
        }
        long lastMillis = Prefs.getInstance(this).getLong(Prefs.KEY_LAST_RESUME_MILLIS, 0);
        if (System.currentTimeMillis() - lastMillis > millis) {
            Prefs.getInstance(this).putLong(Prefs.KEY_LAST_RESUME_MILLIS, System.currentTimeMillis());
            //如果上次可视的时间距离现在短于2分钟,就去赚取广告费 嘎嘎嘎嘎
            //验证手机的google play service是否可用
            int playServicesAvailable = GoogleApiAvailabilityLight.getInstance().isGooglePlayServicesAvailable(this);
            Log.e(TAG, String.format("playServicesAvailable - > %s", playServicesAvailable));
            if (flag) {
                showAlertAds();
            } /*else {
                openSplashActivity();
            }*/
        }
    }

    private void openSplashActivity() {
        Intent intent = new Intent(App.getCtx(), SplashActivity.class);
        intent.putExtra(MainActivity.KEY_IS_WILL_GO_LOGIN_ACTIVITY, false);
        startActivity(intent);
    }

    /**
     * 请求位置权限
     */
    protected boolean requestLocationPermission(int requestCode) {
        return checkPermission(requestCode, Manifest.permission_group.LOCATION, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,});
    }

    @Override
    public void onPermissionsAllowed(int requestCode, String[] permissions, int[] grantResults) {
        super.onPermissionsAllowed(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_AD) {
//            showRewardedAds();//显示激励广告
        }
    }

    @Override
    public void onPermissionsRefused(int requestCode, String[] permissions, int[] grantResults) {
        super.onPermissionsRefused(requestCode, permissions, grantResults);
        //当位置权限被拒绝后，加载插屏广告
        if (requestCode == REQUEST_CODE_LOCATION_AD) {
            showAlertAds();
        }
    }

    @Override
    public void onPermissionsRefusedNever(int requestCode, String[] permissions, int[] grantResults) {
        super.onPermissionsRefusedNever(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_AD) {
            showAlertAds();//当位置权限被拒绝后，加载插屏广告
        }
    }
}
