package com.sunnybear.library.basic.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.sunnybear.library.basic.R;
import com.sunnybear.library.eventbus.EventBusHelper;

import butterknife.ButterKnife;

/**
 * 基础dialog封装
 * Created by chenkai.gu on 2016/6/23.
 */
public abstract class BasicDialog extends Dialog {
    protected Context mContext;

    private View rootView;

    public BasicDialog(Context context) {
        this(context, false);
    }

    public BasicDialog(Context context, boolean cancelable) {
        super(context, R.style.custom_dialog);
        this.mContext = context;
        rootView = LayoutInflater.from(context).inflate(getLayoutId(), null);
        setContentView(rootView);
        ButterKnife.bind(this);
        setCancelable(cancelable);
    }

    @Override
    public void show() {
        EventBusHelper.register(this);
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        EventBusHelper.unregister(this);
    }

    /**
     * 设置布局id
     *
     * @return 布局id
     */
    public abstract int getLayoutId();
}
