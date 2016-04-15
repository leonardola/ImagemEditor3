package imagemeditor;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * Created by leonardoalbuquerque on 12/04/16.
 */
public class RasterCreator {

    private static void getPartialGreyRasterFromImage(int[] greyPixels, int[] rgbPixels, int start, int end) {

        for (int i = start; i < end; i++) {
            greyPixels[i] = BlackAndWhite.getGray(rgbPixels[i]);
        }

    }

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

        //int[] alphaPixels = RasterCreator.getAlphaRasterFromImage(image);

        int[] rgb = new int[numberOfPixels];

        long startTime = System.nanoTime();

        for (int i = 0; i < numberOfPixels; i++) {
            rgb[i] = 0xFF000000 | (pixels[i * 3] << 16) | (pixels[i * 3 + 1] << 8) | (pixels[i * 3 + 2] << 0);
        }

        long endTime = System.nanoTime();

        long duration = (endTime - startTime);
        System.out.println("Tempo sequencial de criacao do array de rgb: " + duration);

        return rgb;
    }

    public static int[] getGreyRasterFromImage(BufferedImage image, int[] pixels) {

        int[] rgbPixels = getRgbRasterFromImage(image, pixels);
        int[] greyPixels = new int[rgbPixels.length];

        long startTime = System.nanoTime();

        int numberOfCores = ThreadConstants.NUMBER_OF_CORES;

        Thread[] partialGreyRasterThreads = new Thread[numberOfCores];

        int chunkSize = rgbPixels.length / numberOfCores;

        int start = 0;
        int end = chunkSize;

        for (int i = 0; i < numberOfCores; i++) {

            final int startFinal = start;
            final int endFinal = end;

            Runnable partial = new Runnable() {
                @Override
                public void run() {
                    getPartialGreyRasterFromImage(greyPixels, rgbPixels, startFinal, endFinal);
                }
            };

            partialGreyRasterThreads[i] = new Thread(partial);
            partialGreyRasterThreads[i].start();

            start = end;
            end += chunkSize;

        }

        for (int i = 0; i < numberOfCores; i++) {
            try{
                partialGreyRasterThreads[i].join();
            }catch (Exception e){
                System.out.println("Erro ao sincronizar thread de criação de raster preto e branco");
            }
        }

        long endTime = System.nanoTime();

        long duration = (endTime - startTime);
        System.out.println("Tempo threaded de criacao do array preto e branco: " + duration);

        return greyPixels;
    }
}
