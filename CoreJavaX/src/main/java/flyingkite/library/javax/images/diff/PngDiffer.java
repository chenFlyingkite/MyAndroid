package com.flyingkite.javaxlibrary.images.diff;

import com.flyingkite.javaxlibrary.images.base.PngParam;

public class PngDiffer {
    private PngDiffer() {}

    public static PngDiffRequest from(PngParam p) {
        return new PngDiffRequest(p);
    }
}
