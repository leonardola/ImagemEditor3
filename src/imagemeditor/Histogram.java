package imagemeditor;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 * Created by leonardoalbuquerque on 05/04/16.
 */
public class Histogram {

    final static int SUBPIXEL_MAX_VALUE = 256;
    final static int NUMBER_OF_COLOURS = 3;
    public static BufferedImage criaHistograma(BufferedImage imagem, JProgressBar barraProgresso, JLabel campoTexto) {


        int w = imagem.getWidth();
        int h = imagem.getHeight();
        int[][] colourHist = new int[3][SUBPIXEL_MAX_VALUE];

        Raster raster = imagem.getRaster();

        int[] pixels = new int[w * h * NUMBER_OF_COLOURS];
        raster.getPixels(0, 0, w, h, pixels);

        campoTexto.setText("Criando o histograma");

        long startTime = System.nanoTime();

        Runnable redColor = new Runnable() {
            @Override
            public void run() {
                colourHist[RED] = countColor(pixels, RED);
            }
        };

        Runnable greenColor = new Runnable() {
            @Override
            public void run() {
                colourHist[GREEN] = countColor(pixels, GREEN);
            }
        };

        Runnable blueColor = new Runnable() {
            @Override
            public void run() {
                colourHist[BLUE] = countColor(pixels, BLUE);
            }
        };

        try{
            Thread processOne = new Thread(redColor);
            Thread processTwo = new Thread(greenColor);
            Thread processThree = new Thread(blueColor);

            processOne.start();
            processTwo.start();
            processThree.start();

            processOne.join();
            processTwo.join();
            processThree.join();
        }catch (Exception e){
            System.out.println("erro ao sincronizar threads");
        }


        long endTime = System.nanoTime();

        long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
        System.out.println("tempo threaded: "+duration);

        campoTexto.setText("Vetor de Histograma Criado");
        return CreateImagefromIntArray(colourHist);
    }

    private static int[] countColor(int[] pixels, int colorOffset){
        int[] sum = new int[SUBPIXEL_MAX_VALUE];

        for (int i = colorOffset; i < pixels.length; i += NUMBER_OF_COLOURS) {
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

        long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
        System.out.println("Max color tempo sequencial: "+duration);

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