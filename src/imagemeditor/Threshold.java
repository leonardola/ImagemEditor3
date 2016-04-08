package imagemeditor;

import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 * Created by leonardoalbuquerque on 08/04/16.
 */
public class Threshold {

    public static BufferedImage thresholdImg(BufferedImage im, JProgressBar barraProgresso, JLabel campoTexto) {
        BufferedImage imagem = BlackAndWhite.imageToBW(im);
        int i, j, pixel;
        long media = 0;

        int w = imagem.getWidth(null);
        int h = imagem.getHeight(null);

        BufferedImage localImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        campoTexto.setText("Criando o histograma");
        for (i = 0; i < w; i++) {
            for (j = 0; j < h; j++) {
                pixel = BlackAndWhite.getGray(imagem.getRGB(i, j));
                media += pixel;
            }
        }
        campoTexto.setText("Definindo o thresold");
        media /= (w * h);

        // corta os pixels, conforme a media
        for (i = 0; i < w; i++) {
            for (j = 0; j < h; j++) {
                pixel = BlackAndWhite.getGray(imagem.getRGB(i, j));

                int n;
                if (pixel > media) {
                    n = 255;
                } else {
                    n = 0;
                }

                localImage.setRGB(i, j, BlackAndWhite.setGray(n));
            }
        }
        campoTexto.setText("Thresold pronto");
        return localImage;
    }
}
