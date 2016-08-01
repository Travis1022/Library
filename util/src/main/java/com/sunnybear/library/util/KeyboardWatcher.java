package com.sunnybear.library.util;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

/**
 * 软键盘显示隐藏监听
 * Created by chenkai.gu on 2016/3/28.
 */
public class KeyboardWatcher {
    private Activity mActivity;
    private ViewGroup mRootView;
    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener;
    private OnKeyboardToggleListener mOnKeyboardToggleListener;

    public static KeyboardWatcher initWith(Activity activity) {
        KeyboardWatcher watcher = new KeyboardWatcher();
        watcher.mActivity = activity;
        return watcher;
    }

    public KeyboardWatcher bindKeyboardWatcher(OnKeyboardToggleListener onKeyboardToggleListener) {
        this.mOnKeyboardToggleListener = onKeyboardToggleListener;
        final View root = mActivity.findViewById(android.R.id.content);
        if (hasAdjustResizeInputMode()) {

            if (root instanceof ViewGroup) {
                mRootView = (ViewGroup) root;
                mGlobalLayoutListener = new GlobalLayoutListener();
                mRootView.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);
            }
        } else {
            Logger.e("KeyboardWatcher", "Activity " + mActivity.getClass().getSimpleName() + " should have windowSoftInputMode=\"adjustResize\"" +
                    "to make KeyboardWatcher working. You can set it in AndroidManifest.xml");
        }
        return this;
    }

    private boolean hasAdjustResizeInputMode() {
        return (mActivity.getWindow().getAttributes().softInputMode & WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE) != 0;
    }

    public void unbindKeyboardWatcher() {
        if (mRootView != null && mOnKeyboardToggleListener != null) {
            if (Build.VERSION.SDK_INT >= 16)
                mRootView.getViewTreeObserver().removeOnGlobalLayoutListener(mGlobalLayoutListener);
            else
                mRootView.getViewTreeObserver().removeGlobalOnLayoutListener(mGlobalLayoutListener);
            this.mOnKeyboardToggleListener = null;
            this.mActivity = null;
        }
    }

    /**
     *
     */
    private class GlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
        int initialValue;
        private boolean hasSentInitialAction;
        private boolean isKeyboardShown;

        @Override
        public void onGlobalLayout() {
            if (initialValue == 0) {
                initialValue = mRootView.getHeight();
            } else {
                if (initialValue > mRootView.getHeight()) {
                    if (mOnKeyboardToggleListener != null) {
                        if (!hasSentInitialAction || !isKeyboardShown) {
                            isKeyboardShown = true;
                            mOnKeyboardToggleListener.onKeyboardShown(initialValue - mRootView.getHeight());
                        }
                    }
                } else {
                    if (!hasSentInitialAction || isKeyboardShown) {
                        isKeyboardShown = false;
                        mRootView.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mOnKeyboardToggleListener != null)
                                    mOnKeyboardToggleListener.onKeyboardClosed();
                            }
                        });
                    }
                }
                hasSentInitialAction = true;
            }
        }
    }

    /**
     * 软键盘显示隐藏回调
     */
    public interface OnKeyboardToggleListener {
        void onKeyboardShown(int keyboardSize);

        void onKeyboardClosed();
    }
}
