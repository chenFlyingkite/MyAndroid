package flyingkite.library.android.util;

/**
 * Use it easily like AsyncTask
 */
public interface RunningTask extends Runnable {

    @Override
    default void run() {
        ThreadUtil.runOnUiThread(this::onPreExecute);
        doInBackground();
        ThreadUtil.runOnUiThread(this::onPostExecute);
    }

    default void onPreExecute() {
    }

    void doInBackground();

    default void onPostExecute() {
    }
}
