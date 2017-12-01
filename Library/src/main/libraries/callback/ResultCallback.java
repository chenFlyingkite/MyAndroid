package com.cyberlink.yousnap.libraries.callback;

import android.os.Handler;
import android.util.Log;

/**
 * @see <a href="http://clt-svn/svn-md/repos/PowerDirector_Android/trunk/android/CESAREngine/src/com/cyberlink/util">PowerDirector Android</a>
 */
public abstract class ResultCallback<C, E> {

    private static final String TAG = ResultCallback.class.getSimpleName();

    /**
     * A handler to deliver callback in desired message thread.
     */
    protected final Handler handler;

    public ResultCallback() {
        this(null);
    }

    public ResultCallback(Handler handler) {
        this.handler = handler;
    }

    /**
     * <p><b>NOTE:</b> Don't invoke this method directly. Use {@link #complete(Object)} instead.
     *
     * @param result
     */
    public abstract void onComplete(C result);

    /**
     * <p><b>NOTE:</b> Don't invoke this method directly. Use {@link #error(Object)} instead.
     *
     * @param error
     */
    public abstract void onError(E error);

    /**
     * <p>We catch all exception while invoking onComplete callback.
     *
     * <p>Because the callback implementation from caller would have the chance to throw the unhandled exception.
     * In this case, the callee would also throw the exception, therefore, raise another onError event.
     * So it has the <b>opportunity</b> to execute <b>partial commands in onComplete</b> and <b>commands in onError</b>.
     *
     * @param result
     */
    public final void complete(final C result) {
        try {
            if (handler == null) {
                onComplete(result);
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onComplete(result);
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "invoke onComplete failed", e);
        }
    }

    /**
     * Handy function: invoke {@link #complete(Object)} with null argument.
     */
    public final void complete() {
        complete(null);
    }

    /**
     * <p>Refer to {@link #complete(Object)}
     *
     * @param error
     */
    public final void error(final E error) {
        try {
            if (handler == null) {
                onError(error);
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onError(error);
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "invoke onError failed", e);
        }
    }

    /**
     * Handy function: invoke {@link #error(Object)} with null argument.
     */
    public final void error() {
        error(null);
    }
}