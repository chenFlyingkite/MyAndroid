package flyingkite.library.androidx.mediastore.listener;

import android.database.Cursor;

import java.util.List;

public interface DataListener<T> {
    default void onPreExecute() {}

    default void onQueried(int count, Cursor cursor) {}

    default void onInfo(int position, int count, String info) {}
    default void onProgress(int position, int count, T data) {}

    default void onComplete(List<T> all) {}

    default void onError(Exception error) {}
}
