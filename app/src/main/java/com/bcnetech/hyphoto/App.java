package com.bcnetech.hyphoto;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.multidex.MultiDex;

import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.BcnetechAppInstance;
import com.bcnetech.bcnetechlibrary.blurkit.BlurKit;
import com.bcnetech.bcnetechlibrary.util.ActivityManager;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.hyphoto.utils.okHttp.WBImageDownloader;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.HashMap;

//import com.bcnetech.bcnetechlibrary.util.okHttp.OkHttpClientManager;

/**
 * Created by wb on 2016/5/3.
 */
public class App extends Application {
    private String version;//部分activity中共享数据
    private boolean isend=true;
    private int newTime;
    private SoftReference<HashMap<String,Object>> cacheData; //用于替代bundle 传递Serializable等数据
    private static App instance;
    private float Ratio;
    public static void setInstance(App instance) {
        App.instance = instance;
    }

    public static App getInstance() {
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        cacheData=new SoftReference(new HashMap<>());
       // AppCrashHandler.getInstance().init(this);

        BlurKit.init(this);
        //开启debug模式，方便定位错误，具体错误检查方式可以查看http://dev.umeng.com/social/android/quick-integration的报错必看，正式发布，请关闭该模式
//        Config.DEBUG = true;
        //集成测试 发布关闭
//        MobclickAgent.setDebugMode(true);
       // JniAppInstance.init(this);

//        OkHttpClientManager.getInstance().initSSL(this);
        BcnetechAppInstance.init(this);
        initImageLoading();

        Thread.setDefaultUncaughtExceptionHandler(restartHandler); // 程序崩溃时触发线程

    }

    public void initUm(){
        UMConfigure.init(this,"5987d1cd1061d2181e0001cc"
                ,"umeng",UMConfigure.DEVICE_TYPE_PHONE,"");

        UMConfigure.setLogEnabled(false);
        intShareAppKey();

    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (cacheData!=null){
            cacheData.clear();
        }
    }

    public Thread.UncaughtExceptionHandler restartHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            ActivityManager.removeAndFinishAll();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    };

    private void initImageLoading() {

        File cacheDir = new File(Flag.LOCAL_IMAGE_PATH);
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.NONE)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .showImageOnLoading(Flag.DEFAULT_AVT)
                .showImageForEmptyUri(Flag.DEFAULT_AVT)
                .showImageOnFail(Flag.DEFAULT_AVT)
                .build();

        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(this.getApplicationContext());
        builder.threadPoolSize(5);// 设置线程池 大小
        builder.threadPriority(Thread.NORM_PRIORITY - 2);// 线程池的优先级
        builder.defaultDisplayImageOptions(options); // default
        builder.tasksProcessingOrder(QueueProcessingType.LIFO);// 处理任务的顺序
        builder.denyCacheImageMultipleSizesInMemory();// 当同一个Uri获取不同大小的图片，缓存到内存时，只缓存一个。默认会缓存多个不同的大小的相同图片
        builder.memoryCache(new LruMemoryCache(5 * 1024 * 1024));
        builder.memoryCacheSize(5 * 1024 * 1024); //设置高速缓存大小
        //builder.memoryCacheSizePercentage(18);
        builder.diskCache(new UnlimitedDiskCache(cacheDir, cacheDir, new FileNameGenerator() {
            @Override
            public String generate(String imageUri) {
                return ImageUtil.getPicLocalUrl(imageUri);
            }
        }));

        builder.diskCacheSize(80 * 1024 * 1024); //最大缓存大小
        builder.diskCacheFileCount(1000);//最大缓存文件数
        builder.imageDownloader(new WBImageDownloader(this.getApplicationContext())); // default

        ImageLoaderConfiguration config = builder.build();
        ImageLoader.getInstance().init(config);
    }
    public HashMap<String,Object> getCacheData() {
        HashMap<String,Object> data=cacheData.get();
        if(data==null){
            cacheData=new SoftReference(new HashMap<String,Object>());
            data=cacheData.get();
        }
        return data;
    }

    private void intShareAppKey(){
        PlatformConfig.setWeixin("wx641f3e1465189266", "75168b2414d9dcaf30f6b559cd07de07");
        //豆瓣RENREN平台目前只能在服务器端配置
        PlatformConfig.setSinaWeibo("3707240422", "56cdedbef7406e80736744db29572f6c","https://www.bcyun.com");
        PlatformConfig.setQQZone("1106394618", "X9202tM6P5ql9Ght");

    }

    public int getNewTime() {
        return newTime;
    }

    public void setNewTime(int newTime) {
        this.newTime = newTime;
    }

    public  boolean isend() {
        return isend;
    }

    public void setIsend(boolean isend) {
        this.isend = isend;
    }

    public float getRatio() {
        return Ratio;
    }

    public void setRatio(float ratio) {
        Ratio = ratio;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
