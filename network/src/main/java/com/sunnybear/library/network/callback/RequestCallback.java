package com.sunnybear.library.network.callback;

import android.content.Context;

import com.sunnybear.library.loading.LoadingHUD;
import com.sunnybear.library.network.R;
import com.sunnybear.library.util.ResourcesUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 请求回调
 * Created by guchenkai on 2016/1/25.
 */
public abstract class RequestCallback implements Callback {
    protected LoadingHUD mLoading;

    public RequestCallback(Context context) {
        mLoading = LoadingHUD.create(context)
                .setStyle(LoadingHUD.Style.SPIN_INDETERMINATE)
                .setLabel(ResourcesUtils.getString(context, R.string.loading))
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5F);
    }

    /**
     * 开始请求
     */
    public void onStart() {
        mLoading.show();
    }

    /**
     * 完成请求
     *
     * @param url       url
     * @param isSuccess 请求是否成功
     * @param msg       请求完成的消息
     */
    public void onFinish(String url, boolean isSuccess, String msg) {
        mLoading.dismiss();
    }

    public abstract void onCacheResponse(Call call, Response response) throws IOException;
}
