package imagemeditor;

import imagemeditor.fft.FFT;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 * @author Cendron
 */
public class trataImagem {

    private BufferedImage localImage;

    public static BufferedImage rescale(BufferedImage originalImage, int w, int h) {
        int finalw = w;
        int finalh = h;
        double factor = 1;
        if (originalImage.getWidth() > originalImage.getHeight()) {
            factor = ((double) originalImage.getHeight() / (double) originalImage.getWidth());
            finalh = (int) (finalw * factor);
        } else {
            factor = ((double) originalImage.getWidth() / (double) originalImage.getHeight());
            finalw = (int) (finalh * factor);
        }

        BufferedImage resizedImage = new BufferedImage(finalw, finalh, originalImage.getType());
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, finalw, finalh, null);
        g.dispose();
        return resizedImage;
    }

    public void saveImage(BufferedImage image, File file) {
        try {
            ImageIO.write(image, "png", new File(file.getAbsolutePath()));
        } catch (IOException ex) {
            Logger.getLogger(trataImagem.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public BufferedImage getImage() {

        return this.localImage;
    }

    public BufferedImage convolution2D(BufferedImage image, double[][] kernel, JProgressBar barraProgresso, JLabel campoTexto) {
        int width = image.getWidth();
        int height = image.getHeight();
        int total = width * height;
        int kernelWidth = kernel.length;
        int kernelHeight = kernel[0].length;

        int smallWidth = width - kernelWidth + 1;
        int smallHeight = height - kernelHeight + 1;
        int pixel;
        campoTexto.setText("Aplicando a convolução");
        barraProgresso.setValue(0);
        localImage = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);
        for (int i = 0; i < smallWidth; ++i) {
            barraProgresso.setValue((i / smallWidth) * 100);
            for (int j = 0; j < smallHeight; ++j) {

                pixel = singlePixelConvolution(image, i, j, kernel, kernelWidth, kernelHeight);
                localImage.setRGB(i, j, pixel);

            }
        }
        campoTexto.setText("Convolução concluída");
        return localImage;
    }

    public BufferedImage geraFFT(BufferedImage image, JProgressBar barraProgresso, JLabel campoTexto) {

        final int RADIUS_MIN = 5;
        final int RADIUS_MAX = 100;
        final int RADIUS_INIT = 100;
        int radius = RADIUS_INIT;

        int width = image.getWidth();
        int height = image.getHeight();
        int total = width * height;
        int[] orig = new int[total];
        int[] finalImage = new int[total];
        float[] origFloat = new float[total];

        for (int i = 0; i < total; i++) {
            origFloat[i] = (float) orig[i];
        }
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                orig[i * height + j] = BlackAndWhite.rgbToGrayScale(image.getRGB(i, j));
            }
        }
        FFT fft = new FFT(orig, width, height, campoTexto);

        finalImage = fft.getPixels();

        localImage = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                localImage.setRGB(i, j, finalImage[i * height + j]);
            }
        }

        campoTexto.setText("FFT concluída");
        return localImage;
    }

    public int singlePixelConvolution(BufferedImage image, int x, int y, double[][] k, int kernelWidth, int kernelHeight) {
        int pixel;
        double tmp = 0;
        for (int i = 0; i < kernelWidth; ++i) {
            for (int j = 0; j < kernelHeight; ++j) {
                pixel = BlackAndWhite.rgbToGrayScale(image.getRGB(x + i, y + j));
                tmp = tmp + (pixel * k[i][j]);
            }
        }
        return (int) tmp;
    }

    public BufferedImage applySepia(BufferedImage imagem, JProgressBar barraProgresso, JLabel campoTexto) {
        int w = imagem.getWidth(null);
        int h = imagem.getHeight(null);
        int r, g, b, pixel;

        localImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        campoTexto.setText("Aplicando o filtro sepia...");
        barraProgresso.setValue(0);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                pixel = imagem.getRGB(x, y);

                r = (int) (ColorPixels.getRed(pixel) * 0.393 + ColorPixels.getGreen(pixel) * 0.769 + ColorPixels.getBlue(pixel) * 0.189);
                g = (int) (ColorPixels.getRed(pixel) * 0.349 + ColorPixels.getGreen(pixel) * 0.686 + ColorPixels.getBlue(pixel) * 0.168);
                b = (int) (ColorPixels.getRed(pixel) * 0.272 + ColorPixels.getGreen(pixel) * 0.534 + ColorPixels.getBlue(pixel) * 0.131);

                localImage.setRGB(x, y, ColorPixels.setPixel(0, r, g, b));
            }
        }

        campoTexto.setText("Imagem em sepia pronta");
        return localImage;
    }

    public BufferedImage resizeBilinear(BufferedImage img, int w2, int h2, JProgressBar barraProgresso, JLabel campoTexto) {
        int w, h;
        w = img.getWidth();
        h = img.getHeight();

        localImage = new BufferedImage(w2, h2, BufferedImage.TYPE_INT_RGB);

        int a, b, c, d, x, y;
        float x_ratio = ((float) (w - 1)) / w2;
        float y_ratio = ((float) (h - 1)) / h2;
        campoTexto.setText("Redimensionando a imagem...");
        float x_diff, y_diff, blue, red, green;
        for (int i = 0; i < h2; i++) {
            for (int j = 0; j < w2; j++) {
                x = (int) (x_ratio * j);
                y = (int) (y_ratio * i);
                x_diff = (x_ratio * j) - x;
                y_diff = (y_ratio * i) - y;

                a = img.getRGB(x, y);
                b = img.getRGB(x + 1, y);
                c = img.getRGB(x, y + 1);
                d = img.getRGB(x + 1, y + 1);

                // blue element
                // Yb = Ab(1-w)(1-h) + Bb(w)(1-h) + Cb(h)(1-w) + Db(wh)
                blue = (a & 0xff) * (1 - x_diff) * (1 - y_diff) + (b & 0xff) * (x_diff) * (1 - y_diff)
                        + (c & 0xff) * (y_diff) * (1 - x_diff) + (d & 0xff) * (x_diff * y_diff);

                // green element
                // Yg = Ag(1-w)(1-h) + Bg(w)(1-h) + Cg(h)(1-w) + Dg(wh)
                green = ((a >> 8) & 0xff) * (1 - x_diff) * (1 - y_diff) + ((b >> 8) & 0xff) * (x_diff) * (1 - y_diff)
                        + ((c >> 8) & 0xff) * (y_diff) * (1 - x_diff) + ((d >> 8) & 0xff) * (x_diff * y_diff);

                // red element
                // Yr = Ar(1-w)(1-h) + Br(w)(1-h) + Cr(h)(1-w) + Dr(wh)
                red = ((a >> 16) & 0xff) * (1 - x_diff) * (1 - y_diff) + ((b >> 16) & 0xff) * (x_diff) * (1 - y_diff)
                        + ((c >> 16) & 0xff) * (y_diff) * (1 - x_diff) + ((d >> 16) & 0xff) * (x_diff * y_diff);


                localImage.setRGB(j, i, ColorPixels.setPixel(255, (int) red, (int) green, (int) blue));
            }
        }
        campoTexto.setText("Redimensionamento pronto");
        return localImage;
    }
}
