package com.flyingkite.library.util;

import android.content.res.AssetManager;

public class GsonUtil extends flyingkite.tool.GsonUtil {

    public static <T> T loadAsset(String assetFile, Class<T> clazz, AssetManager am) {
        return load(IOUtil.getReader(assetFile, am), clazz);
    }
}