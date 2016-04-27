package imagemeditor;

import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 * Created by leonardoalbuquerque on 26/04/16.
 */
public class Equalize {

    public static BufferedImage equalizeImg(BufferedImage im, JProgressBar barraProgresso, JLabel campoTexto) {
        BufferedImage imagem = BlackAndWhite.imageToBW(im);
        int i, j, k, pixel;
        long x, y;
        double mediaImagem, desvioImage;
        final float MEDIA = 160;
        float DESVIO = 150;


        int w = imagem.getWidth(null);
        int h = imagem.getHeight(null);

        BufferedImage localImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);

        campoTexto.setText("Criando o histograma da imagem em cinza");
        /* Calcula o histograma em escala de cinza */
        k = 0;
        x = y = 0;
        for (i = 0; i < w; i++) {
            for (j = 0; j < h; j++) {
                pixel = BlackAndWhite.getGray(imagem.getRGB(i, j));
                x += pixel;
                y += pixel * pixel;
                k += 1;
            }
        }
        campoTexto.setText("Histograma pronto");
        /* Compute estimate - mean noise is 0 */
        desvioImage = (double) (y - x * x / (float) k) / (float) (k - 1);
        desvioImage = Math.sqrt(desvioImage);
        mediaImagem = (double) (x / (float) k);

        campoTexto.setText("Fazendo o ajuste baseado na média");
        for (i = 0; i < w; i++) {
            for (j = 0; j < h; j++) {
                pixel = BlackAndWhite.getGray(imagem.getRGB(i, j));
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