package flyingkite.library.androidx.mediastore.store;

import android.net.Uri;
import android.provider.MediaStore;

import flyingkite.library.androidx.mediastore.request.MediaGroupRequest;
import flyingkite.library.androidx.mediastore.request.MediaRequest;

import java.io.File;

import flyingkite.library.java.util.ArrayUtil;

public interface StoreUnit {

    Uri baseUri();

    void queryItem(String file, MediaRequest request);

    void queryAllItems(MediaRequest request);

    void queryAllFolder(MediaGroupRequest request);

    void queryAtFolder(String folder, MediaRequest request);

    default MediaRequest newRequest() {
        MediaRequest r = new MediaRequest();
        r.uri = baseUri();
        return r;
    }

    default MediaGroupRequest newGroupRequest() {
        MediaGroupRequest r = new MediaGroupRequest();
        r.uri = baseUri();
        return r;
    }

    /**
     * Add request to perform selections in folder and NOT In its sub-folders
     * Append on projection, selection, selectionArgs
     */
    default MediaRequest applyAtFolder(String folder, MediaRequest request) {
        MediaRequest r = request;

        final String data = MediaStore.MediaColumns.DATA;
        String sel = "( " + data + " LIKE ? ) AND ( " + data + " NOT GLOB ? )";
        String[] args = {folder + "/%", folder + "/*/*"};
        String[] proj = addData(r.projection);

        return appendSelection(request, proj, sel, args);
    }

    /**
     * Add request to perform selections in folder and NOT In its sub-folders
     * Append on projection, selection, selectionArgs
     */
    default MediaRequest applyFile(String file, MediaRequest request) {
        MediaRequest r = request;

        final String data = MediaStore.MediaColumns.DATA;
        String sel = data + " = ?";
        String[] args = {file};
        String[] proj = addData(r.projection);

        return appendSelection(request, proj, sel, args);
    }

    /**
     * Add request to perform selections in folder and its sub-folders
     */
    default MediaRequest applyInFolder(String folder, MediaRequest request) {
        MediaRequest r = request;

        final String data = MediaStore.MediaColumns.DATA;
        String sel = data + " LIKE ?";
        String[] args = {folder + "/%"};
        String[] proj = addData(r.projection);

        return appendSelection(request, proj, sel, args);
    }

    /**
     * Append new selections & args in head of query, also adjust its projection
     */
    default MediaRequest appendSelection(MediaRequest request, String[] newProjection, String selection, String[] selectionArgs) {
        MediaRequest r = request;
        // 1. Build new selection
        String sel = r.selection;
        boolean selNull = sel == null;
        if (selNull) {
            sel = selection;
        } else {
            sel = "(( " + selection + " ) AND ( " + sel + " ))";
        }

        // 2. Build new selectionArgs
        // https://www.w3schools.com/sql/sql_like.asp
        String[] args = r.selectionArgs;
        if (selNull) {
            args = selectionArgs;
        } else {
            // selectionArgs + args
            args = ArrayUtil.join(selectionArgs, args, new String[1]);
        }

        r.projection = newProjection;
        r.selection = sel;
        r.selectionArgs = args;
        return r;
    }

    // Add bucket_id and bucket_display_name if missing
    default String[] addBucket(String[] projection) {
        String[] keys = {
                //MediaStore.MediaColumns.BUCKET_ID
                "bucket_id",
                //MediaStore.MediaColumns.BUCKET_DISPLAY_NAME
                "bucket_display_name"
        };

        return sqlAdd(projection, keys);
    }

    // Add _data if missing
    default String[] addData(String[] projection) {
        String[] keys = {
                MediaStore.MediaColumns.DATA
        };

        return sqlAdd(projection, keys);
    }

    // Add _data if missing
    default String[] addAlbum(String[] projection) {
        String[] keys = {
                MediaStore.Audio.AudioColumns.ALBUM
        };

        return sqlAdd(projection, keys);
    }

    default String[] sqlAdd(String[] x, String[] y) {
        if (x == null) {
            return null; // null is usually for all columns
        } else {
            return ArrayUtil.addAtHeadIfMissing(x, y, new String[1]);
        }
    }

    default String parentName(String path, String nullValue) {
        String s = nullValue;
        if (path != null) {
            File f = new File(path).getParentFile();
            if (f != null) {
                s = f.getName();
            }
        }
        return s;
    }
}

