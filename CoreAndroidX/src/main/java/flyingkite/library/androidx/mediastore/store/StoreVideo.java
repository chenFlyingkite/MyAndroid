package flyingkite.library.androidx.mediastore.store;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;

import flyingkite.library.androidx.mediastore.MediaStoreKit;
import flyingkite.library.androidx.mediastore.request.MediaGroupRequest;
import flyingkite.library.androidx.mediastore.request.MediaRequest;

import androidx.annotation.NonNull;

public class StoreVideo extends MediaStoreKit implements StoreUnit {
    public StoreVideo(@NonNull Context c) {
        super(c);
    }

    @Override
    public Uri baseUri() {
        return MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
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
        r.projection = addBucket(r.projection);
        queryRequest(r, "bucket_id", "_null");
    }

    @Override
    public void queryAtFolder(String folder, MediaRequest request) {
        MediaRequest r = applyAtFolder(folder, request);
        queryRequest(r);
    }
}
