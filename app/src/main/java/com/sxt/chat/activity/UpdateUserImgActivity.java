package com.sxt.chat.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.comm.util.AdError;
import com.sxt.chat.App;
import com.sxt.chat.R;
import com.sxt.chat.base.HeaderActivity;
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

public class UpdateUserImgActivity extends HeaderActivity implements View.OnClickListener {

    private ImageView img;
    private final int REQUEST_CHOOSE_PHOTO = 1000;
    private final int REQUEST_CROP_PHOTO = 1001;
    private Uri bitmapUri;
    private final String CMD_UPLOAD_FILE = this.getClass().getName() + "CMD_UPLOAD_FILE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_img);

        img = (ImageView) findViewById(R.id.img);
        findViewById(R.id.root).setOnClickListener(this);
        setTitle(R.string.header_img_update);
        updateHeadPortrait();
        initAdBanner();
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

    private void initAdBanner() {
        FrameLayout bannerContainer = (FrameLayout) findViewById(R.id.ad_banner_container);
        // 创建Banner广告AdView对象
        // appId : 在 http://e.qq.com/dev/ 能看到的app唯一字符串
        // posId : 在 http://e.qq.com/dev/ 生成的数字串，并非 appid 或者 appkey
        BannerView banner = new BannerView(this, ADSize.BANNER, Constants.APPID, Constants.BannerPosID);
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
    protected void onResume() {
        super.onResume();
//        showAD();
    }
//
//    private void showAD() {
//        if (interstitialAD != null) {
//            interstitialAD.closePopupWindow();
//            interstitialAD.destroy();
//            interstitialAD = null;
//        }
//        interstitialAD = new InterstitialAD(this, Constants.APPID, Constants.InterteristalPosID);
//        interstitialAD.setADListener(new AbstractInterstitialADListener() {
//
//            @Override
//            public void onNoAD(AdError error) {
//                Log.i(
//                        "AD_DEMO",
//                        String.format("LoadInterstitialAd Fail, error code: %d, error msg: %s",
//                                error.getErrorCode(), error.getErrorMsg()));
//            }
//
//            @Override
//            public void onADReceive() {
//                Log.i("AD_DEMO", "onADReceive");
//                interstitialAD.show();
//            }
//        });
//        interstitialAD.loadAD();
//    }

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
