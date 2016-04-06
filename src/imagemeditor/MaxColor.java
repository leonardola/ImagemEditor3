package imagemeditor;

/**
 * Created by leonardoalbuquerque on 05/04/16.
 */
public class MaxColor {

    long maxR, maxG, maxB;

    public MaxColor() {
        maxR = maxG = maxB = 0;
    }

    public void compare(int R, int G, int B) {
        if (maxR < R) {
            maxR = R;
        }
        if (maxG < G) {
            maxG = G;
        }
        if (maxB < B) {
            maxB = B;
        }
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
        return Math.max(maxR, Math.max(maxG, maxB));
    }
}

