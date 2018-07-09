package com.sxt.chat.ws;


import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class BmobRequest {

    public static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ssZ").create();
    private static BmobRequest instance;
    private Context context;
    public static final String AUTH = "IDCARD_OCR";

    private BmobRequest(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized BmobRequest getInstance(Context context) {
        if (instance == null) {
            instance = new BmobRequest(context);
        }
        return instance;
    }

    public void onDestroy() {
        instance = null;
    }

}
