package com.flyingkite.library.widget;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyingkite.library.util.ListUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple {@link RecyclerView RecyclerView} Adapter using the List as data set to show content.
 * RVAdapter is the abbreviation for RecyclerViewAdapter.<br/>
 *
 * We abstract on the method {@link RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int) onCreateViewHolder} since proper creation of {@link RecyclerView.ViewHolder ViewHolder} is your turn. :)
 * <p>
 * To use the template type in inner or static classes, see <br/>
 * https://www.safaribooksonline.com/library/view/java-generics-and/0596527756/ch04s03.html
 * </p>
 *
 * @param <T> The data type for {@link List List}
 * @param <VH> Same as {@link RecyclerView.Adapter RecyclerView.Adapter}, A class that extends ViewHolder that will be used by the adapter.
 * @param <TListener> The listener type for {@link RecyclerView.ViewHolder ViewHolder}
 */
public abstract class RVAdapter<T,
        VH extends RecyclerView.ViewHolder,
        TListener extends RVAdapter.ItemListener<T, VH>>
        extends RecyclerView.Adapter<VH> {
    /**
     * Item listener for RVAdapter(RecyclerViewAdapter)
     */
    public interface ItemListener<M, MVH> {
        void onClick(M item, MVH holder, int position);
    }
    // To use the template type in inner or static classes, see
    // https://www.safaribooksonline.com/library/view/java-generics-and/0596527756/ch04s03.html

    // Members & setters
    protected List<T> dataList = new ArrayList<>();
    protected TListener onItem;

    // Center Scroller
    protected WeakReference<RecyclerView> recycler;
    protected boolean autoScroll = true;
    protected CenterScroller scroller = new CenterScroller() {
        @Override
        public RecyclerView getRecyclerView() {
            return recycler == null ? null : recycler.get();
        }
    };

    //region Member setters
    public RVAdapter<T, VH, TListener> setDataList(List<T> list) {
        dataList = nonNull(list);
        return this;
    }

    public RVAdapter<T, VH, TListener> setItemListener(TListener listener) {
        onItem = listener;
        return this;
    }

    public void setAutoScroll(boolean toCenter) {
        autoScroll = toCenter;
    }

    //endregion

    // We left creation to be abstract
    /*
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }
    */

    @Override
    public void onBindViewHolder(VH holder, int position) {
        T item = itemOf(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                if (autoScroll) {
                    scroller.smoothScrollToCenter(pos);
                }

                onClickItem(item, holder);
                if (onItem != null) {
                    onItem.onClick(item, holder, pos);
                }
            }
        });
    }


    protected void initCenterScroller(ViewGroup vg) {
        if (vg instanceof RecyclerView) {
            recycler = new WeakReference<>((RecyclerView) vg);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    //region Utility methods
    protected View inflateView(ViewGroup parent, @LayoutRes int layoutId) {
        if (parent == null) {
            return null;
        } else {
            return LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        }
    }

    /**
     * Called when {@link RecyclerView.ViewHolder#itemView itemView} is clicked.
     */
    protected void onClickItem(T item, VH holder) {

    }

    public T itemOf(int index) {
        return ListUtil.itemOf(dataList, index);
    }

    protected <Z> List<Z> nonNull(List<Z> list) {
        return ListUtil.nonNull(list);
    }
    //endregion
}
