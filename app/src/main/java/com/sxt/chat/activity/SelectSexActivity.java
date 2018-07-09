package com.sxt.chat.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxt.chat.App;
import com.sxt.chat.R;
import com.sxt.chat.base.HeaderActivity;
import com.sxt.chat.utils.Prefs;

/**
 * Created by izhaohu on 2018/2/5.
 */

public class SelectSexActivity extends HeaderActivity implements View.OnClickListener {

    private TextView tvWoman;
    private ImageView imgWoman;
    private TextView tvMan;
    private ImageView imgMan;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_sex);
        setTitle(R.string.sex);
        findViewById(R.id.woman).setOnClickListener(this);
        findViewById(R.id.man).setOnClickListener(this);
        tvWoman = (TextView) findViewById(R.id.tv_woman);
        imgWoman = (ImageView) findViewById(R.id.img_woman);
        tvMan = (TextView) findViewById(R.id.tv_man);
        imgMan = (ImageView) findViewById(R.id.img_man);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.woman:
                tvMan.setTextColor(ContextCompat.getColor(this, R.color.text_color_2));
                imgMan.setBackground(ContextCompat.getDrawable(this, R.drawable.blue_stroke_circle_unselect));
                tvWoman.setTextColor(ContextCompat.getColor(this, R.color.main_blue));
                imgWoman.setBackground(ContextCompat.getDrawable(this, R.drawable.blue_stroke_circle_select));

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        if (getIntent().getBooleanExtra(Prefs.KEY_HAVE_NEXT, false)) {
                            intent.putExtra(Prefs.KEY_IS_WEI_QUEZHEN, getIntent().getIntExtra(Prefs.KEY_IS_WEI_QUEZHEN, 0));
                            intent.putExtra(Prefs.KEY_HAVE_NEXT, getIntent().getBooleanExtra(Prefs.KEY_HAVE_NEXT, false));
                            intent.setClass(App.getCtx(), SelectNumberActivity.class);
                            startActivity(intent);
                        } else {
                            intent.putExtra(String.valueOf(BasicInfoActivity.REQUESTCODE_SEX), "F");
                            setResult(RESULT_OK, intent);
                        }

                        finish();
                    }
                }, 500);
                break;

            case R.id.man:

                tvMan.setTextColor(ContextCompat.getColor(this, R.color.main_blue));
                imgMan.setBackground(ContextCompat.getDrawable(this, R.drawable.blue_stroke_circle_select));
                tvWoman.setTextColor(ContextCompat.getColor(this, R.color.text_color_2));
                imgWoman.setBackground(ContextCompat.getDrawable(this, R.drawable.blue_stroke_circle_unselect));

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        if (getIntent().getBooleanExtra(Prefs.KEY_HAVE_NEXT, false)) {
                            intent.putExtra(Prefs.KEY_IS_WEI_QUEZHEN, getIntent().getIntExtra(Prefs.KEY_IS_WEI_QUEZHEN, 0));
                            intent.putExtra(Prefs.KEY_HAVE_NEXT, getIntent().getBooleanExtra(Prefs.KEY_HAVE_NEXT, false));
                            intent.setClass(App.getCtx(), SelectNumberActivity.class);
                            startActivity(intent);
                        } else {
                            intent.putExtra(String.valueOf(BasicInfoActivity.REQUESTCODE_SEX), "M");
                            setResult(RESULT_OK, intent);
                        }

                        finish();
                    }
                }, 500);

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) handler.removeCallbacksAndMessages(null);
    }
}
