package flyingkite.javaxlibrary.images.base;

import java.io.File;

public class PngParam {
    public File file;
    public int w = -1; // -1 = Use parent's size
    public int h = -1;

    public PngParam(String name) {
        this(new File(name));
    }

    public PngParam(File f) {
        file = f;
    }

    public PngParam size(int width, int height) {
        w = width;
        h = height;
        return this;
    }

    public PngParam copy() {
        PngParam p = new PngParam(file.getAbsolutePath());
        p.w = w;
        p.h = h;
        return p;
    }
}
