package flyingkite.library.java.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import flyingkite.library.java.data.FileInfo;

public class FileUtil {

    public static boolean isGone(File f) {
        return f == null || !f.exists();
    }

    public static boolean isGone(String s) {
        return s == null || isGone(new File(s));
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

    /**
     * @return true if the named file does not exist and was successfully created; false if the named file already exists
     */
    public static boolean createNewFile(File f) {
        if (f == null) return false;
        if (f.exists() && f.isDirectory()) {
            ensureDelete(f);
        }

        File g = f.getParentFile();
        if (g != null) {
            g.mkdirs();
        }
        if (!f.exists()) {
            try {
                return f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // List files in DFS order
    public static List<File> listAllFiles(File root) {
        List<File> ans = new ArrayList<>();
        if (root == null) return ans;

        ans.add(root);
        if (root.isDirectory()) {
            File[] fs = root.listFiles();
            if (fs != null) {
                List<File> sub;
                for (int i = 0; i < fs.length; i++) {
                    sub = listAllFiles(fs[i]);
                    ans.addAll(sub);
                }
            }
        }
        return ans;
    }

    // List files in BFS order
    public static List<File> listAllFilesBFS(File root) {
        List<File> ans = new ArrayList<>();
        Deque<File> queue = new ArrayDeque<>();
        queue.add(root);
        ans.add(root);
        while (queue.size() > 0) {
            File now = queue.removeFirst();

            if (now.isDirectory()) {
                File[] fs = now.listFiles();
                if (fs != null) {
                    for (int i = 0; i < fs.length; i++) {
                        File it = fs[i];
                        queue.addLast(it);
                        ans.add(it);
                    }
                }
            }
        }
        return ans;
    }

    public static void copy(File dst, File src) {
        if (FileUtil.isGone(src)) return;

        if (src.isDirectory()) {
            dst.mkdirs();
        } else {
            FileUtil.createNewFile(dst);
            try {
                copy(new FileInputStream(src), new FileOutputStream(dst));
            } catch (FileNotFoundException e) {
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
            fos.flush();
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

    public static String getExtension(String path) {
        if (path == null) {
            return null;
        }
        int dot = path.lastIndexOf('.');
        if (dot >= 0) {
            return path.substring(dot + 1);
        } else {
            return "";
        }
    }

    public static String toMbKbB(long size) {
        return toGbMbKbB(size, new boolean[]{false, true, true});
    }

    public static String toGbMbKbB(long size) {
        return toGbMbKbB(size, new boolean[]{true, true, true});
    }

    public static int kb = 1024;
    public static double kbf = 1.0 * kb;

    /**
     * Return a[0:3] so that size = a[0] GB + a[1] MB + a[2] KB + a[3] Bytes
     * @return a[0:3] so that size = a[0] GB + a[1] MB + a[2] KB + a[3] Bytes
     */
    public static long[] toGbMbKbBUnit(long size) {
        //       GB, MB, KB, B
        long[] mod = {0, 0, 0, 0};
        long now = size;
        for (int i = mod.length - 1; i >= 0; i--) {
            mod[i] = now % kb;
            now /= kb;
        }
        return mod;
    }

    public static String toGbMbKbB(long size, boolean[] gbMbKb) {
        long[] mod = toGbMbKbBUnit(size);
        long b = mod[3];
        long kb = mod[2];
        long mb = mod[1];
        long gb = mod[0];

        if (gb > 0 && gbMbKb[0]) {
            double val = gb + mb / kbf;
            return String.format(Locale.US, "%.2f GB", val);
        } else if (mb > 0 && gbMbKb[1]) {
            double val = mb + kb / kbf;
            return String.format(Locale.US, "%.2f MB", val);
        } else if (kb > 0 && gbMbKb[2]) {
            double val = kb + b / kbf;
            return String.format(Locale.US, "%.2f KB", val);
        } else {
            return String.format(Locale.US, "%3d Bytes", b);
        }
    }

    public interface OnDFSFile<T> {
        default void onStart(File f) { }
        // sub = root.listFiles()
        default File[] onFileListed(File root, File[] sub) { return sub; }
        // complete counting size under file
        default void onFileInfo(File f, T info) { }

        default void onFileVisited(int visited, int found) { }
    }

    // Folder count = subfolder counts of it self + 1. (Self is also a folder)
    // For one single file : file count = 1, folder count = 0
    // For an empty folder : file count = 0, folder count = 1
    // If /a/ is directory and its content is /a/t.txt, /a/b, /a/c/z.txt
    // => /a/ has 2 files (of t.txt and z.txt), has 3 folders (of a/ itself, a/b, a/c )
    public static FileInfo getFileInfo(File root, OnDFSFile<FileInfo> listener) {
        if (root == null) {
            return null;
        }
        FileInfo ans = new FileInfo();

        // report
        if (listener != null) {
            listener.onStart(root);
        }

        if (root.isDirectory()) {
            ans.folderCount = 1;
            File[] sub = root.listFiles();
            // report
            if (listener != null) {
                sub = listener.onFileListed(root, sub);
            }
            // core
            if (sub != null) {
                for (int i = 0; i < sub.length; i++) {
                    File g = sub[i];
                    FileInfo it = getFileInfo(g, listener);
                    if (it != null) {
                        ans.fileSize += it.fileSize;
                        ans.fileCount += it.fileCount;
                        ans.folderCount += it.folderCount;
                        ans.lastModified = Math.max(ans.lastModified, it.lastModified);
                    }
                }
            }
        } else {
            // listener
            if (listener != null) {
                listener.onFileListed(root, null);
            }
            // core
            ans.fileSize = root.length();
            ans.fileCount = 1;
            ans.lastModified = root.lastModified();
        }
        // report
        if (listener != null) {
            listener.onFileInfo(root, ans);
        }
        return ans;
    }

    public static Map<File, FileInfo> getFileInfoMap(File root, OnDFSFile<FileInfo> listener) {
        Map<File, FileInfo> map = new HashMap<>();
        final int[] now = {0, 1}; // visited = now[0] / found = now[1]
        getFileInfo(root, new OnDFSFile<>() {
            @Override
            public void onStart(File f) {
                if (listener != null) {
                    listener.onStart(f);
                }
            }

            @Override
            public File[] onFileListed(File parent, File[] sub) {
                now[0]++;
                if (sub != null) {
                    now[1] += sub.length;
                }
                if (listener != null) {
                    listener.onFileVisited(now[0], now[1]);
                    return listener.onFileListed(parent, sub);
                }
                return sub;
            }


            @Override
            public void onFileInfo(File f, FileInfo size) {
                map.put(f, size);
                if (listener != null) {
                    listener.onFileInfo(f, size);
                }
            }
        });
        return map;
    }

    public static boolean isAPK(File f) {
        if (isGone(f)) return false;
        return isAPK(f.getAbsolutePath());
    }

    // aab cannot install
    public static boolean isAPK(String path) {
        return StringUtil.endsOf(path, ".apk");
    }

    public static boolean isTXT(String path) {
        return StringUtil.endsOf(path, ".txt");
    }

    public static boolean isJson(String path) {
        return StringUtil.endsOf(path, ".json");
    }

    public static boolean isPDF(String path) {
        return StringUtil.endsOf(path, ".pdf");
    }

    public static boolean isMicrosoftExcel(String path) {
        return StringUtil.endsOf(path, ".xls", ".xlsx", ".csv");
    }

    public static boolean isMicrosoftWord(String path) {
        return StringUtil.endsOf(path, ".doc", ".docx");
    }

    public static boolean isMicrosoftPowerPoint(String path) {
        return StringUtil.endsOf(path, ".ppt", ".pptx");
    }

    // get next name of format "{$parent}/{$name} {$i}"
    // i = 1, 2, ...
    public static File getUnconflictFile(File parent, String name) {
        List<String> part = new ArrayList<>();
        int dot = name.lastIndexOf('.');
        if (dot >= 0) {
            part.add(name.substring(0, dot)); // name
            part.add(""); // blank
            part.add(name.substring(dot)); // extension
        } else {
            part.add(name);
            part.add(""); // blank
        }

        File dst = new File(parent, name);
        int i = 1;
        while (dst.exists()) {
            part.set(1, " " + i);
            String nextName = StringUtil.join(part, "", "", "");
            dst = new File(parent, nextName);
            i++;
        }
        return dst;
    }
}
