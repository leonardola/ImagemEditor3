package imagemeditor;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 * Created by leonardoalbuquerque on 08/04/16.
 */
public class Threshold {

    public static BufferedImage thresholdImg(BufferedImage im, JProgressBar barraProgresso, JLabel campoTexto) {
        BufferedImage image = BlackAndWhite.imageToBW(im);

        int width = image.getWidth();
        int height = image.getHeight();

        campoTexto.setText("Criando o histograma");

        WritableRaster raster = image.getRaster();

        int[] pixels = RasterCreator.getPixelsFromRaster(raster, width, height);
        int[] greyPixels = RasterCreator.getGreyRasterFromImage(image, pixels);

        long media = getGreyHistogramMedium(image, greyPixels);

        campoTexto.setText("Definindo o thresold");

        long startTime = System.nanoTime();

        for (int i = 0; i < greyPixels.length; i++) {
            if (greyPixels[i] > media) {
                pixels[i * 3] = 255;
                pixels[i * 3 + 1] = 255;
                pixels[i * 3 + 2] = 255;
            } else {
                pixels[i * 3] = 0;
                pixels[i * 3 + 1] = 0;
                pixels[i * 3 + 2] = 0;
            }
        }

        long endTime = System.nanoTime();

        long duration = (endTime - startTime);
        System.out.println("Tempo sequencial de threshold: " + duration);

        BufferedImage localImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster localRaster = localImage.getRaster();

        localRaster.setPixels(0, 0, width, height, pixels);

        campoTexto.setText("Thresold pronto");
        return localImage;
    }

    private static long getGreyHistogramMedium(BufferedImage image, int[] greyPixels) {

        int width = image.getWidth();
        int height = image.getHeight();

        long medium = 0;

        long startTime = System.nanoTime();

        for (int i = 0; i < greyPixels.length; i++) {
            medium += greyPixels[i];
        }

        long endTime = System.nanoTime();

        long duration = (endTime - startTime);
        System.out.println("Tempo sequencial criação média: " + duration);


        medium /= height * width;

        return medium;
    }


}
