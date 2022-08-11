package flyingkite.library.android.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
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

    default void installApp(Uri data, int requestCode) {
        Activity a = getActivity();
        if (a == null) return;

        Intent it = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        it.setData(data);
        it.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
        it.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            a.startActivityForResult(it, requestCode);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }


}
