package com.cyberlink.yousnap.libraries.callback;

import android.os.Handler;
import android.util.Log;

/**
 * @see <a href="http://clt-svn/svn-md/repos/PowerDirector_Android/trunk/android/CESAREngine/src/com/cyberlink/util">PowerDirector Android</a>
 */
public abstract class ProgressCallback<C, E, P> extends ResultCallback<C, E> {
    private static final String TAG = ProgressCallback.class.getSimpleName();

    public ProgressCallback() {
        super();
    }

    public ProgressCallback(Handler handler) {
        super(handler);
    }

    public abstract void onProgress(P progress);

    /**
     * <p>Refer to {@link ResultCallback#complete(Object)}
     *
     * @param progress
     */
    public final void progress(final P progress) {
        try {
            if (handler == null) {
                onProgress(progress);
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onProgress(progress);
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "invoke onProgress failed", e);
        }
    }
}