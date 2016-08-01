package com.sunnybear.library.network;

import android.content.Context;

import com.sunnybear.library.network.callback.DownloadCallback;
import com.sunnybear.library.network.callback.RequestCallback;
import com.sunnybear.library.network.interceptor.NetworkInterceptor;
import com.sunnybear.library.network.request.FormRequestBuilder;
import com.sunnybear.library.network.request.RequestMethod;
import com.sunnybear.library.util.FileUtils;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.util.ResourcesUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 普通网络请求助手
 * Created by guchenkai on 2016/1/22.
 */
public class OkHttpRequestHelper {
    private static final String TAG = OkHttpRequestHelper.class.getSimpleName();

    private Context mContext;
    private int mCacheType;
    private OkHttpClient mOkHttpClient;

    public OkHttpRequestHelper(Context context) {
        mContext = context;
        mOkHttpClient = OkHttpManager.getInstance()
                .addInterceptor(new NetworkInterceptor())
                /*.addInterceptor(new ResponseInfoInterceptor())*/.build();
        mCacheType = CacheType.NETWORK_ELSE_CACHE;//默认是先请求网络,再请求缓存
    }

    public static OkHttpRequestHelper newInstance(Context context) {
        return new OkHttpRequestHelper(context);
    }

    /**
     * 设置缓存策略
     *
     * @param cacheType 缓存策略
     * @return OkHttpFormEncodingHelper实例
     */
    public OkHttpRequestHelper cacheType(int cacheType) {
        mCacheType = cacheType;
        return this;
    }

    /**
     * 添加拦截器
     *
     * @param interceptor 拦截器
     * @return OkHttpFormEncodingHelper实例
     */
    public OkHttpRequestHelper addInterceptor(Interceptor interceptor) {
        mOkHttpClient.networkInterceptors().add(interceptor);
        return this;
    }

    /**
     * 添加拦截器
     *
     * @param interceptors 拦截器组
     * @return OkHttpFormEncodingHelper实例
     */
    public OkHttpRequestHelper addInterceptors(List<Interceptor> interceptors) {
        mOkHttpClient.networkInterceptors().addAll(interceptors);
        return this;
    }

    /**
     * 网络请求
     *
     * @param request  请求实例
     * @param callback 请求回调
     */
    private void requestFromNetwork(Request request, Callback callback) {
        Logger.d(TAG, "读取网络信息,Url=" + getUrl(request));
        mOkHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 缓存请求
     *
     * @param request  请求实例
     * @param callback 请求回调
     */
    private void requestFromCache(Request request, RequestCallback callback) {
        Response response = getResponse(mOkHttpClient.cache(), request);
        if (response != null)
            try {
                Logger.d(TAG, "读取缓存信息,Url=" + getUrl(request));
                callback.onCacheResponse(null, response);
            } catch (IOException e) {
                callback.onFailure(null, e);
                Logger.e(e);
            }
        else
            callback.onFailure(null, new IOException(ResourcesUtils.getString(NetworkConfiguration.getContext(), R.string.not_cache)));
    }

    /**
     * 反射方法取得响应体
     *
     * @param cache   缓存
     * @param request 请求体
     * @return 响应体
     */
    private Response getResponse(Cache cache, Request request) {
        Class clz = cache.getClass();
        try {
            Method get = clz.getDeclaredMethod("get", Request.class);
            get.setAccessible(true);
            return (Response) get.invoke(cache, request);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(e);
        }
        return null;
    }

    /**
     * 请求
     *
     * @param request  请求实例
     * @param callback 请求回调
     */
    public void request(final Request request, final RequestCallback callback) {
        if (callback == null)
            throw new NullPointerException("请设置请求回调");
        //如果不是缓存请求,执行OnStart方法
        if (mCacheType == CacheType.NETWORK || mCacheType == CacheType.NETWORK_ELSE_CACHE)
            callback.onStart();
        switch (mCacheType) {
            case CacheType.NETWORK:
                requestFromNetwork(request, callback);
                break;
            case CacheType.CACHE:
                requestFromCache(request, callback);
                break;
            case CacheType.CACHE_ELSE_NETWORK:
                requestFromCache(request, new RequestCallback(mContext) {
                    @Override
                    public void onCacheResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful())
                            callback.onCacheResponse(call, response);
                        else
                            requestFromNetwork(request, callback);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        requestFromNetwork(request, callback);
                    }
                });
                break;
            case CacheType.NETWORK_ELSE_CACHE:
                requestFromNetwork(request, new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful())
                            callback.onResponse(call, response);
                        else
                            requestFromCache(request, callback);
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        requestFromCache(request, callback);
                    }
                });
                break;
        }
    }

    /**
     * 获取Url
     *
     * @param request 请求体
     * @return url
     */
    private String getUrl(Request request) {
        return request.url().url().toString();
    }

    /**
     * 下载文件
     *
     * @param url      下载地址
     * @param filePath 保存文件的路径
     * @param callback 下载文件回调
     */
    public void download(String url, String filePath, DownloadCallback callback) {
        callback.onStart();
        callback.setFilePath(filePath);
        if (FileUtils.isExists(filePath)) {
            callback.onFinish(url, true, "现在文件已存在,请不要重复下载");
            return;
        }
        mOkHttpClient.newCall(FormRequestBuilder.newInstance()
                .url(url).method(RequestMethod.GET)
                .build()).enqueue(callback);
    }

    /**
     * 取消请求
     *
     * @param url url
     */
    public void cancelRequest(String url) {
//        mOkHttpClient.cancel(url);
    }
}
