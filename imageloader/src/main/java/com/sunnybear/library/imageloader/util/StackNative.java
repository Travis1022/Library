package com.sunnybear.library.imageloader.util;

import android.graphics.Bitmap;

/**
 * 图片模糊jni定义
 * Created by sunnybear on 16/1/26.
 */
public class StackNative {

    static {
        System.loadLibrary("blur");
    }

    /**
     * 图片模糊
     *
     * @param bitmap 图片
     * @param r      模糊半径
     */
    public static native void blurBitmap(Bitmap bitmap, int r);

    /**
     * 图片模糊
     *
     * @param img 图片数组
     * @param w   图片宽
     * @param h   图片高
     * @param r   模糊半径
     */
    public static native void blurPixels(int[] img, int w, int h, int r);
}
