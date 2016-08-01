package com.sunnybear.library.imageloader.processor;

import android.content.Context;
import android.graphics.Bitmap;

import com.sunnybear.library.imageloader.ImageLoaderView;
import com.sunnybear.library.imageloader.util.StackBlur;

/**
 * 高斯模糊处理器
 * Created by guchenkai on 2016/1/12.
 */
public class BlurProcessor implements ImageLoaderView.ProcessorInterface {
    private final int DEFAULT_BLUR_RADIUS = 25;
    private int radius = 0;

    public BlurProcessor() {
    }

    public BlurProcessor(int radius) {
        this.radius = radius;
    }

    @Override
    public void process(Context context, Bitmap bitmap) {
        StackBlur.blurNatively(bitmap, radius != 0 ? radius : DEFAULT_BLUR_RADIUS, true);
    }
}
