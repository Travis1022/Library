package com.sunnybear.library.widget.tag;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sunnybear.library.widget.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 标签适配器
 * Created by chenkai.gu on 2016/7/11.
 */
abstract class TagAdapter<T> extends BaseAdapter implements OnInitSelectedPosition {
    private final Context mContext;
    private final List<T> mDataList;
    private TextView textView;

    private int mBackgroundStyle;
    private ColorStateList mTextColor;
    private int defalutSelected;

    public TagAdapter(Context context, int backgroundStyle, ColorStateList textColor) {
        this(context, backgroundStyle, textColor, -1);
    }

    public TagAdapter(Context context, int backgroundStyle, ColorStateList textColor, int defalutSelected) {
        this.mContext = context;
        mDataList = new ArrayList<>();
        mBackgroundStyle = backgroundStyle;
        mTextColor = textColor;
        this.defalutSelected = defalutSelected;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.tag_item, null);
        textView = (TextView) view.findViewById(R.id.tv_tag);
        textView.setBackgroundResource(mBackgroundStyle);
        textView.setTextColor(mTextColor);
        T t = mDataList.get(position);
        if (t instanceof String)
            textView.setText((String) t);
        else
            textView.setText(getTagText(t));
        return view;
    }

    /**
     * 自定义加载数据
     *
     * @param t 数据源
     */
    public abstract String getTagText(T t);

    public void onlyAddAll(List<T> datas) {
        mDataList.addAll(datas);
        notifyDataSetChanged();
    }

    public void clearAndAddAll(List<T> datas) {
        mDataList.clear();
        onlyAddAll(datas);
    }

    public List<T> getData() {
        return mDataList;
    }

    @Override
    public boolean isSelectedPosition(int position) {
        if (defalutSelected != -1 && position == defalutSelected)
            return true;
        return false;
    }
}
