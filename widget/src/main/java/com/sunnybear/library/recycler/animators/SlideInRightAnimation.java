package com.sunnybear.library.recycler.animators;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * 右侧进入
 * Created by guchenkai on 2016/1/20.
 */
public class SlideInRightAnimation implements IAnimation {
    @Override
    public Animator[] getAnimators(View itemView) {
        return new Animator[]{
                ObjectAnimator.ofFloat(itemView, "translationX", itemView.getRootView().getWidth(), 0)
        };
    }
}
