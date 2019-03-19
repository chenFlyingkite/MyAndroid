package com.flyingkite.library.widget;

import android.view.View;
import android.view.ViewGroup;

import com.flyingkite.library.util.ListUtil;
import com.flyingkite.library.util.ViewCreatorUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

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
        extends RecyclerView.Adapter<VH> implements ViewCreatorUtil {

    /**
     * Item listener for RVAdapter(RecyclerViewAdapter)
     */
    public interface ItemListener<M, MVH> {
        default void onClick(M item, MVH holder, int position) {}
    }
    // To use the template type in inner or static classes, see
    // https://www.safaribooksonline.com/library/view/java-generics-and/0596527756/ch04s03.html

    // Members & setters
    protected List<T> dataList = new ArrayList<>();
    protected TListener onItem;

    // Center Scroller
    protected WeakReference<RecyclerView> recycler;
    protected boolean autoScroll;
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

                onWillClickItem(item, holder);
                if (onItem != null) {
                    onItem.onClick(item, holder, pos);
                }
                onDidClickItem(item, holder);
            }
        });
    }

    protected void initCenterScroller(ViewGroup vg) {
        if (vg instanceof RecyclerView) {
            recycler = new WeakReference<>((RecyclerView) vg);
        }
    }

    public CenterScroller getScroller() {
        return scroller;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    //region Utility methods

    /**
     * Called when {@link RecyclerView.ViewHolder#itemView itemView} is clicked.
     * Before notify listener
     */
    protected void onWillClickItem(T item, VH holder) {

    }

    /**
     * Called when {@link RecyclerView.ViewHolder#itemView itemView} is clicked.
     * After notify listener
     */
    protected void onDidClickItem(T item, VH holder) {

    }

    public T itemOf(int index) {
        return ListUtil.itemOf(dataList, index);
    }

    protected <Z> List<Z> nonNull(List<Z> list) {
        return ListUtil.nonNull(list);
    }
    //endregion
}
