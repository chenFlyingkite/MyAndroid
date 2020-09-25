package com.flyingkite.library.widget.roi;

import android.graphics.RectF;

public interface ViewROIUnit {
    /**
     * Returns the width of ROI range is 1.
     * Value should be stable and unchanged during ROIKit is receiving touch event
     */
    int getMovieViewWidth();

    /**
     * Returns the height of ROI range is 1.
     * Value should be stable and unchanged during ROIKit is receiving touch event
     */
    int getMovieViewHeight();

    /**
     * Called when unit need to set view's position(x, y) & size(width, height)
     */
    void setMovieViewPosition(int x, int y, int width, int height, float scaleX, float scaleY, RectF roi);
}
