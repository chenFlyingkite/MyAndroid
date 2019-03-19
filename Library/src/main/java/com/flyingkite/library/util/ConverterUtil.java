package com.flyingkite.library.util;

import android.content.Context;
import android.util.DisplayMetrics;

public interface ConverterUtil {
    /**
     * Covert dp to pixel
     * @return pixel
     */
    default float dpToPixel(float dp, Context context) {
        float px = dp * getDensity(context);
        return px;
    }

    /**
     * Covert pixel to dp
     * @return dp
     */
    default float pixelToDp(float px, Context context) {
        float dp = px / getDensity(context);
        return dp;
    }

    /**
     * 120dpi = 0.75
     * 160dpi = 1 (default)
     * 240dpi = 1.5
     */
    default float getDensity(Context context) {
        DisplayMetrics m = context.getResources().getDisplayMetrics();
        return m.density;
    }
}
