package flyingkite.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import flyingkite.tool.IOUtil;

public class FileUtil {

    public static boolean isGone(File f) {
        return f == null || !f.exists();
    }

    /**
     * Delete the file by renaming to another one and call delete.
     * <p>
     * When file is created and deleted and then again created, it will throw
     * FileNotFoundException : open failed: EBUSY (Device or resource busy)
     * So we rename it to safe delete the file.
     * </p><p>
     * Also see
     * <a href="
     * https://stackoverflow.com/questions/11539657/open-failed-ebusy-device-or-resource-busy
     * ">open-failed-ebusy-device-or-resource-busy</a>
     * </p>
     *
     * @param from The file or directory to be safely deleted (so it can be created again in code)
     * @return {@link File#delete()}
     * @see File#delete()
     * */
    public static boolean ensureDelete(File from) {
        if (isGone(from)) return true;

        // When file is created and deleted and then again created, it will throw
        // FileNotFoundException : open failed: EBUSY (Device or resource busy)
        // So we rename it to safe delete the file
        // See https://stackoverflow.com/questions/11539657/open-failed-ebusy-device-or-resource-busy
        final File to = new File(from.getAbsolutePath() + System.currentTimeMillis());
        from.renameTo(to);
        boolean r = deleteAll(to);
        return r;
    }


    /**
     * Delete file, or folder with all files within it.
     *
     * @param file The file or folder that caller want to delete.
     * @return <code>true</code> all files were deleted successfully.
     *         Otherwise, <code>false</code> some files cannot be deleted.
     */
    private static boolean deleteAll(File file) {
        if (isGone(file)) return true;

        boolean r = true;
        if (file.isDirectory()) {
            File[] inner = file.listFiles();
            if (inner != null && inner.length > 0) {
                for (File f : inner) {
                    r &= deleteAll(f);
                }
            }
        }
        r &= file.delete();
        return r;
    }

    public static void createNewFile(File f) {
        if (f == null) return;
        if (f.exists() && f.isDirectory()) {
            ensureDelete(f);
        }

        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void copy(InputStream is, OutputStream fos) {
        if (is == null || fos == null) return;

        try {
            // Read stream and write to file
            int read;
            byte[] buffer = new byte[65536];
            while ((read = is.read(buffer)) != -1) {
                fos.write(buffer, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeIt(is, fos);
        }
    }

    public static List<String> readFromFile(String name) {
        return readFromFile(new File(name));
    }

    public static List<String> readFromFile(File file) {
        if (isGone(file)){
            return Collections.emptyList();
        }

        List<String> contents = new ArrayList<>();
        BufferedReader br = null;
        InputStreamReader is = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            is = new InputStreamReader(fis, "UTF-8");
            br = new BufferedReader(is);

            String line;
            while ((line = br.readLine()) != null) {
                contents.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeIt(fis, is, br);
        }
        return contents;
    }
    public static boolean createFile(File f) {
        if (f == null) return false;

        f.getParentFile().mkdirs();
        boolean b = false;
        try {
            b = f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }
}
