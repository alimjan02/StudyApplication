package com.sxt.chat.download;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by izhaohu on 2018/7/31.
 */

public class UrlInterceptor implements Interceptor {

    final String TAG = this.getClass().getName();

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        Response response = chain.proceed(request);
        String url = request.url().toString();
        ResponseBody body = response.body();
        Log.e(TAG, "拦截到URL : " + url);


        return response.newBuilder().body(body).build();
    }


}
