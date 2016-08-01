package com.sunnybear.library.network;

import android.content.Context;

/**
 * 网络参数配置
 * Created by chenkai.gu on 2016/4/13.
 */
public class NetworkConfiguration {
    private static Context mContext;
    private static String mNetworkCacheDirectoryPath;
    private static int mNetworkCacheSize;

    public static int CONNECT_TIMEOUT_MILLIS;//连接时间超时
    public static int WRITE_TIMEOUT_MILLIS;//写入时间超时
    public static int READ_TIMEOUT_MILLIS;//读取时间超时


    public static void initialize(Context context,
                                  int connectTimeoutMillis,
                                  int writeTimeoutMillis,
                                  int readTimeoutMillis,
                                  String networkCacheDirectoryPath, int networkCacheSize) {
        mContext = context;
        mNetworkCacheDirectoryPath = networkCacheDirectoryPath;
        mNetworkCacheSize = networkCacheSize;

        CONNECT_TIMEOUT_MILLIS = connectTimeoutMillis;
        WRITE_TIMEOUT_MILLIS = writeTimeoutMillis;
        READ_TIMEOUT_MILLIS = readTimeoutMillis;
    }

    public static Context getContext() {
        return mContext;
    }

    public static String getNetworkCacheDirectoryPath() {
        return mNetworkCacheDirectoryPath;
    }

    public static int getNetworkCacheSize() {
        return mNetworkCacheSize;
    }
}
