package com.sunnybear.library.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.BaseRepeatedPostProcessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.sunnybear.library.util.ResourcesUtils;
import com.sunnybear.library.util.StringUtils;

import java.util.LinkedList;

/**
 * 图片加载控件
 * Created by guchenkai on 2015/11/4.
 */
public class ImageLoaderView extends SimpleDraweeView {
    private ResizeOptions mResizeOptions;
    private PictureProcessor mProcessor;

    private float mRatio;
    private Drawable mDefaultDrawable;

    public ImageLoaderView(Context context) {
        this(context, null);
    }

    public ImageLoaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageLoaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ImageDraweeView);
//        mRatio = array.getFloat(R.styleable.ImageDraweeView_aspect_ratio, -1f);
//        if (mRatio != -1f)
//            setAspectRatio(mRatio);
//        mDefaultDrawable = array.getDrawable(R.styleable.ImageDraweeView_default_drawable);
//        if (mDefaultDrawable != null)
//            setDefaultImage(mDefaultDrawable);
//        array.recycle();
        init(context);
    }

    private void init(Context context) {
        GenericDraweeHierarchy hierarchy = getHierarchy();
        hierarchy.setFadeDuration(300);//淡入淡出
        setHierarchy(hierarchy);
        mProcessor = new PictureProcessor(context);
    }

    public ImageLoaderView resize(int width, int height) {
        if (width == 0 || height == 0) {
            mResizeOptions = null;
            return this;
        }
        mResizeOptions = new ResizeOptions(width, height);
        return this;
    }


    /**
     * 添加加载后处理器
     *
     * @param processor 加载后处理器
     */
    public ImageLoaderView addProcessor(ProcessorInterface processor) {
        mProcessor.addProcessor(processor);
        return this;
    }

    /**
     * 设置默认图片
     *
     * @param bitmap 默认图片
     */
    public void setDefaultImage(Bitmap bitmap) {
        GenericDraweeHierarchy hierarchy = getHierarchy();
        hierarchy.setPlaceholderImage(new BitmapDrawable(bitmap), ScalingUtils.ScaleType.FOCUS_CROP);
        setHierarchy(hierarchy);
    }

    /**
     * 设置默认图片
     *
     * @param resId 默认图片资源id
     */
    public void setDefaultImage(int resId) {
        Drawable drawable = ResourcesUtils.getDrawable(getContext(), resId);
        GenericDraweeHierarchy hierarchy = getHierarchy();
        hierarchy.setPlaceholderImage(drawable, ScalingUtils.ScaleType.FOCUS_CROP);
        setHierarchy(hierarchy);
    }

    /**
     * 设置默认图片
     *
     * @param drawable 默认图片
     */
    public void setDefaultImage(Drawable drawable) {
        GenericDraweeHierarchy hierarchy = getHierarchy();
        hierarchy.setPlaceholderImage(drawable, ScalingUtils.ScaleType.FOCUS_CROP);
        setHierarchy(hierarchy);
    }

    /**
     * 设置ScaleType
     *
     * @param scaleType ScaleType
     */
    public void setScaleType(ScalingUtils.ScaleType scaleType) {
        GenericDraweeHierarchy hierarchy = getHierarchy();
        hierarchy.setActualImageScaleType(scaleType);
        setHierarchy(hierarchy);
    }

    /**
     * 加载图片
     *
     * @param url 图片的url
     */
    public void setImageURL(String url) {
        if (StringUtils.isEmpty(url)) return;
        Uri uri = Uri.parse(url);
//        setImageURI(uri);
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setPostprocessor(mProcessor)
                .setAutoRotateEnabled(true)
//                .setLocalThumbnailPreviewsEnabled(true)//预先加载本地缩略图
                .setProgressiveRenderingEnabled(true)//允许渐进式JPEG图片加载
                .setResizeOptions(mResizeOptions)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setTapToRetryEnabled(true)//加载失败时点击重新加载
                .setOldController(getController())
                .build();
        setController(controller);
    }

    /**
     * fresco图片处理器
     */
    class PictureProcessor extends BaseRepeatedPostProcessor {
        private Context mContext;
        private LinkedList<ProcessorInterface> processorList = new LinkedList<>();

        public PictureProcessor(Context context) {
            mContext = context;
        }

        public PictureProcessor addProcessor(ProcessorInterface processorInterface) {
            processorList.add(processorInterface);
            return this;
        }

        @Override
        public void process(Bitmap bitmap) {
            for (ProcessorInterface processor : processorList) {
                processor.process(mContext, bitmap);
            }
        }
    }

    /**
     * 图片处理器接口
     * Created by guchenkai on 2015/11/17.
     */
    public interface ProcessorInterface {

        void process(Context context, Bitmap bitmap);
    }
}
