package com.flyingkite.library.util;

import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStreamReader;

public class IOUtil extends flyingkite.tool.IOUtil {

    public static InputStreamReader getReader(String assetFile, AssetManager am) {
        try {
            return new InputStreamReader(am.open(assetFile), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
