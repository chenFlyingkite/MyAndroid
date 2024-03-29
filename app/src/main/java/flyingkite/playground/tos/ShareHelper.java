package flyingkite.playground.tos;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import flyingkite.library.android.util.IOUtil;
import flyingkite.library.android.util.ThreadUtil;
import flyingkite.library.java.util.FileUtil;
import flyingkite.library.androidx.TicTac2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;

/**
 * <a href="https://developer.android.com/training/sharing/send">
 *     https://developer.android.com/training/sharing/send
 *     </a>
 */
public class ShareHelper {
    private ShareHelper() {}

    public static void shareString(@NonNull Context context, String msg) {
        shareString(context, msg, "share_to");
    }

    public static void shareString(@NonNull Context context, String msg, String chooser) {
        Intent it = new Intent(Intent.ACTION_SEND);
        it.putExtra(Intent.EXTRA_TEXT, msg);
        it.setType("text/plain");
        try {
            context.startActivity(Intent.createChooser(it, chooser));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void viewLink(@NonNull Context context, String link) {
        Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        try {
            context.startActivity(it);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void sendUriIntent(@NonNull Context context, Uri uri, String type) {
        Intent it = new Intent(Intent.ACTION_SEND);
        it.putExtra(Intent.EXTRA_STREAM, uri);
        it.setType(type);
        try {
            context.startActivity(it);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void shareImage(@NonNull Activity context, View view, String filename) {
        shareImage(context, view, filename, view.getWidth(), view.getHeight());
    }

    public static void shareImage(@NonNull Activity context, View view, String filename, int width, int height) {
        SaveViewToBitmapTask task = new SaveViewToBitmapTask(context, view, filename){
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                MediaScannerConnection.scanFile(context, new String[]{filename}, null,
                        (path, uri) -> {
                            LogV("Scanned %s\n  as -> %s", path, uri);
                            sendUriIntent(context, uri, "image/png");
                        });
            }
        };
        task.ofSize(width, height);
        task.executeOnExecutor(ThreadUtil.cachedThreadPool);
    }

    public static String cacheName(Context c, String name) {
        File folder = c.getExternalCacheDir();
        return folder.getAbsolutePath() + File.separator + name;
    }

    private static final String data = "data";

    public static File extFilesFile(Context c, String filename) {
        File folder = c.getExternalFilesDir(data);
        return new File(folder, filename);
    }

//    @Deprecated
//    public static void shareBitmap(@NonNull Activity activity, String url) {
//        Glide.with(activity).asBitmap().load(url)
//        .listener(new RequestListener<Bitmap>() {
//            @Override
//            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
//                LogE("onLoadFailed, first = %s, model = %s, e = %s", Say.ox(isFirstResource), model, e);
//                return false;
//            }
//
//            @Override
//            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
//                LogV("onResourceReady, first = %s, bmp = %s", Say.ox(isFirstResource), resource);
//                return false;
//            }
//        }).into(new SimpleTarget<Bitmap>() {
//            @Override
//            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                LogV("onResourceReady, bmp = %s", resource);
//
//                Intent it = new Intent(Intent.ACTION_SEND);
//                it.putExtra(Intent.EXTRA_STREAM, resource);
//                it.setType("image/png");
//                try {
//                    activity.startActivity(it);
//                } catch (ActivityNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

    public static class SaveViewToBitmapTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<Activity> activity;
        private WeakReference<View> view;
        private String savedName;
        private int width;
        private int height;
        private WaitingDialog w;
        private TicTac2 tt = new TicTac2();

        public SaveViewToBitmapTask(Activity act, View v, String filename) {
            activity = new WeakReference<>(act);
            view = new WeakReference<>(v);
            savedName = filename;
            ofSize(v.getWidth(), v.getHeight());
        }

        public SaveViewToBitmapTask ofSize(int w, int h) {
            width = w;
            height = h;
            return this;
        }

        private <T> T getW(WeakReference<T> obj) {
            if (obj != null) {
                return obj.get();
            } else {
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            if (getW(activity) == null || getW(view) == null) return;

            w = new WaitingDialog.Builder(getW(activity), true)
                    .onCancel((dialog) -> {
                        cancel(true);
                    }).buildAndShow();
            tt.tic();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            View vw = getW(view);
            if (vw == null || savedName == null) {
                LogV("Cannot save bitmap : %s, %s", view, savedName);
                return null;
            }

            // 1. [<10ms] Create new bitmap
            Bitmap bitmap = Bitmap.createBitmap(vw.getWidth(), vw.getHeight(), Bitmap.Config.ARGB_8888);
            if (isCancelled()) return null;

            // 2. [100ms] Let view draws to the bitmap
            Canvas c = new Canvas(bitmap);
            vw.draw(c);
            if (isCancelled()) return null;

            // 3. [<10ms] Create output file
            File f = new File(savedName);
            File fp = f.getParentFile();
            if (fp != null) {
                fp.mkdirs();
            }
            FileUtil.ensureDelete(f);
            if (isCancelled()) return null;

            // 4. Scale bitmap
            Bitmap bmp = Bitmap.createScaledBitmap(bitmap, width, height, false);
            bitmap.recycle();
            bitmap = bmp;

            // 5. [~1kms] Writing bitmap to file
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                IOUtil.closeIt(fos);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            tt.tac("Save done");
            if (w != null) {
                w.dismiss();
            }
        }
    }

    private static void LogV(String msg, Object... param) {
        LogV(String.format(msg, param));
    }

    private static void LogV(String msg) {
        Log.v("ShareHelper", msg);
    }

    private static void LogE(String msg, Object... param) {
        LogE(String.format(msg, param));
    }

    private static void LogE(String msg) {
        Log.e("ShareHelper", msg);
    }

}
