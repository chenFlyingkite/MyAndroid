package com.flyingkite.library.widget;

import android.view.View;
import android.view.ViewGroup;

import com.flyingkite.core.util.ViewCreatorUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import flyingkite.collection.ListUtil;

public class PGAdapter<T> extends PagerAdapter implements ViewCreatorUtil {

    protected ViewGroup parent;

    private List<T> dataList = new ArrayList<>();

    public void setDataList(List<T> items) {
        dataList = ListUtil.nonNull(items);
    }

    @LayoutRes
    public int pageLayoutId(int position) {
        return 0;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        if (parent == null) {
            parent = container;
        }
        View v = inflateView(container, pageLayoutId(position));
        onViewInflated(v, position);
        container.addView(v);
        onViewAdded(v, position);
        return v;
    }

    public void onViewInflated(View page, int position) {

    }

    public void onViewAdded(View page, int position) {

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (object instanceof View) {
            View v = (View) object;
            container.removeView(v);
        }
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    //TODO
    public T itemOf(int index) {
        return ListUtil.itemOf(dataList, index);
    }
}