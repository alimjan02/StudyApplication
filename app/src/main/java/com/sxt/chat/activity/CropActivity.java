package com.sxt.chat.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.steelkiwi.cropiwa.CropIwaView;
import com.steelkiwi.cropiwa.config.CropIwaSaveConfig;
import com.sxt.chat.App;
import com.sxt.chat.R;
import com.sxt.chat.base.BaseActivity;
import com.sxt.chat.utils.Prefs;
import com.sxt.chat.utils.ToastUtil;

import java.io.File;

/**
 * Created by SXT on 2018/3/18.
 */

public class CropActivity extends BaseActivity {

    public static final String CROP_IMG_URI = "CROP_IMG_URI";
    private CropIwaView cropView;
    private Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("裁剪图片");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cropView = (CropIwaView) findViewById(R.id.crop_view);
        imageUri = getIntent().getParcelableExtra(CROP_IMG_URI);
        cropView.setImageUri(imageUri);
        cropView.configureOverlay().setDynamicCrop(true).apply();

        cropView.setCropSaveCompleteListener(new CropIwaView.CropSaveCompleteListener() {
            @Override
            public void onCroppedRegionSaved(Uri bitmapUri) {
                loading.dismiss();
                Intent intent = new Intent();
                intent.putExtra(CROP_IMG_URI, bitmapUri);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        cropView.setErrorListener(new CropIwaView.ErrorListener() {
            @Override
            public void onError(Throwable e) {
                loading.dismiss();
                ToastUtil.showToast(App.getCtx(), e.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_crop_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.done) {
            loading.show();
            File files = new File(Prefs.KEY_PATH_CROP_IMG);
            if (!files.exists()) {
                files.mkdirs();
            }
            File file = new File(files.getPath() + File.separator + System.currentTimeMillis() + ".png");
            cropView.crop(new CropIwaSaveConfig.Builder(Uri.fromFile(file)).build());

        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}