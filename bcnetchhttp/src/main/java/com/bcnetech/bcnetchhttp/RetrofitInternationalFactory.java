package com.bcnetech.bcnetchhttp;


import com.bcnetech.bcnetchhttp.config.UrlConstants;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by yhf on 2018/9/11.
 */

public class RetrofitInternationalFactory extends RetrofitFactoryBase{
    protected APIFunction mAPIFunction;

    protected static RetrofitInternationalFactory mRetrofitFactory;
    public RetrofitInternationalFactory() {
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(UrlConstants.DEFAUL_INTERNATIONAL_WEB_SITE)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();

        mAPIFunction=retrofit.create(APIFunction.class);

    }

    public static RetrofitInternationalFactory getInstence(){
        if (mRetrofitFactory==null){
            synchronized (RetrofitInternationalFactory.class) {
                if (mRetrofitFactory == null)
                    mRetrofitFactory = new RetrofitInternationalFactory();
            }

        }
        return mRetrofitFactory;
    }

    public  APIFunction API(){
        return mAPIFunction;
    }

}
