package com.sxt.chat.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.sxt.chat.R;
import com.sxt.chat.base.BaseActivity;
import com.sxt.chat.utils.Constants;
import com.sxt.chat.utils.Prefs;
import com.sxt.chat.utils.ScreenCaptureUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by sxt on 2018/11/1.
 */
public class ShareCaptureActivity extends BaseActivity {

    private SaveImgTask asyncTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_layout);
        ImageView img = findViewById(R.id.img);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            img.setTransitionName("shareView");
        }
        final String path = getIntent().getStringExtra(Prefs.KEY_BITMAP);
        if (path != null) {
            final Bitmap bitmap = ScreenCaptureUtil.getInstance(this).getBitmapFromMemory(path);
            if (bitmap != null) {
                img.setImageBitmap(bitmap);
                findViewById(R.id.save).setOnClickListener(v -> {
                    if (asyncTask == null) {
                        asyncTask = new SaveImgTask();
                    } else {
                        if (!asyncTask.isCancelled()) {
                            asyncTask.cancel(true);
                        }
                        asyncTask = new SaveImgTask();
                    }
                    asyncTask.execute(bitmap, path);
                });
                findViewById(R.id.share).setOnClickListener(v -> share());
                findViewById(R.id.delete).setOnClickListener(v -> onBackPressed());
            }
        }
    }

    private void share() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, Constants.SHARE_CONTENT);
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, getString(R.string.share_with)));
    }

    private class SaveImgTask extends AsyncTask<Object, Void, String> {

        @Override
        protected String doInBackground(Object... objects) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                FileOutputStream fos = new FileOutputStream(new File((String) objects[1]));
                ((Bitmap) objects[0]).compress(Bitmap.CompressFormat.PNG, 100, baos);
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
                baos.close();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return (String) objects[1];
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                Toast(String.format("保存成功%s", s));
            } else {
                Toast("保存失败");
            }
        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            initWindowStyle();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (asyncTask != null && !asyncTask.isCancelled()) asyncTask.cancel(true);
    }
}
