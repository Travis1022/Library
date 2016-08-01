package com.sunnybear.library.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.io.Serializable;

import butterknife.ButterKnife;

/**
 * 重新封装ViewHolder
 * Created by guchenkai on 2015/11/9.
 */
public abstract class BasicViewHolder<Item extends Serializable> extends RecyclerView.ViewHolder {
    protected Context mContext;

    public BasicViewHolder(Context context, View itemView) {
        super(itemView);
        mContext = context;
        ButterKnife.bind(this, itemView);
    }

    public BasicViewHolder(View itemView) {
        this(null, itemView);
    }

    /**
     * 向itemView上绑定数据
     *
     * @param item     item数据
     * @param position 该条数据的位置
     */
    public abstract void onBindItem(Item item, int position);
}
