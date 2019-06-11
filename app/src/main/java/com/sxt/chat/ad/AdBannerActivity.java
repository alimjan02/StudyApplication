package com.sxt.chat.ad;

import android.graphics.Point;
import android.util.Log;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.ads.banner2.UnifiedBannerADListener;
import com.qq.e.ads.banner2.UnifiedBannerView;
import com.qq.e.comm.util.AdError;
import com.sxt.chat.R;
import com.sxt.chat.base.HeaderActivity;
import com.sxt.chat.utils.Constants;

import java.util.Locale;

/**
 * 实现了google admob的Banner广告和腾讯的Banner广告
 * 注意：子类需要在xml中保证含有Banner的容器
 * <p>
 * <FrameLayout
 * android:layout_width="match_parent"
 * android:layout_height="wrap_content">
 * <p>
 * <com.google.android.gms.ads.AdView
 * android:id="@+id/ad_view"
 * android:layout_width="match_parent"
 * android:layout_height="wrap_content"
 * ads:adSize="BANNER"
 * ads:adUnitId="@string/adsense_app_ad_banner_personal" />
 * <p>
 * <FrameLayout
 * android:id="@+id/ad_container"
 * android:layout_width="match_parent"
 * android:layout_height="wrap_content" />
 *
 * </FrameLayout>
 */
public class AdBannerActivity extends HeaderActivity {

    protected AdView adGoogleBanner;
    private UnifiedBannerView adTencentBanner;

    /**
     * Google Banner广告位
     */
    protected void initGoogleAdBanner() {
        adGoogleBanner = findViewById(R.id.ad_view);
        //製作廣告請求。檢查您的logcat輸出中的散列設備ID，
        AdRequest adRequest = new AdRequest.Builder().build();
        adGoogleBanner.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.e(TAG, "Banner广告加载成功");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.e(TAG, "Banner加载失败 error code: " + errorCode);
            }

            @Override
            public void onAdClosed() {
                Log.e(TAG, "Banner广告关闭");
            }
        });
        // 開始在後台加載廣告。
        adGoogleBanner.loadAd(adRequest);
    }

    /**
     * 腾讯Banner广告位
     */
    protected void initTencentAdBanner(String postId) {
        FrameLayout bannerContainer = findViewById(R.id.ad_container);
        // 创建Banner广告AdView对象
        BannerView banner = new BannerView(this, ADSize.BANNER, Constants.APPID, postId);
        //设置广告轮播时间，为0或30~120之间的数字，单位为s,0标识不自动轮播
        banner.setRefresh(30);
        banner.setADListener(new AbstractBannerADListener() {

            @Override
            public void onNoAD(AdError error) {
                Log.e(TAG, "BannerNoAD，eCode=" + error.getErrorCode());
            }

            @Override
            public void onADReceiv() {
                Log.i(TAG, "ONBannerReceive");

            }
        });
        bannerContainer.addView(banner);
        /* 发起广告请求，收到广告数据后会展示数据   */
        banner.loadAD();
    }

    /**
     * 腾讯Banner2.0 广告 ， 实现方式与Banner不一样
     *
     * @param postId 广告id
     */
    protected void initTencentAdBanner2(String postId) {
        FrameLayout bannerContainer = findViewById(R.id.ad_container);
        adTencentBanner = new UnifiedBannerView(this, Constants.APPID, postId, new UnifiedBannerADListener() {
            @Override
            public void onNoAD(AdError adError) {
                String msg = String.format(Locale.getDefault(), "onNoAD, error code: %d, error msg: %s",
                        adError.getErrorCode(), adError.getErrorMsg());
                Log.i(TAG, "onADReceive " + msg);
            }

            @Override
            public void onADReceive() {
                Log.i(TAG, "onADReceive");
            }

            @Override
            public void onADExposure() {
                Log.i(TAG, "onADExposure");
            }

            @Override
            public void onADClosed() {
                Log.i(TAG, "onADClosed");
            }

            @Override
            public void onADClicked() {
                Log.i(TAG, "onADClicked");
            }

            @Override
            public void onADLeftApplication() {
                Log.i(TAG, "onADLeftApplication");
            }

            @Override
            public void onADOpenOverlay() {
                Log.i(TAG, "onADOpenOverlay");
            }

            @Override
            public void onADCloseOverlay() {
                Log.i(TAG, "onADCloseOverlay");
            }
        });
        adTencentBanner.setRefresh(30);
        bannerContainer.addView(adTencentBanner, getUnifiedBannerLayoutParams());
        adTencentBanner.loadAD();
    }

    /**
     * banner2.0规定banner宽高比应该为6.4:1 , 开发者可自行设置符合规定宽高比的具体宽度和高度值
     */
    private FrameLayout.LayoutParams getUnifiedBannerLayoutParams() {
        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        return new FrameLayout.LayoutParams(screenSize.x, Math.round(screenSize.x / 6.4F));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adGoogleBanner != null) {
            adGoogleBanner.resume();
        }
    }

    @Override
    public void onPause() {
        if (adGoogleBanner != null) {
            adGoogleBanner.pause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (adGoogleBanner != null) {
            adGoogleBanner.destroy();
        }
        if (adTencentBanner != null) adTencentBanner.destroy();
        super.onDestroy();
    }
}
