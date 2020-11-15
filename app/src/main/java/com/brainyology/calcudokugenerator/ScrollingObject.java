package com.brainyology.calcudokugenerator;

/**
 * Created by testtube24 on 2/24/18.
 */

public class ScrollingObject implements Comparable<ScrollingObject> {
    private int time;
    private long seed;
    private int height;

    public  ScrollingObject(int time, long seed, int height) {
        this.time = time;
        this.seed = seed;
        this.height = height;
    }

    public int getTime() {
        return time;
    }

    public long getSeed() {
        return seed;
    }

    public int getHeight() {
        return height;
    }


    /**
     * + returned, time > s
     * - returned, time < s
     * = returned, time = s
     */
    public int compareTo(ScrollingObject s) {
        return time - s.time;
    }
}
