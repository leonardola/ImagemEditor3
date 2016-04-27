package imagemeditor;

import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 * Created by leonardoalbuquerque on 26/04/16.
 */
public class Equalize {

    private static final float DESVIO = 150;
    private static final float MEDIA = 160;
    private static long[] histogramPartialValues = {0, 0, 0};
    private static int numberOfCores = 4;

    public static BufferedImage equalizeImg(BufferedImage im, JProgressBar barraProgresso, JLabel campoTexto) {
        histogramPartialValues[0] = 0;
        histogramPartialValues[1] = 0;
        histogramPartialValues[2] = 0;

        BufferedImage image = BlackAndWhite.imageToBW(im);
        int k;
        long x, y;
        double mediaImagem, desvioImage;

        int w = image.getWidth(null);
        int height = image.getHeight(null);

        BufferedImage outputImage = new BufferedImage(w, height, BufferedImage.TYPE_BYTE_GRAY);

        campoTexto.setText("Criando o histograma da imagem em cinza");

        /* Calcula o histograma em escala de cinza */
        long startTime = System.nanoTime();

        createHistorgram(image);

        long endTime = System.nanoTime();

        long duration = (endTime - startTime);
        System.out.println("Tempo threaded de histograma: " + duration);

        x = histogramPartialValues[0];
        y = histogramPartialValues[1];
        k = (int) histogramPartialValues[2];

        campoTexto.setText("Histograma pronto");
        /* Compute estimate - mean noise is 0 */
        desvioImage = Math.sqrt((double) (y - x * x / (float) k) / (float) (k - 1));
        mediaImagem = (double) (x / (float) k);

        campoTexto.setText("Fazendo o ajuste baseado na média");


        //média parcial
        startTime = System.nanoTime();

        int chunkSize = height / numberOfCores;
        int start = 0;
        int end = chunkSize;

        Thread[] chunkThreads = new Thread[numberOfCores];

        for (int i = 0; i < numberOfCores; i++) {
            final int startLocal = start;
            final int endLocal = end;

            Runnable chunk = new Runnable() {
                @Override
                public void run() {
                    ajustPartialMedium(0, height, mediaImagem, desvioImage, image, outputImage);
                }
            };

            start = end;
            end += chunkSize;

            chunkThreads[i] = new Thread(chunk);
            chunkThreads[i].start();
        }

        try {
            for (int i = 0; i < numberOfCores; i++) {
                chunkThreads[i].join();
            }
        } catch (Exception e) {
            System.out.println("Erro ao esperar threads na aplicação de média " + e.getMessage());
        }

        endTime = System.nanoTime();

        duration = (endTime - startTime);
        System.out.println("Tempo threaded de ajuste de média: " + duration);

        campoTexto.setText("Equalização pronto");
        System.out.println("Média: " + mediaImagem + ", Desvio padrão: " + desvioImage);

        return outputImage;
    }

    private static void createHistorgram(BufferedImage image) {

        int chunkSize = image.getHeight() / numberOfCores;

        int start = 0;
        int end = chunkSize;

        final int startLocal = start;
        final int endLocal = end;

        Runnable firstChunk = new Runnable() {
            @Override
            public void run() {
                long[] partialValues = createPartialHistogram(startLocal, endLocal, image);
                setHistogramPartialValue(partialValues);
            }
        };

        start = end;
        end += chunkSize;

        final int startLocal1 = start;
        final int endLocal1 = end;
        Runnable secondChunk = new Runnable() {
            @Override
            public void run() {
                long[] partialValues = createPartialHistogram(startLocal1, endLocal1, image);
                setHistogramPartialValue(partialValues);
            }
        };

        start = end;
        end += chunkSize;

        final int startLocal2 = start;
        final int endLocal2 = end;
        Runnable thirdChunk = new Runnable() {
            @Override
            public void run() {
                long[] partialValues = createPartialHistogram(startLocal2, endLocal2, image);
                setHistogramPartialValue(partialValues);
            }
        };

        start = end;
        end += chunkSize;

        final int startLocal3 = start;
        final int endLocal3 = end;
        Runnable fourthChunk = new Runnable() {
            @Override
            public void run() {
                long[] partialValues = createPartialHistogram(startLocal3, endLocal3, image);
                setHistogramPartialValue(partialValues);
            }
        };

        try {
            Thread processOne = new Thread(firstChunk);
            Thread processTwo = new Thread(secondChunk);
            Thread processThree = new Thread(thirdChunk);
            Thread processFour = new Thread(fourthChunk);

            processOne.start();
            processTwo.start();
            processThree.start();
            processFour.start();

            processOne.join();
            processTwo.join();
            processThree.join();
            processFour.join();
        } catch (Exception e) {
            System.out.println("erro ao sincronizar threads");
        }

    }

    private static long[] createPartialHistogram(int start, int end, BufferedImage imagem) {
        long k = 0;
        long x = 0;
        long y = 0;

        int width = imagem.getWidth(null);

        for (int j = start; j < end; j++) {
            for (int i = 0; i < width; i++) {
                int pixel = BlackAndWhite.getGray(imagem.getRGB(i, j));
                x += pixel;
                y += pixel * pixel;
                k += 1;
            }
        }

        long[] returnValues = {x, y, k};

        return returnValues;
    }

    private static void ajustPartialMedium(int start, int end, double mediaImagem, double desvioImage, BufferedImage image, BufferedImage outputImage) {

        int width = image.getWidth();

        for (int j = start; j < end; j++) {
            for (int i = 0; i < width; i++) {
                int pixel = BlackAndWhite.getGray(image.getRGB(i, j));
                float common = (float) Math.sqrt((DESVIO * (float) Math.pow(pixel - mediaImagem, 2)) / desvioImage);

                int n;
                if (pixel > mediaImagem) {
                    n = (int) (MEDIA + common);
                } else {
                    n = (int) (MEDIA - common);
                }

                outputImage.setRGB(i, j, BlackAndWhite.setGray(n));
            }
        }
    }

    private static synchronized void setHistogramPartialValue(long[] histrogramValues) {
        histogramPartialValues[0] += histrogramValues[0];
        histogramPartialValues[1] += histrogramValues[1];
        histogramPartialValues[2] += histrogramValues[2];
    }
}