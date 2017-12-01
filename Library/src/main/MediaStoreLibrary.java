package com.cyberlink.actiondirector.libraries;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.MediaColumns;
import android.provider.MediaStore.Video.VideoColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.cyberlink.actiondirector.AppConstants;
import com.cyberlink.actiondirector.BuildConfig;
import com.cyberlink.actiondirector.libraries.MediaItem.MediaType;
import com.cyberlink.actiondirector.util.ContentResolverUtils;
import com.cyberlink.actiondirector.util.MediaFormatPolicy;
import com.cyberlink.service.util.VideoConverterUtil;
import com.cyberlink.util.FileUtils;
import com.cyberlink.util.IOUtils;
import com.cyberlink.util.ProgressCallback;
import com.cyberlink.util.ResultCallback;
import com.cyberlink.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class MediaStoreLibrary {

    private static final String TAG = MediaStoreLibrary.class.getSimpleName();
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final boolean DEBUG_INFO = BuildConfig.DEBUG && false; // Turn it on if R&D need it. Default is off.

    private static final Uri URI_AUDIO_MEDIA = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private static final Uri URI_IMAGE_MEDIA = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private static final Uri URI_VIDEO_MEDIA = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

    // Suppress default constructor for non-instantiability
    private MediaStoreLibrary() {
        throw new AssertionError();
    }

    @WorkerThread
    public static void getImageFolders(@NonNull Context context, @NonNull ResultCallback<List<FolderItem>, Void> callback) {
        getMediaFolders(context, URI_IMAGE_MEDIA, callback);
    }

    @WorkerThread
    public static void getVideoFolders(@NonNull Context context, @NonNull ResultCallback<List<FolderItem>, Void> callback) {
        getMediaFolders(context, URI_VIDEO_MEDIA, callback);
    }

    public static void getAudioFolders(@NonNull Context context, @NonNull ResultCallback<List<FolderItem>, Void> callback) {
        getMediaFolders(context, URI_AUDIO_MEDIA, callback);
    }

    /**
     * Query all folders contains Music, Videos or Pictures (depend on {@code uri} argument).
     * <p/>
     * <b>NOTE</b>: This method execute as <b>synchronous</b> way, and all folders will be callback after all query finished, sorted.
     */
    @WorkerThread
    private static void getMediaFolders(@NonNull Context context, @NonNull Uri uri, @NonNull ResultCallback<List<FolderItem>, Void> callback) {
        long timestamp = System.currentTimeMillis();
        if (DEBUG_INFO) Log.v(TAG, "[getMediaFolders] start");

        String[] projection = {
                MediaColumns.DATA
        };
        Cursor folderCursor = null;
        try {
            folderCursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (folderCursor == null) {
                if (DEBUG_INFO) Log.d(TAG, "[getMediaFolders] no cursor");
                callback.error();
                return;
            }

            final int idxData = folderCursor.getColumnIndexOrThrow(MediaColumns.DATA);
            String filePath;
            File folderFile;
            String folderPath;
            FolderItem folderItem;

            File convertedFolder = null;
            HashMap<String, FolderItem> folders = new HashMap<>();

            // https://ecl.cyberlink.com/Ebug/EbugHandle/HandleMainEbug2.asp?BugCode=ADA165451-0013
            // Don't want to see converted folders in video browsing case.
            if (URI_VIDEO_MEDIA.equals(uri)) {
                convertedFolder = VideoConverterUtil.getConvertedFolder(AppConstants.APP_DATA_FOLDER, AppConstants.CONVERTED_FOLDER);
            }

            while (folderCursor.moveToNext()) {
                // XXX: Some attributes could be null, from PDR-A experience. Skip to next one.
                filePath = folderCursor.getString(idxData);
                if (StringUtils.isEmpty(filePath)) continue;
                folderFile = new File(filePath).getParentFile();
                if (folderFile == null) continue;
                folderPath = folderFile.getAbsolutePath();
                if (StringUtils.isEmpty(folderPath)) continue;

                folderItem = folders.get(folderPath);
                if (folderItem != null) {
                    folderItem.increaseMediaCount();
                } else {
                    if (convertedFolder != null && convertedFolder.equals(folderFile)) continue; // SPEC: Ignore converted folder in browser.
                    folderItem = new FolderItem(folderFile.getName(), folderPath, 1);
                    folders.put(folderPath, folderItem);
                    if (DEBUG_INFO) Log.d(TAG, "[getMediaFolders] > " + folderItem);
                }
            }

            ArrayList<FolderItem> folderItems = new ArrayList<>(folders.values());
            Collections.sort(folderItems, new Comparator<FolderItem>() {
                @Override
                public int compare(FolderItem lhs, FolderItem rhs) {
                    return lhs.getName().compareToIgnoreCase(rhs.getName());
                }
            });

            if (DEBUG) Log.v(TAG, "[getMediaFolders] took " + (System.currentTimeMillis() - timestamp) + "ms");
            callback.complete(folderItems);
        } catch (Exception e) {
            if (DEBUG) Log.e(TAG, "[getMediaFolders] something was wrong", e);
            callback.error();
        } finally {
            IOUtils.closeQuietly(folderCursor);
        }
    }

    @WorkerThread
    public static void getVPAtFolder(Context context, String folderPath, String orderBy, ProgressCallback<Void, Void, MediaItem> callback) {
        getVideosAtFolder(context, folderPath, null, callback);
        getImagesAtFolder(context, folderPath, null, callback);
    }

    @WorkerThread
    public static void getImagesAtFolder(Context context, String folderPath, String orderBy, ProgressCallback<Void, Void, MediaItem> callback) {
        getImagesAtFolder(context, folderPath, orderBy, callback, 0);
    }

    @Nullable
    @WorkerThread
    public static MediaItem getImageAtFolder(Context context, String folderPath) {
        // XXX: Use Stack to be a mediator because Java doesn't allow inner class to access outer variables without final keyword.
        // TRICK: Use asynchronous style (but implement as synchronous within actual method implementation) to get desired MediaItem.
        final Stack<MediaItem> mediator = new Stack<>();
        getImagesAtFolder(context, folderPath, null, new ProgressCallback<Void, Void, MediaItem>() {
            @Override
            public void onProgress(MediaItem mediaItem) {
                mediator.add(mediaItem);
            }

            @Override
            public void onComplete(Void result) { /* Do nothing. */ }

            @Override
            public void onError(Void error) { /* Do nothing. */ }
        }, 1);

        return mediator.isEmpty() ? null : mediator.pop();
    }

    @WorkerThread
    private static void getImagesAtFolder(Context context, String folderPath, String orderBy, ProgressCallback<Void, Void, MediaItem> callback, int limit) {
        long timestamp = System.currentTimeMillis();
        if (DEBUG_INFO) Log.d(TAG, "[getImagesAtFolder] start");

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
        String selection = ImageColumns.DATA + " LIKE ? AND " + ImageColumns.DATA + " NOT GLOB ? ";
        String[] selectionArgs = {
                folderPath + "/%",
                folderPath + "/*/*"
        };
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(URI_IMAGE_MEDIA, projection, selection, selectionArgs, orderBy);
            if (cursor == null) {
                if (DEBUG_INFO) Log.d(TAG, "[getImagesAtFolder] no cursor");
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
                if (DEBUG_INFO) Log.d(TAG, "[getImagesAtFolder] > " + mediaItem);
                callback.progress(mediaItem);

                retrieveCount++;
                if (limit > 0 && retrieveCount >= limit) { // limit <= 0 means unlimited.
                    if (DEBUG_INFO) Log.d(TAG, "[getImagesAtFolder] > reach limit: " + retrieveCount + "/" + limit);
                    break;
                }
            }

            if (DEBUG) Log.v(TAG, "[getImagesAtFolder] took " + (System.currentTimeMillis() - timestamp) + "ms");
            callback.complete();
        } catch (Exception e) {
            if (DEBUG) Log.e(TAG, "[getImagesAtFolder] something was wrong", e);
            callback.error();
        } finally {
            IOUtils.closeQuietly(cursor);
        }
    }

    @WorkerThread
    public static void getVideosAtFolder(Context context, String folderPath, String orderBy, ProgressCallback<Void, Void, MediaItem> callback) {
        getVideosAtFolder(context, folderPath, orderBy, callback, 0);
    }

    @Nullable
    @WorkerThread
    public static MediaItem getVideoAtFolder(Context context, String folderPath) {
        // XXX: Use Stack to be a mediator because Java doesn't allow inner class to access outer variables without final keyword.
        // TRICK: Use asynchronous style (but implement as synchronous within actual method implementation) to get desired MediaItem.
        final Stack<MediaItem> mediator = new Stack<>();
        getVideosAtFolder(context, folderPath, null, new ProgressCallback<Void, Void, MediaItem>() {
            @Override
            public void onProgress(MediaItem mediaItem) {
                mediator.add(mediaItem);
            }

            @Override
            public void onComplete(Void result) { /* Do nothing. */ }

            @Override
            public void onError(Void error) { /* Do nothing. */ }
        }, 1);

        return mediator.isEmpty() ? null : mediator.pop();
    }

    /**
     * Get Video MediaItem from specific folder.
     * <p/>
     * <b>NOTE</b>: MediaItem attributes could be different (miss or inconsistent) with metadata extracted from {@link MediaMetadataRetriever},
     * to speed up browsing performance, we don't do any metadata retrieval here.
     */
    @WorkerThread
    private static void getVideosAtFolder(Context context, String folderPath, String orderBy, ProgressCallback<Void, Void, MediaItem> callback, int limit) {
        long timestamp = System.currentTimeMillis();
        if (DEBUG_INFO) Log.d(TAG, "[getVideosAtFolder] start");

        String[] projection = {
                VideoColumns._ID,
                VideoColumns.MIME_TYPE,
                VideoColumns.SIZE,
                VideoColumns.DURATION,
                VideoColumns.DATE_TAKEN,
                VideoColumns.DATA,
                VideoColumns.WIDTH,
                VideoColumns.HEIGHT,
                VideoColumns.BUCKET_DISPLAY_NAME
        };
        String selection = VideoColumns.DATA + " LIKE ? AND " + VideoColumns.DATA + " NOT GLOB ? ";
        String[] selectionArgs = {
                folderPath + "/%",
                folderPath + "/*/*"
        };
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(URI_VIDEO_MEDIA, projection, selection, selectionArgs, orderBy);
            if (cursor == null) {
                if (DEBUG_INFO) Log.d(TAG, "[getVideosAtFolder] no cursor");
                callback.error();
                return;
            }

            final int idxId = cursor.getColumnIndexOrThrow(VideoColumns._ID);
            final int idxMimeType = cursor.getColumnIndexOrThrow(VideoColumns.MIME_TYPE);
            final int idxSize = cursor.getColumnIndexOrThrow(VideoColumns.SIZE);
            final int idxDuration = cursor.getColumnIndexOrThrow(VideoColumns.DURATION);
            final int idxDateTaken = cursor.getColumnIndexOrThrow(VideoColumns.DATE_TAKEN);
            final int idxData = cursor.getColumnIndexOrThrow(VideoColumns.DATA);
            final int idxWidth = cursor.getColumnIndexOrThrow(VideoColumns.WIDTH);
            final int idxHeight = cursor.getColumnIndexOrThrow(VideoColumns.HEIGHT);
            final int idxBucketName = cursor.getColumnIndexOrThrow(VideoColumns.BUCKET_DISPLAY_NAME);

            long id;
            String displayName;
            String mimeType;
            long size;
            long duration;
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
                duration = cursor.getLong(idxDuration) * 1000; // ms -> us
                dateTaken = cursor.getLong(idxDateTaken);
                filePath = cursor.getString(idxData);
                width = cursor.getInt(idxWidth);
                height = cursor.getInt(idxHeight);
                bucketName = cursor.getString(idxBucketName);
                displayName = filePath.substring(1 + filePath.lastIndexOf(File.separatorChar));

                boolean hasCache = MediaFormatPolicy.hasCache(filePath);
                if (hasCache) {
                    orientation = MediaFormatPolicy.getOrientationOfSupportedVideo(filePath);
                } else {
                    orientation = 0;
                }

                if (hasCache && (width == 0 || height == 0)) {
                    width = MediaFormatPolicy.getWidthOfSupportedVideo(filePath);
                    height = MediaFormatPolicy.getHeightOfSupportedVideo(filePath);
                }

                final MediaItem mediaItem = MediaItem.createVideo(id, filePath, displayName, mimeType, width, height, size, duration, orientation, dateTaken, bucketName);
                if (DEBUG_INFO) Log.d(TAG, "[getVideosAtFolder] > " + mediaItem);
                callback.progress(mediaItem);

                retrieveCount++;
                if (limit > 0 && retrieveCount >= limit) { // limit <= 0 means unlimited.
                    if (DEBUG_INFO) Log.d(TAG, "[getVideosAtFolder] > reach limit: " + retrieveCount + "/" + limit);
                    break;
                }
            }

            if (DEBUG) Log.v(TAG, "[getVideosAtFolder] took " + (System.currentTimeMillis() - timestamp) + "ms");
            callback.complete();
        } catch (Exception e) {
            if (DEBUG) Log.e(TAG, "[getVideosAtFolder] something was wrong", e);
            callback.error();
        } finally {
            IOUtils.closeQuietly(cursor);
        }
    }

    @WorkerThread
    public static void getAudiosAtFolder(Context context, String folderPath, String orderBy, ProgressCallback<Void, Void, MediaItem> callback) {
        getAudiosAtFolder(context, folderPath, orderBy, callback, 0);
    }

    @Nullable
    @WorkerThread
    public static MediaItem getAudioAtFolder(Context context, String folderPath) {
        // XXX: Use Stack to be a mediator because Java doesn't allow inner class to access outer variables without final keyword.
        // TRICK: Use asynchronous style (but implement as synchronous within actual method implementation) to get desired MediaItem.
        final Stack<MediaItem> mediator = new Stack<>();
        getAudiosAtFolder(context, folderPath, null, new ProgressCallback<Void, Void, MediaItem>() {
            @Override
            public void onProgress(MediaItem mediaItem) {
                mediator.add(mediaItem);
            }

            @Override
            public void onComplete(Void result) { /* Do nothing. */ }

            @Override
            public void onError(Void error) { /* Do nothing. */ }
        }, 1);

        return mediator.isEmpty() ? null : mediator.pop();
    }

    @WorkerThread
    private static void getAudiosAtFolder(Context context, String folderPath, String orderBy, ProgressCallback<Void, Void, MediaItem> callback, int limit) {
        long timestamp = System.currentTimeMillis();
        if (DEBUG_INFO) Log.d(TAG, "[getAudiosAtFolder] start");

        String[] projection = {
                AudioColumns._ID,
                AudioColumns.DISPLAY_NAME,
                AudioColumns.MIME_TYPE,
                AudioColumns.SIZE,
                AudioColumns.ALBUM_ID,
                AudioColumns.DURATION,
                AudioColumns.DATA,
                AudioColumns.ARTIST
        };
        String selection = AudioColumns.DATA + " LIKE ? AND " + AudioColumns.DATA + " NOT GLOB ? ";
        String[] selectionArgs = {
                folderPath + "/%",
                folderPath + "/*/*"
        };
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(URI_AUDIO_MEDIA, projection, selection, selectionArgs, orderBy);
            if (cursor == null) {
                if (DEBUG_INFO) Log.d(TAG, "[getAudiosAtFolder] no cursor");
                callback.error();
                return;
            }

            final int idxId = cursor.getColumnIndexOrThrow(AudioColumns._ID);
            final int idxMimeType = cursor.getColumnIndexOrThrow(AudioColumns.MIME_TYPE);
            final int idxSize = cursor.getColumnIndexOrThrow(AudioColumns.SIZE);
            final int idxAlbumId = cursor.getColumnIndexOrThrow(AudioColumns.ALBUM_ID);
            final int idxDuration = cursor.getColumnIndexOrThrow(AudioColumns.DURATION);
            final int idxData = cursor.getColumnIndexOrThrow(AudioColumns.DATA);
            final int idxArtist = cursor.getColumnIndexOrThrow(AudioColumns.ARTIST);

            long id;
            String displayName;
            String mimeType;
            long size;
            long albumId;
            long duration;
            String filePath;
            String artist;

            int retrieveCount = 0;
            while (cursor.moveToNext()) {
                id = cursor.getLong(idxId);
                mimeType = cursor.getString(idxMimeType);
                size = cursor.getLong(idxSize);
                albumId = cursor.getInt(idxAlbumId);
                duration = cursor.getLong(idxDuration) * 1000; // ms -> us
                filePath = cursor.getString(idxData);
                artist = cursor.getString(idxArtist);
                displayName = filePath.substring(1 + filePath.lastIndexOf(File.separatorChar));

                MediaItem mediaItem = MediaItem.createAudio(id, filePath, displayName, mimeType, size, duration, albumId, artist);
                if (DEBUG_INFO) Log.d(TAG, "[getAudiosAtFolder] > " + mediaItem);
                callback.progress(mediaItem);

                retrieveCount++;
                if (limit > 0 && retrieveCount >= limit) { // limit <= 0 means unlimited.
                    if (DEBUG_INFO) Log.d(TAG, "[getAudiosAtFolder] > reach limit: " + retrieveCount + "/" + limit);
                    break;
                }
            }

            if (DEBUG) Log.v(TAG, "[getAudiosAtFolder] took " + (System.currentTimeMillis() - timestamp) + "ms");
            callback.complete();
        } catch (Exception e) {
            if (DEBUG) Log.e(TAG, "[getAudiosAtFolder] something was wrong", e);
            callback.error();
        } finally {
            IOUtils.closeQuietly(cursor);
        }
    }

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
                MediaFormatPolicy.MediaFormatResult formatResult = MediaFormatPolicy.getResult(new File(filePath), MediaFormatPolicy.Type.IMAGE);

                return MediaItem.createImage(
                        id,
                        filePath,
                        displayName,
                        mimeType,
                        formatResult.getSize().width,
                        formatResult.getSize().height,
                        size,
                        orientation,
                        dateTaken,
                        bucketName);
            }
        },
        FOR_VIDEO (MediaType.VIDEO, URI_VIDEO_MEDIA,
            new String[]{
                    VideoColumns._ID
                    ,VideoColumns.DISPLAY_NAME
                    ,VideoColumns.MIME_TYPE
                    ,VideoColumns.SIZE
                    ,VideoColumns.DATE_TAKEN
                    ,VideoColumns.DATA
                    ,VideoColumns.BUCKET_DISPLAY_NAME
                    ,VideoColumns.WIDTH
                    ,VideoColumns.HEIGHT
            }) {
            @Override
            public MediaItem getMediaItem(Cursor cursor) {
                long id = cursor.getLong(0);
                String strDisplayName = cursor.getString(1);
                String strMimeType = cursor.getString(2);
                long size = cursor.getLong(3);
                long dateTaken = cursor.getLong(4);
                String filePath = cursor.getString(5);
                String strBucketName = cursor.getString(6);
                MediaFormatPolicy.MediaFormatResult formatResult = MediaFormatPolicy.getResult(new File(filePath), MediaFormatPolicy.Type.VIDEO);

                return MediaItem.createVideo(
                        id,
                        filePath,
                        strDisplayName,
                        strMimeType,
                        formatResult.getSize().width,
                        formatResult.getSize().height,
                        size,
                        formatResult.durationUs,
                        formatResult.orientation,
                        dateTaken,
                        strBucketName);
            }
        },
        FOR_AUDIO (MediaType.AUDIO, URI_AUDIO_MEDIA,
            new String[]{ // TODO : define proper projection
                    Media._ID
                    , Media.DATE_MODIFIED
                    , Media.DATA
            }) {
            @Override
            public MediaItem getMediaItem(Cursor cursor) {
                return null; // TODO : define proper creation way to fetch from cursor's projections
            }
        };

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
            if (type == null) {
                return FOR_IMAGE; // TODO: Default one?
            } else if (type.contains("image")) {
                return FOR_IMAGE;
            } else if (type.contains("video")) {
                return FOR_VIDEO;
            } else if (type.contains("audio")) {
                return FOR_AUDIO;
            } else {
                return FOR_IMAGE; // TODO: Default one?
            }
        }
    }

    /** See detail discussion in <a herf="http://stackoverflow.com/questions/28342678/how-to-get-image-from-gallery-which-supports-for-api-19-api19-both">stackoverflow</>
     *  To fetch special column, consider to use {@link MediaStoreLibrary#extractMetadataFromFilepath(String, int, String)}
     *  This method is not well tested for parameter fileUri, like SD card's media item
     *  @param context Required context for using ContentResolver by {@link Context#getContentResolver()}
     *  @param fileUri Preferred uri is ContentUri (formed as "content://..."), but we still accept FileUri (formed as "file://...")
     * */
    @Deprecated
    public static MediaItem getMediaItemFromUri(Context context, Uri fileUri) {
        Cursor cursor = null;

        UriToMediaItemHelper helper = UriToMediaItemHelper.getHelper(null);
        // HONEYCOMB = 11 <= API <= 18 = JELLY_BEAN_MR2
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB &&
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            CursorLoader cursorLoader = new CursorLoader(context, fileUri, helper.mProjection, null, null, null);
            cursor = cursorLoader.loadInBackground();
        }
        // API >= 19 = KITKAT
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String selection = null;
            String[] selectionArgs = null;
            String type = "";

            if ("content".equalsIgnoreCase(fileUri.getScheme())
                || "file".equalsIgnoreCase(fileUri.getScheme())) {
                // Get column <MIME_TYPE> in <fileUri> as <type>
                type = ContentResolverUtils.getQueriedString("", context, fileUri, new String[]{MediaColumns.MIME_TYPE}, null, null, null);
                selection = MediaColumns.DATA + "=?";
                String path = FileUtils.getMediaFilePath(context, fileUri);
                selectionArgs = new String[]{path};
                if (type == null) {
                    // TODO : More Testing on the MIME_TYPE column on different videos, like from file://, content://, or cloud (like google drive)
                    type = extractMetadataFromFilepath(path, MediaMetadataRetriever.METADATA_KEY_MIMETYPE, "");
                }
            } else if (DocumentsContract.isDocumentUri(context, fileUri)) {
                // Prepare for query type, selection, selectionArgs
                String docId = DocumentsContract.getDocumentId(fileUri);
                String[] split = docId.split(":");

                type = split[0];
                selection = MediaColumns._ID + "=?";
                selectionArgs = new String[]{split[1]};
            }

            helper = UriToMediaItemHelper.getHelper(type);

            // Get Data Column
            cursor = context.getContentResolver().query(helper.mQueryContentUri,
                    helper.mProjection, selection, selectionArgs, null);
        }

        // Get data from cursor and create media item
        MediaItem result = null;
        if (cursor != null) {
            if (cursor.moveToNext()) {
                result = helper.getMediaItem(cursor);
            }
            IOUtils.closeQuietly(cursor);
        }
        return result;
    }

    public static String extractMetadataFromUri(Context context, Uri uri, int metadataKey, String exceptionValue) {
        // Performance tracking notes:
        //   0~1ms : new MediaMetadataRetriever();
        //   1~3ms : setDataSource
        //   1~4ms : extractMetadata
        //   0~2ms : release
        try {
            MediaMetadataRetriever fetcher = new MediaMetadataRetriever();
            fetcher.setDataSource(context, uri);
            String answer = fetcher.extractMetadata(metadataKey);
            fetcher.release();
            return answer;
        } catch (RuntimeException e) {
            // IllegalArgumentException for // DRA155018-0001
            // IllegalStateException for // https://fabric.io/cyberlink/android/apps/com.cyberlink.powerdirector.dra140225_01/issues/562a0d9bf5d3a7f76b03baa0
            Log.e(TAG, "Exception thrown when fetching metadata from Uri " + uri, e);
            return exceptionValue;
        }
    }

    public static String extractMetadataFromFilepath(String path, int metadataKey, String exceptionValue) {
        // Performance tracking notes:
        //   0~1ms : new MediaMetadataRetriever();
        //   1~3ms : setDataSource
        //   1~4ms : extractMetadata
        //   0~2ms : release
        try {
            MediaMetadataRetriever fetcher = new MediaMetadataRetriever();
            fetcher.setDataSource(path);
            String answer = fetcher.extractMetadata(metadataKey);
            fetcher.release();
            return answer;
        } catch (RuntimeException e) {
            // IllegalArgumentException for // DRA155018-0001
            // IllegalStateException for // https://fabric.io/cyberlink/android/apps/com.cyberlink.powerdirector.dra140225_01/issues/562a0d9bf5d3a7f76b03baa0
            Log.e(TAG, "Exception thrown when fetching metadata from path " + path, e);
            return exceptionValue;
        }
    }
}
