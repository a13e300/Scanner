package io.github.a13e300.scanner;

import android.graphics.Bitmap;

import com.google.zxing.common.BitMatrix;

public class Utils {
    private static final int bgColor = 0xFFFFFFFF;
    private static final int fgColor = 0xFF000000;

    public static Bitmap createBitmap(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = matrix.get(x, y) ? fgColor : bgColor;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }
}
