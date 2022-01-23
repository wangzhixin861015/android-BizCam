package com.bcnetech.bcnetchhttp;


import com.bcnetech.bcnetchhttp.config.UrlConstants;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by yhf on 2018/9/11.
 */

public class RetrofitUploadFactory extends RetrofitFactoryBase{
    protected  APIFunction mAPIFunction;
    protected static RetrofitUploadFactory mRetrofitUploadFactory;
    public RetrofitUploadFactory() {
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(UrlConstants.DEFAUL_WEB_SITE_UPLOAD)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();

        mAPIFunction=retrofit.create(APIFunction.class);

    }

    public static RetrofitUploadFactory getUPloadInstence(){
        if (mRetrofitUploadFactory==null){
            synchronized (RetrofitUploadFactory.class) {
                if (mRetrofitUploadFactory == null)
                    mRetrofitUploadFactory = new RetrofitUploadFactory();
            }

        }
        return mRetrofitUploadFactory;
    }


    public  APIFunction API(){
        return mAPIFunction;
    }

}
