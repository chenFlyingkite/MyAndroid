package com.flyingkite.library;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class Zipper {
    private static final String TAG = "Zipper";
    private Zipper() {}

    private static boolean showLog = true;

    public static void setShowLog(boolean show) {
        showLog = show;
    }

    /**
     * Unzip the srcFile into dstFolder
     *
     * @param srcFile Source Zip file to be unzipped
     * @param dstFolder Destination folder where to put the extracted files
     */
    public static File unzip(File dstFolder, File srcFile) {
        if (dstFolder == null || srcFile == null) return null;

        try {
            return _unzip(dstFolder, new FileInputStream(srcFile));
        } catch (IOException e) { // or FileNotFoundException
            e.printStackTrace();
            Log.e(TAG, e.toString());
            return null;
        }
    }

    public static File unzip(File dstFolder, final InputStream fis) {
        if (dstFolder == null || fis == null) return null;

        try {
            return _unzip(dstFolder, fis);
        } catch (IOException e) { // or FileNotFoundException
            e.printStackTrace();
            Log.e(TAG, e.toString());
            return null;
        }
    }

    private static ZipInputStream makeZis(InputStream fis) throws FileNotFoundException {
        return new ZipInputStream(new BufferedInputStream(fis));
    }

    private static File _unzip(File dstFolder, InputStream fis) throws IOException {
        dstFolder.mkdirs();
        ZipInputStream zis = makeZis(fis);

        ZipEntry ze;
        while ((ze = zis.getNextEntry()) != null) {
            String filename = ze.getName();

            File dstFile = new File(dstFolder, filename);
            if (showLog) {
                Log.v(TAG, "Extracting file: " + dstFile.getAbsolutePath());
            }

            if (ze.isDirectory()) {
                continue;
            }

            // Handle for cases that ZipInputStream's entry reaches file and then file's parent folder
            // Like "/a/b" and then next is "/a"
            dstFile.getParentFile().mkdirs();

            // Read from zis and flush to dstFile
            FileOutputStream fos = new FileOutputStream(dstFile);
            FileUtil.copy(zis, fos);
            zis.closeEntry();
        }

        return dstFolder;
    }
}
