package com.sunnybear.library.network;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * OkHttp管理
 * Created by guchenkai on 2016/5/18.
 */
public class OkHttpManager {
    private static int CONNECT_TIMEOUT_MILLIS;//连接时间超时
    private static int WRITE_TIMEOUT_MILLIS;//写入时间超时
    private static int READ_TIMEOUT_MILLIS;//读取时间超时

    private volatile static OkHttpManager instance;
    private static List<Interceptor> mInterceptors;

    private int mCacheSize;
    private String mCacheDirectoryPath;

    public OkHttpManager() {
        mCacheDirectoryPath = NetworkConfiguration.getNetworkCacheDirectoryPath();
        mCacheSize = NetworkConfiguration.getNetworkCacheSize();
        mInterceptors = new LinkedList<>();

        CONNECT_TIMEOUT_MILLIS = NetworkConfiguration.CONNECT_TIMEOUT_MILLIS;
        WRITE_TIMEOUT_MILLIS = NetworkConfiguration.WRITE_TIMEOUT_MILLIS;
        READ_TIMEOUT_MILLIS = NetworkConfiguration.READ_TIMEOUT_MILLIS;
    }

    /**
     * 单例实例
     *
     * @return OkHttpHelper实例
     */
    public static OkHttpManager getInstance() {
        if (instance == null)
            synchronized (OkHttpManager.class) {
                if (instance == null)
                    instance = new OkHttpManager();
            }
        return instance;
    }

    /**
     * 构建OkHttpClient
     *
     * @return OkHttpClient
     */
    public OkHttpClient build() {
        return generateOkHttpClient(mInterceptors);
    }

    /**
     * 添加拦截器
     *
     * @param interceptor 拦截器
     * @return OkHttpManager
     */
    public OkHttpManager addInterceptor(Interceptor interceptor) {
        mInterceptors.add(interceptor);
        return this;
    }

    /**
     * 获得OkHttp客户端
     *
     * @return OkHttp客户端
     */
    public OkHttpClient generateOkHttpClient(List<Interceptor> interceptors) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        builder.writeTimeout(WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        builder.readTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        if (interceptors != null && interceptors.size() > 0)
            builder.networkInterceptors().addAll(interceptors);
//        try {
//            SSLContext context = SSLContext.getInstance("TLS");
//            context.init(null, new TrustManager[]{new SSLTrustManager()}, new SecureRandom());
//            builder.sslSocketFactory(context.getSocketFactory());
//        } catch (Exception e) {
//            Logger.e(e);
//            e.printStackTrace();
//        }
        setCache(builder);
        return builder.build();
    }

    /**
     * 获得缓存器
     *
     * @param builder OkHttpClient建造器
     */
    private void setCache(OkHttpClient.Builder builder) {
        File cacheDirectory = new File(mCacheDirectoryPath);
        if (!cacheDirectory.exists())
            cacheDirectory.mkdirs();
        builder.cache(new Cache(cacheDirectory, mCacheSize));
    }
}
