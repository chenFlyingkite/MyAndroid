package flyingkite.library.androidx.download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

import flyingkite.library.android.log.Loggable;
import flyingkite.library.android.util.IOUtil;
import flyingkite.library.java.util.FileUtil;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DownloadUrlTask implements Runnable, Loggable {
    private static final int BUFFER_SIZE = 65536; // 64KB = Max TCP packet size

    public interface Listener<T> {
        default void onComplete(T result) {}

        default void onError(Exception error) {}

        default void onCancelled() {}

        default void onProgress(long progress, long max) {}

        default void onPreExecute() {}

        default void onPostExecute() {}
    }

    private AtomicBoolean mIsCancelled = new AtomicBoolean(false);

    // The Listener that do nothing
    private static final Listener<File> silent = new Listener<File>(){};
    private String mURL;
    private File mFolder;
    private String mFilename;
    private File mFile;
    private Listener<File> mListener = silent;

    /**
     * As DownloadUrlTask#DownloadUrlTask(String, File, null, Listener)
     * @see DownloadUrlTask#DownloadUrlTask(String, File, String)
     */
    public DownloadUrlTask(String sourceUrl, File folder) {
        this(sourceUrl, folder, null);
    }

    /**
     * As DownloadUrlTask#DownloadUrlTask(String, File, name, Listener)
     * @see DownloadUrlTask#DownloadUrlTask(String, File, String)
     */
    public DownloadUrlTask(String sourceUrl, String folder, String name) {
        this(sourceUrl, new File(folder), name);
    }

    /**
     * Download the sourceUrl into folder and file named as name
     *
     * @param sourceUrl Url to download
     * @param folder The target folder to put downloaded file
     * @param name The downloaded file name. Null to use the name in sourceUrl's name after last '/'
     */
    public DownloadUrlTask(String sourceUrl, File folder, String name) {
        mURL = sourceUrl;
        mFolder = folder;
        mFilename = name;
    }

    /**
     * @param listener The listener to be notified download status
     */
    public void setListener(Listener<File> listener) {
        mListener = listener == null ? silent : listener;
    }

    @Override
    public void run() {
        mListener.onPreExecute();

        if (checkCancel()) return;

        InputStream is = null;
        FileOutputStream fos = null;
        HttpURLConnection con = null;
        File tmp = null;

        try {
            con = (HttpURLConnection) new URL(mURL).openConnection();

            if (checkCancel()) return;

            int code = con.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK) {
                logI("No file to download. Server replied HTTP code: %s", code);
            } else {
                mFolder.mkdirs();
                // Null -> extracts file name from URL
                if (mFilename == null) {
                    mFilename = mURL.substring(mURL.lastIndexOf("/") + 1);
                }
                mFile = new File(mFolder, mFilename);
                tmp = new File(mFolder, mFilename + ".download." + _fmt("yyyyMMdd_hhmmss_SSS", System.currentTimeMillis()));
                if (checkCancel()) return;

                // Prepare I/O streams
                is = new BufferedInputStream(con.getInputStream());
                fos = new FileOutputStream(tmp);
                final long length = con.getContentLength();

                // Read stream and write to file
                int read;
                int write = 0;
                byte[] buffer = new byte[BUFFER_SIZE];

                while ((read = is.read(buffer)) != -1) {
                    if (checkCancel()) return;

                    fos.write(buffer, 0, read);
                    write += read;
                    mListener.onProgress(write, length);
                }
                fos.flush();
                tmp.renameTo(mFile);

                mListener.onComplete(mFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.ensureDelete(tmp);
            FileUtil.ensureDelete(mFile);
            mListener.onError(e);
        } finally {
            IOUtil.closeIt(is, fos);
            FileUtil.ensureDelete(tmp);
            if (con != null) {
                con.disconnect();
            }
            // if task be cancelled, delete file.
            if (mIsCancelled.get()) {
                FileUtil.ensureDelete(mFile);
            }
        }

        mListener.onPostExecute();
    }

    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static Response getOkUrl(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        return CLIENT.newCall(request).execute();
    }

    public static String postOkJson(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder().url(url).post(body).build();
        Response response = CLIENT.newCall(request).execute();
        ResponseBody resBody = response.body();
        return resBody == null ? null : resBody.string();

    }

    public void cancel() {
        mIsCancelled.set(true);
    }

    private boolean checkCancel() {
        if (mIsCancelled.get()) {
            mListener.onCancelled();
            FileUtil.ensureDelete(mFile);
            mListener.onPostExecute();
            return true;
        }
        return false;
    }
}
