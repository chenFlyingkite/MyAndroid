package com.flyingkite.library.util;

import android.app.Activity;
import android.os.Build;

public interface ActivityUtil {
    Activity getActivity();

    default boolean isActivityGone() {
        Activity a = getActivity();
        if (a == null || a.isFinishing()) return true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return a.isDestroyed();
        }
        return false;
    }


}
