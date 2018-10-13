package com.sxt.chat.ws;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sxt.chat.App;
import com.sxt.chat.R;
import com.sxt.chat.download.UrlInterceptor;
import com.sxt.chat.receiver.WatchDogReceiver;
import com.sxt.chat.utils.Constants;
import com.sxt.chat.utils.Prefs;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class TJProtocol {

    public static final String MediaType_JSON = "Content-Type: application/json;charset=UTF-8";
    public static final String MediaType_ACCEPT = "Accept: application/json";

    public static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ssZ").create();
    private static TJProtocol instance;
    private Context context;
    private Retrofit retrofit;
    public WebService apiService;

    private TJProtocol(Context context) {
        this.context = context.getApplicationContext();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                //打印retrofit日志
                Log.e("RetrofitLog", "retrofitBack = " + message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).writeTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new UrlInterceptor())
                .build();

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

    public Map<String, Object> createBasicParams() {

        String ticket = Prefs.getInstance(context).getTicket();
        int userId = Prefs.getInstance(context).getUserId();
        String authorization = Prefs.getInstance(context).getAuthorization();
        Map<String, Object> params = new HashMap<String, Object>();
        if (TextUtils.isEmpty(ticket) || TextUtils.isEmpty(authorization) || userId == 0) {
            Intent intent = new Intent();
            intent.setAction(WatchDogReceiver.ACTION_LOGOUT);
            intent.setComponent(new ComponentName(App.getCtx().getPackageName(), App.getCtx().getPackageName() + ".receiver.WatchDogReceiver"));
            App.getCtx().sendBroadcast(intent);
        } else {
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put(Constants.KEY_Ticket, ticket);
            headerMap.put(Constants.KEY_Authorization, authorization);
            headerMap.put(Constants.KEY_UserId, String.valueOf(userId));
            params.put(Constants.KEY_Header, headerMap);
        }

        return params;
    }



    public void onDestroy() {
        instance = null;
    }

}
