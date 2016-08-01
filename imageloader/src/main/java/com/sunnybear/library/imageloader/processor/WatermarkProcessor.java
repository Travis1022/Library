package com.sunnybear.library.imageloader.processor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.sunnybear.library.imageloader.ImageLoaderView;
import com.sunnybear.library.util.ImageUtils;
import com.sunnybear.library.util.ResourcesUtils;

/**
 * 水印处理器
 * Created by sunnybear on 16/1/27.
 */
public class WatermarkProcessor implements ImageLoaderView.ProcessorInterface {
    private Bitmap mWatermark;//水印
    private int mLocation;//方位

    public WatermarkProcessor(Drawable watermark, int location) {
        mWatermark = ImageUtils.drawableToBitmap(watermark);
        mLocation = location;
    }

    public WatermarkProcessor(Context context, int watermarkResId, int location) {
        this(ResourcesUtils.getDrawable(context, watermarkResId), location);
    }

    @Override
    public void process(Context context, Bitmap bitmap) {
        if (bitmap == null) return;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int ww = mWatermark.getWidth();
        int wh = mWatermark.getHeight();

        float x = 0, y = 0;//水印的初始位置

        Canvas canvas = new Canvas(bitmap);
        switch (mLocation) {
            case WatermarkLocation.TOP_LEFT:
                x = 0;
                y = 0;
                break;
            case WatermarkLocation.TOP_RIGHT:
                x = w - ww;
                y = 0;
                break;
            case WatermarkLocation.BOTTOM_LEFT:
                x = 0;
                y = h - wh;
                break;
            case WatermarkLocation.BOTTOM_RIGHT:
                x = w - ww;
                y = h - wh;
                break;
            case WatermarkLocation.CENTER:
                x = w / 2 - ww / 2;
                y = h / 2 - wh / 2;
                break;
        }
        canvas.drawBitmap(mWatermark, x, y, null);
        canvas.save(Canvas.ALL_SAVE_FLAG);//保存
        canvas.restore();//存储
    }

    /**
     * 水印方位
     */
    public interface WatermarkLocation {
        /**
         * 左上
         */
        int TOP_LEFT = 0;
        /**
         * 右上
         */
        int TOP_RIGHT = 1;
        /**
         * 左下
         */
        int BOTTOM_LEFT = 2;
        /**
         * 右下
         */
        int BOTTOM_RIGHT = 3;
        /**
         * 中间
         */
        int CENTER = 4;
    }
}
