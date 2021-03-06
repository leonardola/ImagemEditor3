package imagemeditor;

/**
 * Created by leonardoalbuquerque on 07/04/16.
 */
public class MaxSubColor {

    private int[] subPixels;
    private int maxSubPixel = 0;

    MaxSubColor(int [] subPixels){
        this.subPixels = subPixels;
        run();
    }

    public void run(){
        for (int i = 0; i < subPixels.length; i++) {
            if(subPixels[i] > maxSubPixel ){
                maxSubPixel = subPixels[i];
            }
        }
    }

    public int getMaxSubPixel(){
        return maxSubPixel;
    }
}
