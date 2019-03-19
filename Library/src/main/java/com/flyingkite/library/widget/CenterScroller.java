package com.flyingkite.library.widget;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * CenterScroller can move view to be at recyclerView's layout center.
 * {@link RecyclerView} uses {@link LinearLayoutManager} to determine scroll
 * {@link LinearLayoutManager#HORIZONTAL} or {@link LinearLayoutManager#VERTICAL} center.
 * If it is not LinearLayoutManager, we use the {@link LinearLayoutManager#HORIZONTAL} as default.
 */
public abstract class CenterScroller {
    private boolean isHorizontal = true; // Default for horizontal

    /**
     * @return recyclerView, to perform {@link #smoothScrollToCenter(int)} by {@link RecyclerView#smoothScrollBy(int, int)}
     */
    public abstract RecyclerView getRecyclerView();

    public final void smoothScrollToCenter(RecyclerView.ViewHolder holder) {
        smoothScrollToCenter(holder.getAdapterPosition());
    }

    public final void smoothScrollToCenter(int position) {
        RecyclerView parent = getRecyclerView();
        if (parent == null) return;

        isHorizontal = isHorizontal();

        // Peek the parent's current item positions to check the position item is visible or not
        // head = first partly visible, tail = last partly visible
        int n = parent.getChildCount();
        if (n == 0) return;

        ChildInfo head = new ChildInfo(parent, 0);
        ChildInfo tail = new ChildInfo(parent, n - 1);

        int anchor = (getLeft(parent) + getRight(parent)) / 2;

        // Determine the view is located at which position
        int viewAt;
        if (head.adapterPos <= position && position <= tail.adapterPos) {
            // Case : Target is (partly or completely) visible within recycler
            View target = parent.getChildAt(position - head.adapterPos);
            viewAt = (getLeft(target) + getRight(target)) / 2;
        } else {
            // Case : Target is at left outside or right outside of recycler
            boolean targetAtLeft = position < head.adapterPos;
            ChildInfo info = targetAtLeft ? head : tail;
            View ref = info.view;
            int distance = onPredictScrollOffset(targetAtLeft, info, position);

            viewAt = (getLeft(ref) + getRight(ref)) / 2 + distance;
        }

        int offset = viewAt - anchor + getLeft(parent);

        if (isHorizontal) {
            parent.smoothScrollBy(offset, 0); // It is used for horizontal layout manager
        } else {
            parent.smoothScrollBy(0, offset); // It is used for vertical layout manager
        }
    }

    protected final int getLeft(View view) {
        return isHorizontal ? view.getLeft() : view.getTop();
    }

    protected final int getRight(View view) {
        return isHorizontal ? view.getRight() : view.getBottom();
    }

    protected boolean isHorizontal() {
        RecyclerView parent = getRecyclerView();
        if (parent == null) return true;

        RecyclerView.LayoutManager lm = parent.getLayoutManager();
        if (lm instanceof LinearLayoutManager) {
            LinearLayoutManager llm = (LinearLayoutManager) lm;
            return llm.getOrientation() == LinearLayoutManager.HORIZONTAL;
        } else {
            return true;
        }
    }

    protected int onPredictScrollOffset(boolean targetAtLeft, @NonNull ChildInfo info, int position) {
        // Simplest case, all the item view has same width
        ScrollInfo s = new ScrollInfo(info.view);
        return s.widthAddMargins * (position - info.adapterPos);
    }

    private Rect getMargins(View v) {
        Rect margins = new Rect();
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        margins.left = lp.leftMargin;
        margins.top = lp.topMargin;
        margins.right = lp.rightMargin;
        margins.bottom = lp.bottomMargin;
        return margins;
    }

    public final class ScrollInfo {
        public final Rect margins;
        public final int widthAddMargins;
        public final boolean forHorizontal;

        public ScrollInfo(View v) {
            margins = getMargins(v);
            forHorizontal = isHorizontal();
            if (forHorizontal) {
                widthAddMargins = margins.left + v.getWidth() + margins.right;
            } else {
                widthAddMargins = margins.top + v.getHeight() + margins.bottom;
            }
        }

        @Override
        public String toString() {
            return "widthAddMargins = " + widthAddMargins + ", forHorizontal = " + forHorizontal + ", Margins = " + margins;
        }
    }

    public final class ChildInfo {
        public final View view;
        public final RecyclerView.ViewHolder viewHolder;
        public final int adapterPos;

        public ChildInfo(RecyclerView parent, int childIndex) {
            view = parent.getChildAt(childIndex);
            viewHolder = parent.getChildViewHolder(view);
            adapterPos = viewHolder.getAdapterPosition();
        }

        @Override
        public String toString() {
            return String.format("#%s, view = %s", adapterPos, view);
        }
    }
}
