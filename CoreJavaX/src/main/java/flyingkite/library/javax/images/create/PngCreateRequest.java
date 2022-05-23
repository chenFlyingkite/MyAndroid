package com.flyingkite.javaxlibrary.images.create;

import com.flyingkite.javaxlibrary.images.base.PngParam;
import com.flyingkite.javaxlibrary.images.base.PngRequest;
import flyingkite.library.java.math.MathUtil;
import flyingkite.library.java.data.Rect2;
import flyingkite.library.java.log.L;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

public class PngCreateRequest extends PngRequest {
    private static final String TAG = "PngCreateRequest";

    // Main components
    private PngParam reqParam;
    private BufferedImage srcImg;
    private BufferedImage dstImg;
    private Rect2 allDstRect = new Rect2();

    public PngCreateRequest(PngParam param) {
        File f = param.file;
        reqParam = param;

        mClock.enable(false); // print log or not
        mClock.tic();
        // Step : Load image of PNG
        srcImg = loadImage(f);
        mClock.tac("ImageIO read << " + f.getAbsolutePath());

        if (srcImg == null) {
            L.log("Fail to load image from %s", f);
            return;
        }

        int w = valueIfNegative(param.w, srcImg.getWidth());
        int h = valueIfNegative(param.h, srcImg.getHeight());

        dstImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        allDstRect.set(0, 0, w, h);
    }

    @Override
    protected BufferedImage getResultImage() {
        return dstImg;
    }

    public PngCreateRequest copy() {
        return copy(new Rect2(0, 0, srcImg.getWidth(), srcImg.getHeight()));
    }

    public PngCreateRequest copy(Rect2 rect) {
        // common used
        final int x = rect.left;
        final int y = rect.top;
        final int w = rect.width();
        final int h = rect.height();

        // Step : Copy image
        mClock.tic();
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int rgba;
                int sx = x + i;
                int sy = y + j;
                boolean valid = MathUtil.isInRange(sx, 0, srcImg.getWidth()) && MathUtil.isInRange(sy, 0, srcImg.getHeight());
                if (valid) {
                    rgba = srcImg.getRGB(sx, sy);
                } else {
                    rgba = 0; // Black if OutOfBound
                }
                dstImg.setRGB(i, j, rgba);
            }
        }
        mClock.tac(TAG + " copy " + rect);
        return this;
    }

    public PngCreateRequest eraseCorners() {
        return eraseCorners(allDstRect, new int[]{9, 7, 5, 4, 3, 2, 2, 1, 1});
    }

    /**
     * Erase the image with transparent color,
     * Range: (x, y, x + w, y + h)
     * @param erase The erase array
     */
    public PngCreateRequest eraseCorners(Rect2 rect, int[] erase) {
        // common used
        final int x = rect.left;
        final int y = rect.top;
        final int w = rect.width();
        final int h = rect.height();

        BufferedImage imgErase = dstImg;
        if (DEBUG) {
            L.log("eraseCorners (w, h, x, y) = (%s, %s, %s, %s)\nerase = %s", w, h, x, y, Arrays.toString(erase));
        }
        // Erase *, A = image, left/right top
        // ********AA*********
        // ******AAAAAA*******
        // *****AAAAAAAAA*****
        // ****AAAAAAAAAAA****
        // ***AAAAAAAAAAAAA***
        // **AAAAAAAAAAAAAAA**
        // **AAAAAAAAAAAAAAA**
        // *AAAAAAAAAAAAAAAAA*
        // *AAAAAAAAAAAAAAAAA*

        mClock.tic();
        // Step : Make 4 corners as transparent
        for (int i = 0; i < erase.length; i++) {
            for (int j = 0; j < erase[i]; j++) {
                int pi = x + i;
                int pj = y + j;
                int ri = x + w - i - 1;
                int rj = y + h - j - 1;
                if (DEBUG) {
                    L.log("pi = %s, pj = %s, ri = %s, rj = %s", pi, pj, ri, rj);
                }
                // Left Top color, 0xARGB
                imgErase.setRGB(pi, pj, noColor(0));
                // Left Bottom color
                imgErase.setRGB(pi, rj, noColor(1));
                // Right Top color
                imgErase.setRGB(ri, pj, noColor(2));
                // Right Bottom color
                imgErase.setRGB(ri, rj, noColor(3));
            }
        }
        mClock.tac(TAG + " erase corner = %s", Arrays.toString(erase));
        return this;
    }

}
