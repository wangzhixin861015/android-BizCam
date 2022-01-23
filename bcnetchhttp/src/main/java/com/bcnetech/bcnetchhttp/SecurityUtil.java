package com.bcnetech.bcnetchhttp;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by wb on 2016/5/3.
 */
public abstract class SecurityUtil {

    /**
     * 使用 MD5 对字符串加密。
     *
     * @param str
     *            源字符串
     * @return 加密后字符串
     */
    public static String encodeByMD5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            return base64(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not encodeByMD5", e);
        }
    }

    /**
     * 把一个字节数组转换为16进制表达的字符串
     *
     * @param bytes
     * @return
     */
    private static String base64(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
}
