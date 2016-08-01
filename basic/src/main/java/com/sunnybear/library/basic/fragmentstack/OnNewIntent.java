package com.sunnybear.library.basic.fragmentstack;

import android.os.Bundle;

/**
 * 在SingleTop模式下,如果当前任务栈有当前Fragment实例,则回调此方法
 * Created by sunnybear on 16/2/1.
 */
public interface OnNewIntent {

    /**
     * SingleTop模式下,Fragment不会被重新创建
     */
    void onNewIntent(Bundle args);
}
