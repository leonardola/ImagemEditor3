package imagemeditor;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * Created by leonardoalbuquerque on 25/04/16.
 */
public class Equalize {

    public static BufferedImage equalizaImg(BufferedImage im, JProgressBar barraProgresso, JLabel campoTexto) {
        BufferedImage image = BlackAndWhite.imageToBW(im);

        double mediaImagem, desvioImage;
        final float MEDIA = 160;
        float DESVIO = 150;

        int w = image.getWidth(null);
        int h = image.getHeight(null);

        campoTexto.setText("Criando o histograma da imagem em cinza");

        WritableRaster raster = image.getRaster();

        int[] pixels = RasterCreator.getPixelsFromRaster(raster);
        pixels = RasterCreator.getGreyRasterFromImage(im, pixels);

        long x = 0, y = 0;
        int k = 0;

        //Calcula o histograma em escala de cinza
        for (int i = 0; i < pixels.length; i++) {
            x += pixels[i];
            y += pixels[i] * pixels[i];
            k += 1;
        }

        campoTexto.setText("Histograma pronto");
        //Compute estimate - mean noise is 0
        desvioImage = (double) (y - x * x / (float) k) / (float) (k - 1);
        desvioImage = Math.sqrt(desvioImage);
        mediaImagem = (double) (x / (float) k);

        campoTexto.setText("Fazendo o ajuste baseado na média");

        BufferedImage localImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int pixel = BlackAndWhite.getGray(image.getRGB(i, j));
                float common = (float) Math.sqrt((DESVIO * (float) Math.pow(pixel - mediaImagem, 2)) / desvioImage);

                int n;
                if (pixel > mediaImagem) {
                    n = (int) (MEDIA + common);
                } else {
                    n = (int) (MEDIA - common);
                }

                localImage.setRGB(i, j, BlackAndWhite.setGray(n));
            }
        }

        campoTexto.setText("Equalização pronto");
        System.out.println("Média: " + mediaImagem + ", Desvio padrão: " + desvioImage);

        return localImage;
    }
}