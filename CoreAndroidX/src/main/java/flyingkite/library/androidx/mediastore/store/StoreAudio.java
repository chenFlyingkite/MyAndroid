package flyingkite.library.androidx.mediastore.store;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;

import flyingkite.library.androidx.mediastore.MediaStoreKit;
import flyingkite.library.androidx.mediastore.request.MediaGroupRequest;
import flyingkite.library.androidx.mediastore.request.MediaRequest;

import java.io.File;

import androidx.annotation.NonNull;

public class StoreAudio extends MediaStoreKit implements StoreUnit {
    public StoreAudio(@NonNull Context c) {
        super(c);
    }

    @Override
    public Uri baseUri() {
        return MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    }

    @Override
    public void queryItem(String file, MediaRequest request) {
        MediaRequest r = applyFile(file, request);
        queryRequest(r);
    }

    @Override
    public void queryAllItems(MediaRequest request) {
        MediaRequest r = request;
        queryRequest(r);
    }

    @Override
    public void queryAllFolder(MediaGroupRequest request) {
        MediaGroupRequest r = request;
        r.projection = addData(r.projection);
        queryRequest(r, source -> {
            final String key = MediaStore.MediaColumns.DATA;
            int ki = source.getColumnIndex(key);
            // k = File full path
            String k = source.getString(ki);
            // Change k to parent path
            if (k != null) {
                k = new File(k).getParent();
            }
            if (k == null) {
                k = "_null";
            }
            return k;
        });
    }

    public void queryAllBuckets(MediaGroupRequest request) {
        MediaGroupRequest r = request;
        r.projection = addBucket(r.projection);
        queryRequest(r, "bucket_id", "_null");
    }

    @Override
    public void queryAtFolder(String folder, MediaRequest request) {
        MediaRequest r = applyAtFolder(folder, request);
        queryRequest(r);
    }
}
