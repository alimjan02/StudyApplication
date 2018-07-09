package com.sxt.chat.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.comm.util.AdError;
import com.sxt.chat.App;
import com.sxt.chat.R;
import com.sxt.chat.base.HeaderActivity;
import com.sxt.chat.db.User;
import com.sxt.chat.utils.Constants;
import com.sxt.chat.utils.glide.GlideCircleTransform;

import java.io.File;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by izhaohu on 2018/3/13.
 */

public class UpdateUserImgActivity extends HeaderActivity implements View.OnClickListener {

    private ImageView img;
    private boolean successful;
    private final int REQUEST_CHOOSE_PHOTO = 1000;
    private final int REQUEST_CROP_PHOTO = 1001;
    private Uri bitmapUri;

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
            load(user.getImgUri(), "M".equals(user.getGender()) ? R.mipmap.men : R.mipmap.female, false);
        }
    }

    private void load(final String url, final int placeHolder, boolean isUpdate) {

        if (isUpdate) {
            User newUser = new User();
            newUser.setImgUri(url);
            BmobUser bmobUser = BmobUser.getCurrentUser();
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
                                    successful = true;
                                    load(BmobUser.getCurrentUser(User.class).getImgUri(), placeHolder);
                                } else {
                                    Toast("errorCode: " + e.getErrorCode() + " , " + e.getMessage());
                                }
                            }
                        });
                    }
                }
            });
        } else {
            load(url, placeHolder);
        }
    }

    private void load(String url, int placeHolder) {
        Glide.with(App.getCtx())
                .load(url)
                .error(placeHolder)
                .bitmapTransform(new GlideCircleTransform(App.getCtx()))
//                .skipMemoryCache(true)//跳过内存
//                .diskCacheStrategy(DiskCacheStrategy.NONE)//想要生效必须添加 跳过内存
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
                boolean b = checkPermission(REQUEST_CHOOSE_PHOTO, Manifest.permission.WRITE_EXTERNAL_STORAGE, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
                if (b) {
                    startGalleryApp();
                }
                break;
            default:
                break;
        }
    }

    private boolean checkPermission(int requestCode, String permssion, String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(App.getCtx(), permssion) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissions, requestCode);
                return false;
            }
            return true;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CHOOSE_PHOTO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startGalleryApp();
            } else {
                if (!shouldShowRequestPermissionRationale(permissions[0])) {
                    Toast(R.string.allow_WRITE_EXTERNAL_STORAGE);
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void upload(Uri bitmapUri) {
        loading.show();
        final BmobFile bmobFile = new BmobFile(new File(bitmapUri.getPath()));
        bmobFile.uploadblock(new UploadFileListener() {

            @Override
            public void done(BmobException e) {
                if (e == null) {
                    //bmobFile.getFileUrl()--返回的上传文件的完整地址
                    load(bmobFile.getUrl(), "M".equals(BmobUser.getCurrentUser(User.class).getGender()) ? R.mipmap.men : R.mipmap.female, true);

                } else {
                    loading.dismiss();
                    Toast(getString(R.string.upload_img_failed) + e.getMessage());
                }
            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
            }
        });
//            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).writeTimeout(20, TimeUnit.SECONDS);
//            final File file = new File(bitmapUri.getPath());
//            MultipartBody.Builder formBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
////            RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
//            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
////            RequestBody fileBody = RequestBody.create(MediaType.parse("text/x-markdown; charset=utf-8"), file);
//
//            formBody.addFormDataPart("0", file.getName(), fileBody)//worker_id 对应的是 1, user_id对应的是0
//                    .addFormDataPart("id", String.valueOf(Prefs.getInstance(this).getInt(Prefs.KEY_USER_ID, 0)))
//                    .addFormDataPart("type", "0");//worker_id 对应的是 1, user_id对应的是0
//
//            Request request = new Request.Builder()
//                    .url(Prefs.getInstance(this).getUploadUrl())
//                    .post(formBody.build())
//                    .tag(this)
//                    .build();
//
//            clientBuilder.build().newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    UpdateUserImgActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            loading.dismiss();
//                            Toast(getString(R.string.upload_img_failed));
//                        }
//                    });
//                }
//
//                @Override
//                public void onResponse(Call call, final Response response) {
//                    UpdateUserImgActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (response.isSuccessful()) {
//                                loading.dismiss();
//                                Toast(getString(R.string.upload_img_successful));
//                                successful = true;
//                                updateHeadPortrait();
//                            } else {
//                                loading.dismiss();
//                                Toast(getString(R.string.error_request_data));
//                            }
//                        }
//                    });
//                }
//            });
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast(getString(R.string.error_request_data));
//        }
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
