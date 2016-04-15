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

        int numberOfCores = ThreadConstants.NUMBER_OF_CORES;

        int chunkSize = greyPixels.length / numberOfCores;
        int end = chunkSize;
        int start = 0;

        Thread[] partialThreads = new Thread[numberOfCores];

        for (int i = 0; i < numberOfCores; i++) {

            final int endFinal = end;
            final int startFinal = start;

            Runnable partial = new Runnable() {
                @Override
                public void run() {
                    doPartialThreshold(pixels, greyPixels, startFinal, endFinal, media);
                }
            };

            partialThreads[i] = new Thread(partial);
            partialThreads[i].start();

            start = end;
            end += chunkSize;
        }

        for (int i = 0; i < numberOfCores; i++) {
            try {
                partialThreads[i].join();
            } catch (Exception e) {
                System.out.println("Erro ao unir thread " + i);
            }
        }


        long endTime = System.nanoTime();

        long duration = (endTime - startTime);
        System.out.println("Tempo threaded de threshold: " + duration);

        BufferedImage localImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster localRaster = localImage.getRaster();

        localRaster.setPixels(0, 0, width, height, pixels);

        campoTexto.setText("Thresold pronto");
        return localImage;
    }

    private static long getGreyHistogramMedium(BufferedImage image, int[] greyPixels) {

        int width = image.getWidth();
        int height = image.getHeight();

        long startTime = System.nanoTime();
        int numberOfCores = ThreadConstants.NUMBER_OF_CORES;
        int chunkSize = greyPixels.length / numberOfCores;

        long[] mediums = new long[numberOfCores];

        Thread[] partialGreyHIstogramThreads = new Thread[numberOfCores];

        int start = 0;
        int end = chunkSize;

        for (int i = 0; i < numberOfCores; i++) {

            final int x = i;
            final int endFinal = end;
            final int startFinal = start;

            Runnable partialGrey = new Runnable() {
                @Override
                public void run() {
                    mediums[x] = doPartialGreyHistogram(greyPixels, startFinal, endFinal);
                }
            };

            partialGreyHIstogramThreads[i] = new Thread(partialGrey);
            partialGreyHIstogramThreads[i].start();

            start = end;
            end += chunkSize;
        }

        long medium = 0;
        for (int i = 0; i < numberOfCores; i++) {
            try {
                partialGreyHIstogramThreads[i].join();
                medium += mediums[i];
            } catch (Exception e) {
                System.out.println("Erro ao esperar thread de calculo de media");
            }
        }

        long endTime = System.nanoTime();

        long duration = (endTime - startTime);
        System.out.println("Tempo threaded criação média: " + duration);

        medium /= height * width;

        return medium;
    }

    private static long doPartialGreyHistogram(int[] greyPixels, int start, int end) {
        long medium = 0;

        for (int i = start; i < end; i++) {
            medium += greyPixels[i];
        }

        return medium;
    }

    private static void doPartialThreshold(int[] pixels, int[] greyPixels, int start, int end, long media) {

        for (int i = start; i < end; i++) {
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
    }
}
