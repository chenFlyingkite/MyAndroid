package flyingkite.library.android.media;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.text.TextUtils;

import flyingkite.library.android.log.Loggable;

public class MediaMetadataRetrieverUtil {
    private static final Loggable z = new Loggable() {};

    public static String extractMetadataFromUri(Context context, Uri uri, int metadataKey, String exceptionValue) {
        // Performance tracking notes:
        //   0~1ms : new MediaMetadataRetriever();
        //   1~3ms : setDataSource
        //   1~4ms : extractMetadata
        //   0~2ms : release
        MediaMetadataRetriever fetcher = null;
        try {
            fetcher = new MediaMetadataRetriever();
            fetcher.setDataSource(context, uri);
            String answer = fetcher.extractMetadata(metadataKey);
            return answer;
        } catch (RuntimeException e) {
            // IllegalArgumentException for // DRA155018-0001
            // IllegalStateException for // https://fabric.io/cyberlink/android/apps/com.cyberlink.powerdirector.dra140225_01/issues/562a0d9bf5d3a7f76b03baa0
            e.printStackTrace();
            return exceptionValue;
        } finally {
            if (fetcher != null) {
                fetcher.release();
            }
        }
    }

    public static String extractMetadataFromFilepath(String path, int metadataKey, String exceptionValue) {
        // Performance tracking notes:
        //   0~1ms : new MediaMetadataRetriever();
        //   1~3ms : setDataSource
        //   1~4ms : extractMetadata
        //   0~2ms : release
        MediaMetadataRetriever fetcher = null;
        try {
            fetcher = new MediaMetadataRetriever();
            fetcher.setDataSource(path);
            String answer = fetcher.extractMetadata(metadataKey);
            return answer;
        } catch (RuntimeException e) {
            e.printStackTrace();
            // IllegalArgumentException for // DRA155018-0001
            // IllegalStateException for // https://fabric.io/cyberlink/android/apps/com.cyberlink.powerdirector.dra140225_01/issues/562a0d9bf5d3a7f76b03baa0
            return exceptionValue;
        } finally {
            if (fetcher != null) {
                fetcher.release();
            }
        }
    }

    public static long extractMetadataFromFilepath(String path, int metadataKey, long exceptionValue) {
        String s = extractMetadataFromFilepath(path, metadataKey, "");
        long ans = exceptionValue;
        if (!TextUtils.isEmpty(s)) {
            try {
                ans = Long.parseLong(s);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return ans;
    }
}
