package imagemeditor;

/**
 * Created by leonardoalbuquerque on 05/04/16.
 */
public class MaxColor {

    private long maxR, maxG, maxB, max;
    private int[][] pixels;

    public MaxColor(int [][] pixels) {
        maxR = maxG = maxB = 0;
        this.pixels = pixels;
        this.compare();
    }

    public void compare() {

        MaxSubColor maxRedColor = new MaxSubColor(pixels[ColorConstants.RED_OFFSET]);
        MaxSubColor maxGreenColor = new MaxSubColor(pixels[ColorConstants.GREEN_OFFSET]);
        MaxSubColor maxBlueColor = new MaxSubColor(pixels[ColorConstants.BLUE_OFFSET]);


        try{
            Thread processOne = new Thread(maxRedColor);
            Thread processTwo = new Thread(maxGreenColor);
            Thread processThree = new Thread(maxBlueColor);

            processOne.start();
            processTwo.start();
            processThree.start();

            processOne.join();
            processTwo.join();
            processThree.join();
        }catch (Exception e){
            System.out.println("N�o pode executar as threads da maxcolor");
        }

        maxR = maxRedColor.getMaxSubPixel();
        maxG = maxGreenColor.getMaxSubPixel();
        maxB = maxBlueColor.getMaxSubPixel();

        max = Math.max(maxR, Math.max(maxG, maxB));
    }

    public long getMaxR() {
        return maxR;
    }

    public long getMaxG() {
        return maxG;
    }

    public long getMaxB() {
        return maxB;
    }

    public long getMax() {
        return this.max;
    }
}

