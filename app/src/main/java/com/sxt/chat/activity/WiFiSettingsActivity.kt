package com.sxt.chat.activity

import android.os.Bundle

import com.sxt.chat.R
import com.sxt.chat.base.HeaderActivity
import com.sxt.chat.fragment.WiFiFragment

/**
 * Created by sxt on 2017/12/15.
 */

class WiFiSettingsActivity : HeaderActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_settings_layout)

        val fragment = WiFiFragment()
        supportFragmentManager
                .beginTransaction()
                .add(R.id.wifi_container_layout, fragment, WiFiFragment::class.java.name)
                .commit()
    }

    override fun finish() {
        try {
            supportFragmentManager.popBackStack(null, 1)
        } catch (e: Exception) {

        }
        super.finish()
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


    override fun onSaveInstanceState(outState: Bundle) {
        //        super.onSaveInstanceState(outState);
    }
}
