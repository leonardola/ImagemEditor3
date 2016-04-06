package imagemeditor;

import imagemeditor.fft.FFT;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 *
 * @author Cendron
 */
public class trataImagem {

    private BufferedImage localImage;

    public void trataImagem() {
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
                orig[i * height + j] = rgbToGray(image.getRGB(i, j));
                //System.out.println("i " + i + ", j: " + j + "-->"  + (i * 6 + j ));
            }
        }
        FFT fft = new FFT(orig, width, height, campoTexto);
        //  fft.intermediate = FreqFilter.filter(fft.intermediate,lowpass, radius);

        finalImage = fft.getPixels();

        localImage = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                localImage.setRGB(i, j, finalImage[i * height + j]);
            }
        }
        // TwoDArray output = inverse.transform(fft.intermediate);

        //final Image invFourier = createImage(new MemoryImageSource(output.width, output.height, inverse.getPixels(output), 0, output.width));
        campoTexto.setText("FFT concluída");
        return localImage;
    }

    public int singlePixelConvolution(BufferedImage image, int x, int y, double[][] k, int kernelWidth, int kernelHeight) {
        int pixel;
        double tmp = 0;
        for (int i = 0; i < kernelWidth; ++i) {
            for (int j = 0; j < kernelHeight; ++j) {
                pixel = rgbToGray(image.getRGB(x + i, y + j));
                tmp = tmp + (pixel * k[i][j]);
            }
        }
        //return setPixel(0, (int) red/257, (int) green/257, (int) blue/257)) ;
        return (int) tmp;
    }

    public BufferedImage applySepia(BufferedImage imagem, JProgressBar barraProgresso, JLabel campoTexto) {
        int i;
        int w = imagem.getWidth(null);
        int h = imagem.getHeight(null);
        int r, g, b, pixel;

        localImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        campoTexto.setText("Aplicando o filtro sepia...");
        barraProgresso.setValue(0);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                pixel = imagem.getRGB(x, y);

                r = (int) (getRed(pixel) * 0.393 + getGreen(pixel) * 0.769 + getBlue(pixel) * 0.189);
                g = (int) (getRed(pixel) * 0.349 + getGreen(pixel) * 0.686 + getBlue(pixel) * 0.168);
                b = (int) (getRed(pixel) * 0.272 + getGreen(pixel) * 0.534 + getBlue(pixel) * 0.131);

                localImage.setRGB(x, y, setPixel(0, r, g, b));
            }
        }

        campoTexto.setText("Imagem em sepia pronta");
        return localImage;
    }

    public BufferedImage imageToBW(BufferedImage imagem, JProgressBar barraProgresso, JLabel campoTexto) {
        campoTexto.setText("Convertendo para escala de Cinza...");
        this.imageToBW(imagem);
        barraProgresso.setValue(0);
        campoTexto.setText("Imagem em cinza pronta");
        return localImage;
    }

    public BufferedImage imageToBW(BufferedImage imagem) {
        int w = imagem.getWidth(null);
        int h = imagem.getHeight(null);
        int pixel;
        localImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                pixel = imagem.getRGB(x, y);
                localImage.setRGB(x, y, rgbToGray(pixel));
                // tmpImagem.setRGB(x, y, setPixel(getAlpha(pixel), getRed(pixel), getGreen(pixel), getBlue(pixel)));  
            }
        }

        return localImage;
    }
    
    


    public BufferedImage equalizaImg(BufferedImage im, JProgressBar barraProgresso, JLabel campoTexto) {
        BufferedImage imagem = imageToBW(im);
        int i, j, k, pixel;
        long x, y;
        double mediaImagem, desvioImage;
        final float MEDIA = 160;
        float DESVIO = 150;

       
        int w = imagem.getWidth(null);
        int h = imagem.getHeight(null);
        
        localImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        
        campoTexto.setText("Criando o histograma da imagem em cinza");
        /* Calcula o histograma em na escala de cinza */
        k = 0;
        x = y = 0;
        for (i = 0; i < w; i++) {
            for (j = 0; j < h; j++) {
                pixel = getGray(imagem.getRGB(i, j));
                x +=  pixel;
                y +=  pixel * pixel;
                k += 1;
            }
        }
        campoTexto.setText("Histograma pronto");
        /* Compute estimate - mean noise is 0 */
        desvioImage = (double)(y - x*x/(float)k)/(float)(k-1);
        desvioImage = Math.sqrt(desvioImage);
        mediaImagem = (double)(x/(float)k);
        
        campoTexto.setText("Fazendo o ajuste baseado na média");
         for (i = 0; i < w; i++) {
            for (j = 0; j < h; j++) {
                pixel = getGray(imagem.getRGB(i, j));
                float common = (float) Math.sqrt((DESVIO * (float) Math.pow(pixel - mediaImagem, 2)) / desvioImage);

                int n;
                if (pixel > mediaImagem) {
                    n = (int) (MEDIA + common);
                } else {
                    n = (int) (MEDIA - common);
                }
                
                localImage.setRGB(i, j, setGray(n));
            }
        }
        campoTexto.setText("Equalização pronto");
        System.out.println("Média: " + mediaImagem + ", Desvio padrão: " + desvioImage);
        
        return localImage;
    }
    
    public BufferedImage thresholdImg(BufferedImage im, JProgressBar barraProgresso, JLabel campoTexto) {
        BufferedImage imagem = imageToBW(im);
        int i, j, pixel;
        long media = 0;
       
        int w = imagem.getWidth(null);
        int h = imagem.getHeight(null);
        
        localImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        campoTexto.setText("Criando o histograma");
        for (i = 0; i < w; i++) {
            for (j = 0; j < h; j++) {
                pixel = getGray(imagem.getRGB(i, j));
                media +=  pixel;
            }
        }
        campoTexto.setText("Definindo o thresold");
        media /= (w * h);
        
        // corta os pixels, conforme a media
         for (i = 0; i < w; i++) {
            for (j = 0; j < h; j++) {
                pixel = getGray(imagem.getRGB(i, j));
                
                int n;
                if (pixel > media) {
                    n = 255;
                } else {
                    n = 0;
                }
                
                localImage.setRGB(i, j, setGray(n));
            }
        }
        campoTexto.setText("Thresold pronto");
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

                
                localImage.setRGB(j, i, setPixel(255, (int)red, (int)green, (int)blue ));
            }
        }
        campoTexto.setText("Redimensionamento pronto");
        return localImage;
    }
    
    
    private int rgbToGray(int pixel) {
        double RED_MULT = 76.843;
        double GRN_MULT = 151.446;
        double BLU_MULT = 29.526;

        int gray = (int) (getRed(pixel) * RED_MULT
                + getGreen(pixel) * GRN_MULT
                + getBlue(pixel) * BLU_MULT
                + 0.5);

        return (gray < 65536 ? gray : 65535);

    }

    private int setPixel(int A, int R, int G, int B) {
        int pixel
                = (A < 255 ? A : 255) << 24
                | (R < 255 ? R : 255) << 16
                | (G < 255 ? G : 255) << 8
                | (B < 255 ? B : 255);

        return pixel;
    }
    
    private int getAlpha(int pixel) {
        return (pixel >> 24) & 0xff;
    }

    private int getRed(int pixel) {
        return (pixel >> 16) & 0xff;
    }

    private int getGreen(int pixel) {
        return (pixel >> 8) & 0xff;
    }

    private int getBlue(int pixel) {
        return (pixel) & 0xff;
    }
    
   private int getGray(int pixel) {
        return (pixel) & 0xff;
    }
   
   private int setGray(int gray){
       int p; 
       p = gray > 255 ? 255 : gray;
       p = gray < 0 ? 0 : gray;
              
      return setPixel(0, p, p, p);
   }

    private static int[] getPixelData(BufferedImage img, int x, int y) {
        int argb = img.getRGB(x, y);

        int rgb[] = new int[]{
            (argb >> 16) & 0xff, //red
            (argb >> 8) & 0xff, //green
            (argb) & 0xff //blue
        };
        return rgb;
    }

    private void printImage(BufferedImage imagem) {
        for (int x = 0; x < imagem.getWidth(); x++) {
            for (int y = 0; y < imagem.getHeight(); y++) {
                int pixel = imagem.getRGB(x, y);
                printPixelARGB(pixel);

            }
        }
    }

    private void printPixelARGB(int pixel) {
        int alpha = getAlpha(pixel);
        int red = getRed(pixel);
        int green = getGreen(pixel);
        int blue = getBlue(pixel);
        System.out.println("argb: " + alpha + ", " + red + ", " + green + ", " + blue);
    }

    public static BufferedImage rescale(BufferedImage originalImage, int w, int h) {
        int finalw = w;
        int finalh = h;
        double factor = 1.0d;
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
}
