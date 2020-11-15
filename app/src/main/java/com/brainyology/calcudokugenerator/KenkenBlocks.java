package com.brainyology.calcudokugenerator;

import java.util.Random;

/**
 * Created by testtube24 on 12/31/17.
 */

class KenkenBlocks {
    public int[] coords;
    public int[] values;
    public int[] options;

    public int cLength;
    public KenkenBlocks tail;
    public String hint;
    public KenkenBlocks (int[] coords, int[] values, int cLength) {
        this.coords = coords;
        this.cLength = cLength;
        this.values = values;
    }

    public void draw () {
        for (int i = 0; i < cLength; i++) {
            System.out.print(coords[i] + " ");
        }
        System.out.print("     " + hint);
        System.out.println("");
    }

    public void assignHint (long seed) {
        Random generator = new Random(seed);


        if (values[2] != 0) {
            if (generator.nextDouble() < 0.5) {
                hint = "+" + Integer.toString(values[0] + values[1] + values[2]);
            } else {
                hint = "*" + Integer.toString(values[0] * values[1] * values[2]);
            }
        } else if (values[1] != 0) {
            if (Math.max(values[1],values[0]) % Math.min(values[1],values[0]) == 0 && generator.nextDouble() < 0.5) {
                hint = "/" + Integer.toString(Math.max(values[1],values[0]) / Math.min(values[1],values[0]));
            } else if (generator.nextDouble() < 0.7) {
                hint = "-" + Integer.toString(Math.max(values[1],values[0]) - Math.min(values[1],values[0]));
            } else if (generator.nextDouble() < 0.5) {
                hint = "+" + Integer.toString(values[0] + values[1]);
            } else {
                hint = "*" + Integer.toString(values[0] * values[1]);
            }
        } else {
            hint = Integer.toString(values[0]);
        }
    }

}