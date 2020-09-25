package com.flyingkite.library.recyclerview;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// CenterScroller to move view to be at recyclerView's horizontal center
public abstract class CenterScroller {

    public abstract RecyclerView getRecyclerView();

    // position = holder.getAdapterPosition()
    public final void smoothScrollToCenter(int position) {
        scrollCenter(position, true);
    }

    public final void scrollToCenter(int position) {
        scrollCenter(position, false);
    }

    private void scrollCenter(int position, boolean smooth) {
        scrollToPercent(position, 50, 50, smooth);
    }

    // position = holder.getAdapterPosition()
    public final void smoothScrollToLeft(int position) {
        scrollLeft(position, true);
    }

    public final void scrollToLeft(int position) {
        scrollLeft(position, false);
    }

    private void scrollLeft(int position, boolean smooth) {
        scrollToPercent(position, 0, 0, smooth);
    }

    public void scrollToPercent(int position, int parentPercent, int childPercent, boolean smooth) {
        int[] xy = evalOffset(position, parentPercent, childPercent);
        if (xy == null) return;
        RecyclerView parent = getRecyclerView();
        if (parent == null) return;

        if (smooth) {
            parent.smoothScrollBy(xy[0], xy[1]);
        } else {
            parent.scrollBy(xy[0], xy[1]);
        }
    }

    private int anchorAt(View v, int percent) {
        if (v == null) return 0;
        int l = v.getLeft();
        int r = v.getRight();
        return l + (r - l) * percent / 100;
    }

    // position = 0 ~ n,
    // parentPercent = anchor of recycler
    // childPercent = anchor of view holder
    // evaluate the offsets to make parent's anchor and child's anchor
    private int[] evalOffset(int position, int parentPercent, int childPercent) {
        RecyclerView parent = getRecyclerView();
        if (parent == null) return null;

        // Peek the parent's current item positions to check the position item is visible or not
        // head = first partly visible, tail = last partly visible
        int n = parent.getChildCount();
        ChildInfo head = new ChildInfo(parent, 0);
        ChildInfo tail = new ChildInfo(parent, n - 1);

        int anchor = anchorAt(parent, parentPercent);

        // Determine the view is located at which position
        int viewAt;
        if (head.adapterPos <= position && position <= tail.adapterPos) {
            // Case : Target is (partly or completely) visible within recycler
            View target = parent.getChildAt(position - head.adapterPos);
            if (target == null) return null;
            viewAt = anchorAt(target, childPercent);
        } else {
            // Case : Target is at left outside or right outside of recycler
            boolean targetAtLeft = position < head.adapterPos;
            ChildInfo info = targetAtLeft ? head : tail;
            View ref = info.view;
            int distance = onPredictScrollOffset(targetAtLeft, info, position);

            viewAt = anchorAt(ref, childPercent) + distance;
        }

        int offset = viewAt - anchor + parent.getLeft();
        return new int[]{offset, 0};
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

        public ScrollInfo(View v) {
            margins = getMargins(v);
            widthAddMargins = margins.left + v.getWidth() + margins.right;
        }

        @Override
        public String toString() {
            return "widthAddMargins = " + widthAddMargins + ", Margins = " + margins;
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
