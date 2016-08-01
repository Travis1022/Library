package com.sunnybear.library.network.request;

import com.sunnybear.library.network.util.JsonUtils;
import com.sunnybear.library.util.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.FormBody;
import okhttp3.Request;

/**
 * form请求构建类
 * Created by guchenkai on 2016/5/18.
 */
public class FormRequestBuilder {
    private static final String TAG = FormRequestBuilder.class.getSimpleName();
    private Map<String, String> headers;//请求头参数
    private Map<String, String> params;//请求参数

    private Request.Builder builder;
    private int method;
    private String url;

    public FormRequestBuilder() {
        headers = new ConcurrentHashMap<>();
        params = new ConcurrentHashMap<>();

        builder = new Request.Builder();
    }

    /**
     * 创建CommonRequestHelper实例
     *
     * @return RequestHelper实例
     */
    public static FormRequestBuilder newInstance() {
        return new FormRequestBuilder();
    }

    /**
     * 设置请求类型
     *
     * @param method 请求类型
     * @return RequestHelper实例
     */
    public FormRequestBuilder method(int method) {
        this.method = method;
        return this;
    }

    /**
     * 设置请求url
     *
     * @param url url
     * @return RequestHelper实例
     */
    public FormRequestBuilder url(String url) {
        this.url = url;
        return this;
    }

    /**
     * 添加请求头
     *
     * @param name  name
     * @param value value
     * @return RequestHelper实例
     */
    public FormRequestBuilder header(String name, String value) {
        try {
            headers.put(name, value);
        } catch (NullPointerException e) {
            Logger.e(TAG, "设置头参数为空,参数名:" + name + ",参数值:" + value);
        }
        return this;
    }

    /**
     * 添加请求参数
     *
     * @param name  name
     * @param value value
     * @return RequestHelper实例
     */
    public FormRequestBuilder param(String name, String value) {
        try {
            params.put(name, value);
        } catch (NullPointerException e) {
            Logger.e(TAG, "设置参数为空,参数名:" + name + ",参数值:" + value);
        }
        return this;
    }

    public Request build() {
        Request request = null;
        switch (method) {
            case RequestMethod.GET:
                for (String name : headers.keySet()) {
                    builder.addHeader(name, headers.get(name));
                }
                url = jointUrl(url, params);
                Logger.d(TAG, "get请求,url=" + url);
                if (url.contains("?"))
                    url = url.substring(0, url.indexOf("?"));
                request = builder.url(url).get().tag(url).build();
                break;
            case RequestMethod.POST:
                for (String name : headers.keySet()) {
                    builder.addHeader(name, headers.get(name));
                }
                FormBody.Builder param = new FormBody.Builder();
                for (String name : params.keySet()) {
                    param.add(name, params.get(name));
                }
                Logger.d(TAG, "post请求,url=" + jointUrl(url, params) + "\n" + "请求 参数:" + "\n" + formatParams(params));
                request = builder.url(url).post(param.build()).tag(url).build();
                break;
        }
        return request;
    }

    /**
     * 拼接参数
     *
     * @param url
     * @param params
     * @return
     */
    private String jointUrl(String url, Map<String, String> params) {
        StringBuffer sb = new StringBuffer(url + "?");
        for (String name : params.keySet()) {
            sb.append(name).append("=").append(params.get(name)).append("&");
        }
        url = sb.delete(sb.length() - 1, sb.length()).toString();
        return url;
    }

    /**
     * 格式化显示参数信息
     *
     * @return 参数信息
     */
    private String formatParams(Map<String, String> params) {
        StringBuffer sb = new StringBuffer();
        for (String name : params.keySet()) {
            String value = params.get(name);
            if (JsonUtils.JsonType.JSON_TYPE_ERROR != JsonUtils.getJSONType(value))
                value = JsonUtils.jsonFormatter(value);
            sb.append(name).append("=").append(value).append("\n");
        }
        return sb.delete(sb.length() - 1, sb.length()).toString();
    }
}
