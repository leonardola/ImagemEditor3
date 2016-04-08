package imagemeditor;

/**
 * Created by leonardoalbuquerque on 08/04/16.
 */
public class ColorPixels {

    public static int getRed(int pixel) {
        return (pixel >> 16) & 0xff;
    }

    public static int getGreen(int pixel) {
        return (pixel >> 8) & 0xff;
    }

    public static int getBlue(int pixel) {
        return (pixel) & 0xff;
    }

    public static int setPixel(int A, int R, int G, int B) {
        int pixel
                = (A < 255 ? A : 255) << 24
                | (R < 255 ? R : 255) << 16
                | (G < 255 ? G : 255) << 8
                | (B < 255 ? B : 255);

        return pixel;
    }
}
