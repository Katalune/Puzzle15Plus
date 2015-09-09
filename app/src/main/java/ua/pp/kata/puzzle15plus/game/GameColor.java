package ua.pp.kata.puzzle15plus.game;

import java.util.Random;

/**
 * Creates color palette for gameboard tiles
 */
public class GameColor {

    /**
     * Create new palette, where palette[i] represent 3 float values for H, S and B respectively.
     * @param LEN size of new palette [LEN][3].
     * @return Created palette, palette[0] represent black color!
     */
    public static float[][] createPalette(int LEN) {
        final float STEP = 360f / (LEN - 1);
        float[][] palette = new float[LEN][3];

        Random generator = new Random();
        final float START = generator.nextFloat() * STEP;
        for (int i = 1; i < LEN; i++) {
            float H = (STEP * i - START) % 360;
            float S = (float) Math.sin(H * Math.PI / 75f)*0.15f + 0.6f;
            float B = H > 60 && H < 290 ?
                    (generator.nextInt(10) + 75) / 100f : (generator.nextInt(7) + 93) / 100f;
            float[] hsb = {H, S, B};
            palette[i] = hsb;
        }
        return palette;
    }

}
