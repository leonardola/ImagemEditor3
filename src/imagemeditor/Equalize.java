package imagemeditor;

import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 * Created by leonardoalbuquerque on 26/04/16.
 */
public class Equalize {

    private static final float DESVIO = 150;
    private static final float MEDIA = 160;

    public static BufferedImage equalizeImg(BufferedImage im, JProgressBar barraProgresso, JLabel campoTexto) {
        BufferedImage image = BlackAndWhite.imageToBW(im);
        int i, j, k, pixel;
        long x, y;
        double mediaImagem, desvioImage;


        int w = image.getWidth(null);
        int h = image.getHeight(null);

        BufferedImage outputImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);

        campoTexto.setText("Criando o histograma da imagem em cinza");
        /* Calcula o histograma em escala de cinza */

        long startTime = System.nanoTime();

        long[] partialValues = createPartialHistogram(0, h, image);

        long endTime = System.nanoTime();

        long duration = (endTime - startTime);
        System.out.println("Tempo sequencial de histograma: " + duration);

        x = partialValues[0];
        y = partialValues[1];
        k = (int) partialValues[2];

        campoTexto.setText("Histograma pronto");
        /* Compute estimate - mean noise is 0 */
        desvioImage = (double) (y - x * x / (float) k) / (float) (k - 1);
        desvioImage = Math.sqrt(desvioImage);
        mediaImagem = (double) (x / (float) k);

        campoTexto.setText("Fazendo o ajuste baseado na média");

        startTime = System.nanoTime();

        ajustPartialMedium(0, h, mediaImagem, desvioImage, image, outputImage);

        endTime = System.nanoTime();

        duration = (endTime - startTime);
        System.out.println("Tempo sequencial de ajuste de média: " + duration);

        campoTexto.setText("Equalização pronto");
        System.out.println("Média: " + mediaImagem + ", Desvio padrão: " + desvioImage);

        return outputImage;
    }

    private static long[] createPartialHistogram(int start, int end, BufferedImage imagem) {
        long k = 0;
        long x = 0;
        long y = 0;

        int width = imagem.getWidth(null);
        int height = imagem.getHeight(null);

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
}