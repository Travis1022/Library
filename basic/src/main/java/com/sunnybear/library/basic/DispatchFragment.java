package com.sunnybear.library.basic;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sunnybear.library.basic.fragmentstack.OnNewIntent;
import com.sunnybear.library.basic.fragmentstack.StackManager;
import com.sunnybear.library.eventbus.EventBusHelper;

import butterknife.ButterKnife;

/**
 * 基础Fragment,主管模组分发
 * Created by sunnybear on 16/1/29.
 */
public abstract class DispatchFragment<VB extends ViewModelBridge> extends Fragment implements Dispatch, OnNewIntent {
    protected Context mContext;
    protected VB mViewBinder;

    private Bundle args;
    private View mFragmentView = null;

    @Override
    public final void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        FragmentActivity activity = (FragmentActivity) context;
        if (!(activity instanceof DispatchActivity))
            throw new RuntimeException("DispatchFragment必须依赖DispatchActivity");
        //注册EventBus
        EventBusHelper.register(this);
    }

    @Override
    public final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        args = getArguments();
    }

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewBinder = getViewBinder(mContext);
        int layoutId = mViewBinder.getLayoutId();
        if (layoutId == 0)
            throw new RuntimeException("找不到Layout资源,Fragment初始化失败");
        mFragmentView = inflater.inflate(layoutId, container, false);
        ViewGroup parent = (ViewGroup) mFragmentView.getParent();
        if (parent != null)
            parent.removeView(mFragmentView);
        ButterKnife.bind(mViewBinder, mFragmentView);
        return mFragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewBinder.onBindView(args != null ? args : new Bundle());
        mViewBinder.onViewCreatedFinish();
        mViewBinder.addListener();

        dispatchModelOnCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        dispatchModelOnStart();
        //loading加载框初始化
    }

    @Override
    public final void onDestroyView() {
        super.onDestroyView();
        if (mFragmentView != null)
            ((ViewGroup) mFragmentView.getParent()).removeView(mFragmentView);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBusHelper.unregister(this);
    }

    /**
     * 获得根Activity
     *
     * @return 根activity实例
     */
    public DispatchActivity getRoot() {
        FragmentActivity activity = getActivity();
        if (activity instanceof DispatchActivity)
            return (DispatchActivity) activity;
        else
            throw new ClassCastException("this activity mast be extends DispatchActivity");
    }

    /**
     * 切换新的Fragment
     *
     * @param fragmentClass 新Fragment类型
     * @param args          传递参数
     */
    public void open(@NonNull Class<? extends DispatchFragment> fragmentClass, Bundle args, int stackMode) {
        DispatchFragment fragment = (DispatchFragment) Fragment.instantiate(mContext, fragmentClass.getName(), args);
        getRoot().manager.switchFragment(this, fragment, args, stackMode);
    }

    /**
     * 切换新的Fragment
     *
     * @param fragmentClass 新Fragment类型
     * @param args          传递参数
     */
    public void open(@NonNull Class<? extends DispatchFragment> fragmentClass, Bundle args) {
        open(fragmentClass, args, StackManager.STANDARD);
    }

    /**
     * 切换新的Fragment
     *
     * @param fragmentClass 新Fragment类型
     * @param stackMode     任务栈模式
     */
    public void open(@NonNull Class<? extends DispatchFragment> fragmentClass, int stackMode) {
        open(fragmentClass, null, stackMode);
    }

    /**
     * 切换新的Fragment
     *
     * @param fragmentClass 新Fragment类型
     */
    public void open(@NonNull Class<? extends DispatchFragment> fragmentClass) {
        open(fragmentClass, null, StackManager.STANDARD);
    }

    /**
     * 设置动画
     *
     * @param nextIn  下一页进入动画
     * @param nextOut 下一页动画
     * @param quitIn  当前页面的动画
     * @param quitOut 退出当前页面的动画
     */
    public void setAnim(@AnimRes int nextIn, @AnimRes int nextOut, @AnimRes int quitIn, @AnimRes int quitOut) {
        getRoot().manager.setAnim(nextIn, nextOut, quitIn, quitOut);
    }

    /**
     * 关闭当前fragment
     */
    public void close() {
        getRoot().manager.close(this);
    }

    /**
     * 关闭指定的Fragment
     *
     * @param fragment fragment实例
     */
    public void close(DispatchFragment fragment) {
        getRoot().manager.close(fragment);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden)
            onNowHidden();
        else
            onNextShow();
    }

    /**
     * 当前页面暂停交互时的回调
     */
    public void onNowHidden() {

    }

    /**
     * 当前页面重启交互时的回调
     */
    public void onNextShow() {

    }

    @Override
    public void onNewIntent(Bundle args) {

    }

    /**
     * 设置Presenter实例,绑定View
     *
     * @param context 上下文
     */
    protected abstract VB getViewBinder(Context context);

    /**
     * 分发model到ViewBinder
     */
    protected abstract void dispatchModelOnCreate(@Nullable Bundle savedInstanceState);

    /**
     * 分发model到ViewBinder(onStart时调用)
     */
    public abstract void dispatchModelOnStart();
}
