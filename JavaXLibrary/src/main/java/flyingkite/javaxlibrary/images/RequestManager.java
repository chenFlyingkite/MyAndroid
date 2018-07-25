package flyingkite.javaxlibrary.images;

import flyingkite.javaxlibrary.images.base.PngParam;
import flyingkite.javaxlibrary.images.create.PngCreateRequest;

public class RequestManager {
    @Deprecated
    public static PngCreateRequest from(PngParam param) {
        return new PngCreateRequest(param);
    }
}
