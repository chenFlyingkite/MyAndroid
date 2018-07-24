package flyingkite.tool;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

public class GsonUtil {
    private GsonUtil() {}

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

    public static <T> T loadFile(File file, Class<T> clazz) {
        return load(IOUtil.getReader(file), clazz);
    }

//    public static <T> T loadAsset(String assetFile, Class<T> clazz, AssetManager am) {
//        return load(IOUtil.getReader(assetFile, am), clazz);
//    }

    public static <T> T load(Reader reader, Class<T> clazz) {
        Gson gson = new Gson();
        try {
            if (reader != null) {
                return gson.fromJson(reader, clazz);
            }
        } finally {
            IOUtil.closeIt(reader);
        }
        return null;
    }
}