package com.flyingkite.library.util;

import android.content.res.AssetManager;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class IOUtil {
    private IOUtil() {}

    public static void closeIt(Closeable... cs) {
        if (cs == null) return;

        for (Closeable c : cs) {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static InputStreamReader getReader(String assetFile, AssetManager am) {
        try {
            return new InputStreamReader(am.open(assetFile), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static InputStreamReader getReader(File file) {
        try {
            return new InputStreamReader(new FileInputStream(file), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
