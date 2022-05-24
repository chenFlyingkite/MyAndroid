package flyingkite.library.java.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

public class GsonUtil {

    public static void writeFile(File file, String msg) {
        PrintWriter fos = null;
        try {
            fos = new PrintWriter(file);
            fos.print(msg);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeIt(fos);
        }
    }

    public static <T> T fromFile(File file, Class<T> clazz) {
        return from(IOUtil.getReader(file), clazz);
    }

    public static <T> T from(Reader reader, Class<T> clazz) {
        if (reader == null) return null;

        Gson gson = new Gson();
        try {
            return gson.fromJson(reader, clazz);
        } finally {
            IOUtil.closeIt(reader);
        }
    }

    public static <T> T from(String src, Class<T> clazz) {
        Gson g = new Gson();
        T ans = null;
        try {
            ans = g.fromJson(src, clazz);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return ans;
    }
}