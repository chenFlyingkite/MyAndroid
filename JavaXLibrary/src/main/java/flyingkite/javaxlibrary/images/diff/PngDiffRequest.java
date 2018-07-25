package flyingkite.javaxlibrary.images.diff;

import flyingkite.javaxlibrary.images.base.PngParam;
import flyingkite.javaxlibrary.images.base.PngRequest;
import flyingkite.math.MathUtil;
import flyingkite.log.L;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

public class PngDiffRequest extends PngRequest {
    private static final String TAG = "PngDiffRequest";
    private static final String diffName = "diff.png";

    // Main components
    private PngParam reqParam;
    private BufferedImage imgA;
    private BufferedImage imgB;
    private File diffFolder;
    private File diffFile;

    public PngDiffRequest(PngParam param) {
        reqParam = param;
        diffFolder = param.file;
        diffFile = new File(diffFolder, diffName);
        mClock.enable(false);

        // Step : List the pngs in folder, only accept diff 2 files
        File f = diffFolder;
        File[] child = f.listFiles((name) -> {
            return name.isFile() && !name.getName().equals(diffName);
        });
        if (child == null) {
            L.log("No file to diff in %s", f);
            return;
        }
        if (child.length != 2) {
            L.log("Only can diff 2 files,\nFound = %s", Arrays.toString(child));
            return;
        }

        // Step : Load the pngs as Image
        imgA = loadImage(child[0]);
        imgB = loadImage(child[1]);

        if (imgA == null || imgB == null) {
            L.log("Fail to load image A = %s, B = %s", child[0], child[1]);
            return;
        }
    }

    public void diff() {
        // Step : Start diff, same = its color, diff -> magenta
        diff(0xFFFF00FF);
    }

    public void diff(int diffColor) {
        if (imgA == null || imgB == null) return;

        final int wa = imgA.getWidth();
        final int ha = imgA.getHeight();
        final int wb = imgB.getWidth();
        final int hb = imgB.getHeight();
        final int maxW = Math.max(wa, wb);
        final int maxH = Math.max(ha, hb);

        // Step : Prepare the diff file
        BufferedImage diff = new BufferedImage(maxW, maxH, BufferedImage.TYPE_INT_ARGB);

        // Step : Start diff, same = its color, diff -> diffColor
        RGBInfo infoA;
        RGBInfo infoB;
        mClock.tic();
        for (int i = 0; i < maxW; i++) {
            for (int j = 0; j < maxH; j++) {
                infoA = new RGBInfo(imgA, i, j);
                infoB = new RGBInfo(imgB, i, j);
                int x = (infoA.hasColor ? 2 : 0)
                        + (infoB.hasColor ? 1 : 0);
                switch (x) {
                    case 0:
                        break;
                    case 1: // No A, Has B
                        diff.setRGB(i, j, infoB.argb);
                        break;
                    case 2: // Has A, No B
                        diff.setRGB(i, j, infoA.argb);
                        break;
                    case 3:
                        int col = diffColor;
                        if (infoA.argb == infoB.argb) {
                            col = infoA.argb;
                        }
                        diff.setRGB(i, j, col);
                        break;
                    default:
                        throw new NullPointerException();
                }
            }
        }
        mClock.tac(TAG + " diff OK");

        // Step : Write to file
        into(diff, diffFile);
    }

    private static class RGBInfo {
        private boolean hasColor;
        private int argb;

        private RGBInfo(BufferedImage image, int x, int y) {
            hasColor = MathUtil.isInRange(x, 0, image.getWidth()) &&
                    MathUtil.isInRange(y, 0, image.getHeight());

            if (hasColor) {
                argb = image.getRGB(x, y);
            }
        }
    }
}
