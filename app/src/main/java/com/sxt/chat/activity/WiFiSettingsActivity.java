package com.sxt.chat.activity;

import android.os.Bundle;

import com.sxt.chat.R;
import com.sxt.chat.base.HeaderActivity;
import com.sxt.chat.fragment.WiFiConnectFragment;

/**
 * Created by sxt on 2017/12/15.
 */

public class WiFiSettingsActivity extends HeaderActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_settings_layout);

        WiFiConnectFragment fragment = new WiFiConnectFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.wifi_container_layout, fragment, WiFiConnectFragment.class.getName())
                .commit();
    }

    @Override
    public void finish() {
        try {
            getSupportFragmentManager().popBackStack(null, 1);
        } catch (Exception e) {

        }
        super.finish();
    }

    //    @Override
//    public void onBackPressed() {
//        if (getSupportFragmentManager().getBackStackEntryCount() <= 1) {
//            super.onBackPressed();
//        } else {
//            //一个参数为null时，第二个参数为0时： 会弹出回退栈中最上层的那一个fragment。
//            getSupportFragmentManager().popBackStack(null, 0);
//        }
//    }
//
//    @Override
//    protected void onGoBackClicked(View v) {
//        onBackPressed();
//    }
}
