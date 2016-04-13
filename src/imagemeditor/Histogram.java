package imagemeditor;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * Created by leonardoalbuquerque on 05/04/16.
 */
public class Histogram {

    final static int SUBPIXEL_MAX_VALUE = 256;

    public static BufferedImage criaHistograma(BufferedImage imagem, JProgressBar barraProgresso, JLabel campoTexto) {

        int[][] colourHist = new int[3][SUBPIXEL_MAX_VALUE];

        WritableRaster raster = imagem.getRaster();

        int[] pixels = RasterCreator.getPixelsFromRaster(raster);

        campoTexto.setText("Criando o histograma");

        long startTime = System.nanoTime();

        colourHist[ColorConstants.RED_OFFSET] = countColor(pixels, ColorConstants.RED_OFFSET);
        colourHist[ColorConstants.GREEN_OFFSET] = countColor(pixels, ColorConstants.GREEN_OFFSET);
        colourHist[ColorConstants.BLUE_OFFSET] = countColor(pixels, ColorConstants.BLUE_OFFSET);

        long endTime = System.nanoTime();

        long duration = (endTime - startTime);
        System.out.println("Histograma tempo sequencial: " + duration);

        campoTexto.setText("Vetor de Histograma Criado");
        return CreateImagefromIntArray(colourHist);
    }

    private static int[] countColor(int[] pixels, int colorOffset) {
        int[] sum = new int[SUBPIXEL_MAX_VALUE];

        for (int i = colorOffset; i < pixels.length; i += ColorConstants.NUMBER_OF_COLOURS) {
            sum[pixels[i]]++;
        }

        return sum;
    }

    /**
     * Creates an BufferedImage from a 2D array of integers
     *
     * @return
     */
    public static BufferedImage CreateImagefromIntArray(int[][] pixels) {
        int BI_WIDTH, BI_HEIGHT;
        BI_WIDTH = BI_HEIGHT = 828;
        int largura = BI_HEIGHT / 256;

        long startTime = System.nanoTime();

        MaxColor mx = new MaxColor(pixels);

        long endTime = System.nanoTime();

        long duration = (endTime - startTime);
        System.out.println("Max color tempo sequencial: " + duration);

        double ratio = (BI_HEIGHT - 100) * 1.0 / mx.getMax();
        // Build the histogram image
        BufferedImage bImage = new BufferedImage(BI_WIDTH, BI_HEIGHT, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2D = bImage.createGraphics();

        for (int i = 0; i < 256; i++) {
            g2D.setColor(Color.RED);
            g2D.drawLine(20 + (i * largura), BI_HEIGHT - 20, 20 + (i * largura), BI_HEIGHT - 20 - (int) (pixels[ColorConstants.RED_OFFSET][i] * ratio));
            g2D.setColor(Color.GREEN);
            g2D.drawLine(21 + (i * largura), BI_HEIGHT - 20, 21 + (i * largura), BI_HEIGHT - 20 - (int) (pixels[ColorConstants.GREEN_OFFSET][i] * ratio));
            g2D.setColor(Color.BLUE);
            g2D.drawLine(22 + (i * largura), BI_HEIGHT - 20, 22 + (i * largura), BI_HEIGHT - 20 - (int) (pixels[ColorConstants.BLUE_OFFSET][i] * ratio));
        }

        System.out.println("larg:" + largura + ", " + (256 * largura));
        g2D.setBackground(Color.BLACK);
        g2D.setColor(Color.WHITE);
        g2D.drawLine(10, 40, 10, BI_HEIGHT - 20);
        g2D.drawLine(BI_HEIGHT - 40, BI_HEIGHT - 20, 10, BI_HEIGHT - 20);

        Font myFont = new Font("Arial", 1, 24);
        g2D.setFont(myFont);
        g2D.drawString("RGB Histogram", BI_HEIGHT / 2 - 56, 20);

        return bImage;
    }
}