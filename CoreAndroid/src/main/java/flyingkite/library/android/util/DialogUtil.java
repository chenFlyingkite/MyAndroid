package flyingkite.library.android.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

public class DialogUtil {

    public interface InflateListener {
        void onFinishInflate(View view, AlertDialog dialog);
    }

    public static class Alert implements ActivityUtil {

        private final Activity owner;
        @LayoutRes
        private final int viewLayoutId;
        private final InflateListener onViewInflated;
        @StyleRes
        private final int themeResId;

        public Alert(@NonNull Activity activity, @LayoutRes int layoutId, @StyleRes int themeId, InflateListener onInflate) {
            owner = activity;
            viewLayoutId = layoutId;
            onViewInflated = onInflate;
            themeResId = themeId;
        }

        public AlertDialog buildAndShow() {
            if (ThreadUtil.isUIThread()) {
                return _buildAndShow();
            }
            owner.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    _buildAndShow();
                }
            });
            return null;
        }

        private AlertDialog _buildAndShow() {
            if (isActivityGone()) return null;

            View dialogView = LayoutInflater.from(owner).inflate(viewLayoutId, null);
            final AlertDialog dialog = new AlertDialog.Builder(owner, themeResId).setView(dialogView).create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);

            if (onViewInflated != null) {
                onViewInflated.onFinishInflate(dialogView, dialog);
            }
            dialog.show();
            return dialog;
        }

        @Override
        public Activity getActivity() {
            return owner;
        }
    }
}
