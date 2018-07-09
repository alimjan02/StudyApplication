package com.sxt.chat.ws;


import com.sxt.chat.json.ResponseInfo;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * @Descripition
 * @Auther liubing
 * @CreateTime 2017/7/13
 * @Version
 * @Since
 */
public interface WebService {

    @Headers("Content-Type:text/json;Authorization:")
    @FormUrlEncoded
    @POST("ocrapi/idcardocr")
    Call<ResponseInfo> idCardOcr(@FieldMap Map<String, String> params);
}
