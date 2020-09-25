package com.flyingkite.library.mediastore;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import com.flyingkite.library.TicTac2;
import com.flyingkite.library.log.Loggable;
import com.flyingkite.library.mediastore.listener.DataListener;
import com.flyingkite.library.mediastore.listener.RequestListener;
import com.flyingkite.library.mediastore.request.MediaGroupRequest;
import com.flyingkite.library.mediastore.request.MediaRequest;
import com.flyingkite.library.mediastore.store.StoreAudio;
import com.flyingkite.library.mediastore.store.StoreDownloads;
import com.flyingkite.library.mediastore.store.StoreFiles;
import com.flyingkite.library.mediastore.store.StoreImages;
import com.flyingkite.library.mediastore.store.StoreVideo;
import com.flyingkite.library.util.IOUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

public class MediaStoreTester implements Loggable {
    private Context context;

    public MediaStoreTester(Context c) {
        context = c;
    }

    public void test() {
        logE("---\n\nImage\n\n---");
        m1();
        logE("---\n\nVideo\n\n---");
        v1();
        logE("---\n\nAudio\n\n---");
        a1();
        logE("---\n\nFiles\n\n---");
        f1();
        logE("---\n\nDownloads\n\n---");
        d1();
    }

    private void m1() {
        RequestListener lis = new QueryListener() {
            @Override
            public void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            public void onQueried(int count, Cursor cursor) {
                super.onQueried(count, cursor);
            }

            @Override
            public void onProgress(int position, int count, Map<String, String> data) {
                super.onProgress(position, count, data);
                int width = 0;
                String sp = data.get(MediaStore.Images.ImageColumns.DATA);
                String sw = data.get(MediaStore.Images.ImageColumns.WIDTH); // ImageColumns.WIDTH = "width"

                if (sw != null) {
                    try {
                        width = Integer.parseInt(sw);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                if (true && sp != null && position < 100) {
                    //if (width == 0 && sp != null) {
                    MediaScannerConnection.scanFile(context, new String[]{sp}, null, (path, uri) -> {
                        logE("Scanned %s\n->%s", path, uri);

                        readWH(uri);
                    });

                    Uri up = Uri.fromFile(new File(sp));
                    readWH(up);
                }
            }

            private void readWH(Uri uri) {
                // Target at | Run on | file:/// | content://
                //      Q    |    Q   |   Fail   |   OK
                //      P    |    Q   |    OK    |   OK
                //      Q    |    P   |    OK    |   OK
                //      P    |    P   |    OK    |   OK
                // Fail should use cr.openFile(uri, "r", null);
                logE("Read WH of %s", uri);
                TicTac2 t = new TicTac2();
                t.tic();
                ContentResolver cr = context.getContentResolver();
                if (cr != null) {
                    ParcelFileDescriptor pd = null;
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            pd = cr.openFile(uri, "r", null);
                        } else {
                            pd = cr.openFileDescriptor(uri, "r");
                        }
                        if (pd != null) {
                            Bitmap b = BitmapFactory.decodeFileDescriptor(pd.getFileDescriptor());
                            if (b != null) {
                                int bytes = -1;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    bytes = b.getAllocationByteCount();
                                }
                                logE("bmp = %sx%s, bytes = %s for %s", b.getWidth(), b.getHeight(), bytes, uri);
                            }
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        IOUtil.closeIt(pd);
                    }
                }
                t.tac("Done for %s", uri);
            }

            @Override
            public void onComplete(List<Map<String, String>> all) {
                super.onComplete(all);
            }

            @Override
            public void onError(Exception error) {
                super.onError(error);
            }
        };

        StoreImages si = new StoreImages(context);
        MediaRequest r = si.newRequest();
        r.listener = new DataLis<>();
        String folder = "/storage/emulated/0/DCIM/PhotoDirector";
        MediaGroupRequest rg = si.newGroupRequest();
        rg.listener = new DataLis<>();

        si.queryAllFolder(rg);
        logE("---\n\n---");
        si.queryAllItems(r);
        logE("---\n\n---");
        si.queryAtFolder(folder, r);
        logE("---\n\n---");
        logE("---\nPart 2 \n---");

        r.selection = "_display_name LIKE ?";
        r.selectionArgs = new String[]{"2%"};
        si.queryAllItems(r);
        logE("---\n\n---");
        si.queryAtFolder(folder, r);
        logE("---\n\n---");

    }

    private void v1() {
        StoreVideo si = new StoreVideo(context);
        MediaRequest r = si.newRequest();
        r.listener = new DataLis<>();
        String folder = "/storage/emulated/0/Movies/cyberlink/ActionDirector";
        MediaGroupRequest rg = si.newGroupRequest();
        rg.listener = new DataLis<>();

        si.queryAllFolder(rg);
        logE("---\n\n---");
        si.queryAllItems(r);
        logE("---\n\n---");
        si.queryAtFolder(folder, r);
        logE("---\n\n---");
        logE("---\nPart 2 \n---");

        r.selection = "_display_name LIKE ?";
        r.selectionArgs = new String[]{"2%"};
        si.queryAllItems(r);
        logE("---\n\n---");
        si.queryAtFolder(folder, r);
        logE("---\n\n---");
    }

    private void a1() {
        StoreAudio si = new StoreAudio(context);
        MediaRequest r = si.newRequest();
        r.listener = new DataLis<>();
        String folder = "/storage/emulated/0/DCIM/ActionDirector";
        MediaGroupRequest rg = si.newGroupRequest();
        rg.listener = new DataLis<>();

        logE("---\nAll Folder\n---");
        si.queryAllFolder(rg);
        logE("---\nAll Buckets\n---");
        si.queryAllBuckets(rg);
        logE("---\n\n---");
        si.queryAllItems(r);
        logE("---\n\n---");
        si.queryAtFolder(folder, r);
        logE("---\n\n---");
        logE("---\nPart 2 \n---");

        r.selection = "_display_name LIKE ?";
        r.selectionArgs = new String[]{"2%"};
        si.queryAllItems(r);
        logE("---\n\n---");
        si.queryAtFolder(folder, r);
        logE("---\n\n---");
    }

    private void f1() {
        StoreFiles si = new StoreFiles(context);
        MediaRequest r = si.newRequest();
        r.listener = new DataLis<>();
        String folder = "/storage/emulated/0/DCIM/ActionDirector";
        MediaGroupRequest rg = si.newGroupRequest();
        rg.listener = new DataLis<>();

        si.queryAllFolder(rg);
        logE("---\n\n---");
        si.queryAllItems(r);
        logE("---\n\n---");
        si.queryAtFolder(folder, r);
        logE("---\n\n---");
        //logE("\n\n-----\n\n");
        logE("---\nPart 2 \n---");

        r.selection = "_display_name LIKE ?";
        r.selectionArgs = new String[]{"2%"};
        si.queryAllItems(r);
        logE("---\n\n---");
        si.queryAtFolder(folder, r);
        logE("---\n\n---");
    }

    private void d1() {
        StoreDownloads si = new StoreDownloads(context);
        MediaRequest r = si.newRequest();
        r.listener = new DataLis<>();
        String folder = "/storage/emulated/0/Download";
        MediaGroupRequest rg = si.newGroupRequest();
        rg.listener = new DataLis<>();

        si.queryAllFolder(rg);
        logE("---\n\n---");
        si.queryAllItems(r);
        logE("---\n\n---");
        si.queryAtFolder(folder, r);
        logE("---\n\n---");
        logE("---\nPart 2 \n---");

        r.selection = "_display_name LIKE ?";
        r.selectionArgs = new String[]{"2%"};
        si.queryAllItems(r);
        logE("---\n\n---");
        si.queryAtFolder(folder, r);
        logE("---\n\n---");
    }

    public static class DataLis<T> implements DataListener<T>,  Loggable {
        @Override
        public void onPreExecute() {
            logE("onPreExecute");
        }

        @Override
        public void onQueried(int count, Cursor cursor) {
            logE(" > %s items, %s columns in %s", count, cursor.getColumnCount(), cursor);
        }

        @Override
        public void onProgress(int position, int count, T data) {
            logE(" -> #%4d/%4d : %s", position, count, data);
        }

        @Override
        public void onComplete(List<T> all) {
//            logE("%s items", all.size());
//            for (int i = 0; i < all.size(); i++) {
//                logE("#%4d : %s", i, all.get(i));
//            }
        }

        @Override
        public void onError(Exception error) {
            logE("X_X failed %s", error);
        }
    }

    public class QueryListener implements RequestListener {
        private TicTac2 t = new TicTac2();
        @Override
        public void onPreExecute() {
            logE("onPreExecute");
            t.tic();
        }

        @Override
        public void onQueried(int count, Cursor cursor) {
            logE(" > %s items, %s columns in %s", count, cursor.getColumnCount(), cursor);
        }

        @Override
        public void onProgress(int position, int count, Map<String, String> data) {
            logE(" -> #%4d/%4d : %s", position, count, data);
        }

        @Override
        public void onComplete(List<Map<String, String>> all) {
//            logE("%s items", all.size());
//            for (int i = 0; i < all.size(); i++) {
//                logE("#%4d : %s", i, all.get(i));
//            }
        }

        @Override
        public void onError(Exception error) {
            logE("X_X failed %s", error);
        }
    }
}