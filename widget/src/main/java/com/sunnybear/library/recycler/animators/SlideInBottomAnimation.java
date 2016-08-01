package com.sunnybear.library.recycler.animators;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * 底部进入
 * Created by guchenkai on 2016/1/20.
 */
public class SlideInBottomAnimation implements IAnimation {

    @Override
    public Animator[] getAnimators(View itemView) {
        return new Animator[]{
                ObjectAnimator.ofFloat(itemView, "translationY", itemView.getMeasuredHeight(), 0)
        };
    }
}
