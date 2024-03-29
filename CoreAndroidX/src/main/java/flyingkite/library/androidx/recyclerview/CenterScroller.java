package flyingkite.library.androidx.recyclerview;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// CenterScroller to move view to be at recyclerView's horizontal / vertical center
// For simplifying our terminology, we define
// Left = left in horizontal, top in vertical
// Right = right in horizontal, bottom in vertical
// Center = center in horizontal and vertical
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

    // Core method, alignment by percent line
    public void scrollToPercent(int position, int parentPercent, int childPercent, boolean smooth) {
        scrollToPercent(position, parentPercent, childPercent, parentPercent, childPercent, smooth);
    }

    // Core method
    public void scrollToPercent(int position, int parentPercentX, int childPercentX, int parentPercentY, int childPercentY, boolean smooth) {
        int[] xy = evalOffset(position, parentPercentX, childPercentX, parentPercentY, childPercentY);
        if (xy == null) return;
        RecyclerView parent = getRecyclerView();
        if (parent == null) return;

        if (smooth) {
            parent.smoothScrollBy(xy[0], xy[1]);
        } else {
            parent.scrollBy(xy[0], xy[1]);
        }
    }

    private int anchorAtLR(View v, int percent) {
        if (v == null) return 0;
        return interpolate(v.getLeft(), v.getRight(), percent);
    }

    private int anchorAtTB(View v, int percent) {
        if (v == null) return 0;
        return interpolate(v.getTop(), v.getBottom(), percent);
    }

    private int interpolate(int l, int r, int p) {
        return l + (r - l) * p / 100;
    }

    // position = 0 ~ n,
    // parentPercent = anchor of recycler
    // childPercent = anchor of view holder
    // evaluate the offsets to make parent's anchor and child's anchor
    private int[] evalOffset(int position, int parentPercentX, int childPercentX, int parentPercentY, int childPercentY) {
        RecyclerView parent = getRecyclerView();
        if (parent == null) return null;

        // Peek the parent's current item positions to check the position item is visible or not
        // head = first partly visible, tail = last partly visible
        int n = parent.getChildCount();
        CenterScroller.ChildInfo head = new ChildInfo(parent, 0);
        CenterScroller.ChildInfo tail = new ChildInfo(parent, n - 1);
        if (head.isEmpty() || tail.isEmpty()) return null;

        int anchorX = anchorAtLR(parent, parentPercentX);
        int anchorY = anchorAtTB(parent, parentPercentY);

        // Determine the view is located at which position
        int viewAtX, viewAtY;
        if (head.adapterPos <= position && position <= tail.adapterPos) {
            // Case : Target is (partly or completely) visible within recycler
            View target = parent.getChildAt(position - head.adapterPos);
            if (target == null) return null;
            viewAtX = anchorAtLR(target, childPercentX);
            viewAtY = anchorAtTB(target, childPercentY);
        } else {
            // Case : Target is at left outside or right outside of recycler
            boolean targetAtLeft = position < head.adapterPos;
            CenterScroller.ChildInfo info = targetAtLeft ? head : tail;
            View ref = info.view;
            int distanceX = onPredictScrollOffsetX(targetAtLeft, info, position);
            int distanceY = onPredictScrollOffsetY(targetAtLeft, info, position);

            viewAtX = anchorAtLR(ref, childPercentX) + distanceX;
            viewAtY = anchorAtTB(ref, childPercentY) + distanceY;
        }

        int offsetX = viewAtX - anchorX + parent.getLeft();
        int offsetY = viewAtY - anchorY + parent.getTop();
        return new int[]{offsetX, offsetY};
    }

    protected int onPredictScrollOffsetX(boolean targetAtLeft, @NonNull CenterScroller.ChildInfo info, int position) {
        // Simplest case, all the item view has same width
        CenterScroller.ScrollInfo s = new CenterScroller.ScrollInfo(info.view);
        return s.widthAddMargins * (position - info.adapterPos);
    }

    protected int onPredictScrollOffsetY(boolean targetAtTop, @NonNull CenterScroller.ChildInfo info, int position) {
        // Simplest case, all the item view has same height
        CenterScroller.ScrollInfo s = new CenterScroller.ScrollInfo(info.view);
        return s.heightAddMargins * (position - info.adapterPos);
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
        public final int heightAddMargins;

        public ScrollInfo(View v) {
            margins = getMargins(v);
            widthAddMargins = margins.left + v.getWidth() + margins.right;
            heightAddMargins = margins.top + v.getHeight() + margins.bottom;
        }

        @Override
        public String toString() {
            return String.format("Margins = %s, +width = %s, +height = %s", margins, widthAddMargins, heightAddMargins);
        }
    }

    public static final class ChildInfo {
        public View view;
        public RecyclerView.ViewHolder viewHolder;
        public int adapterPos = RecyclerView.NO_POSITION;

        public ChildInfo(RecyclerView parent, int childIndex) {
            view = parent.getChildAt(childIndex);
            if (view != null) {
                viewHolder = parent.getChildViewHolder(view);
            }
            if (viewHolder != null) {
                adapterPos = viewHolder.getBindingAdapterPosition();
            }
        }

        public boolean isEmpty() {
            return view == null || viewHolder == null;
        }

        @Override
        public String toString() {
            return String.format("#%s, view = %s", adapterPos, view);
        }
    }
}