package com.sxt.chat.youtu;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.sxt.chat.App;
import com.sxt.chat.R;
import com.sxt.chat.json.OCRObject;
import com.sxt.chat.utils.Base64Util;
import com.sxt.chat.utils.YoutuSign;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class OCRTask extends AsyncTask<Void, Void, OCRObject> {
    /**
     * 卡片的图片路径
     */
    private String imgPath;
    /**
     * 卡片的正反面类型 0 正面  ; 1 反面
     */
    private int card_type;
    private String TYPE = YouTuConfig.TYPE_IMAGE_CARDCOR;//默认是 图片类别
    private String TAG = "YouTu_SXT";
    private static int EXPIRED_SECONDS = 2592000;
    private OCRListener listener;

    public OCRTask(String imgPath, String TYPE, int card_type, OCRListener listener) {
        this.imgPath = imgPath;
        this.TYPE = TYPE;
        this.card_type = card_type;
        this.listener = listener;
    }

    /**
     * 身份证OCR
     * 接口 JSONObject idCardOcr(String image_path, int card_type) JSONObject IdCardOcrUrl(String url, int card_type)
     * 参数
     * image_path 待检测图片路径
     * url 待检测图片的url
     * card_type 0 代表输入图像是身份证正面， 1代表输入是身份证反面
     */
    @Override
    protected OCRObject doInBackground(Void... voids) {
        if (listener != null) {
            listener.onStart();
        }
        //TODO YouTu 封装好的  , 可以直接用 , 内部是用的android原生的HttpUrlConnection
        //OkHttp没用走通 , 原因暂未可知
//        Youtu youtu = new Youtu(APP_ID, SECRET_ID, SECRET_KEY, Youtu.API_YOUTU_END_POINT, USER_ID);
//            JSONObject jsonObject = null;
//            try {
//                jsonObject = youtu.IdCardOcr(imgPath, card_type);
//                Log.i("YouTu", "YouTu json = " + jsonObject.toString());
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.i("YouTu", e.toString());
//            }
//            return jsonObject == null ? null : new Gson().fromJson(jsonObject.toString(), OCRObject.class);

//            Response response = null;
        StringBuffer resposeBuffer = null;
        try {
            Log.i(TAG, "=======================================================================");
            Log.i(TAG, "腾讯YouTu 图文识别 开始... ");
            File imageFile = new File(imgPath);
            if (imageFile.exists() && imageFile.length() > 0) {
//                    OkHttpClient.Builder clientBuilder = getClientBuilder();
//                    MultipartBody.Builder formBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
//
//                    StringBuffer base64 = new StringBuffer();
//                    InputStream in = new FileInputStream(imageFile);
//                    byte[] data = new byte[(int) imageFile.length()];
//                    in.read(data);
//                    in.popub_close();
//                    base64.append(Base64Util.encode(data));
//
//                    MultipartBody multipartBody = formBody
//                            .addFormDataPart("app_id", APP_ID)
//                            .addFormDataPart("image", base64.toString())
//                            .addFormDataPart("card_type", String.valueOf(card_type)).build();
//
//                    StringBuffer mySign = new StringBuffer("");
//                    YoutuSign.appSign(APP_ID, SECRET_ID, SECRET_KEY, System.currentTimeMillis() / 1000L + (long) EXPIRED_SECONDS, USER_ID, mySign);
//
//                    Request request = new Request.Builder()
////                            .addHeader("accept", "*/*")
////                            .removeHeader("user-agent")
////                            .addHeader("user-agent", "youtu-java-sdk")
//                            .addHeader("Content-Length", String.valueOf(multipartBody.contentLength()))
//                            .addHeader("Content-Type", "text/json")
////                            .addHeader("Content-Type", "multipart/form-data")
//                            .addHeader("Authorization", mySign.toString())
//                            .url("https://api.youtu.qq.com/youtu/ocrapi/idcardocr"
//                                  /*  + "Content-Length=" + multipartBody.contentLength()
//                                    + "&Authorization=" + mySign.toString()*/)
//                            .post(multipartBody)
//                            .tag(this)
//                            .build();
//
//                    response = clientBuilder.build().newCall(request).execute();
                JSONObject postData = new JSONObject();
                StringBuffer base64 = new StringBuffer();
                InputStream in = new FileInputStream(imageFile);
                byte[] data = new byte[(int) imageFile.length()];
                in.read(data);
                in.close();
                base64.append(Base64Util.encode(data));
                if (base64.length() == 0) {
                    return null;
                }
                float length = base64.length();
                Log.i(TAG, "图片读取完毕 Image.length = " + (length > 1024 ? length / (1204f * 1024f) + "M" : (length / 1024f) + "K"));

                postData.put("app_id", YouTuConfig.APP_ID);//设置请求体的数据
                postData.put("card_type", card_type);
                postData.put("image", base64.toString());

                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init((KeyManager[]) null, new TrustManager[]{new TrustAnyTrustManager()}, new SecureRandom());
                StringBuffer mySign = new StringBuffer("");
                YoutuSign.appSign(YouTuConfig.APP_ID, YouTuConfig.SECRET_ID, YouTuConfig.SECRET_KEY, System.currentTimeMillis() / 1000L + (long) EXPIRED_SECONDS, YouTuConfig.USER_ID, mySign);
                System.setProperty("sun.net.client.defaultConnectTimeout", "30000");
                System.setProperty("sun.net.client.defaultReadTimeout", "30000");

                URL url = new URL(YouTuConfig.Base_Url + TYPE);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setSSLSocketFactory(sc.getSocketFactory());
                connection.setHostnameVerifier(new TrustAnyHostnameVerifier());
                connection.setRequestMethod("POST");
                connection.setRequestProperty("accept", "*/*");
                connection.setRequestProperty("user-agent", "youtu-java-sdk");
                connection.setRequestProperty("Authorization", mySign.toString());
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.setInstanceFollowRedirects(true);
                connection.setRequestProperty("Content-Type", "text/json");
                connection.setConnectTimeout(20 * 1000);
                connection.connect();
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());

                out.write(postData.toString().getBytes("utf-8"));
                out.flush();
                out.close();
                Log.i(TAG, "发起请求...");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String lines;
                resposeBuffer = new StringBuffer("");
                while ((lines = reader.readLine()) != null) {
                    lines = new String(lines.getBytes(), "utf-8");
                    resposeBuffer.append(lines);
                }

                reader.close();
                connection.disconnect();
                Log.i(TAG, "接收到返回数据...");

            } else {
                Log.i(TAG, imgPath + " not exist");
            }

            Log.i(TAG, "resposeBuffer = " + (resposeBuffer == null ? "resposeBuffer == null" : resposeBuffer.toString()));
            return resposeBuffer == null || resposeBuffer.length() == 0 ? null : new Gson().fromJson(resposeBuffer.toString(), OCRObject.class);
//            return response == null || response.code() != 200 ? null : new Gson().fromJson(response.toString(), OCRObject.class);

        } catch (Exception e) {
            Log.i(TAG, "Exception = " + e.toString());
            e.printStackTrace();

        }
        return null;
    }

//    private OkHttpClient.Builder getClientBuilder() {
//        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).writeTimeout(20, TimeUnit.SECONDS);
//        try {
//            final TrustManager[] trustAllCerts = new TrustManager[]{
//                    new X509TrustManager() {
//                        @Override
//                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
//                        }
//
//                        @Override
//                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
//                        }
//
//                        @Override
//                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                            return new java.security.cert.X509Certificate[]{};
//                        }
//                    }
//            };
//            final SSLContext sslContext = SSLContext.getInstance("SSL");
//            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
//            final javax.net.ssl.SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
//            clientBuilder.sslSocketFactory(sslSocketFactory);
//            clientBuilder.hostnameVerifier(new HostnameVerifier() {
//                @Override
//                public boolean verify(String hostname, SSLSession session) {
//                    return true;
//                }
//            });
//
//            //自动管理cookie
//            clientBuilder.cookieJar(new CookieJar() {
//                private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
//
//                @Override
//                public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
//                    cookieStore.put(httpUrl.host(), list);
//                }
//
//                @Override
//                public List<Cookie> loadForRequest(HttpUrl httpUrl) {
//                    List<Cookie> cookies = cookieStore.get(httpUrl.host());
//                    return cookies != null ? cookies : new ArrayList<Cookie>();
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return clientBuilder;
//    }

    @Override
    protected void onPostExecute(OCRObject OCRObject) {
        super.onPostExecute(OCRObject);
        String result = null;
        if (OCRObject == null) {
            result = "识别结果 :\r\n 未能识别出您选择的图片/或图片不规范";
            if (listener != null) {
                listener.onFaied(new Exception(result));
            }
        } else {
            if ("OK".equals(OCRObject.getErrormsg())) {
                OCRObject.setCode(OCRObject.SUCCESS);
                if (YouTuConfig.TYPE_ID_CARD.equals(TYPE)) {//身份证
                    result = ID_CARD(OCRObject);
                } else if (YouTuConfig.TYPE_CREDIT_CARDCOR.equals(TYPE)) {//银行卡/信用卡
                    result = CREDIT_CARD(OCRObject);
                } else if (YouTuConfig.TYPE_FOOD_CARDCOR.equals(TYPE)) {//美食图片
                    result = FOOD_CARD(OCRObject);
                } else if (YouTuConfig.TYPE_IMAGE_CARDCOR.equals(TYPE)) {//图片标签/类别 识别
                    result = IMAGE_CARD(OCRObject);
                }
                if (TextUtils.isEmpty(result)) {
                    result = "识别结果 : \r\n\r\n" + "error_msg  : " + "未能识别出您选择的图片/或图片不规范";
                }
                if (listener != null) {
                    listener.onSuccess(OCRObject, result);
                }
            } else {
                result = "识别结果 :\r\n 未能识别出您选择的图片/或图片不规范";
                if (listener != null) {
                    listener.onFaied(new Exception(result));
                }
            }
        }

        Log.i(TAG, "腾讯YouTu 识别结果 : \r\n" + result);
        Log.i(TAG, "=======================================================================");
    }

    private String IMAGE_CARD(OCRObject OCRObject) {
        StringBuilder result = new StringBuilder("识别结果 :");
        if (OCRObject.getTags() != null && OCRObject.getTags().size() != 0) {
            for (int i = 0; i < OCRObject.getTags().size(); i++) {
                result.append(OCRObject.getTags().get(i).getTag_name()).append("、");
            }
        }
        String str = result.toString();
        return str.endsWith("、") ? str.substring(0, str.length() - 1) : str;
    }

    private String FOOD_CARD(OCRObject OCRObject) {
        if (OCRObject != null && "OK".equals(OCRObject.getErrormsg())) {
            return OCRObject.isFood() ? "是美食图片" : "不是美食图片";
        }
        return null;
    }

    private String CREDIT_CARD(OCRObject OCRObject) {
        String result = null;
        if (OCRObject.getItems() != null && OCRObject.getItems().size() != 0) {
            result = "识别结果 : \r\n \r\n"
                    + App.getCtx().getString(R.string.kong_ge) + "卡号 : " + OCRObject.getItems().get(0).getItemstring() + "\r\n"
                    + App.getCtx().getString(R.string.kong_ge) + "卡类型 : " + (OCRObject.getItems().size() > 1 ? OCRObject.getItems().get(1).getItemstring() : "") + "\r\n"
                    + App.getCtx().getString(R.string.kong_ge) + "卡名字 : " + (OCRObject.getItems().size() > 2 ? OCRObject.getItems().get(2).getItemstring() : "") + "\r\n"
                    + App.getCtx().getString(R.string.kong_ge) + "银行信息 : " + (OCRObject.getItems().size() > 3 ? OCRObject.getItems().get(3).getItemstring() : "") + "\r\n"
                    + App.getCtx().getString(R.string.kong_ge) + "有效期 : " + (OCRObject.getItems().size() > 4 ? OCRObject.getItems().get(4).getItemstring() : "") + "\r\n";

        }
        return result;
    }

    private String ID_CARD(OCRObject OCRObject) {
        String result = null;
        if (card_type == 0) {
            result = "识别结果 : \r\n \r\n"
                    + App.getCtx().getString(R.string.kong_ge) + "姓名 : " + OCRObject.getName() + "\r\n"
                    + App.getCtx().getString(R.string.kong_ge) + "生日 : " + OCRObject.getBirth() + "\r\n"
                    + App.getCtx().getString(R.string.kong_ge) + "性别 : " + OCRObject.getSex() + "\r\n"
                    + App.getCtx().getString(R.string.kong_ge) + "地址 : " + OCRObject.getAddress() + "\r\n"
                    + "身份证号 : " + OCRObject.getId() + "\r\n";
        } else if (card_type == 1) {
            result = "识别结果 : \r\n \r\n"
                    + "签发机关 : " + OCRObject.getAuthority() + "\r\n"
                    + "有效期限 : " + OCRObject.getValid_date() + "\r\n";
        }
        return result;
    }

    private class TrustAnyHostnameVerifier implements HostnameVerifier {
        private TrustAnyHostnameVerifier() {
        }

        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private class TrustAnyTrustManager implements X509TrustManager {
        private TrustAnyTrustManager() {
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

}
