package com.cyberlink.yousnap.libraries;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.MediaColumns;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Log;

import com.cyberlink.yousnap.libraries.MediaItem.MediaType;
import com.cyberlink.yousnap.libraries.callback.ProgressCallback;
import com.cyberlink.yousnap.libraries.callback.ResultCallback;
import com.cyberlink.yousnap.util.IOUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaStoreLibrary {
    private static final String TAG = MediaStoreLibrary.class.getSimpleName();
    private static final boolean DEBUG = false;

//    private static final Uri URI_VIDEO_MEDIA = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    private static final Uri URI_IMAGE_MEDIA = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//    private static final Uri URI_AUDIO_MEDIA = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

    // Suppress default constructor for non-instantiability
    private MediaStoreLibrary() {
        throw new AssertionError();
    }

    @WorkerThread
    public static void getImageFolders(@NonNull Context context, @NonNull ResultCallback<List<FolderItem>, Void> callback) {
        getMediaFolders(context, URI_IMAGE_MEDIA, null, null, callback);
    }

    @WorkerThread
    public static void getImageFoldersJPEG(@NonNull Context context, @NonNull ResultCallback<List<FolderItem>, Void> callback) {
        getMediaFolders(context, URI_IMAGE_MEDIA
                , MediaStore.Images.Media.MIME_TYPE + " = ?", new String[] {"image/jpeg"}
                , callback);
    }

    @WorkerThread
    public static void getImageFolders(@NonNull Context context, String selection, String[] selectionArgs, @NonNull ResultCallback<List<FolderItem>, Void> callback) {
        getMediaFolders(context, URI_IMAGE_MEDIA, selection, selectionArgs, callback);
    }

//    @WorkerThread
//    public static void getVideoFolders(@NonNull Context context, @NonNull ResultCallback<List<FolderItem>, Void> callback) {
//        getMediaFolders(context, URI_VIDEO_MEDIA, callback);
//    }

//    public static void getAudioFolders(@NonNull Context context, @NonNull ResultCallback<List<FolderItem>, Void> callback) {
//        getMediaFolders(context, URI_AUDIO_MEDIA, callback);
//    }

    /**
     * Query all folders contains Music, Videos or Pictures (depend on {@code uri} argument).
     * <p/>
     * <b>NOTE</b>: This method execute as <b>synchronous</b> way, and all folders will be callback after all query finished, sorted.
     */
    @WorkerThread
    private static void getMediaFolders(@NonNull Context context, @NonNull Uri uri, String selection, String[] selectionArgs, @NonNull ResultCallback<List<FolderItem>, Void> callback) {
        final String _fn = "[getMediaFolders]";
        long tic = System.currentTimeMillis();
        logD("%s start", _fn);

        String[] projection = {
                MediaColumns.DATA
        };
        Cursor folderCursor = null;
        try {
            folderCursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (folderCursor == null) {
                logI("%s no cursor", _fn);
                callback.error();
                return;
            }

            final int idxData = folderCursor.getColumnIndexOrThrow(MediaColumns.DATA);
            String filePath;
            File folderFile;
            String folderPath;
            FolderItem folderItem;

            Map<String, FolderItem> folders = new HashMap<>();

            while (folderCursor.moveToNext()) {
                // XXX: Some attributes could be null, from PDR-A experience. Skip to next one.
                filePath = folderCursor.getString(idxData);
                if (TextUtils.isEmpty(filePath)) continue;
                folderFile = new File(filePath).getParentFile();
                if (folderFile == null) continue;
                folderPath = folderFile.getAbsolutePath();
                if (TextUtils.isEmpty(folderPath)) continue;

                folderItem = folders.get(folderPath);
                if (folderItem != null) {
                    folderItem.increaseMediaCount();
                } else {
                    folderItem = new FolderItem(folderFile.getName(), folderPath, 1);
                    folders.put(folderPath, folderItem);
                    logD("%s > %s", _fn, folderItem);
                }
            }

            // Perform sorting
            List<FolderItem> folderItems = new ArrayList<>(folders.values());
            Collections.sort(folderItems, new Comparator<FolderItem>() {
                @Override
                public int compare(FolderItem lhs, FolderItem rhs) {
                    return lhs.getName().compareToIgnoreCase(rhs.getName());
                }
            });

            long tac = System.currentTimeMillis();
            logD("%s %s ms spent", _fn, tac - tic);
            callback.complete(folderItems);
        } catch (Exception e) {
            logE(e, "%s thrown exception", _fn);
            callback.error();
        } finally {
            IOUtil.closeIt(folderCursor);
        }
    }

    private static void logI(String format, Object... param) {
        if (DEBUG) {
            Log.i(TAG, format(format, param));
        }
    }

    private static void logD(String format, Object... param) {
        if (DEBUG) {
            Log.d(TAG, format(format, param));
        }
    }

    private static void logE(Throwable tr, String format, Object... param) {
        Log.e(TAG, format(format, param), tr);
    }

    private static String format(String format, Object... param) {
        return String.format(java.util.Locale.US, format, param);
    }

//    @WorkerThread
//    public static void getVPAtFolder(Context context, String folderPath, String orderBy, ProgressCallback<Void, Void, MediaItem> callback) {
//        getVideosAtFolder(context, folderPath, null, callback);
//        getImagesAtFolder(context, folderPath, null, callback);
//    }

    @WorkerThread
    public static void getImagesAtFolder(Context context, String folderPath
            , String orderBy
            , ProgressCallback<Void, Void, MediaItem> callback) {
        getImagesAtFolder(context, folderPath, null, null, orderBy, callback);
    }

    @WorkerThread
    public static void getJpegsAtFolder(Context context, String folderPath
            , String orderBy
            , ProgressCallback<Void, Void, MediaItem> callback) {
        getImagesAtFolder(context, folderPath, MediaStore.Images.Media.MIME_TYPE + " = ?", "image/jpeg", orderBy, callback);
    }

    @WorkerThread
    public static void getImagesAtFolder(Context context, String folderPath
            , String selectionMore, String selectionArgsMore
            , String orderBy
            , ProgressCallback<Void, Void, MediaItem> callback) {
        getImagesAtFolder(context, folderPath, selectionMore, selectionArgsMore, orderBy, callback, 0);
    }

    @Nullable
    @WorkerThread
    public static MediaItem getImageAtFolder(Context context, String folderPath) {
        return getImageAtFolder(context, folderPath, null, null, null);
    }

    @Nullable
    @WorkerThread
    public static MediaItem getImageAtFolder(Context context, String folderPath, String orderBy) {
        return getImageAtFolder(context, folderPath, null, null, orderBy);
    }

    @Nullable
    @WorkerThread
    public static MediaItem getImageAtFolder(Context context, String folderPath
            , String selectionMore, String selectionArgsMore
            , String orderBy) {
        // Use List to be a mediator because Java doesn't allow inner class to access outer variables without final keyword.
        // TRICK: Use asynchronous style (but implement as synchronous within actual method implementation) to get desired MediaItem.
        final List<MediaItem> mediator = new ArrayList<>();
        getImagesAtFolder(context, folderPath, selectionMore, selectionArgsMore, orderBy, new ProgressCallback<Void, Void, MediaItem>() {
            @Override
            public void onProgress(MediaItem mediaItem) {
                mediator.add(mediaItem);
            }

            @Override
            public void onComplete(Void result) { /* Do nothing. */ }

            @Override
            public void onError(Void error) { /* Do nothing. */ }
        }, 1);

        return mediator.isEmpty() ? null : mediator.get(0);
    }

    @WorkerThread
    public static void getImagesAtFolder(Context context, String folderPath, String selectionMore, String selectionArgsMore, String orderBy, ProgressCallback<Void, Void, MediaItem> callback, int limit) {
        // Creating selection
        String selection = ImageColumns.DATA + " LIKE ? AND " + ImageColumns.DATA + " NOT GLOB ? ";
        if (selectionMore != null) {
            selection += " AND " + selectionMore;
        }

        // Creating selectionArgs
        List<String> args = new ArrayList<>();
        args.add(folderPath + "/%");
        args.add(folderPath + "/*/*");
        if (selectionArgsMore != null) {
            args.add(selectionArgsMore);
        }
        String[] selectionArgs = args.toArray(new String[args.size()]);

        getImages(context, selection, selectionArgs, orderBy, callback, limit);
    }

    @WorkerThread
    public static void getAllImagesJPEG(Context context, String orderBy, ProgressCallback<Void, Void, MediaItem> callback) {
        getAllImages(context, MediaStore.Images.Media.MIME_TYPE + " = ?", "image/jpeg", orderBy, callback);
    }

    @WorkerThread
    public static void getAllImages(Context context, String selectionMore, String selectionArgsMore, String orderBy, ProgressCallback<Void, Void, MediaItem> callback) {
        // Creating selection
        String selection = null;
        if (selectionMore != null) {
            selection = selectionMore;
        }

        // Creating selectionArgs
        List<String> args = new ArrayList<>();
        if (selectionArgsMore != null) {
            args.add(selectionArgsMore);
        }
        String[] selectionArgs = args.toArray(new String[args.size()]);

        getImages(context, selection, selectionArgs, orderBy, callback, 0);
    }

    @WorkerThread
    private static void getImages(Context context, String selection, String[] selectionArgs, String orderBy, ProgressCallback<Void, Void, MediaItem> callback, int limit) {
        final String _fn = "[getImages]";
        long tic = System.currentTimeMillis();
        logD("%s start", _fn);

        String[] projection = {
                ImageColumns._ID,
                ImageColumns.MIME_TYPE,
                ImageColumns.SIZE,
                ImageColumns.ORIENTATION,
                ImageColumns.DATE_TAKEN,
                ImageColumns.DATA,
                ImageColumns.WIDTH,
                ImageColumns.HEIGHT,
                ImageColumns.BUCKET_DISPLAY_NAME
        };

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(URI_IMAGE_MEDIA, projection, selection, selectionArgs, orderBy);
            if (cursor == null) {
                logI("%s no cursor", _fn);
                callback.error();
                return;
            }

            final int idxId = cursor.getColumnIndexOrThrow(ImageColumns._ID);
            final int idxMimeType = cursor.getColumnIndexOrThrow(ImageColumns.MIME_TYPE);
            final int idxSize = cursor.getColumnIndexOrThrow(ImageColumns.SIZE);
            final int idxOrientation = cursor.getColumnIndexOrThrow(ImageColumns.ORIENTATION);
            final int idxDateTaken = cursor.getColumnIndexOrThrow(ImageColumns.DATE_TAKEN);
            final int idxData = cursor.getColumnIndexOrThrow(ImageColumns.DATA);
            final int idxWidth = cursor.getColumnIndexOrThrow(ImageColumns.WIDTH);
            final int idxHeight = cursor.getColumnIndexOrThrow(ImageColumns.HEIGHT);
            final int idxBucketName = cursor.getColumnIndexOrThrow(ImageColumns.BUCKET_DISPLAY_NAME);

            long id;
            String displayName;
            String mimeType;
            long size;
            int orientation;
            long dateTaken;
            String filePath;
            int width;
            int height;
            String bucketName;

            int retrieveCount = 0;
            while (cursor.moveToNext()) {
                id = cursor.getLong(idxId);
                mimeType = cursor.getString(idxMimeType);
                size = cursor.getLong(idxSize);
                orientation = cursor.getInt(idxOrientation);
                dateTaken = cursor.getLong(idxDateTaken);
                filePath = cursor.getString(idxData);
                width = cursor.getInt(idxWidth);
                height = cursor.getInt(idxHeight);
                bucketName = cursor.getString(idxBucketName);
                displayName = filePath.substring(1 + filePath.lastIndexOf(File.separatorChar));

                final MediaItem mediaItem = MediaItem.createImage(id, filePath, displayName, mimeType, width, height, size, orientation, dateTaken, bucketName);
                logD("%s > %s", _fn, mediaItem);
                callback.progress(mediaItem);

                retrieveCount++;
                if (limit > 0 && retrieveCount >= limit) { // limit <= 0 means unlimited.
                    //logD("%s > reach limit: %s/%s", _fn, retrieveCount, limit);
                    break;
                }
            }

            long tac = System.currentTimeMillis();
            logD("%s %s ms spent", _fn, tac - tic);
            callback.complete();
        } catch (Exception e) {
            logE(e, "%s thrown exception", _fn);
            callback.error();
        } finally {
            IOUtil.closeIt(cursor);
        }
    }

    @WorkerThread
    public static void getAllImagesCountJPEG(Context context, ResultCallback<Integer, Void> callback) {
        SelectionHelper sh = SelectionHelper.JPEG();
        getAllImagesCount(context, sh.selection, sh.selectionArgs, callback);
    }

    @WorkerThread
    public static void getAllImagesCountJPEG(Context context, String selectionMore, String selectionArgsMore, ResultCallback<Integer, Void> callback) {
        SelectionHelper sh = SelectionHelper.JPEG();
        sh.join(selectionMore, selectionArgsMore);
        getAllImagesCount(context, sh.selection, sh.selectionArgs, callback);
    }

    @WorkerThread
    public static void getAllImagesCount(Context context, String selection, String[] selectionArgs, ResultCallback<Integer, Void> callback) {
        final String _fn = "[getAllImagesCount]";
        long tic = System.currentTimeMillis();
        logI("%s start", _fn);

        String[] projection = {
                ImageColumns._ID,
                ImageColumns.DATA,
        };

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(URI_IMAGE_MEDIA, projection, selection, selectionArgs, null);
            if (cursor == null) {
                logD("%s no cursor", _fn);
                callback.error();
                return;
            }

            long tac = System.currentTimeMillis();
            logD("%s took %s ms", _fn, tac - tic);
            callback.complete(cursor.getCount());
        } catch (Exception e) {
            logE(e, "%s thrown exception", _fn);
            callback.error();
        } finally {
            IOUtil.closeIt(cursor);
        }
    }

    private static class SelectionHelper {
        private String selection;
        private String[] selectionArgs;

        SelectionHelper(String sel, String[] selArg) {
            selection = sel;
            selectionArgs = selArg;
        }

        void join(String selectionMore, String selectionArgsMore) {
            // Creating selection
            if (selectionMore != null) {
                if (selection == null) {
                    selection = selectionArgsMore;
                } else {
                    selection += " AND " + selectionMore;
                }
            }

            // Creating selectionArgs

            List<String> args = new ArrayList<>();
            Collections.addAll(Arrays.asList(selectionArgs));
            if (selectionArgsMore != null) {
                args.add(selectionArgsMore);
            }
            selectionArgs = args.toArray(new String[args.size()]);
        }

        static SelectionHelper JPEG() {
            return new SelectionHelper(MediaStore.Images.Media.MIME_TYPE + " = ?", new String[] {"image/jpeg"});
        }
    }

//
//    //TODO : Should merge with getImagesAtFolder(), it only passing null
//    @WorkerThread
//    public static void getAllImages(Context context, String selectionMore, String selectionArgsMore, String orderBy, ProgressCallback<Void, Void, MediaItem> callback) {
//        final String _fn = "[getImagesAtFolder]";
//        long tic = System.currentTimeMillis();
//        logI("%s start", _fn);
//
//        String[] projection = {
//                ImageColumns._ID,
//                ImageColumns.MIME_TYPE,
//                ImageColumns.SIZE,
//                ImageColumns.ORIENTATION,
//                ImageColumns.DATE_TAKEN,
//                ImageColumns.DATA,
//                ImageColumns.WIDTH,
//                ImageColumns.HEIGHT,
//                ImageColumns.BUCKET_DISPLAY_NAME
//        };
//        // Creating selection
//        String selection = null;// = ImageColumns.DATA + " LIKE ? AND " + ImageColumns.DATA + " NOT GLOB ? ";
//        if (selectionMore != null) {
//            selection = selectionMore; //MediaStore.Images.Media.MIME_TYPE + " = ?"
//        }
//
//        // Creating selectionArgs
//        List<String> args = new ArrayList<>();
////        args.add(folderPath + "/%");
////        args.add(folderPath + "/*/*");
//        if (selectionArgsMore != null) {
//            args.add(selectionArgsMore);
//        }
//        String[] selectionArgs = args.toArray(new String[args.size()]);
//
//        Cursor cursor = null;
//        try {
//            cursor = context.getContentResolver().query(URI_IMAGE_MEDIA, projection, selection, selectionArgs, orderBy);
//            if (cursor == null) {
//                logD("%s no cursor", _fn);
//                callback.error();
//                return;
//            }
//
//            final int idxId = cursor.getColumnIndexOrThrow(ImageColumns._ID);
//            final int idxMimeType = cursor.getColumnIndexOrThrow(ImageColumns.MIME_TYPE);
//            final int idxSize = cursor.getColumnIndexOrThrow(ImageColumns.SIZE);
//            final int idxOrientation = cursor.getColumnIndexOrThrow(ImageColumns.ORIENTATION);
//            final int idxDateTaken = cursor.getColumnIndexOrThrow(ImageColumns.DATE_TAKEN);
//            final int idxData = cursor.getColumnIndexOrThrow(ImageColumns.DATA);
//            final int idxWidth = cursor.getColumnIndexOrThrow(ImageColumns.WIDTH);
//            final int idxHeight = cursor.getColumnIndexOrThrow(ImageColumns.HEIGHT);
//            final int idxBucketName = cursor.getColumnIndexOrThrow(ImageColumns.BUCKET_DISPLAY_NAME);
//
//            long id;
//            String displayName;
//            String mimeType;
//            long size;
//            int orientation;
//            long dateTaken;
//            String filePath;
//            int width;
//            int height;
//            String bucketName;
//
//            int retrieveCount = 0;
//            while (cursor.moveToNext()) {
//                id = cursor.getLong(idxId);
//                mimeType = cursor.getString(idxMimeType);
//                size = cursor.getLong(idxSize);
//                orientation = cursor.getInt(idxOrientation);
//                dateTaken = cursor.getLong(idxDateTaken);
//                filePath = cursor.getString(idxData);
//                width = cursor.getInt(idxWidth);
//                height = cursor.getInt(idxHeight);
//                bucketName = cursor.getString(idxBucketName);
//                displayName = filePath.substring(1 + filePath.lastIndexOf(File.separatorChar));
//
//                final MediaItem mediaItem = MediaItem.createImage(id, filePath, displayName, mimeType, width, height, size, orientation, dateTaken, bucketName);
//                logI("%s > %s", _fn, mediaItem);
//                callback.progress(mediaItem);
//
////                retrieveCount++;
////                if (limit > 0 && retrieveCount >= limit) { // limit <= 0 means unlimited.
////                    logD("%s > reach limit: %s/%s", _fn, retrieveCount, limit);
////                    break;
////                }
//            }
//
//            long tac = System.currentTimeMillis();
//            logD("%s took %s ms", _fn, tac - tic);
//            callback.complete();
//        } catch (Exception e) {
//            logE(e, "%s thrown exception", _fn);
//            callback.error();
//        } finally {
//            IOUtil.closeIt(cursor);
//        }
//    }

//    @WorkerThread
//    public static void getVideosAtFolder(Context context, String folderPath, String orderBy, ProgressCallback<Void, Void, MediaItem> callback) {
//        getVideosAtFolder(context, folderPath, orderBy, callback, 0);
//    }
//
//    @Nullable
//    @WorkerThread
//    public static MediaItem getVideoAtFolder(Context context, String folderPath) {
//        // XXX: Use Stack to be a mediator because Java doesn't allow inner class to access outer variables without final keyword.
//        // TRICK: Use asynchronous style (but implement as synchronous within actual method implementation) to get desired MediaItem.
//        final Stack<MediaItem> mediator = new Stack<>();
//        getVideosAtFolder(context, folderPath, null, new ProgressCallback<Void, Void, MediaItem>() {
//            @Override
//            public void onProgress(MediaItem mediaItem) {
//                mediator.add(mediaItem);
//            }
//
//            @Override
//            public void onComplete(Void result) { /* Do nothing. */ }
//
//            @Override
//            public void onError(Void error) { /* Do nothing. */ }
//        }, 1);
//
//        return mediator.isEmpty() ? null : mediator.pop();
//    }
//
//    /**
//     * Get Video MediaItem from specific folder.
//     * <p/>
//     * <b>NOTE</b>: MediaItem attributes could be different (miss or inconsistent) with metadata extracted from {@link MediaMetadataRetriever},
//     * to speed up browsing performance, we don't do any metadata retrieval here.
//     */
//    @WorkerThread
//    private static void getVideosAtFolder(Context context, String folderPath, String orderBy, ProgressCallback<Void, Void, MediaItem> callback, int limit) {
//        long timestamp = System.currentTimeMillis();
//        if (DEBUG_INFO) Log.d(TAG, "[getVideosAtFolder] start");
//
//        String[] projection = {
//                VideoColumns._ID,
//                VideoColumns.MIME_TYPE,
//                VideoColumns.SIZE,
//                VideoColumns.DURATION,
//                VideoColumns.DATE_TAKEN,
//                VideoColumns.DATA,
//                VideoColumns.WIDTH,
//                VideoColumns.HEIGHT,
//                VideoColumns.BUCKET_DISPLAY_NAME
//        };
//        String selection = VideoColumns.DATA + " LIKE ? AND " + VideoColumns.DATA + " NOT GLOB ? ";
//        String[] selectionArgs = {
//                folderPath + "/%",
//                folderPath + "/*/*"
//        };
//        Cursor cursor = null;
//        try {
//            cursor = context.getContentResolver().query(URI_VIDEO_MEDIA, projection, selection, selectionArgs, orderBy);
//            if (cursor == null) {
//                if (DEBUG_INFO) Log.d(TAG, "[getVideosAtFolder] no cursor");
//                callback.error();
//                return;
//            }
//
//            final int idxId = cursor.getColumnIndexOrThrow(VideoColumns._ID);
//            final int idxMimeType = cursor.getColumnIndexOrThrow(VideoColumns.MIME_TYPE);
//            final int idxSize = cursor.getColumnIndexOrThrow(VideoColumns.SIZE);
//            final int idxDuration = cursor.getColumnIndexOrThrow(VideoColumns.DURATION);
//            final int idxDateTaken = cursor.getColumnIndexOrThrow(VideoColumns.DATE_TAKEN);
//            final int idxData = cursor.getColumnIndexOrThrow(VideoColumns.DATA);
//            final int idxWidth = cursor.getColumnIndexOrThrow(VideoColumns.WIDTH);
//            final int idxHeight = cursor.getColumnIndexOrThrow(VideoColumns.HEIGHT);
//            final int idxBucketName = cursor.getColumnIndexOrThrow(VideoColumns.BUCKET_DISPLAY_NAME);
//
//            long id;
//            String displayName;
//            String mimeType;
//            long size;
//            long duration;
//            int orientation;
//            long dateTaken;
//            String filePath;
//            int width;
//            int height;
//            String bucketName;
//
//            int retrieveCount = 0;
//            while (cursor.moveToNext()) {
//                id = cursor.getLong(idxId);
//                mimeType = cursor.getString(idxMimeType);
//                size = cursor.getLong(idxSize);
//                duration = cursor.getLong(idxDuration) * 1000; // ms -> us
//                dateTaken = cursor.getLong(idxDateTaken);
//                filePath = cursor.getString(idxData);
//                width = cursor.getInt(idxWidth);
//                height = cursor.getInt(idxHeight);
//                bucketName = cursor.getString(idxBucketName);
//                displayName = filePath.substring(1 + filePath.lastIndexOf(File.separatorChar));
//
//                boolean hasCache = MediaFormatPolicy.hasCache(filePath);
//                if (hasCache) {
//                    orientation = MediaFormatPolicy.getOrientationOfSupportedVideo(filePath);
//                } else {
//                    orientation = 0;
//                }
//
//                if (hasCache && (width == 0 || height == 0)) {
//                    width = MediaFormatPolicy.getWidthOfSupportedVideo(filePath);
//                    height = MediaFormatPolicy.getHeightOfSupportedVideo(filePath);
//                }
//
//                final MediaItem mediaItem = MediaItem.createVideo(id, filePath, displayName, mimeType, width, height, size, duration, orientation, dateTaken, bucketName);
//                if (DEBUG_INFO) Log.d(TAG, "[getVideosAtFolder] > " + mediaItem);
//                callback.progress(mediaItem);
//
//                retrieveCount++;
//                if (limit > 0 && retrieveCount >= limit) { // limit <= 0 means unlimited.
//                    if (DEBUG_INFO) Log.d(TAG, "[getVideosAtFolder] > reach limit: " + retrieveCount + "/" + limit);
//                    break;
//                }
//            }
//
//            if (DEBUG) Log.v(TAG, "[getVideosAtFolder] took " + (System.currentTimeMillis() - timestamp) + "ms");
//            callback.complete();
//        } catch (Exception e) {
//            if (DEBUG) Log.e(TAG, "[getVideosAtFolder] something was wrong", e);
//            callback.error();
//        } finally {
//            IOUtils.closeQuietly(cursor);
//        }
//    }
//
//    @WorkerThread
//    public static void getAudiosAtFolder(Context context, String folderPath, String orderBy, ProgressCallback<Void, Void, MediaItem> callback) {
//        getAudiosAtFolder(context, folderPath, orderBy, callback, 0);
//    }
//
//    @Nullable
//    @WorkerThread
//    public static MediaItem getAudioAtFolder(Context context, String folderPath) {
//        // XXX: Use Stack to be a mediator because Java doesn't allow inner class to access outer variables without final keyword.
//        // TRICK: Use asynchronous style (but implement as synchronous within actual method implementation) to get desired MediaItem.
//        final Stack<MediaItem> mediator = new Stack<>();
//        getAudiosAtFolder(context, folderPath, null, new ProgressCallback<Void, Void, MediaItem>() {
//            @Override
//            public void onProgress(MediaItem mediaItem) {
//                mediator.add(mediaItem);
//            }
//
//            @Override
//            public void onComplete(Void result) { /* Do nothing. */ }
//
//            @Override
//            public void onError(Void error) { /* Do nothing. */ }
//        }, 1);
//
//        return mediator.isEmpty() ? null : mediator.pop();
//    }
//
//    @WorkerThread
//    private static void getAudiosAtFolder(Context context, String folderPath, String orderBy, ProgressCallback<Void, Void, MediaItem> callback, int limit) {
//        long timestamp = System.currentTimeMillis();
//        if (DEBUG_INFO) Log.d(TAG, "[getAudiosAtFolder] start");
//
//        String[] projection = {
//                AudioColumns._ID,
//                AudioColumns.DISPLAY_NAME,
//                AudioColumns.MIME_TYPE,
//                AudioColumns.SIZE,
//                AudioColumns.ALBUM_ID,
//                AudioColumns.DURATION,
//                AudioColumns.DATA,
//                AudioColumns.ARTIST
//        };
//        String selection = AudioColumns.DATA + " LIKE ? AND " + AudioColumns.DATA + " NOT GLOB ? ";
//        String[] selectionArgs = {
//                folderPath + "/%",
//                folderPath + "/*/*"
//        };
//        Cursor cursor = null;
//        try {
//            cursor = context.getContentResolver().query(URI_AUDIO_MEDIA, projection, selection, selectionArgs, orderBy);
//            if (cursor == null) {
//                if (DEBUG_INFO) Log.d(TAG, "[getAudiosAtFolder] no cursor");
//                callback.error();
//                return;
//            }
//
//            final int idxId = cursor.getColumnIndexOrThrow(AudioColumns._ID);
//            final int idxMimeType = cursor.getColumnIndexOrThrow(AudioColumns.MIME_TYPE);
//            final int idxSize = cursor.getColumnIndexOrThrow(AudioColumns.SIZE);
//            final int idxAlbumId = cursor.getColumnIndexOrThrow(AudioColumns.ALBUM_ID);
//            final int idxDuration = cursor.getColumnIndexOrThrow(AudioColumns.DURATION);
//            final int idxData = cursor.getColumnIndexOrThrow(AudioColumns.DATA);
//            final int idxArtist = cursor.getColumnIndexOrThrow(AudioColumns.ARTIST);
//
//            long id;
//            String displayName;
//            String mimeType;
//            long size;
//            long albumId;
//            long duration;
//            String filePath;
//            String artist;
//
//            int retrieveCount = 0;
//            while (cursor.moveToNext()) {
//                id = cursor.getLong(idxId);
//                mimeType = cursor.getString(idxMimeType);
//                size = cursor.getLong(idxSize);
//                albumId = cursor.getInt(idxAlbumId);
//                duration = cursor.getLong(idxDuration) * 1000; // ms -> us
//                filePath = cursor.getString(idxData);
//                artist = cursor.getString(idxArtist);
//                displayName = filePath.substring(1 + filePath.lastIndexOf(File.separatorChar));
//
//                MediaItem mediaItem = MediaItem.createAudio(id, filePath, displayName, mimeType, size, duration, albumId, artist);
//                if (DEBUG_INFO) Log.d(TAG, "[getAudiosAtFolder] > " + mediaItem);
//                callback.progress(mediaItem);
//
//                retrieveCount++;
//                if (limit > 0 && retrieveCount >= limit) { // limit <= 0 means unlimited.
//                    if (DEBUG_INFO) Log.d(TAG, "[getAudiosAtFolder] > reach limit: " + retrieveCount + "/" + limit);
//                    break;
//                }
//            }
//
//            if (DEBUG) Log.v(TAG, "[getAudiosAtFolder] took " + (System.currentTimeMillis() - timestamp) + "ms");
//            callback.complete();
//        } catch (Exception e) {
//            if (DEBUG) Log.e(TAG, "[getAudiosAtFolder] something was wrong", e);
//            callback.error();
//        } finally {
//            IOUtils.closeQuietly(cursor);
//        }
//    }

    private enum UriToMediaItemHelper {
        FOR_IMAGE (MediaType.IMAGE, URI_IMAGE_MEDIA,
            new String[]{
                    Media._ID
                    , ImageColumns.MIME_TYPE
                    , ImageColumns.SIZE
                    , ImageColumns.ORIENTATION
                    , ImageColumns.DATE_TAKEN
                    , ImageColumns.DATA
                    , ImageColumns.BUCKET_DISPLAY_NAME
            }) {
            @Override
            public MediaItem getMediaItem(Cursor cursor) {
                long id = cursor.getLong(0);
                String mimeType = cursor.getString(1);
                long size = cursor.getLong(2);
                int orientation = cursor.getInt(3);
                long dateTaken = cursor.getLong(4);
                String filePath = cursor.getString(5);
                String bucketName = cursor.getString(6);
                String displayName = filePath.substring(1 + filePath.lastIndexOf(File.separatorChar));
                // TODO
                //MediaFormatPolicy.MediaFormatResult formatResult = MediaFormatPolicy.getResult(new File(filePath), MediaFormatPolicy.Type.IMAGE);

                return MediaItem.createImage(
                        id,
                        filePath,
                        displayName,
                        mimeType,
                        0, //formatResult.getSize().width, // TODO
                        0, //formatResult.getSize().height,
                        size,
                        orientation,
                        dateTaken,
                        bucketName);
            }
        }
//        , FOR_VIDEO (MediaType.VIDEO, URI_VIDEO_MEDIA,
//            new String[]{
//                    VideoColumns._ID
//                    ,VideoColumns.DISPLAY_NAME
//                    ,VideoColumns.MIME_TYPE
//                    ,VideoColumns.SIZE
//                    ,VideoColumns.DATE_TAKEN
//                    ,VideoColumns.DATA
//                    ,VideoColumns.BUCKET_DISPLAY_NAME
//                    ,VideoColumns.WIDTH
//                    ,VideoColumns.HEIGHT
//            }) {
//            @Override
//            public MediaItem getMediaItem(Cursor cursor) {
//                long id = cursor.getLong(0);
//                String strDisplayName = cursor.getString(1);
//                String strMimeType = cursor.getString(2);
//                long size = cursor.getLong(3);
//                long dateTaken = cursor.getLong(4);
//                String filePath = cursor.getString(5);
//                String strBucketName = cursor.getString(6);
//                MediaFormatPolicy.MediaFormatResult formatResult = MediaFormatPolicy.getResult(new File(filePath), MediaFormatPolicy.Type.VIDEO);
//
//                return MediaItem.createVideo(
//                        id,
//                        filePath,
//                        strDisplayName,
//                        strMimeType,
//                        formatResult.getSize().width,
//                        formatResult.getSize().height,
//                        size,
//                        formatResult.durationUs,
//                        formatResult.orientation,
//                        dateTaken,
//                        strBucketName);
//            }
//        }
//        , FOR_AUDIO (MediaType.AUDIO, URI_AUDIO_MEDIA,
//            new String[]{ // TODO : define proper projection
//                    Media._ID
//                    , Media.DATE_MODIFIED
//                    , Media.DATA
//            }) {
//            @Override
//            public MediaItem getMediaItem(Cursor cursor) {
//                return null; // TODO : define proper creation way to fetch from cursor's projections
//            }
//        }
        ;

        public MediaType mMediaType;
        public String[] mProjection;
        public Uri mQueryContentUri;
        public abstract MediaItem getMediaItem(Cursor cursor);

        UriToMediaItemHelper(MediaType type, Uri uri, String[] proj) {
            mMediaType = type;
            mProjection = proj;
            mQueryContentUri = uri;
        }

        static UriToMediaItemHelper getHelper(String type) {
            return FOR_IMAGE;
//            if (type == null) {
//                return FOR_IMAGE;
//            } else if (type.contains("image")) {
//                return FOR_IMAGE;
//            } else if (type.contains("video")) {
//                return FOR_VIDEO;
//            } else if (type.contains("audio")) {
//                return FOR_AUDIO;
//            } else {
//                return FOR_IMAGE;
//            }
        }
    }

//    /** See detail discussion in <a herf="http://stackoverflow.com/questions/28342678/how-to-get-image-from-gallery-which-supports-for-api-19-api19-both">stackoverflow</>
//     *  To fetch special column, consider to use {@link MediaStoreLibrary#extractMetadataFromFilepath(String, int, String)}
//     *  This method is not well tested for parameter fileUri, like SD card's media item
//     *  @param context Required context for using ContentResolver by {@link Context#getContentResolver()}
//     *  @param fileUri Preferred uri is ContentUri (formed as "content://..."), but we still accept FileUri (formed as "file://...")
//     * */
//    @Deprecated
//    public static MediaItem getMediaItemFromUri(Context context, Uri fileUri) {
//        Cursor cursor = null;
//
//        UriToMediaItemHelper helper = UriToMediaItemHelper.getHelper(null);
//        // HONEYCOMB = 11 <= API <= 18 = JELLY_BEAN_MR2
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB &&
//            Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//            CursorLoader cursorLoader = new CursorLoader(context, fileUri, helper.mProjection, null, null, null);
//            cursor = cursorLoader.loadInBackground();
//        }
//        // API >= 19 = KITKAT
//        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            String selection = null;
//            String[] selectionArgs = null;
//            String type = "";
//
//            if ("content".equalsIgnoreCase(fileUri.getScheme())
//                || "file".equalsIgnoreCase(fileUri.getScheme())) {
//                // Get column <MIME_TYPE> in <fileUri> as <type>
//                type = ContentResolverUtils.getQueriedString("", context, fileUri, new String[]{MediaColumns.MIME_TYPE}, null, null, null);
//                selection = MediaColumns.DATA + "=?";
//                String path = FileUtils.getMediaFilePath(context, fileUri);
//                selectionArgs = new String[]{path};
//                if (type == null) {
//                    // TODO : More Testing on the MIME_TYPE column on different videos, like from file://, content://, or cloud (like google drive)
//                    type = extractMetadataFromFilepath(path, MediaMetadataRetriever.METADATA_KEY_MIMETYPE, "");
//                }
//            } else if (DocumentsContract.isDocumentUri(context, fileUri)) {
//                // Prepare for query type, selection, selectionArgs
//                String docId = DocumentsContract.getDocumentId(fileUri);
//                String[] split = docId.split(":");
//
//                type = split[0];
//                selection = MediaColumns._ID + "=?";
//                selectionArgs = new String[]{split[1]};
//            }
//
//            helper = UriToMediaItemHelper.getHelper(type);
//
//            // Get Data Column
//            cursor = context.getContentResolver().query(helper.mQueryContentUri,
//                    helper.mProjection, selection, selectionArgs, null);
//        }
//
//        // Get data from cursor and create media item
//        MediaItem result = null;
//        if (cursor != null) {
//            if (cursor.moveToNext()) {
//                result = helper.getMediaItem(cursor);
//            }
//            IOUtils.closeQuietly(cursor);
//        }
//        return result;
//    }
//
//    public static String extractMetadataFromUri(Context context, Uri uri, int metadataKey, String exceptionValue) {
//        // Performance tracking notes:
//        //   0~1ms : new MediaMetadataRetriever();
//        //   1~3ms : setDataSource
//        //   1~4ms : extractMetadata
//        //   0~2ms : release
//        try {
//            MediaMetadataRetriever fetcher = new MediaMetadataRetriever();
//            fetcher.setDataSource(context, uri);
//            String answer = fetcher.extractMetadata(metadataKey);
//            fetcher.release();
//            return answer;
//        } catch (RuntimeException e) {
//            // IllegalArgumentException for // DRA155018-0001
//            // IllegalStateException for // https://fabric.io/cyberlink/android/apps/com.cyberlink.powerdirector.dra140225_01/issues/562a0d9bf5d3a7f76b03baa0
//            Log.e(TAG, "Exception thrown when fetching metadata from Uri " + uri, e);
//            return exceptionValue;
//        }
//    }
//
//    public static String extractMetadataFromFilepath(String path, int metadataKey, String exceptionValue) {
//        // Performance tracking notes:
//        //   0~1ms : new MediaMetadataRetriever();
//        //   1~3ms : setDataSource
//        //   1~4ms : extractMetadata
//        //   0~2ms : release
//        try {
//            MediaMetadataRetriever fetcher = new MediaMetadataRetriever();
//            fetcher.setDataSource(path);
//            String answer = fetcher.extractMetadata(metadataKey);
//            fetcher.release();
//            return answer;
//        } catch (RuntimeException e) {
//            // IllegalArgumentException for // DRA155018-0001
//            // IllegalStateException for // https://fabric.io/cyberlink/android/apps/com.cyberlink.powerdirector.dra140225_01/issues/562a0d9bf5d3a7f76b03baa0
//            Log.e(TAG, "Exception thrown when fetching metadata from path " + path, e);
//            return exceptionValue;
//        }
//    }
}
