package com.sunnybear.library.imageloader.util;

import android.graphics.Bitmap;

/**
 * 图片模糊工具
 * Created by sunnybear on 16/1/26.
 */
public class StackBlur {

    /**
     * @param bitmap
     * @param canReuseInBitmap
     * @return
     */
    private static Bitmap buildBitmap(Bitmap bitmap, boolean canReuseInBitmap) {
        Bitmap rBitmap = null;
        if (canReuseInBitmap)
            rBitmap = bitmap;
        else
            rBitmap = bitmap.copy(bitmap.getConfig(), true);
        return rBitmap;
    }

    /**
     * @param original
     * @param radius
     * @param canReuseInBitmap
     * @return
     */
    public static Bitmap blurNatively(Bitmap original, int radius, boolean canReuseInBitmap) {
        if (radius < 1) return null;
        Bitmap bitmap = buildBitmap(original, canReuseInBitmap);
        if (radius == 1) return bitmap;
        StackNative.blurBitmap(bitmap, radius);
        return bitmap;
    }

    /**
     * @param original
     * @param radius
     * @param canReuseInBitmap
     * @return
     */
    public static Bitmap blurNativelyPixels(Bitmap original, int radius, boolean canReuseInBitmap) {
        if (radius < 1)
            return null;
        Bitmap bitmap = buildBitmap(original, canReuseInBitmap);
        if (radius == 1)
            return bitmap;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        // Jni Pixels Blur
        StackNative.blurPixels(pix, w, h, radius);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return bitmap;
    }
}
