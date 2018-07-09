package com.sxt.chat.ws;


import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class TJProtocol {

    public static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ssZ").create();
    private static TJProtocol instance;
    private Context context;
    private Retrofit retrofit;
    public WebService apiService;
    public static final String IDCARD_OCR = "IDCARD_OCR";

    private static final OkHttpClient client = new OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).writeTimeout(20, TimeUnit.SECONDS).build();

    private TJProtocol(Context context) {
        this.context = context.getApplicationContext();
        //"https://api.youtu.qq.com/youtu/ocrapi/idcardocr";
        retrofit = new Retrofit.Builder().baseUrl("https://api.youtu.qq.com/youtu/").addConverterFactory(GsonConverterFactory.create()).client(client).build();
        apiService = retrofit.create(WebService.class);
    }

    public static synchronized TJProtocol getInstance(Context context) {
        if (instance == null) {
            instance = new TJProtocol(context);
        }
        return instance;
    }

    public void onDestroy() {
        instance = null;
    }

}
