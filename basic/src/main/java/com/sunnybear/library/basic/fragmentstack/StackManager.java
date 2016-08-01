package com.sunnybear.library.basic.fragmentstack;

import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.sunnybear.library.basic.DispatchFragment;
import com.sunnybear.library.basic.R;
import com.sunnybear.library.util.StringUtils;

/**
 * Fragment任务栈管理
 * Created by sunnybear on 16/2/1.
 */
public class StackManager implements HandlerFragment {
    public static final int STANDARD = 0X11;
    public static final int SINGLE_TOP = 0X22;
    public static final int SINGLE_TASK = 0X33;
    public static final int SINGLE_INSTANCE = 0X44;

    public static boolean isFirstClose = true;

    private FragmentStack stack;
    private final FragmentActivity context;
    private long CLICK_SPACE = 300;
    private long currentTime;
    private int nextIn;
    private int nextOut;
    private int quitIn;
    private int quitOut;
    private Animation next_in;
    private Animation next_out;
    private int dialog_in;
    private int dialog_out;

    /**
     * 设置防止重点击时间,默认300ms
     *
     * @param CLICK_SPACE 防止重点击时间
     */
    public void setCLICK_SPACE(long CLICK_SPACE) {
        this.CLICK_SPACE = CLICK_SPACE;
    }

    public StackManager(FragmentActivity context) {
        stack = new FragmentStack();
        stack.setHandlerFragmentListener(this);
        this.context = context;
    }

    /**
     * 设置最底部的Fragment
     *
     * @param fragment fragment实例
     */
    public void setFragment(@NonNull DispatchFragment fragment) {
        String fragmentName = fragment.getClass().getName();
        FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragment_container, fragment, fragmentName)
                .addToBackStack(fragmentName).commit();
        stack.putStandard(fragment);
    }

    /**
     * 跳转到指定的Fragment
     *
     * @param from 当前Fragment
     * @param to   目标Fragment
     */
    public void switchFragment(@NonNull Fragment from, @NonNull Fragment to) {
        String fragmentName = to.getClass().getName();
        if (System.currentTimeMillis() - currentTime > CLICK_SPACE) {
            currentTime = System.currentTimeMillis();
            FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
            if (nextIn != 0 && nextOut != 0 && quitIn != 0 && quitOut != 0)
                transaction.setCustomAnimations(nextIn, nextOut)
                        .add(R.id.fragment_container, to, to.getClass().getName())
                        .addToBackStack(fragmentName)
                        .hide(from).commit();
            else
                transaction.add(R.id.fragment_container, to, to.getClass().getName())
                        .addToBackStack(fragmentName)
                        .hide(from).commit();
        }
    }

    /**
     * 设置切换动画
     *
     * @param nextIn  下一页进入动画
     * @param nextOut 下一页动画
     * @param quitIn  当前页面的动画
     * @param quitOut 退出当前页面的动画
     */
    public void setAnim(@AnimRes int nextIn, @AnimRes int nextOut, @AnimRes int quitIn, @AnimRes int quitOut) {
        this.nextIn = nextIn;
        this.nextOut = nextOut;
        this.quitIn = quitIn;
        this.quitOut = quitOut;
        next_in = AnimationUtils.loadAnimation(context, quitIn);
        next_out = AnimationUtils.loadAnimation(context, quitOut);
    }

    /**
     * 跳转到指定的Fragment
     *
     * @param from      当前Fragment
     * @param to        目标Fragment
     * @param args      传递参数
     * @param stackMode 任务栈模式`
     */
    public void switchFragment(DispatchFragment from, DispatchFragment to, Bundle args, @StackMode int stackMode) {
        if (args != null) to.setArguments(args);
        switch (stackMode) {
            case STANDARD:
                stack.putStandard(to);
                switchFragment(from, to);
                break;
            case SINGLE_TOP:
                if (!stack.putSingleTop(to, args))
                    switchFragment(from, to);
                else
                    closeOtherFragment(to);
                break;
            case SINGLE_TASK:
                if (!stack.putSingleTask(to))
                    switchFragment(from, to);
                break;
            case SINGLE_INSTANCE:
                stack.putSingleInstance(to);
                switchFragment(from, to);
                break;
        }
    }

    /**
     * 打开Fragment
     *
     * @param from 当前Fragment
     * @param to   目标Fragment
     */
    public void openFragment(DispatchFragment from, DispatchFragment to) {
        switchFragment(from, to, null, STANDARD);
    }

    /**
     * 打开Fragment
     *
     * @param from 当前Fragment
     * @param to   目标Fragment
     * @param args 传递参数
     */
    public void openFragment(DispatchFragment from, DispatchFragment to, Bundle args) {
        switchFragment(from, to, args, STANDARD);
    }

    /**
     * 跳转到Fragment时,不隐藏当前Fragment
     *
     * @param to 目标Fragment
     */
    public void dialogFragment(Fragment to) {
        FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
        if (!to.isAdded())
            if (dialog_in != 0 && dialog_out != 0)
                transaction.setCustomAnimations(dialog_in, dialog_out)
                        .add(R.id.fragment_container, to, to.getClass().getName()).commit();
            else
                transaction.add(R.id.fragment_container, to, to.getClass().getName()).commit();
    }

    /**
     * 设置动画
     *
     * @param dialog_in  dialog进入动画
     * @param dialog_out dialog退出动画
     */
    public void setDialogAnim(@AnimRes int dialog_in, @AnimRes int dialog_out) {
        this.dialog_in = dialog_in;
        this.dialog_out = dialog_out;
    }

    /**
     * 关闭Fragment
     *
     * @param fragment 被关闭的Fragment
     */
    public void closeFragment(Fragment fragment) {
        FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
        transaction.remove(fragment).commit();
    }

    /**
     * 关闭Fragment
     *
     * @param tag 被关闭的Fragment标签
     */
    public void closeFragment(String tag) {
        Fragment fragment = context.getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            closeFragment(fragment);
            context.getSupportFragmentManager().popBackStackImmediate(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    /**
     * 关闭当前的Fragment
     */
    public void close() {
        context.getSupportFragmentManager().popBackStack();
    }

    /**
     * 关闭所有的Fragment
     */
    public void closeAllFragment() {
        int backStackCount = context.getSupportFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < backStackCount; i++) {
            int backStackId = context.getSupportFragmentManager().getBackStackEntryAt(i).getId();
            context.getSupportFragmentManager().popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    /**
     * 关闭其他的Fragment
     *
     * @param fragment 不要关闭的Fragment
     */
    public void closeOtherFragment(Fragment fragment) {
        int backStackCount = context.getSupportFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < backStackCount; i++) {
            String backStackName = context.getSupportFragmentManager().getBackStackEntryAt(i).getName();
            if (!StringUtils.equals(backStackName, fragment.getClass().getName()))
                context.getSupportFragmentManager().popBackStack(backStackName, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void onBackPressed() {
        Fragment[] last = stack.getLast();
        final DispatchFragment from = (DispatchFragment) last[0];
        final DispatchFragment to = (DispatchFragment) last[1];

        if (from != null && to != null) {
            FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
            transaction.show(to).commit();
            to.dispatchModelOnStart();//返回时重新回调dispatchModelOnStart方法
        }
        if (from!=null){
            View fromView = from.getView();
            if (fromView != null && next_out != null) {
                fromView.startAnimation(next_out);
                next_out.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        stack.onBackPressed();
                        closeFragment(from);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            } else {
                stack.onBackPressed();
                closeFragment(from);
            }
        }
        if (to != null) {
            View toView = to.getView();
            if (toView != null && next_in != null)
                toView.startAnimation(next_in);
        } else {
            closeAllFragment();
            context.finish();
        }
    }

    @Override
    public void show(DispatchFragment fragment) {
        FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
        transaction.show(fragment).commit();
        View view = fragment.getView();
        if (view != null && next_in != null)
            view.startAnimation(next_in);
    }

    @Override
    public void close(final DispatchFragment fragment) {
        if (isFirstClose) {
            View view = fragment.getView();
            if (view != null && next_out != null) {
                view.startAnimation(next_out);
                next_out.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        closeFragment(fragment);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            } else {
                closeFragment(fragment);
            }
            isFirstClose = false;
        } else {
            closeFragment(fragment);
        }
    }

    /**
     * 任务栈模式
     * Created by sunnybear on 16/2/1.
     */
    @IntDef({STANDARD, SINGLE_TOP, SINGLE_TASK, SINGLE_INSTANCE})
    public @interface StackMode {
    }
}
