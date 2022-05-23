package flyingkite.library.android.log;

import android.util.Log;

public class L extends flyingkite.library.java.log.L {
    private static String TAG = "L";

    public static void logV(String format, Object... param) {
        Log.v(TAG, String.format(java.util.Locale.US, format, param));
    }

}
