package com.sunnybear.library.basic.fragmentstack;

import android.view.KeyEvent;

/**
 * 在Fragment中拦截Key事件回调
 * Created by sunnybear on 16/2/1.
 */
public interface OnKeyDownCallback {

    /**
     * Key点击事件
     *
     * @param keyCode keyCode
     * @param event   事件
     */
    boolean onKeyDown(int keyCode, KeyEvent event);
}
