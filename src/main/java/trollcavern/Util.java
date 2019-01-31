package trollcavern;

import java.util.Random;

public class Util {

    private static Random random = new Random(System.currentTimeMillis());

    /**
     * This function will randomly generate an integer between <i>min</i> and
     * <i>max</i> <b>inclusively</b>. [min,max]
     *
     * @param min
     *            The included lower bound.
     * @param max
     *            The included maximum bound.
     * @return An integer between <i>min</i> and <i>max</i> inclusive.
     */
    public static int rand(int min, int max) {
        return min + (int) (random.nextDouble() * ((max - min) + 1));
    }

}

