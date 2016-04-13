package imagemeditor;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * Created by leonardoalbuquerque on 12/04/16.
 */
public class RasterCreator {

    public static int[] getPixelsFromRaster(WritableRaster raster, int width, int height) {

        int[] pixels = new int[width * height * ColorConstants.NUMBER_OF_COLOURS];
        raster.getPixels(0, 0, width, height, pixels);

        return pixels;
    }

    public static int[] getPixelsFromRaster(WritableRaster raster) {
        int height = raster.getHeight();
        int width = raster.getWidth();

        int[] pixels = new int[width * height * ColorConstants.NUMBER_OF_COLOURS];
        raster.getPixels(0, 0, width, height, pixels);

        return pixels;
    }

    public static int[] getAlphaRasterFromImage(BufferedImage image) {
        int height = image.getHeight();
        int width = image.getWidth();

        WritableRaster raster = image.getRaster();

        int[] pixels = new int[width * height];
        raster.getPixels(0, 0, width, height, pixels);

        return pixels;
    }

    public static int[] getRgbRasterFromImage(BufferedImage image, int[] pixels) {
        int numberOfPixels = image.getHeight() * image.getWidth();

        int[] alphaPixels = RasterCreator.getAlphaRasterFromImage(image);

        int[] rgb = new int[numberOfPixels];

        for (int i = 0; i < numberOfPixels; i++) {
            rgb[i] = 0xFF000000 | (pixels[i * 3] << 16) | (pixels[i * 3 + 1] << 8) | (pixels[i * 3 + 2] << 0);
        }

        return rgb;
    }

    public static int[] getGreyRasterFromImage(BufferedImage image, int[] pixels) {

        int[] rgbPixels = getRgbRasterFromImage(image, pixels);
        int[] greyPixels = new int[rgbPixels.length];

        for (int i = 0; i < rgbPixels.length; i++) {
            greyPixels[i] = BlackAndWhite.getGray(rgbPixels[i]);
        }

        return greyPixels;
    }

    /*public static int[] getGreyRasterFromImage(BufferedImage image, int[] pixels) {

        int[] rgbPixels = getRgbRasterFromImage(image, pixels);
        int[] greyPixels = new int[rgbPixels.length];

        int[] greyPixels = new int[image.getHeight() * image.getWidth()];

        for (int i = 0; i < pixels.length / 3; i++) {

            Color color = new Color(pixels[i * 3], pixels[i * 3 + 1], pixels[i * 3 + 2]);

            greyPixels[i] = color.getRGB();
        }

        return greyPixels;
    }*/
}
