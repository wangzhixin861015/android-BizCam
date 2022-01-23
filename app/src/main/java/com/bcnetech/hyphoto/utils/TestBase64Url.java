package com.bcnetech.hyphoto.utils;

import android.util.Base64;

/**
 * Created by litao on 2017/2/27.
 */
public class TestBase64Url {

    public static void main(String[] args) throws Exception{

        String url = "[{\"scale\":{\"w\":150}}]";

        System.out.println("1---->"+base64UrlEncode(url.getBytes("UTF-8")));

      //  System.out.println("2---->"+new String(base64UrlDecode("W3sic2NhbGUiOnsidyI6MTUwfX1d")));

    }

    /**
     * base64加密
     * @param simple
     * @return
     */
    public static String base64UrlEncode(byte[] simple) {
       String s = Base64.encodeToString(simple, Base64.URL_SAFE);
      //  String s = new String(Base64.encodeBase64(simple)); // Regular base64 encoder
        s = s.split("=")[0]; // Remove any trailing '='s
        s = s.replace('+', '-'); // 62nd char of encoding
        s = s.replace('/', '_'); // 63rd char of encoding
        return s;
    }



  /*  public static byte[] base64UrlDecode(String cipher) {
        String s = cipher;
        s = s.replace('-', '+'); // 62nd char of encoding
        s = s.replace('_', '/'); // 63rd char of encoding
        switch (s.length() % 4) { // Pad with trailing '='s
            case 0:
                break; // No pad chars in this case
            case 2:
                s += "==";
                break; // Two pad chars
            case 3:
                s += "=";
                break; // One pad char
            default:
                System.err.println("Illegal base64url String!");
        }
        return Base64.decodeBase64(s); // Standard base64 decoder
    }*/

}
