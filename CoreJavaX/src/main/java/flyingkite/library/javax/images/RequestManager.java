package com.flyingkite.javaxlibrary.images;

import com.flyingkite.javaxlibrary.images.base.PngParam;
import com.flyingkite.javaxlibrary.images.create.PngCreateRequest;

public class RequestManager {
    @Deprecated
    public static PngCreateRequest from(PngParam param) {
        return new PngCreateRequest(param);
    }
}
