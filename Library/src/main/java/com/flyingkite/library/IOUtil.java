package com.flyingkite.library;

import java.io.Closeable;
import java.io.IOException;

public class IOUtil {
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
}
