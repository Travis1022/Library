package com.sunnybear.library.basic;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.sunnybear.library.eventbus.EventBusHelper;

/**
 * 绑定View实例
 * Created by sunnybear on 16/1/29.
 */
public abstract class ViewBinder<D extends Dispatch> implements ViewModelBridge {
    protected Context mContext;
    protected D mDispatch;
    protected Fragment mFragment;

    public ViewBinder(Context context) {
        this(context, null);
    }

    public ViewBinder(Context context, Fragment fragment) {
        mContext = context;
        mDispatch = (D) context;
        if (!(mDispatch instanceof DispatchActivity))
            throw new RuntimeException("ViewBinder中的Content必须是DispatchActivity类型");
        if (fragment != null)
            mFragment = fragment;
        EventBusHelper.register(this);
    }

    @Override
    public void onBindView(Bundle args) {

    }

    @Override
    public void addListener() {

    }

    /**
     * 跳转Activity
     *
     * @param targetClass 目标Activity类型
     * @param args        传递参数
     */
    protected void startActivity(Class<? extends Activity> targetClass, Bundle args) {
        ((DispatchActivity) mDispatch).startActivity(targetClass, args);
    }

    /**
     * 跳转Activity
     *
     * @param targetClass 目标Activity类型
     */
    protected void startActivity(Class<? extends Activity> targetClass) {
        startActivity(targetClass, null);
    }

    /**
     * 跳转Service
     *
     * @param targetClass 目标Activity类型
     * @param args        传递参数
     */
    protected void startService(Class<? extends Service> targetClass, Bundle args) {
        ((DispatchActivity) mDispatch).startService(targetClass, args);
    }

    /**
     * 跳转Service
     *
     * @param targetClass 目标Activity类型
     */
    protected void startService(Class<? extends Service> targetClass) {
        startService(targetClass, null);
    }
}
