package flyingkite.javaxlibrary.images.create;

import flyingkite.javaxlibrary.images.base.PngParam;

public class PngCreator {
    private PngCreator() { }

    private static final boolean DEBUG = false;

    public static PngCreateRequest from(PngParam param) {
        return new PngCreateRequest(param);
    }
}
