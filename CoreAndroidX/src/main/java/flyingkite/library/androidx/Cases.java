package flyingkite.library.androidx;

import android.graphics.Color;

public class Cases {
    private static final int MAX = 8; // Cases = 1 ~ MAX
    public static int x = 1;

    private static final int[] colors = {-1
            , Color.TRANSPARENT
            , Color.argb(0xB0, 0x80, 0x00, 0x00) // R
            , Color.argb(0xB0, 0x00, 0x80, 0x00) // G
            , Color.argb(0xB0, 0x00, 0x00, 0x80) // B
            , Color.argb(0xB0, 0x00, 0x80, 0x80) // C
            , Color.argb(0xB0, 0x80, 0x80, 0x00) // Y
            , Color.argb(0xB0, 0x80, 0x00, 0x80) // Magenta
            , Color.argb(0xB0, 0x80, 0x80, 0x80)}; // W

    public static int getColor(int index) {
        return colors[index];
    }

    public static int getColorX() {
        return colors[x];
    }

    public static void next() {
        int next = x + 1;
        if (next > MAX) {
            x = 1;
        } else {
            x = next;
        }
    }

    public static boolean is(int i) { return x == i; }

    public static boolean is(int... args) {
        for (int i : args) {
            if (is(i)) {
                return true;
            }
        }
        return false;
    }
}
