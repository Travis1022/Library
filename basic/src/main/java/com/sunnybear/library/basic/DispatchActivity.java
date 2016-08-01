package com.sunnybear.library.basic;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.sunnybear.library.basic.fragmentstack.OnKeyDownCallback;
import com.sunnybear.library.basic.fragmentstack.StackManager;
import com.sunnybear.library.eventbus.EventBusHelper;
import com.sunnybear.library.util.KeyboardUtils;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.util.StringUtils;
import com.sunnybear.library.util.ToastUtils;

import butterknife.ButterKnife;

/**
 * 基础FragmentActivity,主管模组分发
 * Created by sunnybear on 16/1/29.
 */
public abstract class DispatchActivity<VB extends ViewModelBridge> extends AppCompatActivity
        implements Dispatch {
    protected static final String EVENT_HOME_CLICK = "home_click";//点击Home键的EventBus标签

    protected Context mContext;
    protected VB mViewBinder;
    private Bundle args;
    //Home键广播接受器
    private HomeBroadcastReceiver mBroadcastReceiver = new HomeBroadcastReceiver();

    StackManager manager;
    private OnKeyDownCallback callback;

    public void setOnKeyDownCallback(OnKeyDownCallback callback) {
        this.callback = callback;
    }

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinder = getViewBinder(this);
        int layoutId = mViewBinder.getLayoutId();
        if (layoutId == 0)
            throw new RuntimeException("找不到Layout资源,Activity初始化失败");
        setContentView(layoutId);
        //声明ButterKnife
        ButterKnife.bind(mViewBinder, this);

        mContext = this;
        args = getIntent().getExtras();

        mViewBinder.onBindView(args != null ? args : new Bundle());
        mViewBinder.onViewCreatedFinish();
        mViewBinder.addListener();
        //注册EventBus
        EventBusHelper.register(this);

        ActivityManager.getInstance().addActivity(this);

        //监听Home键
        registerReceiver(mBroadcastReceiver, Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        //分发model到Presenter
        manager = new StackManager(this);
        dispatchModelOnCreate(savedInstanceState);
    }

    @Override
    protected final void onStart() {
        super.onStart();
        dispatchModelOnStart();
        //loading加载框初始化
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        EventBusHelper.unregister(this);
    }

    @Override
    public void finish() {
        super.finish();
        ActivityManager.getInstance().removeCurrentActivity();
    }

    /**
     * 注册广播接收器
     *
     * @param receiver 广播接收器
     * @param actions  广播类型
     */
    protected void registerReceiver(BroadcastReceiver receiver, String... actions) {
        IntentFilter filter = new IntentFilter();
        for (String action : actions) {
            filter.addAction(action);
        }
        super.registerReceiver(receiver, filter);
    }

    /**
     * 跳转Activity
     *
     * @param targetClass 目标Activity类型
     * @param args        传递参数
     */
    protected void startActivity(Class<? extends Activity> targetClass, Bundle args) {
        Intent intent = new Intent(mContext, targetClass);
        if (args != null)
            intent.putExtras(args);
        super.startActivity(intent);
    }

    /**
     * 跳转Activity
     *
     * @param targetClass 目标Activity类型
     */
    protected void startActivity(Class<? extends Activity> targetClass) {
        startActivity(targetClass, null);
    }

    /**
     * 跳转Service
     *
     * @param targetClass 目标Activity类型
     * @param args        传递参数
     */
    protected void startService(Class<? extends Service> targetClass, Bundle args) {
        Intent intent = new Intent(mContext, targetClass);
        if (args != null)
            intent.putExtras(args);
        super.startService(intent);
    }

    /**
     * 跳转Service
     *
     * @param targetClass 目标Activity类型
     */
    protected void startService(Class<? extends Service> targetClass) {
        startService(targetClass, null);
    }

    /**
     * 添加fragment
     *
     * @param targetClass 目标fragmentClass
     * @param args        传递参数
     */
    protected void addFragment(Class<? extends Fragment> targetClass, Bundle args) {
        Fragment fragment = Fragment.instantiate(mContext, targetClass.getName(), args);
        if (!(fragment instanceof DispatchFragment))
            throw new RuntimeException("添加的Fragment必须是DispatchFragment子类");
        manager.setFragment((DispatchFragment) fragment);
    }

    /**
     * 添加fragment
     *
     * @param targetClass 目标fragmentClass
     */
    protected void addFragment(Class<? extends Fragment> targetClass) {
        addFragment(targetClass, null);
    }

    /**
     * 设置动画
     *
     * @param nextIn  下一页进入动画
     * @param nextOut 下一页动画
     * @param quitIn  当前页面的动画
     * @param quitOut 退出当前页面的动画
     */
    protected void setAnim(@AnimRes int nextIn, @AnimRes int nextOut, @AnimRes int quitIn, @AnimRes int quitOut) {
        manager.setAnim(nextIn, nextOut, quitIn, quitOut);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev != null && ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (isShouldHideInput(view, ev))
                KeyboardUtils.closeKeyboard(mContext, view);
            return super.dispatchTouchEvent(ev);
        }
        //必不可少,否则所有的组件都不会有TouchEvent事件了
        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                manager.onBackPressed();
                return true;
            default:
                if (callback != null)
                    return callback.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 是否隐藏软键盘
     *
     * @param view  对应View
     * @param event 事件
     */
    private boolean isShouldHideInput(View view, MotionEvent event) {
        if (view != null && (view instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前位置
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + view.getHeight();
            int right = left + view.getWidth();
            return !(event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }

    private long exitTime = 0;

    /**
     * 双击退出app
     *
     * @param exit 退出间隔时间(毫秒数)
     */
    protected void exit(long exit) {
        if (System.currentTimeMillis() - exitTime > exit) {
            ToastUtils.showToastLong(mContext, "再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            ActivityManager.getInstance().killProcess(mContext.getApplicationContext());
        }
    }

    /**
     * 双击退出app
     */
    protected void exit() {
        exit(2000);
    }

    /**
     * 监听Home键广播接收器
     */
    private static class HomeBroadcastReceiver extends BroadcastReceiver {
        private String SYSTEM_REASON = "reason";
        private String SYSTEM_HOME_KEY = "homekey";
        private String SYSTEM_HOME_KEY_LONG = "recentapp";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (StringUtils.equals(action, Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_REASON);
                if (StringUtils.equals(reason, SYSTEM_HOME_KEY))//表示点击了Home键,程序到后台
//                    EventBusHelper.post("点击Home,程序退到后台", EVENT_HOME_CLICK);
                    Logger.i("点击Home键");
                else if (StringUtils.equals(reason, SYSTEM_HOME_KEY_LONG))//表示长按Home键,显示最近使用的程序列表
                    Logger.i("长按Home键, 显示最近使用的程序列表");
            }
        }
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
    protected abstract void dispatchModelOnStart();
}
