package com.bcnetech.bcnetchhttp;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by a1234 on 2018/9/13.
 */

public abstract class RetrofitFactoryBase {
    OkHttpClient okHttpClient;

    protected RetrofitFactoryBase(){

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.NONE);

        okHttpClient =new OkHttpClient.Builder()
                .connectTimeout(70, TimeUnit.SECONDS)
                .readTimeout(70, TimeUnit.SECONDS)
                .writeTimeout(70, TimeUnit.SECONDS)
                .addInterceptor(InterceptorUtil.HeadInterceptor())
                .addInterceptor(logging)
                .build();//添加日志拦截器
    }

    public abstract APIFunction API();

}
