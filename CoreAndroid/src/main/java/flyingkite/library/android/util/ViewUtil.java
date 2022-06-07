package flyingkite.library.android.util;

import android.graphics.Rect;
import android.view.View;

public class ViewUtil {
    public static Rect getLocationOnScreen(View v) {
        if (v == null) return null;
        Rect r = new Rect();
        int[] xy = new int[2];
        v.getLocationOnScreen(xy);
        r.set(xy[0], xy[1], v.getWidth(), v.getHeight());
        return r;
    }
}
