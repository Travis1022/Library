package com.sunnybear.library.basic.fragmentstack;

import com.sunnybear.library.basic.DispatchFragment;

/**
 *
 * Created by sunnybear on 16/2/1.
 */
public interface HandlerFragment {
    /**
     * 显示Fragment
     *
     * @param fragment fragment实例
     */
    void show(DispatchFragment fragment);

    /**
     * 关闭fragment
     *
     * @param fragment fragment
     */
    void close(DispatchFragment fragment);
}
