package flyingkite.library.android.media;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import flyingkite.library.java.util.FileUtil;

public class MimeTypeMapUtil {
    // http://androidxref.com/4.4.4_r1/xref/libcore/luni/src/main/java/libcore/net/MimeUtils.java
    // https://stackoverflow.com/questions/23385520/android-available-mime-types
    /**
     * return mime.startsWith(prefix) where mime = {@link #getMimeTypeFromExtension(String)}
     */
    public static boolean isPrefixOfWithMimeTypeFromExtension(String prefix, String path) {
        if (TextUtils.isEmpty(prefix) || TextUtils.isEmpty(path)) {
            return false;
        }
        String ext = FileUtil.getExtension(path);
        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        if (mime != null) {
            return mime.startsWith(prefix);
        }
        return false;
    }

    public static String getMimeTypeFromExtension(String path) {
        if (TextUtils.isEmpty(path)) {
            return "";
        }
        String ext = FileUtil.getExtension(path);
        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        return mime;
    }
}
