package flyingkite.images.base;

import flyingkite.files.FileUtil;
import flyingkite.tool.TicTac2;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PngRequest {
    private static final String TAG = "PngRequest";
    protected final boolean DEBUG = false;

    // Tracking performance
    protected TicTac2 mClock = new TicTac2();

    /**
     * @return {@link BufferedImage} to output
     */
    protected BufferedImage getResultImage() {
        return null;
    }


    protected BufferedImage loadImage(String name) {
        return loadImage(new File(name));
    }


    protected BufferedImage loadImage(File f) {
        // Step : Load image by ImageIO
        BufferedImage img = null;
        try {
            img = ImageIO.read(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }

    protected int valueIfNegative(int value, int valueIfNeg) {
        return value < 0 ? valueIfNeg : value;
    }

    /**
     * {@link #into(File)}
     */
    public void into(String name) {
        into(new File(name));
    }

    /**
     * Save the {@link #getResultImage()} into file
     * @param file for output
     */
    public void into(File file) {
        into(getResultImage(), file);
    }

    protected void into(BufferedImage img, File file) {
        FileUtil.ensureDelete(file);
        FileUtil.createFile(file);

        mClock.tic();
        // Step : Write to output
        try {

            ImageIO.write(img, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mClock.tac("ImageIO write >> " + file.getAbsolutePath());
    }

    protected int noColor(int index) {
        // R, G, B, Cyan
        final int[] clrDbg = {0xFFFF0000, 0xFF00FF00, 0xFF0000FF, 0xFF00FFFF};
        // All transparent
        final int[] colors = {0x00000000, 0x00000000, 0x00000000, 0x00000000};
        int[] color = DEBUG ? clrDbg : colors;
        return color[index];
    }
}
