package flyingkite.library.androidx.mediastore.request;

import android.net.Uri;
import android.os.CancellationSignal;

import flyingkite.library.androidx.mediastore.listener.DataListener;

import java.util.Arrays;

import flyingkite.library.java.util.ArrayUtil;

public class DataRequest<T> {

    // Query from Uri
    public Uri uri = Uri.EMPTY;

    public String sortOrder;

    public String selection;

    public String[] projection;

    public String[] selectionArgs;

    public CancellationSignal cancelSignal;

    public DataListener<T> listener;

    public <S> DataRequest<S> copy() {
        DataRequest<S> d = new DataRequest<>();
        d.uri = uri;
        d.sortOrder = sortOrder;
        d.selection = selection;
        d.projection = ArrayUtil.copyOf(projection);
        d.selectionArgs = ArrayUtil.copyOf(selectionArgs);
        d.cancelSignal = new CancellationSignal();
        // No copy on listener
        return d;
    }

    @Override
    public String toString() {
        return "- Uri = " + uri +
                "\n- selection = " + selection +
                "\n- project = " + Arrays.toString(projection) +
                "\n- args = " + Arrays.toString(selectionArgs) +
                "\n- order = " + sortOrder + ", cancel = " + cancelSignal +
                "\n- listener = " + listener
                ;
    }
}
