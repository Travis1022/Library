package com.sunnybear.library.basic;

import android.os.Bundle;

/**
 * View和Model的桥梁,在DispatchActivity中使用
 * Created by sunnybear on 16/1/29.
 */
interface ViewModelBridge {
    /**
     * 设置布局id
     *
     * @return 布局id
     */
    int getLayoutId();

    /**
     * 初始化控件数据
     *
     * @param args 传递参数
     */
    void onBindView(Bundle args);

    /**
     * 布局初始化完成回调
     */
    void onViewCreatedFinish();

    /**
     * 添加监听器
     */
    void addListener();
}
