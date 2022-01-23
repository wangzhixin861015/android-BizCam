package com.bcnetech.hyphoto.utils;

import android.text.TextUtils;
import android.widget.EditText;

public class StringUtils {

    public static boolean isNotEmptyString(final String str) {
        return str != null && str.length() > 0;
    }

    public static boolean isEmptyString(final String str) {
        return str == null || str.length() <= 0;
    }

    public static String getTextTrim(EditText text) {
        return text.getText().toString().replaceAll(" ", "");
    }

    public static String getText(EditText text) {
        return text.getText().toString();
    }

    /**
     * 每1位添加一个空格
     *
     * @param content
     * @return
     */
    public static String addSpeaceByCredit(String content) {
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        content = content.replaceAll(" ", "");
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        StringBuilder newString = new StringBuilder();
        for (int i = 1; i <= content.length(); i++) {
          /*  if (i % 1 == 0 && i != content.length()) {
                newString.append(content.charAt(i - 1) + " ");
            } else {
                newString.append(content.charAt(i - 1));
            }*/
            newString.append(content.charAt(i - 1) + " ");
        }
//        Log.i("mengyuan", "添加空格后："+newString.toString());
        return newString.toString();
    }


}
