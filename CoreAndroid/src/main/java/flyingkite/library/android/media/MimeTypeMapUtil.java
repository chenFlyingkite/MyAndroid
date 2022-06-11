package flyingkite.library.android.media;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import flyingkite.library.java.util.FileUtil;

public class MimeTypeMapUtil {
    // http://androidxref.com/4.4.4_r1/xref/libcore/luni/src/main/java/libcore/net/MimeUtils.java
    // https://stackoverflow.com/questions/23385520/android-available-mime-types
    public static boolean getMimeTypeFromExtension(String prefix, String path) {
        if (TextUtils.isEmpty(prefix) || TextUtils.isEmpty(path)) {
            return false;
        }
        String ext = FileUtil.getExtension(path);
        String map = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        //Log.e("TAG", "isMimeTypeLike: " + map + ", " + prefix + ", ext = " + ext);
        if (map != null) {
            return map.startsWith(prefix);
        }
        return false;
    }
}
