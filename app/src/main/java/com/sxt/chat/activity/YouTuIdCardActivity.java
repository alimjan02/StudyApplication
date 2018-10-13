package com.sxt.chat.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.sxt.chat.App;
import com.sxt.chat.R;
import com.sxt.chat.base.HeaderActivity;
import com.sxt.chat.json.OCRObject;
import com.sxt.chat.utils.glide.GlideRoundTransformer;
import com.sxt.chat.youtu.OCRListener;
import com.sxt.chat.youtu.OCRTask;
import com.sxt.chat.youtu.SDKConfig;

import java.io.File;

/**
 * Created by 11837 on 2018/6/5.
 */

public class YouTuIdCardActivity extends HeaderActivity implements View.OnClickListener {

    private ImageView imgIdCard;
    private ImageView imgPlaceHolder;
    private TextView statusTitle;
    private TextView resultTitle;
    private TextView resultValue;

    private ImageView imgIdCard2;
    private ImageView imgPlaceHolder2;
    private TextView statusTitle2;
    private TextView resultTitle2;
    private TextView resultValue2;

    private TextView next;
    private boolean SUCCESS;
    private boolean COMPLETE;
    private boolean UPLOAD_RE_TRY;
    private final int REQUEST_CODE_GALLERY = 100;
    private final int REQUEST_CODE_CAMARER = 101;
    private final int REQUEST_CROP_PHOTO = 102;
    private final int REQUEST_CODE_TAKE_PHOTO = 103;
    private OCRTask ocrTask;
    private OCRObject ocrResult;
    private String filePath;
    private int position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youto2);
        setTitle(R.string.ocr_scan_id_card);
        imgIdCard = (ImageView) findViewById(R.id.img_idcard);
        imgPlaceHolder = (ImageView) findViewById(R.id.img_placeHolder);
        statusTitle = (TextView) findViewById(R.id.description);
        resultTitle = (TextView) findViewById(R.id.result_title);
        resultValue = (TextView) findViewById(R.id.result_value);

        imgIdCard2 = (ImageView) findViewById(R.id.img_idcard2);
        imgPlaceHolder2 = (ImageView) findViewById(R.id.img_placeHolder2);
        statusTitle2 = (TextView) findViewById(R.id.description2);
        resultTitle2 = (TextView) findViewById(R.id.result_title2);
        resultValue2 = (TextView) findViewById(R.id.result_value2);

        next = (TextView) findViewById(R.id.next);
        imgPlaceHolder.setOnClickListener(this);
        imgPlaceHolder2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_placeHolder:
                startCamera(view);
                position = 0;
                break;

            case R.id.img_placeHolder2:
                startCamera(view);
                position = 1;
                break;
            case R.id.next:
                if (UPLOAD_RE_TRY) {
                    startCardOCR(filePath);
                    return;
                }
                if (COMPLETE) {
                    finish();
                    return;
                }
                if (SUCCESS) {
                    if (ocrResult != null && !TextUtils.isEmpty(ocrResult.getId()) && !TextUtils.isEmpty(ocrResult.getName())) {
                        loading.show();
//                        TJProtocol.getInstance(this).updateUserInfo(Prefs.getInstance(this).getInt(Prefs.KEY_USER_ID, 0),
//                                "", "", 0, ocrResult.getId(), ocrResult.getName(), CMD_UPDATE_USER_INFO);
                    }
                }
                break;
        }
    }

    private void startCardOCR(final String imgPath) {
        File file = new File(imgPath);
        if (file.exists()) {
            Glide.with(this).load(file)
                    .transform(new CenterCrop(this), new GlideRoundTransformer(this, 4))
                    /*.bitmapTransform(new GlideRoundTransformer(this, 8))*/
                    .error(R.mipmap.pic_ida)
                    .into(position == 0 ? imgIdCard : imgIdCard2);

            if (ocrTask != null && !ocrTask.isCancelled()) {
                ocrTask.cancel(true);
            }
            ocrTask = new OCRTask(imgPath, SDKConfig.TYPE_ID_CARD, position, new OCRListener() {
                @Override
                public void onStart() {
                    showDialog();
                }

                @Override
                public void onFaied(final Exception e) {
                    dismiss();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Drawable drawable = ContextCompat.getDrawable(App.getCtx(), R.mipmap.icon_failed);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            if (position == 0) {
                                statusTitle.setCompoundDrawables(drawable, null, null, null);
                                statusTitle.setText("加载失败，重新上传");
//                            next.setText("重新上传");
//                            next.setEnabled(true);
                                resultTitle.setText("");
                                resultValue.setText("");
                                findViewById(R.id.result_layout).setVisibility(View.INVISIBLE);

                            } else if (position == 1) {

                                statusTitle2.setCompoundDrawables(drawable, null, null, null);
                                statusTitle2.setText("加载失败，重新上传");
                                resultTitle2.setText("");
                                resultValue2.setText("");
                                findViewById(R.id.result_layout2).setVisibility(View.INVISIBLE);

                            }
                            SUCCESS = false;
                            COMPLETE = false;
                            UPLOAD_RE_TRY = true;
//                            next.setBackground(ContextCompat.getDrawable(App.getCtx(), R.drawable.btn_pressed_bg));
                        }
                    });
                }

                @Override
                public void onSuccess(OCRObject ocrResult, String result) {
                    dismiss();
                    YouTuIdCardActivity.this.ocrResult = ocrResult;
                    if (OCRObject.SUCCESS == ocrResult.getCode()) {

                        if (position == 0) {
                            if (!TextUtils.isEmpty(ocrResult.getId()) && !TextUtils.isEmpty(ocrResult.getName())) {
                                Drawable drawable = ContextCompat.getDrawable(App.getCtx(), R.mipmap.icon_complete);
                                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                                statusTitle.setCompoundDrawables(drawable, null, null, null);
                                statusTitle.setText("识别成功");
                                resultTitle.setText("  姓名:" + ocrResult.getName());
                                resultValue.setText("身份证: " + ocrResult.getId());
                                findViewById(R.id.result_layout).setVisibility(View.VISIBLE);
                            } else {
                                failed();
                            }

                        } else if (position == 1) {
                            if (!TextUtils.isEmpty(ocrResult.getAuthority()) && !TextUtils.isEmpty(ocrResult.getValid_date())) {
                                Drawable drawable = ContextCompat.getDrawable(App.getCtx(), R.mipmap.icon_complete);
                                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                                statusTitle2.setCompoundDrawables(drawable, null, null, null);
                                statusTitle2.setText("识别成功");
                                resultTitle2.setText("签发机关 : " + ocrResult.getAuthority());
                                resultValue2.setText("有效日期 : " + ocrResult.getValid_date());
                                findViewById(R.id.result_layout2).setVisibility(View.VISIBLE);
                            } else {
                                failed();
                            }
                        }
//                        next.setEnabled(true);
//                        next.setText(R.string.next);
//                        next.setBackground(ContextCompat.getDrawable(App.getCtx(), R.drawable.btn_pressed_bg));
                        SUCCESS = true;
                        COMPLETE = false;
                        UPLOAD_RE_TRY = false;

                    } else {
                        failed();
                        SUCCESS = false;
                        COMPLETE = false;
                        UPLOAD_RE_TRY = false;
                    }
                }

                private void failed() {
                    Drawable drawable = ContextCompat.getDrawable(App.getCtx(), R.mipmap.icon_failed);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    if (position == 0) {
                        statusTitle.setCompoundDrawables(drawable, null, null, null);
                        statusTitle.setText("识别失败，重新拍摄");
//                        next.setText(R.string.next);
//                        next.setEnabled(false);
//                        next.setBackground(ContextCompat.getDrawable(App.getCtx(), R.drawable.blue_gray_solid_round_25));
                        resultTitle.setText("");
                        resultValue.setText("");
                        findViewById(R.id.result_layout).setVisibility(View.INVISIBLE);

                    } else if (position == 1) {
                        statusTitle2.setCompoundDrawables(drawable, null, null, null);
                        statusTitle2.setText("识别失败，重新拍摄");
//                        next.setText(R.string.next);
//                        next.setEnabled(false);
//                        next.setBackground(ContextCompat.getDrawable(App.getCtx(), R.drawable.blue_gray_solid_round_25));
                        resultTitle2.setText("");
                        resultValue2.setText("");
                        findViewById(R.id.result_layout2).setVisibility(View.INVISIBLE);

                    }
                }
            });
            ocrTask.execute();
        }
    }

    private void showDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loading.show();
            }
        });
    }

    public void dismiss() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (loading != null && loading.isShowing()) {
                    loading.dismiss();
                }
            }
        });
    }

    public void startGallery(View view) {
        boolean b = checkPermission(REQUEST_CODE_GALLERY, Manifest.permission.WRITE_EXTERNAL_STORAGE, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
        if (b) {
            startGalleryApp();
        }
    }

    public void startCamera(View view) {
        boolean b = checkPermission(REQUEST_CODE_CAMARER, Manifest.permission.CAMERA, new String[]{Manifest.permission.CAMERA});
        if (b) {
            startCameraApp();
        }
    }

    private void startCameraApp() {
        startActivityForResult(new Intent(this, TakePhotoActivity.class), REQUEST_CODE_TAKE_PHOTO);

//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        file = new File(this.getExternalCacheDir() + File.separator + System.currentTimeMillis() + ".png");
//
//        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
//        //添加权限
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//        startActivityForResult(intent, REQUEST_CODE_CAMARER);
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
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    private void startCropActivity(File file) {
        //在手机相册中显示刚拍摄的图片
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);

        Intent intent = new Intent(this, CropActivity.class);
        intent.putExtra(CropActivity.CROP_IMG_URI, contentUri);
        startActivityForResult(intent, REQUEST_CROP_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
            if (data != null) {
                Uri uri = data.getParcelableExtra(CropActivity.CROP_IMG_URI);
                if (uri != null) {
                    File file = new File(uri.getPath());
                    filePath = file.getPath();
                    startCardOCR(filePath);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onPermissionsaAlowed(int requestCode, String[] permissions, int[] grantResults) {
        super.onPermissionsaAlowed(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_CAMARER:
                startCameraApp();
                break;
            case REQUEST_CODE_GALLERY:
                startGalleryApp();
                break;
        }
    }

    @Override
    public void onPermissionsRefusedNever(int requestCode, String[] permissions, int[] grantResults) {
        super.onPermissionsRefused(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_CAMARER:
                Toast(R.string.allow_CAMERA);
                break;

            case REQUEST_CODE_GALLERY:
                Toast(R.string.allow_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ocrTask != null && !ocrTask.isCancelled()) {
            ocrTask.cancel(true);
        }
    }
}
