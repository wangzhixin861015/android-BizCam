package com.bcnetech.bcnetchhttp;


import com.bcnetech.bcnetchhttp.config.UrlConstants;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by yhf on 2018/9/11.
 */

public class RetrofitFactory extends RetrofitFactoryBase{
    protected APIFunction mAPIFunction;

    protected static RetrofitFactory mRetrofitFactory;
    public RetrofitFactory() {
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(UrlConstants.DEFAUL_WEB_SITE)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();

        mAPIFunction=retrofit.create(APIFunction.class);

    }
    public static RetrofitFactory getInstence(){
        if (mRetrofitFactory==null){
            synchronized (RetrofitFactory.class) {
                if (mRetrofitFactory == null)
                    mRetrofitFactory = new RetrofitFactory();
            }

        }
        return mRetrofitFactory;
    }

    public  APIFunction API(){
        return mAPIFunction;
    }

}
