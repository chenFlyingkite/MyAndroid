package flyingkite.images.diff;

import flyingkite.images.base.PngParam;

public class PngDiffer {
    private PngDiffer() {}

    public static PngDiffRequest from(PngParam p) {
        return new PngDiffRequest(p);
    }
}
