package com.flyingkite.library.mediastore.store;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;

import com.flyingkite.library.mediastore.MediaStoreKit;
import com.flyingkite.library.mediastore.request.MediaGroupRequest;
import com.flyingkite.library.mediastore.request.MediaRequest;

import androidx.annotation.NonNull;

public class StoreImages extends MediaStoreKit implements StoreUnit {
    public StoreImages(@NonNull Context c) {
        super(c);
    }

    @Override
    public Uri baseUri() {
        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
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
