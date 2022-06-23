package flyingkite.library.android.util;

import android.graphics.Rect;
import android.view.View;

public class ViewUtil {
    public static Rect getLocationOnScreen(View v) {
        if (v == null) return null;

        Rect r = new Rect();
        int[] xy = new int[2];
        v.getLocationOnScreen(xy);

        int l = xy[0];
        int t = xy[1];
        int w = v.getWidth();
        int h = v.getHeight();
        r.set(l, t, l + w, t + h);
        return r;
    }
}
