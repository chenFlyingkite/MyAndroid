package flyingkite.library.android.util;

import android.content.res.AssetManager;

public class GsonUtil extends flyingkite.library.java.util.GsonUtil {

    public static <T> T loadAsset(String assetFile, Class<T> clazz, AssetManager am) {
        return load(IOUtil.getReader(assetFile, am), clazz);
    }
}