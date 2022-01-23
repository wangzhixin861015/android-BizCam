package com.bcnetech.hyphoto.utils;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.bcnetech.bcnetchhttp.config.Flag;

import java.util.Locale;

/**
 * Created by wenbin on 16/10/8.
 */

public class LangugeUtil {


    public static void switchLanguage(Activity activity, String language) {
        //设置应用语言类型
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (language.equals(Flag.ENGLISH_US)) {
            config.locale = Locale.US;
        } else if(language.equals(Flag.CHINAE)){
            config.locale = Locale.SIMPLIFIED_CHINESE;
        } else if(language.equals(Flag.CHINAE_HK)){
            config.locale = Locale.TRADITIONAL_CHINESE;
        }else if(language.equals(Flag.CHINAE_TW)){
            config.locale = Locale.TRADITIONAL_CHINESE;
        }
        resources.updateConfiguration(config, dm);
    }

}
