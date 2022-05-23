package com.flyingkite.core.util;

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

    default float getDensity(Context context) {
        DisplayMetrics m = context.getResources().getDisplayMetrics();
        return m.density;
    }
}
