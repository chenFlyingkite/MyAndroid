package com.flyingkite.library.mediastore.listener;

import android.database.Cursor;

import java.util.List;

public interface DataListener<T> {
    default void onPreExecute() {}

    default void onQueried(int count, Cursor cursor) {}

    default void onProgress(int position, int count, T data) {}

    default void onComplete(List<T> all) {}

    default void onError(Exception error) {}
}
