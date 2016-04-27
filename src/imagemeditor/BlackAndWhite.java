package imagemeditor;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * Created by leonardoalbuquerque on 08/04/16.
 */
public class BlackAndWhite {

    public static BufferedImage imageToBW(BufferedImage imagem, JProgressBar barraProgresso, JLabel campoTexto) {
        campoTexto.setText("Convertendo para escala de Cinza...");
        BufferedImage localImage = imageToBW(imagem);
        barraProgresso.setValue(0);
        campoTexto.setText("Imagem em cinza pronta");
        return localImage;
    }

    public static BufferedImage imageToBW(BufferedImage imagem) {
        int w = imagem.getWidth(null);
        int h = imagem.getHeight(null);
        int pixel;
        BufferedImage localImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                pixel = imagem.getRGB(x, y);
                localImage.setRGB(x, y, rgbToGrayScale(pixel));
            }
        }

        return localImage;
    }

    public static int rgbToGrayScale(int pixel) {
        double RED_MULT = 76.843;
        double GRN_MULT = 151.446;
        double BLU_MULT = 29.526;

        int gray = (int) (ColorPixels.getRed(pixel) * RED_MULT
                + ColorPixels.getGreen(pixel) * GRN_MULT
                + ColorPixels.getBlue(pixel) * BLU_MULT
                + 0.5);

        return (gray < 65536 ? gray : 65535);

    }

    public static int getGray(int pixel) {
        return (pixel) & 0xff;
    }

    public static int setGray(int gray) {
        int p;
        p = gray > 255 ? 255 : gray;
        p = gray < 0 ? 0 : gray;

        return ColorPixels.setPixel(0, p, p, p);
    }

}
