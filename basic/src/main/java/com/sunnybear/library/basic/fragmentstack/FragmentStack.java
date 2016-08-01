package com.sunnybear.library.basic.fragmentstack;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.sunnybear.library.basic.DispatchFragment;
import com.sunnybear.library.util.StringUtils;

import java.util.ArrayList;

/**
 * Fragment任务栈
 * Created by sunnybear on 16/2/1.
 */
public class FragmentStack {
    private ArrayList<ArrayList<DispatchFragment>> stackList;
    private ArrayList<DispatchFragment> stack;
    private HandlerFragment listener;

    public FragmentStack() {
        stackList = new ArrayList<>();
        if (stack == null)
            stack = new ArrayList<>();
        stackList.add(stack);
    }

    /**
     * 标准模式,直接添加到当前任务栈
     *
     * @param fragment 被添加的Fragment
     */
    public void putStandard(DispatchFragment fragment) {
        stackList.get(stackList.size() - 1).add(fragment);
    }

    /**
     * SingleTop模式,如果上面没有创建,直接添加到任务栈
     *
     * @param fragment 被添加的Fragment
     * @return 是否包含当前Fragment实例
     */
    public boolean putSingleTop(DispatchFragment fragment, Bundle args) {
        ArrayList<DispatchFragment> lastList = stackList.get(stackList.size() - 1);
        if (lastList.isEmpty()) {
            lastList.add(fragment);
            return false;
        } else {
//            DispatchFragment last = lastList.get(lastList.size() - 1);
            if (matchName(lastList, fragment)) {
                fragment.onNewIntent(args);
                return true;
            } else {
                lastList.add(fragment);
                return false;
            }
        }
    }

    /**
     * SingleTask模式,如果上面没有创建,直接添加到任务栈
     *
     * @param fragment 被添加的Fragment
     * @return 是否包含当前Fragment实例
     */
    public boolean putSingleTask(DispatchFragment fragment) {
        boolean isClear = false;
        ArrayList<DispatchFragment> lastList = stackList.get(stackList.size() - 1);
        if (lastList.isEmpty()) {
            lastList.add(fragment);
        } else {
            int tempIndex = 0;
            for (int x = 0; x < lastList.size() - 1; x++) {
                if (StringUtils.equals(lastList.get(x).getClass().getName(), fragment.getClass().getName())) {
                    isClear = true;//清除所有实例
                    tempIndex = x;
                    break;
                }
            }
            if (!isClear) {
                lastList.add(fragment);
            } else {
                if (listener != null) {
                    listener.show(lastList.get(tempIndex));
                    StackManager.isFirstClose = true;
                    for (int i = lastList.size() - 1; i > tempIndex; i--) {
                        listener.close(lastList.get(i));
                    }
                    for (int j = lastList.size() - 1; j > tempIndex; j--) {
                        lastList.remove(j);
                    }
                }
            }
        }
        return isClear;
    }

    /**
     * SingleInstance模式,创建一个新的任务栈
     *
     * @param fragment 被添加的Fragment
     */
    public void putSingleInstance(DispatchFragment fragment) {
        ArrayList<DispatchFragment> fragments = new ArrayList<>();
        fragments.add(fragment);
        stackList.add(fragments);
    }

    public void onBackPressed() {
        int i = stackList.size() - 1;
        if (i >= 0) {
            ArrayList<DispatchFragment> lastStack = stackList.get(i);
            if (lastStack != null && (!lastStack.isEmpty())) {
                lastStack.remove(lastStack.size() - 1);
                if (lastStack.isEmpty()) stackList.remove(lastStack);
            } else {
                stackList.remove(lastStack);
            }
        } else {
            stackList.clear();
        }
    }

    public void setHandlerFragmentListener(HandlerFragment handlerFragmentListener) {
        this.listener = handlerFragmentListener;
    }

    protected Fragment[] getLast() {
        Fragment[] fragments = new Fragment[2];
        boolean hasFirst = false;
        for (int x = stackList.size() - 1; x >= 0; x--) {
            ArrayList<DispatchFragment> list = stackList.get(x);
            if (list != null && (!list.isEmpty())) {
                if (hasFirst) {
                    fragments[1] = list.get(list.size() - 1);
                    break;
                } else {
                    hasFirst = true;
                    fragments[0] = list.get(list.size() - 1);
                    if (list.size() > 1) fragments[1] = list.get(list.size() - 2);
                }
            }
        }
        return fragments;
    }

    /**
     * 匹配fragment名
     *
     * @param fragments Fragment组
     * @param fragment  fragment
     * @return 是否有匹配
     */
    private boolean matchName(ArrayList<DispatchFragment> fragments, DispatchFragment fragment) {
        boolean isMatch = false;
        for (DispatchFragment f : fragments) {
            isMatch = StringUtils.equals(f.getClass().getName(), fragment.getClass().getName());
            if (isMatch) break;
        }
        return isMatch;
    }
}
