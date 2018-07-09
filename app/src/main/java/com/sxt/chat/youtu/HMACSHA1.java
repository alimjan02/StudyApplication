//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sxt.chat.youtu;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HMACSHA1 {
    private static final String HMAC_SHA1 = "HmacSHA1";

    public HMACSHA1() {
    }

    public static byte[] getSignature(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), mac.getAlgorithm());
        mac.init(signingKey);
        return mac.doFinal(data.getBytes());
    }
}
