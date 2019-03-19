package com.flyingkite.library.widget;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Library<T extends RecyclerView.Adapter> {
    public RecyclerView recyclerView;
    public T adapter;

    /**
     * Library use {@link LinearLayoutManager} on {@link LinearLayoutManager#VERTICAL}
     */
    public Library(RecyclerView view) {
        this(view, false);
    }

    /**
     * Library use {@link LinearLayoutManager},
     * {@link LinearLayoutManager#VERTICAL} (true) or
     * {@link LinearLayoutManager#HORIZONTAL} (false)
     *
     * @see LinearLayoutManager#LinearLayoutManager(Context, int, boolean)
     */
    public Library(RecyclerView view, boolean vertical) {
        recyclerView = view;
        int orient = vertical ? LinearLayoutManager.VERTICAL : LinearLayoutManager.HORIZONTAL;
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), orient, false));
    }

    /**
     * Library use {@link GridLayoutManager}<br/>
     * if rowSpan is positive -> <br/>
     *    rowSpan, with {@link LinearLayoutManager#VERTICAL}<br/>
     * if rowSpan is negative -> <br/>
     *    -rowSpan, with {@link LinearLayoutManager#HORIZONTAL}<br/>
     * @see GridLayoutManager#GridLayoutManager(Context, int, int, boolean)
     */
    public Library(RecyclerView view, int rowSpan) {
        recyclerView = view;
        int orient = rowSpan >= 0 ? LinearLayoutManager.VERTICAL : LinearLayoutManager.HORIZONTAL;
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), Math.abs(rowSpan), orient, false));
    }

    public Library(RecyclerView view, RecyclerView.LayoutManager layout) {
        recyclerView = view;
        recyclerView.setLayoutManager(layout);
    }

    public void setViewAdapter(T rvAdapter) {
        adapter = rvAdapter;
        recyclerView.setAdapter(rvAdapter);
    }
}
