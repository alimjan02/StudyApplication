package com.sxt.chat.activity;

import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.sxt.chat.R;
import com.sxt.chat.base.HeaderActivity;

public class AdmobBannerActivity extends HeaderActivity {

    protected AdView adGoogleBanner;

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
        super.onDestroy();
    }
}
