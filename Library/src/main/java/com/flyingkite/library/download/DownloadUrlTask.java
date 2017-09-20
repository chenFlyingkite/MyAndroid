package com.flyingkite.library.download;

import android.util.Log;

import com.flyingkite.library.FilesHelper;
import com.flyingkite.library.IOUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DownloadUrlTask implements Runnable {
    private static final String TAG = "DownloadUriTask";
    private static final int BUFFER_SIZE = 65536; // 64KB = Max TCP packet size

    public interface Listener {
        void onComplete(File result);

        void onError(Exception error);

        void onCancelled();

        void onProgress(long progress, long max);
    }

    private AtomicBoolean mIsCancelled = new AtomicBoolean(false);

    private String mURL;
    private File mFolder;
    private String mFilename;
    private File mFile;
    private Listener mListener = silent;

    public DownloadUrlTask(String sourceUrl, File folder, Listener listener) {
        this(sourceUrl, folder, null, listener);
    }

    public DownloadUrlTask(String sourceUrl, String folder, String name, Listener listener) {
        this(sourceUrl, new File(folder), name, listener);
    }

    public DownloadUrlTask(String sourceUrl, File folder, String name, Listener listener) {
        mURL = sourceUrl;
        mFolder = folder;
        mFilename = name;
        mListener = listener == null ? silent : listener;
    }

    protected void onPreExecute() {}

    protected void onPostExecute() {}

    @Override
    public void run() {
        onPreExecute();

        if (checkCancel()) return;

        InputStream is = null;
        FileOutputStream fos = null;
        HttpURLConnection con = null;

        try {
            con = (HttpURLConnection) new URL(mURL).openConnection();

            if (checkCancel()) return;

            int code = con.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK) {
                Log.i(TAG, "No file to download. Server replied HTTP code: " + code);
            } else {
                mFolder.mkdirs();
                // Null -> extracts file name from URL
                if (mFilename == null) {
                    mFilename = mURL.substring(mURL.lastIndexOf("/") + 1);
                }
                mFile = new File(mFolder, mFilename);
                if (checkCancel()) return;

                // Prepare I/O streams
                is = new BufferedInputStream(con.getInputStream());
                fos = new FileOutputStream(mFile);
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
                IOUtil.closeIt(is, fos);
                mListener.onComplete(mFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
            FilesHelper.fullDelete(mFile);
            mListener.onError(e);
        } finally {
            IOUtil.closeIt(is, fos);
            if (con != null) {
                con.disconnect();
            }
        }

        onPostExecute();
    }

    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static Response getOkUrl(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        return CLIENT.newCall(request).execute();
    }

    public static String postOkJson(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(body).build();
        Response response = CLIENT.newCall(request).execute();
        return response.body().string();

    }

    public void cancel() {
        mIsCancelled.set(true);
    }

    private boolean checkCancel() {
        if (mIsCancelled.get()) {
            mListener.onCancelled();
            FilesHelper.fullDelete(mFile);
            onPostExecute();
            return true;
        }
        return false;
    }

    private static final Listener silent = new Listener() {
        @Override
        public void onComplete(File result) { }

        @Override
        public void onError(Exception error) { }

        @Override
        public void onCancelled() { }

        @Override
        public void onProgress(long progress, long max) { }
    };

}
