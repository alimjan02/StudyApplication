package com.sxt.chat.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.sxt.chat.App;
import com.sxt.chat.R;
import com.sxt.chat.ad.AdBannerActivity;
import com.sxt.chat.db.User;
import com.sxt.chat.json.ResponseInfo;
import com.sxt.chat.utils.Constants;
import com.sxt.chat.utils.Prefs;
import com.sxt.chat.utils.glide.GlideCircleTransformer;
import com.sxt.chat.ws.BmobRequest;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by izhaohu on 2018/3/13.
 */

public class UpdateUserImgActivity extends AdBannerActivity implements View.OnClickListener {

    private ImageView img;
    private Uri bitmapUri;
    private UnifiedNativeAd adGoogleNative;
    private final int REQUEST_CHOOSE_PHOTO = 1000;
    private final int REQUEST_CROP_PHOTO = 1001;
    private final String CMD_UPLOAD_FILE = this.getClass().getName() + "CMD_UPLOAD_FILE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_img);
        img = findViewById(R.id.img);
        findViewById(R.id.root).setOnClickListener(this);
        setTitle(R.string.header_img_update);
        updateHeadPortrait();
        boolean flag = Prefs.getInstance(this).getBoolean(Prefs.KEY_IS_SHOW_GOOGLE_AD, false);
        Log.e(TAG, "Google admob 显示状态 ：flag " + flag);
        if (flag) {
            initGoogleAdBanner();
        } else {
            initTencentAdBanner2(Constants.BannerPosID_personal_profile);
        }
    }

    private void updateHeadPortrait() {
        User user = BmobUser.getCurrentUser(User.class);
        if (user != null) {
            load(user.getImgUri(), "M".equals(BmobUser.getCurrentUser(User.class).getGender()) ? R.mipmap.men : R.mipmap.female);
        }
    }

    private void updateUser(final String url) {
        User newUser = new User();
        newUser.setImgUri(url);
        final BmobUser bmobUser = BmobUser.getCurrentUser(User.class);
        newUser.update(bmobUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e != null) {
                    loading.dismiss();
                    Toast("errorCode: " + e.getErrorCode() + " , " + e.getMessage());
                } else {
                    BmobUser.fetchUserJsonInfo(new FetchUserInfoListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            loading.dismiss();
                            if (e == null) {
                                load(BmobUser.getCurrentUser(User.class).getImgUri(), "M".equals(BmobUser.getCurrentUser(User.class).getGender()) ? R.mipmap.men : R.mipmap.female);
                            } else {
                                Toast("errorCode: " + e.getErrorCode() + " , " + e.getMessage());
                            }
                        }
                    });
                }
            }
        });
    }

    private void load(String url, int placeHolder) {
        Glide.with(App.getCtx())
                .load(url)
                .error(placeHolder)
                .bitmapTransform(new GlideCircleTransformer(App.getCtx()))
//                .skipMemoryCache(true)//跳过内存
//                .diskCacheStrategy(DiskCacheStrategy.NONE)//想要生效必须添加 跳过内存
                .signature(new StringSignature(Prefs.getInstance(App.getCtx()).getString(Prefs.KEY_USER_HEADER_IMAGE_FLAG, "")))
                .into(img);
    }

    private void startGalleryApp() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        }

        intent = Intent.createChooser(intent, "选择图片");
        startActivityForResult(intent, REQUEST_CHOOSE_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CHOOSE_PHOTO) {
                startCropActivity(data.getData());
            } else if (requestCode == REQUEST_CROP_PHOTO) {
                bitmapUri = data.getParcelableExtra(CropActivity.CROP_IMG_URI);
                if (bitmapUri != null) {
                    upload(bitmapUri);
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void startCropActivity(Uri uri) {
        Intent intent = new Intent(this, CropActivity.class);
        intent.putExtra(CropActivity.CROP_IMG_URI, uri);
        startActivityForResult(intent, REQUEST_CROP_PHOTO);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.root:
                boolean b = checkPermission(REQUEST_CHOOSE_PHOTO, Manifest.permission_group.STORAGE, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
                if (b) {
                    startGalleryApp();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onPermissionsAllowed(int requestCode, String[] permissions, int[] grantResults) {
        super.onPermissionsAllowed(requestCode, permissions, grantResults);
        startGalleryApp();
    }

    @Override
    public void onPermissionsRefusedNever(int requestCode, String[] permissions, int[] grantResults) {
        super.onPermissionsRefusedNever(requestCode, permissions, grantResults);
        Toast(R.string.allow_WRITE_EXTERNAL_STORAGE);
        if (REQUEST_CHOOSE_PHOTO == requestCode) {
            goToAppSettingsPage();
        }
    }

    private void upload(Uri bitmapUri) {
        loading.show();
        BmobRequest.getInstance(this).uploadFile(bitmapUri.getPath(), CMD_UPLOAD_FILE);
    }

    @Override
    public void onMessage(ResponseInfo resp) {
        if (ResponseInfo.OK == resp.getCode()) {
            if (CMD_UPLOAD_FILE.equals(resp.getCmd())) {
                Prefs.getInstance(this).putString(Prefs.KEY_USER_HEADER_IMAGE_FLAG, Prefs.getInstance(this).getUserId() + "-" + System.currentTimeMillis());
                updateUser(resp.getImgUrl());
            }
        } else {
            loading.dismiss();
            Toast(resp.getError());
        }
    }

    /**
     * 原生广告
     */
    private void refreshNativeAd() {
        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.adsense_app_ad_native));
//        AdLoader.Builder builder = new AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110");
        // OnUnifiedNativeAdLoadedListener implementation.
        builder.forUnifiedNativeAd(unifiedNativeAd -> {
            //完成後，您必須在舊廣告上調用destroy，否則您將發生內存泄漏。
            if (adGoogleNative != null) {
                adGoogleNative.destroy();
            }
            adGoogleNative = unifiedNativeAd;
            FrameLayout frameLayout =
                    findViewById(R.id.ad_container);
            UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater()
                    .inflate(R.layout.ad_unified, null);
            populateUnifiedNativeAdView(unifiedNativeAd, adView);
            frameLayout.removeAllViews();
            frameLayout.addView(adView);
        });

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)//显示视频
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.e(TAG, "Failed to load native ad: " + errorCode);
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().addTestDevice("").build());
    }

    /**
     * @param nativeAd the object containing the ad's assets
     * @param adView   the view to be populated
     */
    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAd.getVideoController();

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {
            Log.e(TAG, "Video status: Ad does not contain a video asset.");

            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
                    Log.e(TAG, "Video status: Ad does not contain a video asset.");
                    super.onVideoEnd();
                }
            });
        } else {
            Log.e(TAG, "Video status: Ad does not contain a video asset.");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateHeadPortrait();
    }

    @Override
    public void onDestroy() {
        if (adGoogleNative != null) {
            adGoogleNative.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onGoBack(View view) {
        setResult(RESULT_OK);
        super.onGoBack(view);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }
}
