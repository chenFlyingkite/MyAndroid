package flyingkite.images;

import flyingkite.images.base.PngParam;
import flyingkite.images.create.PngCreateRequest;

public class RequestManager {
    @Deprecated
    public static PngCreateRequest from(PngParam param) {
        return new PngCreateRequest(param);
    }
}
